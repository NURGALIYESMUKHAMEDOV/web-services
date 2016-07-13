package kz.gamma.webra.services.client.test;

import kz.gamma.jce.PKCS10CertificationRequest;
import kz.gamma.jce.provider.GammaTechProvider;
import kz.gamma.tumarcsp.LibraryWrapper;
import kz.gamma.tumarcsp.ProfileParams;
import kz.gamma.tumarcsp.TumarCspFunctions;
import kz.gamma.util.encoders.Base64;
import kz.gamma.webra.services.client.ClientKeyStoreProvider;
import kz.gamma.webra.services.client.WSClient;
import kz.gamma.webra.services.common.entities.*;
import kz.gamma.webra.services.common.msgProcess.CryptoProcessor;
import kz.gamma.webra.services.common.msgProcess.XmlProcessor;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.security.Security;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Random;

/**
 * Created by i_nikulin
 * 27.03.2009 15:55:12
 */

/**
 * тест клиента веб-сервиса
 * Класс содержит самодостаточные тесты
 */
public class TestWebraWSClient {

    public static DateFormat standartDateFormat = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss.SSS");


    @Test
    public void testMetaServices() throws Exception {
        //test echo
        String str = "Test Message";
        String echoStr = WSClient.getInstance().getWebraWS().echo(str);
        Assert.assertEquals("echo not equals", str, echoStr);
        System.out.println("echo ok");

        //test getVersion
        List<String> ver = WSClient.getInstance().getWebraWS().getVersion();
        Assert.assertNotNull("getVersion=null", ver);
        Assert.assertTrue("version count < 0", ver.size() > 0);
        for (String s : ver) {
            System.out.println(s);
        }

        //test getTumarAdditional
        byte[] tumar = WSClient.getInstance().getWebraWS().getTumarAdditional(false, false);
        Assert.assertNotNull("tumar=null", tumar);

        byte[] tumarJCE = WSClient.getInstance().getWebraWS().getTumarAdditional(false, true);
        Assert.assertNotNull("tumarJCE=null", tumarJCE);

        //проверку можно использовать для однозначной оценки совместимости версии сервера и клиента
        //test getDocumentXSD
        String documentXSD = XmlProcessor.getInstance().getXSD("pki_document.xsd");
        String documentXSDserver = WSClient.getInstance().getWebraWS().getDocumentXSD();
        TestUtils.assertXmlEquals("pki_document.xsd from server not equals", documentXSD, documentXSDserver);
        System.out.println("documentXSDserver=" + documentXSDserver);

        //проверку можно использовать для однозначной оценки совместимости версии сервера и клиента
        //test getTypeXSD
        String typeXSD = XmlProcessor.getInstance().getXSD("pki_type.xsd");
        String typeXSDserver = WSClient.getInstance().getWebraWS().getTypeXSD();
        TestUtils.assertXmlEquals("pki_type.xsd from server not equals", typeXSD, typeXSDserver);
        System.out.println("typeXSDserver=" + typeXSDserver);

    }

    //    @Ignore
    @Test
    public void findPerson() throws Exception {
        //findPerson
        String DN = "C=KZ, O=eTrade kz, CN=Касенов Касе Касенович";
//        String DN = "C=KZ, O=ТОО eTrade.kz, CN=Зайцева Светлана Васильевна, UID=IIN500823400421";

        DocFindPersonIn docFindPersonIn = new DocFindPersonIn();
        docFindPersonIn.setDN(DN);
        DocFindPersonOut docFindPersonOut = (DocFindPersonOut) TestUtils.callServer("findPerson", docFindPersonIn);
        System.out.println("findPerson");
        Assert.assertTrue("Пользователь не найден", docFindPersonOut.getPersonId() != null);
        System.out.println("personId: " + docFindPersonOut.getPersonId());
    }

    @Test
    public void getTariffList() throws Exception {
        //getTariffList
        DocTariffListIn docTariffListIn = new DocTariffListIn();
        DocTariffListOut docTariffListOut = (DocTariffListOut) TestUtils.callServer("getTariffList", docTariffListIn);

        Assert.assertTrue("В системе нет тарифов", docTariffListOut.getTariffList().size() > 0);

        System.out.println("getTariffList");
        if (docTariffListOut.getTariffList().size() > 0) {
            for (Tariff t : docTariffListOut.getTariffList()) {
                System.out.println(t.getName() + " " + t.getId() + " " + t.getPrice());
                for (TariffDetail d : t.getFxTariffDetails()) {
                    System.out.println("\t"+d.getCertificate());
                }
            }
        }
    }

