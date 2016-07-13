package kz.gamma.webra.services.common.msgProcess;

import kz.gamma.asn1.*;
import kz.gamma.asn1.cms.MSTemplate;
import kz.gamma.asn1.pkcs.CertificationRequestInfo;
import kz.gamma.asn1.x509.X509Name;
import kz.gamma.cms.*;
import kz.gamma.core.UtilCM;
import kz.gamma.crypto.RuntimeCryptoException;
import kz.gamma.jce.PKCS10CertificationRequest;
import kz.gamma.jce.provider.GammaTechProvider;
import kz.gamma.jce.provider.JDKKeyPairGenerator;
import kz.gamma.tumarcsp.params.StoreObjectParam;
import kz.gamma.util.encoders.Base64;

import java.io.IOException;
import java.security.*;
import java.security.cert.CertStore;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by i_nikulin
 * 30.03.2009 16:44:56
 */

/**
 * Класс для подписывания xml сообщений и проверки подлинности
 */
public class CryptoProcessor {

    static {
        // Инициализация криптопровайдера
        Security.addProvider(new GammaTechProvider());
    }

    public static byte[] sign(byte[] data, KeyStore store, String password) throws Exception {

        Enumeration en = store.aliases();
        String alias = null;
        while (en.hasMoreElements()) {
            alias = ((StoreObjectParam) en.nextElement()).sn;
        }
        PrivateKey privKey = (PrivateKey) store.getKey(alias, password.toCharArray());
        X509Certificate cert = (X509Certificate) store.getCertificate(alias);

        // Формирование подписанного сообщения в формате PKCS#7
        CMSSignedDataGenerator gen = new CMSSignedDataGenerator();

        // Следует отметить что запросы могут подписывать только Центры Ресгистрации,
        // а также пользователи, если формируют сертификат только для себя!!!
        // Это означает DN имя в запросе, совпадает с DN именем в сертификате подписавшего запрос,
        // и этот пользователь присутствует в системе.
        gen.addSigner(privKey, cert, CMSSignedGenerator.DIGEST_GOST3411G);

        // Добавление сертификата подписавшего запрос в PKCS#7
        List<X509Certificate> certList = new ArrayList<X509Certificate>();
        certList.add(cert);
        CertStore certs = CertStore.getInstance("Collection", new CollectionCertStoreParameters(certList), "GAMMA");
        gen.addCertificatesAndCRLs(certs);

        // Упаковываем запрос в PKCS#7
        CMSSignedData signedData = gen.generate(new CMSProcessableByteArray(data), true, "GAMMA");

        return signedData.getEncoded();
    }

    /**
     * Формирование объекта PKCS#7
     *
     * @param pkcs7 PKCS#7 в DER кодировке
     * @return Объект PKCS#7
     * @throws Exception
     */
    public static Pkcs7Data getPkcs7Object(byte[] pkcs7) throws Exception {
        Pkcs7Data pkcs7Data = new Pkcs7Data(pkcs7, "");
        try {
            pkcs7Data.retrieveData();
        } catch (Exception e) {
            throw new Exception("Неправильный формат pkcs7", e);
        }
        return pkcs7Data;
    }

    /**
     * Генерирование PKCS#10 на алгоритме RSA. Генерируется запрос для сертификата на подпись
     *
     * @param userDName DN пользователя
     * @param template  Шаблон
     * @return PKCS#10 на алгоритме RSA
     * @throws Exception
     */
    public static PKCS10CertificationRequest getPkcs10RequestRSA(String userDName, String template) throws Exception {
        return getPkcs10Request(userDName, template, "1.3.6.1.4.1.6801.1.5.21");
    }

    /**
     * Генерирование PKCS#10 на алгоритме ГОСТ. Генерируется запрос для сертификата на подпись
     *
     * @param userDName DN пользователя
     * @param template  Шаблон
     * @return PKCS#10 на алгоритме ГОСТ
     * @throws Exception
     */
    public static PKCS10CertificationRequest getPkcs10RequestGOST(String userDName, String template) throws Exception {
        return getPkcs10Request(userDName, template, "1.3.6.1.4.1.6801.1.5.8");
    }

