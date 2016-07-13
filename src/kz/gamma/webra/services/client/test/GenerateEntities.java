package kz.gamma.webra.services.client.test;

import kz.gamma.jce.PKCS10CertificationRequest;
import kz.gamma.webra.services.client.ClientKeyStoreProvider;
import kz.gamma.webra.services.common.entities.*;
import kz.gamma.webra.services.common.msgProcess.CryptoProcessor;
import org.junit.Assert;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Created by y_makulbek
 * Date: 19.03.2010 11:13:46
 */
public class GenerateEntities extends Thread {

    private int type;
    private int amount;
    private static Date startDate;
    private static Date endDate;
    private static Date operationStart;
    private static Date operationEnd;
    private static int counter = 0;
    private static GenerateEntities thread;
    final private static JButton button = new JButton("Generate");
    final private static JButton stopButton = new JButton("Stop");
    final private static JTextArea resultLabel = new JTextArea(30, 50);
    private static SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss");
    private static boolean stop = false;

    public void setAmount(int amount)
    {
        this.amount = amount;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    @Override
    public void run() {
        try{
            GenerateDName generator = new GenerateDName();
            DocCreatePersonIn personIn = (DocCreatePersonIn) TestUtils.loadObject("src/kz/gamma/webra/services/client/test/createPerson/001_in.xml");
            String login = personIn.getLogin();
            String firstName = personIn.getPerson().getFirstName();
            if(type == 1)
                personIn.getPerson().setLegalData(null);
            else
                personIn.getPerson().getLegalData().setOrgName(generator.getUserOrg());
            //getBankList
            DocBankListIn docBankListIn = new DocBankListIn();
            docBankListIn.setName("Delta");
            DocBankListOut docBankListOut = (DocBankListOut) TestUtils.callServer("getBankList", docBankListIn);
            Assert.assertTrue("В системе нет банков", docBankListOut.getBankList().size() > 0);
            System.out.println("getBankList");

            //getTariffList
            DocTariffListIn docTariffListIn = new DocTariffListIn();
            DocTariffListOut docTariffListOut = (DocTariffListOut) TestUtils.callServer("getTariffList", docTariffListIn);
            Assert.assertTrue("В системе нет тарифов", docTariffListOut.getTariffList().size() > 0);
            System.out.println("getTariffList");

            for(int i = 0; i < amount; i++) {
                try{
                    if(stop)
                    {
                        endDate = new Date();
                        long time = (endDate.getTime() - startDate.getTime()) / 1000;
                        String seconds = Integer.toString((int)(time % 60));
                        String minutes = Integer.toString((int)((time % 3600) / 60));
                        String hours = Integer.toString((int)(time / 3600));
                        String elapsed = hours + " hours " + minutes + " minutes " + seconds + " seconds";
                        resultLabel.append("\nStart time: " + formatter.format(startDate) + "; End time: " + formatter.format(endDate));
                        resultLabel.append("\nGenerated - " + counter + " from - " + amount + "\nTime elapsed - " + elapsed);
                        counter = 0;
                        button.setEnabled(true);
                        resultLabel.setCaretPosition(resultLabel.getText().length());
                        return;
                    }
                    personIn.setLogin(login + "PHYSIC" + new Random().nextInt(100000));
                    personIn.getPerson().setRnn((100000000000L + new Random().nextInt(10000)) + "");
                    personIn.getPerson().setIdNumber((100000000L + new Random().nextInt(10000)) + "");
                    personIn.getPerson().setFirstName(generator.getUserName());
                    personIn.getPerson().setMiddleName(generator.getUserSurname());
                    personIn.getPerson().setAddress("address");
                    operationStart = new Date();
                    DocCreatePersonOut createPersonOut = (DocCreatePersonOut) TestUtils.callServer("createPerson", personIn);
                    operationEnd = new Date();
                    resultLabel.append("\n" + formatter.format(operationStart) + "   createPerson   " + formatter.format(operationEnd));
                    Assert.assertNotNull("getPersonId=null", createPersonOut.getPersonId());
                    System.out.println("createPerson id=="+createPersonOut.getPersonId());
                    counter++;
                    resultLabel.append("\nregistered " + counter + " user(s)");

                    //loadPerson
                    DocLoadPersonIn docLoadPersonIn = new DocLoadPersonIn();
                    docLoadPersonIn.setPersonId(createPersonOut.getPersonId());
                    operationStart = new Date();
                    DocLoadPersonOut docLoadPersonOut = (DocLoadPersonOut) TestUtils.callServer("loadPerson", docLoadPersonIn);
                    operationEnd = new Date();
                    resultLabel.append("\n" + formatter.format(operationStart) + "   loadPerson   " + formatter.format(operationEnd));
                    System.out.println("loadPerson");

                    //createOrder
                    DocCreateOrderIn docCreateOrderIn = new DocCreateOrderIn();
                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setDn(docLoadPersonOut.getDns().get(0));
                    orderDetail.setTariffId(docTariffListOut.getTariffList().get(0).getId());
                    orderDetail.setClaimCount(new BigInteger("20"));
                    docCreateOrderIn.setClientId(createPersonOut.getPersonId());
                    docCreateOrderIn.getFxOrderDetails().add(orderDetail);
                    operationStart = new Date();
                    DocCreateOrderOut docCreateOrderOut = (DocCreateOrderOut) TestUtils.callServer("createOrder", docCreateOrderIn);
                    operationEnd = new Date();
                    resultLabel.append("\n" + formatter.format(operationStart) + "   createOrder   " + formatter.format(operationEnd));
                    System.out.println("order status is: " + docCreateOrderOut.getOrder().getStatus().name());
                    Assert.assertEquals("Найден другой пользователь", docCreateOrderIn.getFxOrderDetails().get(0).getDn(), docCreateOrderOut.getOrder().getFxOrderDetails().get(0).getDn());

                    //confirmOrder
                    DocConfirmOrderIn docConfirmOrderIn = new DocConfirmOrderIn();
                    docConfirmOrderIn.setOrderId(docCreateOrderOut.getOrder().getId());
                    operationStart = new Date();
                    DocConfirmOrderOut docConfirmOrderOut = (DocConfirmOrderOut) TestUtils.callServer("confirmOrder", docConfirmOrderIn);
                    operationEnd = new Date();
                    resultLabel.append("\n" + formatter.format(operationStart) + "   confirmOrder   " + formatter.format(operationEnd));
                    Assert.assertEquals("Статус должен быть 'сформирован счет'", docConfirmOrderOut.getOrder().getStatus(), OrderTYPE.CONFIRMED);
                    System.out.println("confirmOrder");

                    DocRequestCertIn docRequestCertIn = new DocRequestCertIn();
                    Request request = new Request();
                    request.setOrderDetailId(docConfirmOrderOut.getOrder().getFxOrderDetails().get(0).getId());
                    request.setTariffId(docTariffListOut.getTariffList().get(0).getId());
                    request.setDN(docLoadPersonOut.getDns().get(0));
                    docRequestCertIn.setRequest(request);


                    PKCS10CertificationRequest pkcs10;
                    byte[] pkcs7;
                    RequestDetail requestDetail;

                    for (TariffDetail detail : docTariffListOut.getTariffList().get(0).getFxTariffDetails()) {
                        if (detail.getAlgType() == AlgTYPE.RSA)
                            pkcs10 = CryptoProcessor.getPkcs10RequestRSA(createPersonOut.getDN(), detail.getCertificate());

                        else
                            pkcs10 = CryptoProcessor.getPkcs10RequestGOST(createPersonOut.getDN(), detail.getCertificate());

                        Assert.assertTrue("Error on verify pkcs10", pkcs10.verify());
                        pkcs7 = CryptoProcessor.sign(pkcs10.getDEREncoded(), ClientKeyStoreProvider.getInstance().getStore(),
                                ClientKeyStoreProvider.getInstance().getPassword());
                        requestDetail = new RequestDetail();
                        requestDetail.setTariffDetailId(detail.getId());
                        requestDetail.setBodySigned(pkcs7);
                        docRequestCertIn.getRequest().getFxRequestDetails().add(requestDetail);
                    }

                    //полный запрос 2х сертификатов
                    operationStart = new Date();
                    DocRequestCertOut requestCertOut = (DocRequestCertOut) TestUtils.callServer("requestCert", docRequestCertIn);
                    endDate = new Date();
                    resultLabel.append("\n" + formatter.format(operationStart) + "   requestCert   " + formatter.format(operationEnd));
                    Assert.assertNotNull("getStatus=null", requestCertOut.getRequest().getStatus());
                    System.out.println("requestCert");
                    resultLabel.setCaretPosition(resultLabel.getText().length());
                } catch(Throwable ex) {
                    ex.printStackTrace();
                }
            }
            button.setEnabled(true);
            endDate = new Date();
            long time = (endDate.getTime() - startDate.getTime()) / 1000;
            String seconds = Integer.toString((int)(time % 60));
            String minutes = Integer.toString((int)((time % 3600) / 60));  
            String hours = Integer.toString((int)(time / 3600));
            String elapsed = hours + " hours " + minutes + " minutes " + seconds + " seconds";
            resultLabel.append("\nStart time: " + formatter.format(startDate) + "; End time: " + formatter.format(endDate));
            resultLabel.append("\nGenerated - " + counter + " from - " + amount + "\nTime elapsed - " + elapsed);
            resultLabel.setCaretPosition(resultLabel.getText().length());
            counter = 0;
        } catch(Throwable ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        final JFrame frame = new JFrame("Generate users and certs");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 2));
        panel.add(new JLabel("Type:"));
        String[] types = {"Legal", "Physic"};
        final JComboBox type = new JComboBox(types);
        panel.add(type);
        panel.add(new JLabel("Amount:"));
        final JTextField amount = new JTextField();
        panel.add(amount);
        frame.setLayout(new FlowLayout());
        frame.add(panel);
        frame.add(button);
        frame.add(stopButton);
        resultLabel.setEditable(false);
        resultLabel.setMargin(new Insets(0, 10, 0, 10));
        JScrollPane sPane = new JScrollPane();
        sPane.getViewport().add(resultLabel);
        frame.add(sPane);
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);

        button.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(button.isEnabled())
                {
                    resultLabel.setText("");
                    thread = new GenerateEntities();
                    thread.setAmount(Integer.parseInt(amount.getText()));
                    thread.setType(type.getSelectedIndex());
                    startDate = new Date();
                    thread.start();
                }
                button.setEnabled(false);
                stopButton.setEnabled(true);
            }
        });

        stopButton.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e) {
                stop = true;
                stopButton.setEnabled(false);
            }
        });
    }

}