    @Test
    public void requestCert() throws Exception {
        //requestCert

        long kiscId = 77690;
        long tariffId = 45;
        long detailId = 82;
        String DN = "C=KZ, O=eTrade kz, CN=Касенов Касе Касенович";
        String tariffCert = "C=KZ, O=Template, CN=GOST_USER_SIGN_1Y";
        String storage = "TESTUSER";
        String pass = "123";


/*        long kiscId = 2596107;
        long tariffId = 1171;
        long detailId = 2312;
        String DN = "C=KZ, O=ТОО eTrade.kz, CN=Зайцева Светлана Васильевна, UID=IIN500823400421";
        String tariffCert = "C=KZ, O=Template, CN=GOST_USER_SIGN_1Y";
        String storage = "TESTUSER";
        String pass = "123";*/

        Security.addProvider(new GammaTechProvider());
        TumarCspFunctions.initialize(LibraryWrapper.LIBRARY_NAME);


        DocRequestCertIn docRequestCertIn = new DocRequestCertIn();
        Request request = new Request();
        request.setOrderDetailId(BigInteger.valueOf(kiscId));
        request.setTariffId(BigInteger.valueOf(tariffId));
        request.setDN(DN);
        docRequestCertIn.setRequest(request);

        String resMes = createProfile(storage, pass, System.getProperty("user.dir") + "\\cert-base");
//
//
        PKCS10CertificationRequest pkcs10;
        byte[] pkcs7;
        RequestDetail requestDetail;


        pkcs10 = CryptoProcessor.getPKSC10GOSTRequest(DN, storage, pass,tariffCert);

        Assert.assertTrue("Error on verify pkcs10", pkcs10.verify());
        pkcs7 = CryptoProcessor.sign(pkcs10.getDEREncoded(), ClientKeyStoreProvider.getInstance().getStore(),
                ClientKeyStoreProvider.getInstance().getPassword());
        requestDetail = new RequestDetail();
        requestDetail.setTariffDetailId(BigInteger.valueOf(detailId));
        requestDetail.setBodySigned(pkcs7);
        docRequestCertIn.getRequest().getFxRequestDetails().add(requestDetail);

        //полный запрос 2х сертификатов
        DocRequestCertOut requestCertOut = (DocRequestCertOut) TestUtils.callServer("requestCert", docRequestCertIn);
        Assert.assertNotNull("getStatus=null", requestCertOut.getRequest().getStatus());
        System.out.println("requestCert");
    }