    /**
     * Генерирование PKCS#10 на алгоритме ГОСТ по OID-у
     *
     * @param userDName DN пользователя
     * @param template  Шаблон
     * @param oid       OID ключа. Возможные варианты:
     *                  <ul>
     *                  <li>1.3.6.1.4.1.6801.1.5.8 - ГОСТ на подпись;
     *                  <li>1.3.6.1.4.1.6801.1.8.8 - ГОСТ на шифрование;
     *                  <li>1.3.6.1.4.1.6801.1.5.21 - RSA на подпись;
     *                  <li>1.3.6.1.4.1.6801.1.8.21 - RSA на шифрование.
     *                  <ul/>
     * @return PKCS#10 на алгоритме ГОСТ
     * @throws Exception
     */
    public static PKCS10CertificationRequest getPkcs10Request(String userDName, String template, String oid) throws Exception {
        String algorithm = null;
        KeyPairGenerator kpg = null;
        if (oid.equals("1.3.6.1.4.1.6801.1.5.8") || oid.equals("1.3.6.1.4.1.6801.1.8.8")) {
            algorithm = "ECGOST34310";
            kpg = KeyPairGenerator.getInstance(algorithm, "GAMMA");
        } else if (oid.equals("1.3.6.1.4.1.6801.1.5.22") || oid.equals("1.3.6.1.4.1.6801.1.8.22")) {
            algorithm = "SHA1WITHRSA";
            kpg = KeyPairGenerator.getInstance("RSA", "GAMMA");
        } else {
            throw new RuntimeCryptoException("You can set only 4 oids: 1.3.6.1.4.1.6801.1.5.8, 1.3.6.1.4.1.6801.1.8.8, " +
                    "1.3.6.1.4.1.6801.1.5.22, 1.3.6.1.4.1.6801.1.8.22. Your oid: " + oid);
        }
        if (GammaTechProvider.algUtil.algKey.containsKey(oid)) {
            kpg.initialize(GammaTechProvider.algUtil.algKey.get(oid));
        }
        if (oid.equals("1.3.6.1.4.1.6801.1.5.8") || oid.equals("1.3.6.1.4.1.6801.1.8.8")) {
            ((JDKKeyPairGenerator.ECGOST34310) kpg).setProfName("profile://FSystem", "");
        } else if (oid.equals("1.3.6.1.4.1.6801.1.5.22") || oid.equals("1.3.6.1.4.1.6801.1.8.22")) {
            ((JDKKeyPairGenerator.RSA) kpg).setProfName("profile://FSystem", "");
        }
        KeyPair pair = kpg.generateKeyPair();
        PrivateKey privateKey = pair.getPrivate();
        PublicKey publicKey = pair.getPublic();
        return new PKCS10CertificationRequest(algorithm,
                new X509Name(userDName), publicKey, MSTemplate.getASN1Template(template), privateKey);
    }

    public static PKCS10CertificationRequest getPKSC10GOSTRequest(String personDN, String storage, String password, String template) throws NoSuchProviderException, NoSuchAlgorithmException, IOException, SignatureException, InvalidKeyException {
        String signAlgorithm = "ECGOST34310";
        KeyPairGenerator ecGostKpg = KeyPairGenerator.getInstance(signAlgorithm, "GAMMA");
        String key = "1.3.6.1.4.1.6801.1.5.8";
        if(GammaTechProvider.algUtil.algKey.containsKey(key)) {
            ecGostKpg.initialize(((Integer)GammaTechProvider.algUtil.algKey.get(key)).intValue());
        } else {
            ecGostKpg.initialize(2);
        }

        ((JDKKeyPairGenerator.ECGOST34310)ecGostKpg).setProfName("profile://" + storage, password);
        KeyPair pair = ecGostKpg.generateKeyPair();
        PrivateKey privateKey = pair.getPrivate();
        PublicKey publicKey = pair.getPublic();
        return new PKCS10CertificationRequest(signAlgorithm, new X509Name(personDN), publicKey, MSTemplate.getASN1Template(template), privateKey);
    }

