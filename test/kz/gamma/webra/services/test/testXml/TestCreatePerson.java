package kz.gamma.webra.services.test.testXml;

import kz.gamma.webra.services.client.test.TestUtils;
import kz.gamma.webra.services.common.entities.DocCreatePersonIn;
import kz.gamma.webra.services.common.msgProcess.XmlProcessor;
import kz.gamma.webra.test.core.LoopTestResult;
import kz.gamma.webra.test.core.generators.XmlGenerator;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.util.List;

/**
 * Created by i_nikulin
 * 19.04.2010 16:39:19
 */
public class TestCreatePerson {

    @Test
    public void flkLegal() throws Exception
    {
        testXml("test/kz/gamma/webra/services/test/testXml/docCreatePersonIn_legal.xml");
    }

    @Test
    public void flkPhisic() throws Exception 
    {
        testXml("test/kz/gamma/webra/services/test/testXml/docCreatePersonIn_phisic.xml");
    }




    private void testXml(String fileName) throws Exception {
        System.out.println("testing " + fileName);
        LoopTestResult loopTestResult = new LoopTestResult();

        for (int i = 0; i < 100; i++) {

            List<String> gen = XmlGenerator.generate(fileName, true);
            String xmlIn = gen.get(0);
            String name = gen.get(1);
            loopTestResult.start("Сгенерированный с ошибками Ю.Л. ", "Ошибка в элементе:",name, "Входные параметры:", "<xmp>" +xmlIn+ "</xmp>");
            try {
                DocCreatePersonIn docCreatePersonIn = (DocCreatePersonIn) XmlProcessor.getInstance().unmarshal(xmlIn.getBytes("UTF-8"), true, "UTF-8");
                String xmlOut = XmlProcessor.getInstance().marshal(docCreatePersonIn, true);
                TestUtils.assertXmlEquals("xmls not equals", xmlIn, xmlOut);
                throw new Exception("Успешная обработка ошибочных данных:");
            } catch (JAXBException e) {

            } catch (Throwable e) {
                loopTestResult.addError(e);
            }
        }

        for (int i = 0; i < 100; i++) {
            List<String> gen = XmlGenerator.generate(fileName, false);
            String xmlIn = gen.get(0);
            loopTestResult.start("Сгенерированный ", "Входные параметры:", "<xmp>" +xmlIn+ "</xmp>");
            try {
                DocCreatePersonIn docCreatePersonIn = (DocCreatePersonIn) XmlProcessor.getInstance().unmarshal(xmlIn.getBytes("UTF-8"), true, "UTF-8");
                String xmlOut = XmlProcessor.getInstance().marshal(docCreatePersonIn, true);
                TestUtils.assertXmlEquals("xmls not equals", xmlIn, xmlOut);
            } catch (Throwable e) {
                loopTestResult.addError(e);
            }
        }

        throw loopTestResult;

    }



}