    //    @Ignore
    @Test
    public void testPhysicClient() throws Exception {
        //createPerson
        DocCreatePersonIn createPhysicIn = (DocCreatePersonIn) TestUtils.loadObject("src/kz/gamma/webra/services/client/test/createPerson/createPerson.xml");
//        createPhysicIn.setLogin(createPhysicIn.getLogin() + "PHYSIC" + new Random().nextInt(10000));
//        createPhysicIn.getPerson().setIin((100000000000L + new Random().nextInt(10000)) + "");
//        createPhysicIn.getPerson().setIdNumber((100000000L + new Random().nextInt(10000)) + "");
//        createPhysicIn.getPerson().setFirstName(createPhysicIn.getPerson().getFirstName() + "abcdefghijklmnopqrstuvwxyz".charAt(new Random().nextInt(25)));
//        createPhysicIn.getPerson().setLegalData(null);
//        createPhysicIn.getPerson().setAddress("address");
        DocCreatePersonOut createPhysicOut = (DocCreatePersonOut) TestUtils.callServer("createPerson", createPhysicIn);
        Assert.assertNotNull("getPersonId=null", createPhysicOut.getPersonId());
        System.out.println("createPerson id==" + createPhysicOut.getPersonId());

        //loadPerson
        DocLoadPersonIn docLoadPersonIn = new DocLoadPersonIn();
        docLoadPersonIn.setPersonId(createPhysicOut.getPersonId());
        DocLoadPersonOut docLoadPersonOut = (DocLoadPersonOut) TestUtils.callServer("loadPerson", docLoadPersonIn);
        System.out.println("loadPerson");

        //updatePerson
        DocUpdatePersonIn docUpdatePersonIn = new DocUpdatePersonIn();
        docUpdatePersonIn.setPerson(createPhysicIn.getPerson());
        docUpdatePersonIn.getPerson().setFirstName(docUpdatePersonIn.getPerson().getFirstName() + "abcdefghijklmnopqrstuvwxyz".charAt(new Random().nextInt(25)));
        docUpdatePersonIn.setPersonId(createPhysicOut.getPersonId());
        DocUpdatePersonOut docUpdatePersonOut = (DocUpdatePersonOut) TestUtils.callServer("updatePerson", docUpdatePersonIn);
        Assert.assertNotNull("updatePerson error", docUpdatePersonOut);
        System.out.println("updatePerson");

        //loadPerson
        docLoadPersonIn = new DocLoadPersonIn();
        docLoadPersonIn.setPersonId(createPhysicOut.getPersonId());
        docLoadPersonOut = (DocLoadPersonOut) TestUtils.callServer("loadPerson", docLoadPersonIn);
        System.out.println("loadPerson");

        //findPerson
        DocFindPersonIn docFindPersonIn = new DocFindPersonIn();
        docFindPersonIn.setDN(createPhysicOut.getDN());
        DocFindPersonOut docFindPersonOut = (DocFindPersonOut) TestUtils.callServer("findPerson", docFindPersonIn);
        Assert.assertEquals("Найден другой пользователь", docFindPersonOut.getPersonId(), createPhysicOut.getPersonId());
        System.out.println("findPerson");

        //getTariffList
        DocTariffListIn docTariffListIn = new DocTariffListIn();
        DocTariffListOut docTariffListOut = (DocTariffListOut) TestUtils.callServer("getTariffList", docTariffListIn);
        Assert.assertTrue("В системе нет тарифов", docTariffListOut.getTariffList().size() > 0);
        System.out.println("getTariffList");

        //createOrder
        DocCreateOrderIn docCreateOrderIn = new DocCreateOrderIn();
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setDn(docLoadPersonOut.getDns().get(0));
        orderDetail.setTariffId(docTariffListOut.getTariffList().get(0).getId());
        orderDetail.setClaimCount(new BigInteger("20"));

        docCreateOrderIn.setClientId(docFindPersonOut.getPersonId());
        docCreateOrderIn.getFxOrderDetails().add(orderDetail);

        DocCreateOrderOut docCreateOrderOut = (DocCreateOrderOut) TestUtils.callServer("createOrder", docCreateOrderIn);
        System.out.println("order status is: " + docCreateOrderOut.getOrder().getStatus().name());
        Assert.assertEquals("Найден другой пользователь", docCreateOrderIn.getFxOrderDetails().get(0).getDn(), docCreateOrderOut.getOrder().getFxOrderDetails().get(0).getDn());
//
//        //confirmOrder
        DocConfirmOrderIn docConfirmOrderIn = new DocConfirmOrderIn();
        docConfirmOrderIn.setOrderId(docCreateOrderOut.getOrder().getId());
        DocConfirmOrderOut docConfirmOrderOut = (DocConfirmOrderOut) TestUtils.callServer("confirmOrder", docConfirmOrderIn);
        Assert.assertEquals("Статус должен быть 'сформирован счет'", docConfirmOrderOut.getOrder().getStatus(), OrderTYPE.CONFIRMED);
        System.out.println("confirmOrder");

        //requestCert
        DocRequestCertIn docRequestCertIn = new DocRequestCertIn();
        Request request = new Request();
        request.setOrderDetailId(docConfirmOrderOut.getOrder().getFxOrderDetails().get(0).getId());
        request.setTariffId(docTariffListOut.getTariffList().get(0).getId());
        request.setDN(docLoadPersonOut.getDns().get(0));
        docRequestCertIn.setRequest(request);
//
//
        PKCS10CertificationRequest pkcs10;
        byte[] pkcs7;
        RequestDetail requestDetail;

        for (TariffDetail detail : docTariffListOut.getTariffList().get(0).getFxTariffDetails()) {
            if (detail.getAlgType() == AlgTYPE.RSA)
                pkcs10 = CryptoProcessor.getPkcs10RequestRSA(docLoadPersonOut.getDns().get(0), detail.getCertificate());

            else
                pkcs10 = CryptoProcessor.getPkcs10RequestGOST(docLoadPersonOut.getDns().get(0), detail.getCertificate());

            Assert.assertTrue("Error on verify pkcs10", pkcs10.verify());
            pkcs7 = CryptoProcessor.sign(pkcs10.getDEREncoded(), ClientKeyStoreProvider.getInstance().getStore(),
                    ClientKeyStoreProvider.getInstance().getPassword());
            requestDetail = new RequestDetail();
            requestDetail.setTariffDetailId(detail.getId());
            requestDetail.setBodySigned(pkcs7);
            docRequestCertIn.getRequest().getFxRequestDetails().add(requestDetail);
        }

        //полный запрос 2х сертификатов
        DocRequestCertOut requestCertOut = (DocRequestCertOut) TestUtils.callServer("requestCert", docRequestCertIn);
        Assert.assertNotNull("getStatus=null", requestCertOut.getRequest().getStatus());
        System.out.println("requestCert");
    }

