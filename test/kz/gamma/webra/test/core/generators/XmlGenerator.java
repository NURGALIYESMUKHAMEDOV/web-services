package kz.gamma.webra.test.core.generators;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;
import org.jaxen.SimpleNamespaceContext;

import java.io.File;
import java.util.*;

/**
 * Created by i_nikulin
 * 19.04.2010 17:46:20
 */
public class XmlGenerator {


    public static List<String> generate(String fileName, boolean needError) {

        Document document = null;
        String errorPath = "";

        try {
            SAXReader reader = new SAXReader();


            document = reader.read(new File(fileName));

            HashMap<String, String> map = new HashMap<String, String>();
            map.put("webra", "http://www.gamma.kz/webra/xsd");

            XPath xpath = DocumentHelper.createXPath("//webra:generator");
            xpath.setNamespaceContext(new SimpleNamespaceContext(map));

            List<Element> generators = xpath.selectNodes(document);

            int errorNumber = new Random().nextInt(generators.size());


            for (int i = 0, generatorsSize = generators.size(); i < generatorsSize; i++) {
                Element xmlTemplate = generators.get(i);
                String name = xmlTemplate.attribute("name").getValue();

                boolean canError = true;
                if (xmlTemplate.attribute("canError") != null)
                    canError = "true".equals(xmlTemplate.attribute("canError").getValue());

                xpath = DocumentHelper.createXPath("webra:param");
                xpath.setNamespaceContext(new SimpleNamespaceContext(map));
                List<Element> params = xpath.selectNodes(xmlTemplate);

                Map<String, String> paramsMap = new HashMap<String, String>();
                for (Element param : params) {
                    String paramName = param.attribute("name").getValue();
                    String paramValue = param.getText();
                    paramsMap.put(paramName, paramValue);
                }

                Generator generator = GeneratorBuilder.build(name, canError, paramsMap);

                Element parent = xmlTemplate.getParent();
                parent.remove(xmlTemplate);

                String res = null;
                if (needError && i == errorNumber)
                {
                    errorPath = parent.getPath().replace("*[name()='", "").replace("']", "");
                    res = generator.generateError();
                }
                else
                    res = generator.generate();

                if(res != null)
                    parent.setText(res);
                else
                    parent.getParent().remove(parent);
            }


        } catch (Exception e) {
            throw new RuntimeException("Ошибка при генерации объекта", e);
        }

        return Arrays.asList(document.asXML(), errorPath);
    }


    public static void main(String[] args) throws Exception {

        List<String> xml = XmlGenerator.generate("test/kz/gamma/webra/services/test/createPerson/docCreatePersonIn.xml", false);
        System.out.println(xml);

    }

}
