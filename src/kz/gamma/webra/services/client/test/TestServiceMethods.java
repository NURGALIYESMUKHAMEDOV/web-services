package kz.gamma.webra.services.client.test;

import kz.gamma.webra.services.common.entities.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.math.BigInteger;

/**
 * Created by i_nikulin
 * 24.04.2009 11:45:46
 */

/**
 * Класс содержит тесты методов сервиса, но перед запуском используемые данные должны корретироваться
 */
public class TestServiceMethods {

    @Test
    public void loadPerson() throws Exception {
        DocLoadPersonIn docLoadPersonIn = (DocLoadPersonIn) TestUtils.loadObject("test/loadPerson/001_in.xml");
        DocLoadPersonOut docLoadPersonOut = (DocLoadPersonOut) TestUtils.callServer("loadPerson", docLoadPersonIn);
        DocLoadPersonOut docLoadPersonOutSample = (DocLoadPersonOut) TestUtils.loadObject("test/loadPerson/001_out.xml");
        TestUtils.compare(docLoadPersonOut, docLoadPersonOutSample);
    }

    @Test
    public void testLoadPerson() throws Exception
    {
        DocLoadPersonOut loadPersonOut = loadPersonById(BigInteger.valueOf(26));
        Assert.assertNotNull("loadPersonOut.getPerson == null", loadPersonOut.getPerson());
    }

    public DocLoadPersonOut loadPersonById(BigInteger id) throws Exception
    {
        DocLoadPersonIn loadPersonIn = new DocLoadPersonIn();
        loadPersonIn.setPersonId(id);
        DocLoadPersonOut loadPersonOut = (DocLoadPersonOut) TestUtils.callServer("loadPerson", loadPersonIn);
        return loadPersonOut;
    }

    @Test
    public void findPerson() throws Exception {
        DocFindPersonIn docFindPersonIn = new DocFindPersonIn();
        docFindPersonIn.setDN("ST=Алматинская область, L=Алматы, O=GAMMA, CN=testMName1 Yermek3301 testMName1, 2.5.4.5=0000000000, E=ermmak@gmail.com,C=KZ");
        DocFindPersonOut docFindPersonOut = (DocFindPersonOut) TestUtils.callServer("findPerson", docFindPersonIn);
        System.out.println(docFindPersonOut.getPersonId());
    }



    @Test
    public void getCertList() throws Exception {
        DocGetCertListIn docGetCertListIn = (DocGetCertListIn) TestUtils.loadObject("test/getCertList/001_in.xml");
        DocGetCertListOut docGetCertListOut = (DocGetCertListOut) TestUtils.callServer("getCertList", docGetCertListIn);
        List<X509> certList = docGetCertListOut.getCertList();
        System.out.println("There are " + certList.size() + " certificates.");
        for(int i = 0; i < certList.size(); i++){
            System.out.println((i + 1) + " certId: " + new String(certList.get(i).getSerialNumber()) + "\ncertBody: \n" + new String(certList.get(i).getBody()));
            System.out.println();
        }
    }

    @Test
    public void revokeCert() throws Exception{//todo внутренний тест на отзыв

    }

    @Test
    public void getBankList() throws Exception{
        DocBankListIn docBankListIn = new DocBankListIn();
        docBankListIn.setBik("%34%");
        DocBankListOut docBankListOut = (DocBankListOut) TestUtils.callServer("getBankList", docBankListIn);
        System.out.println(docBankListOut.getBankList().size());
    }

    @Test
    public void getTariffList() throws Exception{
        DocTariffListIn docTariffListIn = new DocTariffListIn();
        DocTariffListOut docTariffListOut = (DocTariffListOut) TestUtils.callServer("getTariffList", docTariffListIn);
        System.out.println(docTariffListOut.getTariffList().size());
    }

    @Test
    public void getOrderList() throws Exception{
        DocOrderListIn docOrderListIn = new DocOrderListIn();
        docOrderListIn.setPersonId(BigInteger.valueOf(112));
        DocOrderListOut docOrderListOut = (DocOrderListOut) TestUtils.callServer("getOrderList", docOrderListIn);
        System.out.println(docOrderListOut.getOrderList().size());
    }

    @Test
    public void createOrder() throws Exception{
        DocCreateOrderIn docCreateOrderIn = new DocCreateOrderIn();

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setClaimCount(BigInteger.valueOf(3));
        orderDetail.setTariffId(BigInteger.valueOf(2));
        docCreateOrderIn.setClientId(BigInteger.valueOf(112));
        docCreateOrderIn.getFxOrderDetails().add(orderDetail);

        DocCreateOrderOut docCreateOrderOut = (DocCreateOrderOut) TestUtils.callServer("createOrder", docCreateOrderIn);
        System.out.println(docCreateOrderOut.getOrder());
    }

    @Test
    public void generateSnList() throws Exception{
        DocGenerateSANListIn docGenerateSANListIn=new DocGenerateSANListIn();
        docGenerateSANListIn.setPersonId(BigInteger.valueOf(112));
        DocGenerateSANListOut docGenerateSANListOut=(DocGenerateSANListOut)TestUtils.callServer("generateSnList", docGenerateSANListIn);
        System.out.println(docGenerateSANListOut.getSnList().size());
    }


}