    @Test
    public void testGetRoles() throws Exception {
        //getRoles
        DocGetRolesIn docGetRolesIn = new DocGetRolesIn();
        DocGetRolesOut docGetRolesOut = (DocGetRolesOut) TestUtils.callServer("getRoleList", docGetRolesIn);
        Assert.assertTrue("У регистратора нет ролей", docGetRolesOut.getRoleList().size() > 0);
        System.out.println("getTariffList");
        for (Role role : docGetRolesOut.getRoleList())
            System.out.println("id: " + role.getId() + " name: " + role.getName() + " description: " + role.getDescription());
    }

    @Test
    public void testGetOrganization() throws Exception {
        //getOrganization
        DocGetOrganizationIn docGetOrganizationIn = new DocGetOrganizationIn();
        DocGetOrganizationOut docGetOrganizationOut = (DocGetOrganizationOut) TestUtils.callServer("getOrganization", docGetOrganizationIn);
        Assert.assertNotNull("Организация = null", docGetOrganizationOut.getOrganization());
    }

    @Test
    public void changePassword() throws Exception {
        //changePassword
        DocGetPersonListIn docGetPersonListIn = new DocGetPersonListIn();
        DocGetPersonListOut docGetPersonListOut = (DocGetPersonListOut) TestUtils.callServer("getPersonList", docGetPersonListIn);
        DocChangePasswordIn docChangePasswordIn = new DocChangePasswordIn();
        docChangePasswordIn.setPersonId(docGetPersonListOut.getPersonList().get(0));
        docChangePasswordIn.setPassword("newPassword");
        DocChangePasswordOut docChangePasswordOut = (DocChangePasswordOut) TestUtils.callServer("changePassword", docChangePasswordIn);
    }

