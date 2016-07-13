package kz.gamma.webra.services.client;

import kz.gamma.webra.services.client.test.TestUtils;
import kz.gamma.webra.services.common.entities.RequestPkiService;

import java.math.BigInteger;
import java.util.Date;

/**
 * Created by i_nikulin
 * 13.04.2009 12:41:25
 */

/**
 * класс-фабрика для создания сообщений-запросов
 */
public class RequestFactory {

    public static RequestPkiService createPkiRequest(String type, String encoding) throws Exception {
        RequestPkiService pkiRequest = new RequestPkiService();
        pkiRequest.setRequestDate(TestUtils.dateTimeToGregorianCalendar(new Date()).normalize());
        pkiRequest.setSeance(BigInteger.valueOf(new Date().getTime()));
        pkiRequest.setSystem("WebraWS test case");
        pkiRequest.setService(type);
        pkiRequest.setEncoding(encoding);

        return pkiRequest;
    }

}