package kz.gamma.webra.services.client.monitoring;

import kz.gamma.jce.PKCS10CertificationRequest;
import kz.gamma.webra.services.client.ClientKeyStoreProvider;
import kz.gamma.webra.services.client.WSClient;
import kz.gamma.webra.services.client.WebClientProps;
import kz.gamma.webra.services.common.entities.*;
import kz.gamma.webra.services.common.msgProcess.CryptoProcessor;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * задача мониторит каталоги
 * при наличии запросов подписывает и посылает серверу
 * <p/>
 * Created by i_nikulin
 * 07.08.2009 9:45:28
 */
public class RequestCertTask extends TimerTask {

    private static Logger log = Logger.getLogger(RequestCertTask.class);

    @Override
    public void run() {

        log.info("Запуск обработки");

        File dir = new File(WebClientProps.getInstance().get(WebClientProps.MONITOR_INDIR));
        if (!dir.isDirectory())
            throw new IllegalArgumentException("Требуется указать директорию для мониторинга");

        for (File file : dir.listFiles()) {
            if (file.isFile()) {
                try {
                    processFile(file);
                    copyFile(file, false);
                } catch (Exception e) {
                    log.error("Ошибка при обработки", e);
                    copyFile(file, true);
                }
            }
        }

    }

    private void copyFile(File file, boolean isError) {

        try {

            if (isError) {
                FileUtils.copy(file, new File(WebClientProps.getInstance().get(WebClientProps.MONITOR_ERRORDIR) + "/" + file.getName()));
                log.info("Файл " + file.getName() + " перемещен в директорию ошибок");
            }

            if (!file.delete())
                throw new IOException("Ошибка при удалении файла");

            log.info("Файл " + file.getName() + " удален");

        } catch (IOException e) {
            log.error("Ошибка при перемещении файла", e);
        }

    }


    private void processFile(File file) throws Exception {
        log.info("Обработка файла [" + file.getAbsolutePath() + "]");

        byte[] bin_pkcs10 = FileUtils.getBytesFromFile(file);
        PKCS10CertificationRequest pkcs10 = new PKCS10CertificationRequest(bin_pkcs10);
        log.info("Анализ pkcs10");
        String personDN = pkcs10.getCertificationRequestInfo().getSubject().toString();
        log.info("DN=" + personDN);

        log.info("Поиск пользователя");
        //находим ID пользователя по DN
        DocFindPersonIn docFindPersonIn = new DocFindPersonIn();
        docFindPersonIn.setDN(personDN);
        String encoding = WebClientProps.getInstance().get(WebClientProps.ENCODING);
        DocFindPersonOut docFindPersonOut = (DocFindPersonOut) WSClient.getInstance().callServer("findPerson", docFindPersonIn, encoding);
        log.info("Id пользователя=" + docFindPersonOut.getPersonId());

//        log.info("Создание заказа");
//        DocCreateOrderIn docCreateOrderIn = new DocCreateOrderIn();
//        Order wsOrder = new Order();
//        wsOrder.setClientId(docFindPersonOut.getPersonId());
//        wsOrder.setPayerAddress(WebClientProps.getInstance().get(WebClientProps.PAYER_ADDRESS));
//        wsOrder.setPayerName(WebClientProps.getInstance().get(WebClientProps.PAYER_NAME));
//        wsOrder.setPayerRnn(WebClientProps.getInstance().get(WebClientProps.PAYER_RNN));
//        wsOrder.setAccount(WebClientProps.getInstance().get(WebClientProps.PAYER_ACCOUNT));
//
//        OrderDetail orderDetail = new OrderDetail();
//        orderDetail.setPersonId(docFindPersonOut.getPersonId());
//        orderDetail.setTariffId(BigInteger.valueOf(Long.parseLong(WebClientProps.getInstance().get(WebClientProps.TARIFF_ID))));
//        orderDetail.setCertCount(BigInteger.valueOf(1));
//        docCreateOrderIn.getDetails().add(orderDetail);
//
//        docCreateOrderIn.setBankId(BigInteger.valueOf(Long.parseLong(WebClientProps.getInstance().get(WebClientProps.BANK_ID))));
//        DocCreateOrderOut docCreateOrderOut = (DocCreateOrderOut) WSClient.getInstance().callServer("createOrder", docCreateOrderIn, encoding);
//        log.info("ID заказа=" + docCreateOrderOut.getOrder().getId());
//
//        //confirmOrder
//        log.info("Подтверждение заказа");
//        DocConfirmOrderIn docConfirmOrderIn = new DocConfirmOrderIn();
//        docConfirmOrderIn.setOrderId(docCreateOrderOut.getOrder().getId());
//        DocConfirmOrderOut docConfirmOrderOut = (DocConfirmOrderOut) WSClient.getInstance().callServer("confirmOrder", docConfirmOrderIn, encoding);
//        log.info("Инвойс заказа=" + docConfirmOrderOut.getOrder().getInvoice());

        //requestCert
        log.info("запрос на выпуск");
        byte[] pkcs7 = CryptoProcessor.sign(bin_pkcs10, ClientKeyStoreProvider.getInstance().getStore(), ClientKeyStoreProvider.getInstance().getPassword());
        DocRequestCertIn requestCertIn = new DocRequestCertIn();
//        requestCertIn.setPkcs7(pkcs7);
//        requestCertIn.setOrderDetailId(docCreateOrderOut.getOrder().getDetails().get(0).getOrderDetailId());
        DocRequestCertOut requestCertOut = (DocRequestCertOut) WSClient.getInstance().callServer("requestCert", requestCertIn, encoding);

//        log.info("Серийный номер сертификата=" + new String(requestCertOut.getCert().getCertid(), "UTF-8"));
        log.info("Сохранение сертификата");
        File outFile = new File(WebClientProps.getInstance().get(WebClientProps.MONITOR_OUTDIR) + "/" + file.getName() + ".cer");
        OutputStream out = new FileOutputStream(outFile);
//        out.write(requestCertOut.getCert().getBody());
        out.close();
        log.info("Сертификат сохранен в файл [" + outFile.getAbsolutePath() + "]");

    }


    public static void main(String[] args) throws Exception {

        log.info("start task");
        Timer timer = new Timer();
        timer.schedule(new RequestCertTask(), new Date(), Long.parseLong(WebClientProps.getInstance().get(WebClientProps.MONITOR_DELAY)));
    }

}
