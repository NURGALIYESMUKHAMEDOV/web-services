package kz.gamma.webra.services.client;

import kz.gamma.webra.services.client.jaxws.WebraWS;
import kz.gamma.webra.services.client.jaxws.WebraWS_Service;
import kz.gamma.webra.services.common.entities.PkiDocument;
import kz.gamma.webra.services.common.entities.RequestPkiService;
import kz.gamma.webra.services.common.entities.ResponsePkiService;
import kz.gamma.webra.services.common.entities.StatusTYPE;
import kz.gamma.webra.services.common.msgProcess.MessageHelper;

/**
 * Created by i_nikulin
 * 01.04.2009 12:59:27
 */


/**
 * Клиен web-сервиса
 * <p/>
 * Предоставляет доступ к web-сервису
 */
public class WSClient {

    private static WSClient instance;

    private WebraWS webraWS;

    private WSClient() throws Exception {

        //todo только для проверки
        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(new javax.net.ssl.HostnameVerifier() {
            public boolean verify(String hostname, javax.net.ssl.SSLSession sslSession) {
                return true;
            }
        });

        System.setProperty("javax.net.ssl.trustStore", WebClientProps.getInstance().get(WebClientProps.TRUST_STORE_FILE));
        System.setProperty("javax.net.ssl.trustStorePassword", WebClientProps.getInstance().get(WebClientProps.TRUST_STORE_PASSWORD));
        System.setProperty("javax.net.ssl.trustStoreType", "JKS");

        if(Boolean.parseBoolean(WebClientProps.getInstance().get(WebClientProps.IS_PROXY)))
        {
            System.setProperty("https.proxyHost", WebClientProps.getInstance().get(WebClientProps.PROXY_HOST));
            System.setProperty("https.proxyPort", WebClientProps.getInstance().get(WebClientProps.PROXY_PORT));
        }

        WebraWS_Service service = new WebraWS_Service();
        webraWS = service.getWebraWSPort();

    }

    public static WSClient getInstance() throws Exception {
        if (instance == null)
            instance = new WSClient();
        return instance;
    }

    /**
     * вызов сервиса
     *
     * @param service           имя вызываемого сервиса
     * @param pkiDocumentInTest входной документ
     * @param encoding
     * @return выходной документ
     * @throws Exception e
     */
    public PkiDocument callServer(String service, PkiDocument pkiDocumentInTest, String encoding) throws Exception {

        //формируем документ-запроса
        RequestPkiService pkiRequest = RequestFactory.createPkiRequest(service, encoding);
        pkiRequest.setPkcs7(MessageHelper.objectToPkcs7(pkiDocumentInTest, true, ClientKeyStoreProvider.getInstance(), encoding));

        //вызываем метод web-сервиса
        ResponsePkiService response = webraWS.pkiService(pkiRequest);

        if (response.getStatus() != StatusTYPE.OK) {
            String message = response.getError() != null ? response.getError().getMessage() + "\n" + response.getError().getDetails() : "";
            throw new Exception(message);
        }

        return (PkiDocument) MessageHelper.pkcs7ToObject(response.getPkcs7(), false, encoding);
    }


    public WebraWS getWebraWS() {
        return webraWS;
    }
}
