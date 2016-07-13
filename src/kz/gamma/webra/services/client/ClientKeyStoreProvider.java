package kz.gamma.webra.services.client;

import kz.gamma.webra.services.common.msgProcess.KeyStoreProvider;

/**
 * Created by i_nikulin
 * 14.04.2009 16:38:24
 */
public class ClientKeyStoreProvider extends KeyStoreProvider {

    private static ClientKeyStoreProvider instance;

    private ClientKeyStoreProvider() throws Exception {
        super(WebClientProps.getInstance().get(WebClientProps.PKCS12_FILE), WebClientProps.getInstance().get(WebClientProps.PKCS12_PASSWORD));
    }

    public static ClientKeyStoreProvider getInstance() throws Exception {
        if (instance == null)
            instance = new ClientKeyStoreProvider();

        return instance;
    }
}