    public static PKCS10CertificationRequest getPkcs10RequestRSA(String DN, String store_path, String password, String template)
            throws NoSuchProviderException, NoSuchAlgorithmException, IOException, SignatureException, InvalidKeyException {

        String algorithm = "SHA1WITHRSA";
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", "GAMMA");

        if (GammaTechProvider.algUtil.algKey.containsKey("1.3.6.1.4.1.6801.1.5.21")) {
            kpg.initialize(GammaTechProvider.algUtil.algKey.get("1.3.6.1.4.1.6801.1.5.21"));
        }

        ((JDKKeyPairGenerator.RSA) kpg).setProfName("profile://" + store_path, "");

        KeyPair pair = kpg.generateKeyPair();
        System.out.println("Gotta KeyPair" + pair.toString());
        PrivateKey privateKey = pair.getPrivate();
        System.out.println("Gotta Private:" + privateKey.toString());
        PublicKey publicKey = pair.getPublic();
        System.out.println("Gotta Public:" + privateKey.toString());
        return new PKCS10CertificationRequest(algorithm, new X509Name(DN), publicKey, MSTemplate.getASN1Template(template), privateKey);
    }

    /**
     * Проверка на то, является ли запрос отложенным
     *
     * @param pkcs7 PKCS#7 в DER кодировке
     * @return true, если запрос отложенный, иначе false
     * @throws Exception
     */
    public static boolean isDelayedRequest(byte[] pkcs7) throws Exception {
        // Получение структуры крипто данных
        Pkcs7Data pkcs7Data = CryptoProcessor.getPkcs7Object(pkcs7);
        if (!pkcs7Data.verify()) {
            throw new RuntimeException("Подпись контейнера не прошла проверку");
        }
        byte[] signedContent = pkcs7Data.getData();
        // Пример подписанного контента: MCwKAQEwJwIBAwwTQz1LQztPPU5BTE9HO0NOPTEyMwwNdHJhbnNhY3Rpb25JZA==
        byte[] asn = Base64.decode(signedContent);
        ASN1InputStream in = new ASN1InputStream(asn);
        DERSequence derSequence = (DERSequence) in.readObject();
        DERUTF8String derTransactionId = null;
        try {
            DERSequence secondSequence = (DERSequence) derSequence.getObjectAt(1);
            derTransactionId = (DERUTF8String) secondSequence.getObjectAt(2);
        } catch (Exception e) {
            return false;
        }
        String transactionId = derTransactionId.getString();
        if (transactionId.contains("waiting.BIN")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Извлечение имени шаблона из запроса
     *
     * @param pkcs7 PKCS#7 в DER кодировке
     * @return Имя шаблона
     * @throws Exception
     */
    public static String getTemplateName(byte[] pkcs7) throws Exception {
        // Получение структуры крипто данных
        Pkcs7Data pkcs7Data = CryptoProcessor.getPkcs7Object(pkcs7);
        if (!pkcs7Data.verify()) {
            throw new RuntimeException("Подпись контейнера не прошла проверку");
        }
        byte[] signedContent = pkcs7Data.getData();
        PKCS10CertificationRequest pkcs10CertificationRequest = new PKCS10CertificationRequest(signedContent);
        CertificationRequestInfo cri = pkcs10CertificationRequest.getCertificationRequestInfo();
        ASN1Set set = cri.getAttributes();
        return MSTemplate.getTemplateName(set);
    }

    /**
     * Получение OID-ов из политик сертификата
     *
     * @param x509Certificate Сертификат
     * @return Список OID-ов из политик сертификата
     * @throws IOException
     */
    public static List<String> getOidsFromCertificatePolicies(X509Certificate x509Certificate) throws IOException {
        byte[] content = x509Certificate.getExtensionValue("2.5.29.32");
        if (content == null) {
            return null;
        }
        ASN1InputStream in = new ASN1InputStream(content);
        DEROctetString derOctetString = (DEROctetString) in.readObject();
        ASN1InputStream ais = new ASN1InputStream(derOctetString.getOctetStream());
        DERSequence seq1 = (DERSequence) ais.readObject();
        DERSequence seq2 = (DERSequence) seq1.getObjectAt(0);
        List<String> certificatePolicies = new ArrayList<String>();
        for (int i = 0; i < seq2.size(); i++) {
            DEREncodable enc = seq2.getObjectAt(i);
            if (enc instanceof DERObjectIdentifier) {
                certificatePolicies.add(((DERObjectIdentifier) enc).getId());
            }
        }
        if (certificatePolicies.size() > 0) {
            return certificatePolicies;
        } else {
            return null;
        }
    }

    /**
     * Извлечение DN из PKCS#10, находящегося в PKCS#7
     *
     * @param pkcs7 PKCS#7 в DER кодировке
     * @return DN
     * @throws Exception
     */
    public static String getPkcs10Name(byte[] pkcs7) throws Exception {
        // Получение структуры крипто данных
        Pkcs7Data pkcs7Data = CryptoProcessor.getPkcs7Object(pkcs7);
        if (!pkcs7Data.verify()) {
            throw new RuntimeException("Подпись контейнера не прошла проверку");
        }
        byte[] signedContent = pkcs7Data.getData();
        PKCS10CertificationRequest pkcs10CertificationRequest = new PKCS10CertificationRequest(signedContent);
        return pkcs10CertificationRequest.getCertificationRequestInfo().getSubject().toString();
    }

    public static void main(String[] args) throws Exception {
        String mess = "MIIFuwYJKoZIhvcNAQcCoIIFrDCCBagCAQExEDAOBgorBgEEAbURAQIBBQAwggFuBgkqhkiG9w0BBwGgggFfBIIBWzCCAVcwggEAAgEAME0xCzAJBgNVBAYTAktaMQ4wDAYDVQQKEwVnYW1tYTEXMBUGA1UEAxMORXJtb2xhZXYgRGVuaXMxFTATBgNVBAUTDDM0MzQzNDM0MzQzNDBjMA4GCisGAQQBtREBBQgFAANRAAYCAAA6qgAAAEVDMQACAADt6SxkonnK96H2oahHbjy2XIcncD8OKHFf9g1gFZZqx+CbHYelWjbwl5ChYc1GlGqhmDaupqsFvZse7OUWN5pwoEcwRQYKKwYBBAGCNwIBDjE3MDUwMwYJKwYBBAGCNxQCBCZDPUtaLCBPPVRlbXBsYXRlLCBDTj1HT1NUX01BTl9TSUdOXzE0RDAOBgorBgEEAbURAQICBQADQQBgpUFKpEtWzuk/wcWOEcXJZqjsChYoFXuJOSabDHTzzmTZMxZIZjBOnu4/+LZ9M/THxDm3aX78U0mC+wmUdW/GoIICkDCCAowwggI1oAMCAQICIIIk5gIs2ZFao2nRt/rPFIhH7mtrmxqrJuCWtH7NL/R9MA4GCisGAQQBtREBAgIFADApMQswCQYDVQQDEwJDQTENMAsGA1UEChMES0lTQzELMAkGA1UEBhMCS1owIhgPMjAwOTExMDMwOTUyMjNaGA8yMDEwMTAyNTE1NTIyM1owVDEVMBMGA1UEBRMMMTIzNDY3ODkwMTIwMR4wHAYDVQQDDBXQldGE0LjQvNC+0LIg0KHRgtCw0YExDjAMBgNVBAoTBWdhbW1hMQswCQYDVQQGEwJLWjBjMA4GCisGAQQBtREBBQgFAANRAAYCAAA6qgAAAEVDMQACAABu16CoMosleWFJzRyQTMEa5L3EZLWiGXQH4ZtmnbM7W6OfnE8DiIH2bJwGGlIyOsQGQ/o4jn8EsA5I8wTrkT13o4HxMIHuMAwGA1UdEwQFMAMCAQAwCwYDVR0PBAQDAgHmMDoGA1UdHwQzMDEwL6AtoCuGKWh0dHA6Ly8xOTIuMTY4LjEyLjY6NjIyODAvY2dpL1Jldkxpc3QuY3JsMCkGA1UdDgQiBCCCJOYCLNmRWqNp0bf6zxSIR+5ra5saqybglrR+zS/0fTBqBgNVHSMEYzBhgCC6kWAc5T5dhKaBzWitFZwiu4wizW/x1dexo5ngM5olJaEbhhlodHRwOi8vMTkyLjE2OC4xMi42OjYyMjgwgiC6kWAc5T5dhKaBzWitFZwiu4wizW/x1dexo5ngM5olJTAOBgorBgEEAbURAQICBQADQQD323qxrB9wCTSe7CTzSF36vvh4eYw/mfH8LTeep0rgmBehkT/MIE+8JZjVsaLgL+wXPjdu4KkXOAi7/0IgmgyZMYIBiTCCAYUCAQEwTTApMQswCQYDVQQDEwJDQTENMAsGA1UEChMES0lTQzELMAkGA1UEBhMCS1oCIIIk5gIs2ZFao2nRt/rPFIhH7mtrmxqrJuCWtH7NL/R9MA4GCisGAQQBtREBAgEFAKCBzjAYBgkqhkiG9w0BCQMxCwYJKoZIhvcNAQcBMBwGCSqGSIb3DQEJBTEPFw0wOTEyMjIxMTQwNTVaMC8GCSqGSIb3DQEJBDEiBCAb4IyjZpN+MeMi3SOKv1tvm2EMqrv1BYS6KfVOEWGEyTBjBgkrBgEEAbURAggxVjBUMRUwEwYDVQQFEwwxMjM0Njc4OTAxMjAxHjAcBgNVBAMMFdCV0YTQuNC80L7QsiDQodGC0LDRgTEOMAwGA1UEChMFZ2FtbWExCzAJBgNVBAYTAktaMA4GCisGAQQBtREBBQgFAARAJFp1JB7vFSxr17ojvvzua9KZOj29lMQcjMVaT09VBlizKjRBNSlfFGnYjkmpsdmxqL/ye9eOLc59mXF5TXffDg==";
        byte[] pkcs7 = Base64.decode(mess);
        Pkcs7Data data = getPkcs7Object(pkcs7);
        System.out.println(data);

        PKCS10CertificationRequest pkcs10 = new PKCS10CertificationRequest(data.getData());
        System.out.println(pkcs10);
    }

    public static String postReqToASN(int status, String dn, String tID) {
        String request = null;
        try {
            ASN1EncodableVector seq1 = new ASN1EncodableVector();
            ASN1EncodableVector seq2 = new ASN1EncodableVector();
            seq1.add(new DEREnumerated(1));
            seq2.add(new DERInteger(status));
            seq2.add(new DERUTF8String(dn));
            seq2.add(new DERUTF8String(tID));
            seq1.add(new DERSequence(seq2));
            byte[] ret = new DERSequence(seq1).getDEREncoded();
            request = new String(Base64.encode(ret));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return request;
    }

    public static String getSerialNumber(String pkcs7) throws Exception {
        Pkcs7Data pkcs7Data = getPkcs7Object(Base64.decode(pkcs7));
        String serialNumber = UtilCM.array2hex(pkcs7Data.getSignerInformation().getSID().getSerialNumber().toByteArray());
        return serialNumber;
    }

}
