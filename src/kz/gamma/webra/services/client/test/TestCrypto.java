package kz.gamma.webra.services.client.test;

import kz.gamma.cms.Pkcs7Data;
import kz.gamma.webra.services.common.msgProcess.CryptoProcessor;
import kz.gamma.webra.services.client.ClientKeyStoreProvider;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Created by i_nikulin
 * 22.04.2009 13:28:34
 */
public class TestCrypto {

    @Test
    /**
     * проверка упаковки в pkcs7
     */
    public void testSign() throws Exception {
        String str1 = "0123456789 string for test";
        ClientKeyStoreProvider keyStoreProvider = ClientKeyStoreProvider.getInstance();
        byte[] pkcs7 = CryptoProcessor.sign(str1.getBytes("UTF-8"), keyStoreProvider.getStore(), keyStoreProvider.getPassword());
        Pkcs7Data data = CryptoProcessor.getPkcs7Object(pkcs7);

        assertTrue("Проверка сертификата", data.verify());
        String str2 = new String(data.getData());

        assertEquals("Проверка oбъекта", str1, str2);
    }

    @Test
    /**
     * проверка упаковки в pkcs7
     */
    public void testStr8() throws Exception {
        String str1 = "0123456789 string for test ывапрцукен";
        ClientKeyStoreProvider keyStoreProvider = ClientKeyStoreProvider.getInstance();
        byte[] pkcs7 = CryptoProcessor.sign(str1.getBytes("UTF-8"), keyStoreProvider.getStore(), keyStoreProvider.getPassword());
        Pkcs7Data data = CryptoProcessor.getPkcs7Object(pkcs7);

        assertTrue("Проверка сертификата", data.verify());
        String str2 = new String(data.getData(), "UTF-8");

        assertEquals("Проверка oбъекта", str1, str2);
    }

}
