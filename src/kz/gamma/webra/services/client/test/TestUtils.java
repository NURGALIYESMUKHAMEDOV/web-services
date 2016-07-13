package kz.gamma.webra.services.client.test;

import kz.gamma.webra.services.client.ClientKeyStoreProvider;
import kz.gamma.webra.services.client.RequestFactory;
import kz.gamma.webra.services.client.WSClient;
import kz.gamma.webra.services.client.jaxws.WebraWS;
import kz.gamma.webra.services.common.entities.*;
import kz.gamma.webra.services.common.msgProcess.MessageHelper;
import kz.gamma.webra.services.common.msgProcess.XmlProcessor;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.unitils.reflectionassert.ReflectionAssert;
import org.unitils.reflectionassert.ReflectionComparatorMode;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.Diff;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.DatatypeConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by i_nikulin
 * 23.04.2009 15:34:18
 */
public class TestUtils {

    public static DateFormat standartDateFormat = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss.SSS");

    /**
     * тестовый метод для вызова метода сервера
     * все данные входящие и выходящие данные пишет в папку out
     *
     * @param service           имя вызываемого сервиса
     * @param pkiDocumentInTest входной документ
     * @return выходной документ
     * @throws Exception e
     */
    public static PkiDocument callServer(String service, PkiDocument pkiDocumentInTest) throws Exception {
        return callServer(service, pkiDocumentInTest, "UTF-8");
    }

    /**
     * тестовый метод для вызова метода сервера
     * все данные входящие и выходящие данные пишет в папку out
     *
     * @param service           имя вызываемого сервиса
     * @param pkiDocumentInTest входной документ
     * @param encoding кодировка
     * @return выходной документ
     * @throws Exception e
     */
    public static PkiDocument callServer(String service, PkiDocument pkiDocumentInTest, String encoding) throws Exception {

        saveObject(pkiDocumentInTest);
        //формируем документ-запроса
        RequestPkiService pkiRequest = RequestFactory.createPkiRequest(service, encoding);
        pkiRequest.setPkcs7(MessageHelper.objectToPkcs7(pkiDocumentInTest, true, ClientKeyStoreProvider.getInstance(), encoding));
        saveObject(pkiRequest);

        //готовим клиента web-сервиса
        WebraWS webraWS = WSClient.getInstance().getWebraWS();
        //вызываем метод web-сервиса
        ResponsePkiService response = webraWS.pkiService(pkiRequest);

        saveObject(response);

        //проверяем атрибуты ответа
        assertEquals("response.getService", response.getService(), pkiRequest.getService());
        assertEquals("response.getSeance", response.getSeance(), pkiRequest.getSeance());

        String message = response.getError() != null ? response.getError().getMessage() + "\n" + response.getError().getDetails() : "";
        assertTrue("Сервер вернул ошибку: " + message, response.getStatus() == StatusTYPE.OK);
        assertTrue("Пустой ответ pkcs7", response.getPkcs7() != null);

        PkiDocument pkiDocument = (PkiDocument) MessageHelper.pkcs7ToObject(response.getPkcs7(), false, encoding);

        saveObject(pkiDocument);

        return pkiDocument;

    }

    /**
     * загрузка объекта из xml
     *
     * @param fileIn путь к файлу xml
     * @return документ
     * @throws Exception e
     */
    public static PkiDocument loadObject(String fileIn) throws Exception {
        //загружаем тестовые данные
        return (PkiDocument) XmlProcessor.getInstance().unmarshal(new FileInputStream(fileIn), true);
    }

    /**
     * сохраняет объект в xml в папку out
     *
     * @param o объект (должен иметь представление в xsd)
     * @throws Exception e
     */
    public static void saveObject(Object o) throws Exception {
        //загружаем тестовые данные

        String fileOut = "out/" + standartDateFormat.format(new Date()) + "_" + o.getClass().getSimpleName() + ".xml";
        XmlProcessor.getInstance().marshal(o, false, new File(fileOut));
    }

    /**
     * сравнивает два документа, игнорирует метку времени
     *
     * @param d1 Документ 1
     * @param d2 Документ 2
     * @throws Exception e
     */
    public static void compare(PkiDocument d1, PkiDocument d2) throws Exception {
        d1.setDateTime(d2.getDateTime());
        ReflectionAssert.assertRefEquals(d1, d2, ReflectionComparatorMode.LENIENT_ORDER);
    }

    /**
     * сравнивает два объекта
     *
     * @param o1 объект 1
     * @param o2 объект 2
     * @throws Exception e
     */
    public static void compare(Object o1, Object o2) throws Exception {
        ReflectionAssert.assertRefEquals(o1, o2, ReflectionComparatorMode.LENIENT_ORDER);
    }

    /**
     * сравнивает два ответа, игнорирует метки времени и текст ошибки
     *
     * @param r1 ответ 1
     * @param r2 ответ 2
     * @throws Exception e
     */
    public static void compare(PkiMessageResponse r1, PkiMessageResponse r2) throws Exception {
        r1.setSeance(r2.getSeance());
        r1.setResponseDate(r2.getResponseDate());
        r1.getError().setDetails(r2.getError().getDetails());
        ReflectionAssert.assertRefEquals(r1, r2, ReflectionComparatorMode.LENIENT_ORDER);
    }

    /**
     * метод посылает методу args[0] сервера документ из xml из файла по пути args[1]
     *
     * @param args операция + путь к файлу xml, где хранится xml
     * @throws Exception e
     */
    public static void main(String[] args) throws Exception {
        PkiDocument pkiDocument = loadObject(args[1]);
        PkiDocument pkiDocumentOut = callServer(args[0], pkiDocument);
        saveObject(pkiDocumentOut);
    }


    /**
     * Проверка на сравнение XML
     * @param message сообщение об ошибки
     * @param input xml
     * @param output xml
     * @throws Exception e
     */
    public static void assertXmlEquals(String message, String input, String output) throws Exception {
        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setIgnoreComments(true);
        XMLUnit.setIgnoreAttributeOrder(true);
        Diff myDiff = new Diff(input, output);
        if (!myDiff.similar())
            throw new Error(message + "\n" + myDiff.toString());

    }

    /**
     * 
     * @param date
     * @return
     */
    public static XMLGregorianCalendar dateTimeToGregorianCalendar(Date date)
    {
        if(date == null)
            return null;

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return null;
        }
    }


}
