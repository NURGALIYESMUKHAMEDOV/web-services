package kz.gamma.webra.services.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by i_nikulin
 * 10.04.2009 12:20:34
 */

/**
 * конфигурируемые параметры web-сервиса
 */
public class WebClientProps {

    public static final String WSDL_LOCATION = "WSDL.location";

    public static final String TRUST_STORE_PASSWORD = "trustStorePassword";
    public static final String TRUST_STORE_FILE = "trustStoreFile";

    public static final String PKCS12_PASSWORD = "pkcs12Password";
    public static final String PKCS12_FILE = "pkcs12File";


    public static final String MONITOR_INDIR = "monitor.inDir";
    public static final String MONITOR_OUTDIR = "monitor.outDir";
    public static final String MONITOR_ERRORDIR = "monitor.errorDir";

    public static final String MONITOR_DELAY = "monitor.delay";
    public static final String TARIFF_ID = "tariffId";
    public static final String BANK_ID = "bankId";
    public static final String PAYER_ADDRESS = "payerAddress";
    public static final String PAYER_NAME = "payerName";
    public static final String PAYER_RNN = "payerRNN";
    public static final String PAYER_ACCOUNT = "payerAccount";

    public static final String IS_PROXY = "isProxy";
    public static final String PROXY_HOST = "proxyHost";
    public static final String PROXY_PORT = "proxyPort";

    public static final String ENCODING = "encoding";

    private static WebClientProps ourInstance;

    private Map<String, String> props = new HashMap<String, String>();

    public static WebClientProps getInstance() {
        if(ourInstance == null)
            ourInstance = new WebClientProps();

        return ourInstance;
    }

    private WebClientProps() {

        try {
            Properties instalProperties = new Properties();
            instalProperties.load(new FileInputStream("service.properties"));
            putAll(instalProperties);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки свойств клиента (service.properties)", e);
        }

    }

    public Map<String, String> getProps() {
        return props;
    }

    public void put(String key, String value) {
        props.put(key, value);
    }

    public void putAll(Map map) {
        props.putAll(map);
    }

    public void copy(String from, String to) {
        props.put(to, props.get(from));
    }

    public String get(String key) {
        return props.get(key);
    }

}