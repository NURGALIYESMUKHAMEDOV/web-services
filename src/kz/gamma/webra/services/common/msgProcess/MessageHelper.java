package kz.gamma.webra.services.common.msgProcess;

import kz.gamma.cms.Pkcs7Data;

import javax.xml.bind.JAXBException;

/**
 * Created by i_nikulin
 * 13.04.2009 16:43:22
 */
public class MessageHelper {


    /**
     * Дружественный метод для преобразования объекта в контейнер pkcs7
     *
     * @param obj              объект
     * @param validate валидировать по xsd
     * @param keyStoreProvider провайдер сертификата
     * @param encoding кодировка
     * @return контейнер pkcs7
     * @throws Exception e
     */
    public static byte[] objectToPkcs7(Object obj, boolean validate, KeyStoreProvider keyStoreProvider, String encoding) throws Exception {

        String objStr;
        try {
            objStr = XmlProcessor.getInstance().marshal(obj, validate);
        } catch (JAXBException e) {
            e.printStackTrace();
            throw new Exception("Ошибка при маршилизации объекта " + obj, e);
        }

        byte[] pkcs7;
        try {
            pkcs7 = CryptoProcessor.sign(objStr.getBytes(encoding), keyStoreProvider.getStore(), keyStoreProvider.getPassword());
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Ошибка при подписывании объекта " + obj, e);
        }

        return pkcs7;
    }

    /**
     * Дружественный метод для преобразования контейнера pkcs7 в объект с проверкой подписи и валидацией
     *
     * @param pkcs7 контейнер
     * @param validate валидировать по xsd
     * @param encoding кодировка pkcs7
     * @return объект
     * @throws Exception ошибка подписи
     */
    public static Object pkcs7ToObject(byte[] pkcs7, boolean validate, String encoding) throws Exception {

        Pkcs7Data signedCapicomData = CryptoProcessor.getPkcs7Object(pkcs7);

        //проверка
        if (!signedCapicomData.verify())
            throw new Exception("Подпись контейнера не прошла проверку");

        try {
            return XmlProcessor.getInstance().unmarshal(signedCapicomData.getData(), validate, encoding);
        } catch (JAXBException e) {
            e.printStackTrace();
            throw new Exception("Ошибка при демаршилизации объекта " + pkcs7, e);
        }

    }

    /**
     * Дружественный метод для преобразования контейнера pkcs7 в  файл
     *
     * @param pkcs7 контейнер
     * @param output файл для вывода
     * @throws Exception ошибка подписи
     */
    public static void pkcs7ToFile(byte[] pkcs7, String output) throws Exception {

        Pkcs7Data signedCapicomData = CryptoProcessor.getPkcs7Object(pkcs7);

        //проверка
        if (!signedCapicomData.verify())
            throw new Exception("Подпись контейнера не прошла проверку");

        signedCapicomData.getData();

        
    }

}