    @Test
    public void testServices() throws Exception {

        //getRoles
//        DocGetRolesIn docGetRolesIn = new DocGetRolesIn();
//        DocGetRolesOut docGetRolesOut = (DocGetRolesOut) TestUtils.callServer("getRoleList", docGetRolesIn);
//        Assert.assertTrue("У регистратора нет ролей", docGetRolesOut.getRoleList().size() > 0);
//        System.out.println("getRoles");
//        for(Role role: docGetRolesOut.getRoleList())
//            System.out.println("id: " + role.getId() + " name: " + role.getName() + " description: " + role.getDescription());

        //createPerson
//        DocCreatePersonIn createPersonIn = (DocCreatePersonIn) TestUtils.loadObject("src/kz/gamma/webra/services/client/test/createPerson/001_in.xml");
//        createPersonIn.setLogin(createPersonIn.getLogin() + new Random().nextInt(10000));
//        createPersonIn.getPerson().setRoleId(BigInteger.valueOf(4));
//        createPersonIn.getPerson().setNumber((100000000000L + new Random().nextInt(10000)) + "");
//        createPersonIn.getPerson().setNumberType("IIN");
//        createPersonIn.getPerson().setIdNumber((100000000L + new Random().nextInt(10000)) + "");
//        createPersonIn.getPerson().setFirstName(createPersonIn.getPerson().getFirstName() + "abcdefghijklmnopqrstuvwxyz".charAt(new Random().nextInt(25)));
//        createPersonIn.getPerson().getLegalData().setOrgName(createPersonIn.getPerson().getLegalData().getOrgName() + "abcdefghijklmnopqrstuvwxyz".charAt(new Random().nextInt(25)));
//        createPersonIn.getPerson().setUid("asdf1234");
//        DocCreatePersonOut createPersonOut = (DocCreatePersonOut) TestUtils.callServer("createPerson", createPersonIn);
//        Assert.assertNotNull("getPersonId=null", createPersonOut.getPersonId());
//        System.out.println("createPerson id=="+createPersonOut.getPersonId());

        //loadPerson
//        DocLoadPersonIn docLoadPersonIn = new DocLoadPersonIn();
//        docLoadPersonIn.setPersonId(createPersonOut.getPersonId());
//        DocLoadPersonOut docLoadPersonOut = (DocLoadPersonOut) TestUtils.callServer("loadPerson", docLoadPersonIn);
//        System.out.println("loadPerson");

        //updatePerson
//        DocUpdatePersonIn docUpdatePersonIn = new DocUpdatePersonIn();
//        docUpdatePersonIn.setPerson(createPersonIn.getPerson());
//        docUpdatePersonIn.getPerson().setRoleId(docGetRolesOut.getRoleList().get(0).getId());
//        docUpdatePersonIn.getPerson().setFirstName(docUpdatePersonIn.getPerson().getFirstName() + "abcdefghijklmnopqrstuvwxyz".charAt(new Random().nextInt(25)));
//        docUpdatePersonIn.setPersonId(createPersonOut.getPersonId());
//        DocUpdatePersonOut docUpdatePersonOut = (DocUpdatePersonOut) TestUtils.callServer("updatePerson", docUpdatePersonIn);
//        Assert.assertNotNull("updatePerson error", docUpdatePersonOut);
//        System.out.println("updatePerson");

        //setDN
//        DocSetDNIn docSetDNIn = new DocSetDNIn();
//        docSetDNIn.setPersonId(createPersonOut.getPersonId());
//        docSetDNIn.setDN("C=KZ, ST=" + docLoadPersonOut.getPerson().getSt() + ", L=" + docLoadPersonOut.getPerson().getL() +
//                ", O=gamma, OU=serOrge, CN=" + docLoadPersonOut.getPerson().getFirstName() + " " + docLoadPersonOut.getPerson().getLastName() +
//                "abcdefghijklmnopqrstuvwxyz".charAt(new Random().nextInt(25)) +
//                ", SN=NR" + (100000000000L + new Random().nextInt(10000)) + ", E=y_makulbek@gamma.kz");
//        DocSetDNOut docSetDNOut = (DocSetDNOut) TestUtils.callServer("setDN", docSetDNIn);
//        Assert.assertTrue("У пользователя нет DN", docSetDNOut.getDns().size() > 0);
//        System.out.println("setDN");

        //loadPerson
//        docLoadPersonIn = new DocLoadPersonIn();
//        docLoadPersonIn.setPersonId(createPersonOut.getPersonId());
//        docLoadPersonOut = (DocLoadPersonOut) TestUtils.callServer("loadPerson", docLoadPersonIn);
//        System.out.println("loadPerson");

        //findPerson
//        DocFindPersonIn docFindPersonIn = new DocFindPersonIn();
//        docFindPersonIn.setDN(createPersonOut.getDN());
//        docFindPersonIn.setLogin("dracom");
//        DocFindPersonOut docFindPersonOut = (DocFindPersonOut) TestUtils.callServer("findPerson", docFindPersonIn);
//        Assert.assertEquals("Найден другой пользователь", docFindPersonOut.getPersonId(), createPersonOut.getPersonId());
//        System.out.println("findPerson");

        //getBankList
//        DocBankListIn docBankListIn = new DocBankListIn();
//        docBankListIn.setName("Delta");
//        DocBankListOut docBankListOut = (DocBankListOut) TestUtils.callServer("getBankList", docBankListIn);
//        Assert.assertTrue("В системе нет банков", docBankListOut.getBankList().size() > 0);
//        System.out.println("getBankList");

        //getTariffList
        DocTariffListIn docTariffListIn = new DocTariffListIn();
        DocTariffListOut docTariffListOut = (DocTariffListOut) TestUtils.callServer("getTariffList", docTariffListIn);
        Assert.assertTrue("В системе нет тарифов", docTariffListOut.getTariffList().size() > 0);
        System.out.println("getTariffList");

        //createOrder
//        DocCreateOrderIn docCreateOrderIn = new DocCreateOrderIn();
//        Order order = new Order();
//        order.setClientId(createPersonOut.getPersonId());
//        order.setBankId(docBankListOut.getBankList().get(3).getId());
//        order.setAccount(docLoadPersonOut.getPerson().getAccount());
//        order.setPayerNumber(docLoadPersonOut.getPerson().getNumber());
//        order.setPayerNumberType("RNN");
//        order.setPayerAddress("adress");
//        order.setPayerName(docLoadPersonOut.getPerson().getFirstName() + docLoadPersonOut.getPerson().getLastName());
//        OrderDetail orderDetail = new OrderDetail();
//        orderDetail.setDn(docLoadPersonOut.getDns().get(0));
//        orderDetail.setTariffId(docTariffListOut.getTariffList().get(0).getId());
//        orderDetail.setClaimCount(new BigInteger("20"));
//        order.getFxOrderDetails().add(orderDetail);
//        docCreateOrderIn.setOrder(order);
//        DocCreateOrderOut docCreateOrderOut = (DocCreateOrderOut) TestUtils.callServer("createOrder", docCreateOrderIn);
//        System.out.println("order status is: " + docCreateOrderOut.getOrder().getStatus().name());
//        Assert.assertEquals("Найден другой пользователь", docCreateOrderIn.getOrder().getFxOrderDetails().get(0).getDn(), docCreateOrderOut.getOrder().getFxOrderDetails().get(0).getDn());

        //confirmOrder
//        DocConfirmOrderIn docConfirmOrderIn = new DocConfirmOrderIn();
//        docConfirmOrderIn.setOrderId(docCreateOrderOut.getOrder().getId());
//        DocConfirmOrderOut docConfirmOrderOut = (DocConfirmOrderOut) TestUtils.callServer("confirmOrder", docConfirmOrderIn);
//        Assert.assertEquals("Статус должен быть 'сформирован счет'", docConfirmOrderOut.getOrder().getStatus(), OrderTYPE.CONFIRMED);
//        System.out.println("confirmOrder");

        //getOrderList
//        DocOrderListIn docOrderListIn = new DocOrderListIn();
//        docOrderListIn.setPersonId(createPersonOut.getPersonId());
//        DocOrderListOut docOrderListOut = (DocOrderListOut) TestUtils.callServer("getOrderList", docOrderListIn);
//        for(Order fOrder: docOrderListOut.getOrderList())
//            System.out.println("invoiceNum: " + fOrder.getInvoiceNum());
//        Assert.assertTrue("В системе нет заказов", docOrderListOut.getOrderList().size() > 0);


        //requestCert
//        DocRequestCertIn docRequestCertIn = new DocRequestCertIn();
//        Request request = new Request();
//        request.setClientId(BigInteger.valueOf(1567));
////        request.setOrderDetailId(docConfirmOrderOut.getOrder().getFxOrderDetails().get(0).getId());
//        request.setTariffId(docTariffListOut.getTariffList().get(0).getId());
//        request.setDN("C=KZ, O=BCC-Invest, CN=Карымов Рустем Серикович, UID=IIN810430350083, E=rustem.karymov@gmail.com");
//        docRequestCertIn.setRequest(request);


//        PKCS10CertificationRequest pkcs10;
//        byte[] pkcs7;
//        RequestDetail requestDetail;
//
//        for (TariffDetail detail : docTariffListOut.getTariffList().get(0).getFxTariffDetails()) {
//            if (detail.getAlgType() == AlgTYPE.RSA)
//                pkcs10 = CryptoProcessor.getPkcs10RequestRSA("C=KZ, O=BCC-Invest, CN=Карымов Рустем Серикович, UID=IIN810430350083, E=rustem.karymov@gmail.com", detail.getCertificate());
//
//            else
//                pkcs10 = CryptoProcessor.getPkcs10RequestGOST("C=KZ, O=BCC-Invest, CN=Карымов Рустем Серикович, UID=IIN810430350083, E=rustem.karymov@gmail.com", detail.getCertificate());
//
//            Assert.assertTrue("Error on verify pkcs10", pkcs10.verify());
//            pkcs7 = CryptoProcessor.sign(pkcs10.getDEREncoded(), ClientKeyStoreProvider.getInstance().getStore(),
//                    ClientKeyStoreProvider.getInstance().getPassword());
//            requestDetail = new RequestDetail();
//            requestDetail.setTariffDetailId(detail.getId());
//            requestDetail.setBodySigned(pkcs7);
//            docRequestCertIn.getRequest().getFxRequestDetails().add(requestDetail);
//        }

        //полный запрос 2х сертификатов
//        DocRequestCertOut requestCertOut = (DocRequestCertOut) TestUtils.callServer("requestCert", docRequestCertIn);
//        Assert.assertNotNull("getStatus=null", requestCertOut.getRequest().getStatus());
//        System.out.println("requestCert");

        //getRequestList
//        DocRequestListIn docRequestListIn = new DocRequestListIn();
//        docRequestListIn.setPersonId(createPersonOut.getPersonId());
//        DocRequestListOut docRequestListOut = (DocRequestListOut) TestUtils.callServer("getRequestList", docRequestListIn);
//        for(Request requestl: docRequestListOut.getRequestList())
//            System.out.println("request: " + requestl.getCause() + " " + requestl.getStatus());
//        Assert.assertTrue("В системе нет заказов", docRequestListOut.getRequestList().size() > 0);

        //requestCertList
//        DocGetCertListIn docGetCertListIn = new DocGetCertListIn();
//        docGetCertListIn.setPesonId(createPersonOut.getPersonId());
//        DocGetCertListOut docGetCertListOut = (DocGetCertListOut) TestUtils.callServer("getCertList", docGetCertListIn);
//        for(int i = 0; i < docGetCertListOut.getCertList().size(); i++)
//        {
//            BufferedWriter out = new BufferedWriter(new FileWriter("out/" + standartDateFormat.format(new Date()) + "_cert_" + i + ".cer"));
//            out.write(new String(docGetCertListOut.getCertList().get(i).getBody()));
//            out.close();
//            System.out.println("certBody: " + new String(docGetCertListOut.getCertList().get(i).getBody()));
//        }
//        Assert.assertTrue("certList not empty", !docGetCertListOut.getCertList().isEmpty());

//        createCMS
//        if (!docGetCertListOut.getCertList().isEmpty()) {
//            byte[] cert = Base64.decode(docGetCertListOut.getCertList().get(0).getBody());
//            ByteArrayInputStream bis = new ByteArrayInputStream(cert);
//            Security.addProvider(new GammaTechProvider());
//            CertificateFactory cf = CertificateFactory.getInstance("X.509", "GAMMA");
//            X509Certificate x509cert = (X509Certificate) cf.generateCertificate(bis);
//            RevokeRequest rev = new RevokeRequest(x509cert, 1);
//            byte[] cms = rev.getTBSRequest();
//            System.out.println("create cms");
//
//
//            byte[] revokePkcs7 = CryptoProcessor.sign(cms, ClientKeyStoreProvider.getInstance().getStore(),
//                    ClientKeyStoreProvider.getInstance().getPassword());
//
//            //revokeCert
//            DocRevokeCertIn docRevokeCertIn = new DocRevokeCertIn();
//            docRevokeCertIn.setSignedCMS(revokePkcs7);
//            docRevokeCertIn.setSerialNumber(docGetCertListOut.getCertList().get(0).getSerialNumber());
//            DocRevokeCertOut docRevokeCertOut = (DocRevokeCertOut) TestUtils.callServer("revokeCert", docRevokeCertIn);
//            Assert.assertNotNull("getStatus=null", docRevokeCertOut.getRequest().getStatus());
//            System.out.println("revokeCert");
//        }
    }

