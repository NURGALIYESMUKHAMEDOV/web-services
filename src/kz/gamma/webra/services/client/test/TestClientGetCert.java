package kz.gamma.webra.services.client.test;

import kz.gamma.jce.PKCS10CertificationRequest;
import kz.gamma.webra.services.client.ClientKeyStoreProvider;
import kz.gamma.webra.services.common.entities.*;
import kz.gamma.webra.services.common.msgProcess.CryptoProcessor;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;

/**
 * Created by y_makulbek
 * Date: 15.03.2010 13:49:20
 */
public class TestClientGetCert {

    @Test
    public void testRequestCert() throws Exception {

        //getOrderList
        DocOrderListIn docOrderListIn = new DocOrderListIn();
        docOrderListIn.setPersonId(BigInteger.valueOf(4));
        DocOrderListOut docOrderListOut = (DocOrderListOut) TestUtils.callServer("getOrderList", docOrderListIn);

        //getTariffList
        DocTariffListIn docTariffListIn = new DocTariffListIn();
        DocTariffListOut docTariffListOut = (DocTariffListOut) TestUtils.callServer("getTariffList", docTariffListIn);
        Assert.assertTrue("В системе нет тарифов", docTariffListOut.getTariffList().size() > 0);
        System.out.println("getTariffList");

        DocRequestCertIn docRequestCertIn = new DocRequestCertIn();
        Request request = new Request();
        request.setOrderDetailId(docOrderListOut.getOrderList().get(0).getFxOrderDetails().get(0).getId());
        request.setTariffId(BigInteger.valueOf(2));
        request.setDN("C=KZ, O=kisc, OU=organization, CN=Makulbek Yerema Zharkenuly, SN=RNN600520250552, E=y_makulbek@gamma.kz");
        docRequestCertIn.setRequest(request);


        PKCS10CertificationRequest pkcs10;
        byte[] pkcs7;
        RequestDetail requestDetail;

        for (TariffDetail detail : docTariffListOut.getTariffList().get(0).getFxTariffDetails()) {
            if (detail.getAlgType() == AlgTYPE.RSA)
                pkcs10 = CryptoProcessor.getPkcs10RequestRSA("C=KZ, O=kisc, OU=organization, CN=Makulbek Yerema Zharkenuly, SN=RNN600520250552, E=y_makulbek@gamma.kz", detail.getCertificate());

            else
                pkcs10 = CryptoProcessor.getPkcs10RequestGOST("C=KZ, O=kisc, OU=organization, CN=Makulbek Yerema Zharkenuly, SN=RNN600520250552, E=y_makulbek@gamma.kz", detail.getCertificate());

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

}
