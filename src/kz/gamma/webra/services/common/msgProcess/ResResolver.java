package kz.gamma.webra.services.common.msgProcess;

import com.sun.org.apache.xerces.internal.dom.DOMInputImpl;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ivan.Nikulin
 * Date: 03.10.2007
 * Time: 16:13:25
 */

/**
 * Загрузчик ресурсов
 */
public class ResResolver implements EntityResolver, LSResourceResolver {

    private static Map<String, String> comPaths = new HashMap<String, String>();
    private String path;

    static {
        comPaths.put("pki_type.xsd", "kz/gamma/webra/services/common/xsd/");
    }

    public ResResolver(String path) {
        this.path = path;
    }

    public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
        try {
            String resPath = comPaths.get(systemId);
            if (resPath == null)
                resPath = path;
            URL url = getResURL(resPath + systemId);
            if (url != null) {
                DOMInputImpl is = new DOMInputImpl();
                is.setSystemId(systemId);
                is.setByteStream(url.openStream());
                return is;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        try {
            String resPath = comPaths.get(systemId);
            if (resPath == null)
                resPath = path;
            URL url = getResURL(resPath + systemId);
            if (url != null) {
                InputSource inputSource = new InputSource(url.openStream());
                inputSource.setSystemId(systemId);
                return inputSource;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static URL getResURL(String fileName) {
        return ResResolver.class.getClassLoader().getResource(fileName);
    }

}