    @Test
    public void testDelayedRequests() throws Exception {

        //getPersonList
        DocGetPersonListIn docGetPersonListIn = new DocGetPersonListIn();
        DocGetPersonListOut docGetPersonListOut = (DocGetPersonListOut) TestUtils.callServer("getPersonList", docGetPersonListIn);
        Assert.assertTrue("clients size > 0", !docGetPersonListOut.getPersonList().isEmpty());
        System.out.println("there are " + docGetPersonListOut.getPersonList().size() + " clients in organization");

        for (BigInteger id : docGetPersonListOut.getPersonList()) {
            DocRequestListIn docRequestListIn = new DocRequestListIn();
            docRequestListIn.setPersonId(id);
            DocRequestListOut docRequestListOut = (DocRequestListOut) TestUtils.callServer("getDelayedRequestList", docRequestListIn);
            for (Request requestl : docRequestListOut.getRequestList()) {
                System.out.println("request: " + requestl.getCause() + " " + requestl.getStatus());
                System.out.println("request tId: " + new String(requestl.getFxRequestDetails().get(0).getBodySigned()));
                System.out.println("request DN: " + requestl.getDN());
                DocRequestCertIn docRequestCertIn = new DocRequestCertIn();
                String postRequest = CryptoProcessor.postReqToASN(1, requestl.getDN(), new String(requestl.getFxRequestDetails().get(0).getBodySigned()));
                byte[] derEncoded = Base64.decode(postRequest);
                byte[] pkcs7 = CryptoProcessor.sign(derEncoded, ClientKeyStoreProvider.getInstance().getStore(),
                        ClientKeyStoreProvider.getInstance().getPassword());
                requestl.getFxRequestDetails().get(0).setBodySigned(pkcs7);
                docRequestCertIn.setRequest(requestl);
                DocRequestCertOut requestCertOut = (DocRequestCertOut) TestUtils.callServer("requestCert", docRequestCertIn);
                System.out.println("confirmed certificate status: " + requestCertOut.getRequest().getStatus());
            }
        }

    }

