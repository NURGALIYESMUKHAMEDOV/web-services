package kz.gamma.webra.services.common.msgProcess;

import kz.gamma.jce.provider.GammaTechProvider;

import java.io.FileInputStream;
import java.io.File;
import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.security.Security;

/**
 * Created by i_nikulin
 * 14.04.2009 16:30:55
 */
public abstract class KeyStoreProvider {

    private KeyStore store;
    private String password;

    public KeyStoreProvider(String fileName, String password) throws Exception {

        //Для корректной работы JCE необходимо выполнить установку Java провайдера.
        Security.addProvider(new GammaTechProvider());

        this.password = password;
        store = KeyStore.getInstance("PKCS12", "GAMMA");
        char[] passwd = password.toCharArray();
        store.load(new ByteArrayInputStream(new File(fileName).getAbsolutePath().getBytes()), passwd);
    }

    public KeyStore getStore() {
        return store;
    }

    public void setStore(KeyStore store) {
        this.store = store;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
