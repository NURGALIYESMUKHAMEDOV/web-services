package kz.gamma.webra.services.common.msgProcess;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.*;

/**
 * Created by i_nikulin
 * 30.03.2009 16:10:16
 */

/**
 * Класс для обработки xml сообщений
 * маршилизация и демаршализация
 */
public class XmlProcessor {

    private static XmlProcessor instance;

    private JAXBContext jaxbContext;
    private Schema schema;
    private ResResolver resolver;

    public static XmlProcessor getInstance() {

        if (instance == null) {

            instance = new XmlProcessor();
        }
        return instance;
    }

    private XmlProcessor() {

        try {

            SchemaFactory sf = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
            resolver = new ResResolver("kz/gamma/webra/services/common/xsd/");
            sf.setResourceResolver(resolver);
            Source schemaFile = new SAXSource(resolver.resolveEntity(null, "pki_document.xsd"));
            schema = sf.newSchema(schemaFile);

            jaxbContext = JAXBContext.newInstance("kz.gamma.webra.services.common.entities");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    private Unmarshaller getUnMarshal(boolean validate) throws JAXBException {

        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        if(validate)
            unmarshaller.setSchema(schema);
        return unmarshaller;
    }

    private Marshaller getMarshal(boolean validate) throws JAXBException {

        Marshaller marshaller = jaxbContext.createMarshaller();
        if(validate)
            marshaller.setSchema(schema);
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        return marshaller;
    }


    public String getXSD(String systemId) throws IOException, SAXException {
        InputSource inputSource = resolver.resolveEntity(null, systemId);
        InputStream inputStream = inputSource.getByteStream();

        StringBuffer buffer = new StringBuffer();
        InputStreamReader isr = new InputStreamReader(inputStream, "UTF-8");
        Reader in = new BufferedReader(isr);
        int ch;
        while ((ch = in.read()) > -1)
            buffer.append((char)ch);
        in.close();
        return buffer.toString();
    }

    public String marshal(Object obj, boolean validate) throws JAXBException {
        StringWriter writer = new StringWriter();
        getMarshal(validate).marshal(obj, writer);

        return writer.toString();
    }
    
    public void marshal(Object obj, boolean validate, File output) throws JAXBException {
        getMarshal(validate).marshal(obj, output);
    }

    public Object unmarshal(InputStream xml, boolean validate) throws JAXBException {
        return getUnMarshal(validate).unmarshal(xml);
    }

    public Object unmarshal(InputSource xml, boolean validate) throws JAXBException {
        return getUnMarshal(validate).unmarshal(xml);
    }

    public Object unmarshal(byte[] xml, boolean validate, String encoding) throws JAXBException, UnsupportedEncodingException {
        return getUnMarshal(validate).unmarshal(byteToInputSource(xml, encoding));
    }

    /**
     * конвертация массива байтов в InputSource
     *
     * @param xml сообщение в xml
     * @param encoding кодировка
     * @return InputSource
     * @throws Exception ex
     */
    private static InputSource byteToInputSource(byte[] xml, String encoding) throws UnsupportedEncodingException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(xml);
        if(encoding == null || "".equals(encoding))
            encoding = "UTF-8";
        Reader reader = new BufferedReader(new InputStreamReader(byteArrayInputStream, encoding));
        return new InputSource(reader);
    }

}