    @Test
    public void testInitRequest() throws Exception {
        //getTariffList
        DocTariffListIn docTariffListIn = new DocTariffListIn();
        DocTariffListOut docTariffListOut = (DocTariffListOut) TestUtils.callServer("getTariffList", docTariffListIn);
        Assert.assertTrue("В системе нет тарифов", docTariffListOut.getTariffList().size() > 0);
        System.out.println("getTariffList");

        DocLoadPersonIn docLoadPersonIn = new DocLoadPersonIn();
        docLoadPersonIn.setPersonId(BigInteger.valueOf(4));
        DocLoadPersonOut docLoadPersonOut = (DocLoadPersonOut) TestUtils.callServer("loadPerson", docLoadPersonIn);
        System.out.println("loadPerson");

        //requestCert
        DocRequestCertIn docRequestCertIn = new DocRequestCertIn();
        Request request = new Request();
//        request.setOrderDetailId(docConfirmOrderOut.getOrder().getFxOrderDetails().get(0).getId());
        request.setTariffId(docTariffListOut.getTariffList().get(0).getId());
        request.setDN(docLoadPersonOut.getDns().get(0));
        docRequestCertIn.setRequest(request);

        PKCS10CertificationRequest pkcs10;
        byte[] pkcs7;
        RequestDetail requestDetail;

        for (TariffDetail detail : docTariffListOut.getTariffList().get(0).getFxTariffDetails()) {
            if (detail.getAlgType() == AlgTYPE.RSA)
                pkcs10 = CryptoProcessor.getPkcs10RequestRSA(docLoadPersonOut.getDns().get(0), detail.getCertificateInit());

            else
                pkcs10 = CryptoProcessor.getPkcs10RequestGOST(docLoadPersonOut.getDns().get(0), detail.getCertificateInit());

            Assert.assertTrue("Error on verify pkcs10", pkcs10.verify());
            pkcs7 = CryptoProcessor.sign(pkcs10.getDEREncoded(), ClientKeyStoreProvider.getInstance().getStore(),
                    ClientKeyStoreProvider.getInstance().getPassword());
            requestDetail = new RequestDetail();
            requestDetail.setTariffDetailId(detail.getId());
            requestDetail.setBodySigned(pkcs7);
            docRequestCertIn.getRequest().getFxRequestDetails().add(requestDetail);
        }

        //полный запрос 2х сертификатов
        DocRequestCertOut requestCertOut = (DocRequestCertOut) TestUtils.callServer("requestCert", docRequestCertIn);
        Assert.assertNotNull("getStatus=null", requestCertOut.getRequest().getStatus());
        System.out.println("requestCert");
    }

    public static String createProfile(String storage, String password, String pathToStore) {
        password = password == null?"":password.trim();
        String profile = ProfileParams.getProfileName(storage.trim(), Boolean.valueOf(false));
        Number hProv = TumarCspFunctions.cpAcquireContext("", -268435456, LibraryWrapper.PV_TABLE);
        String result = TumarCspFunctions.cpCreateProfile(profile, "file", profile, password, pathToStore, "bin", 'ꁅ', '\uaa3a', hProv);
        TumarCspFunctions.cpReleaseContext(hProv, 0);
        return result;
    }

}
