package kz.gamma.webra.services.test.createPerson;

import kz.gamma.webra.services.client.WSClient;
import kz.gamma.webra.services.client.test.TestUtils;
import kz.gamma.webra.services.common.entities.DocCreatePersonIn;
import kz.gamma.webra.services.common.entities.DocCreatePersonOut;
import kz.gamma.webra.services.common.msgProcess.XmlProcessor;
import kz.gamma.webra.test.core.LoopTestResult;
import kz.gamma.webra.test.core.generators.XmlGenerator;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by y_makulbek
 * Date: 21.04.2010 11:04:44
 */
public class GeneratePersonToDB {

    @Test
    public void generatePersons() throws Exception
    {
        LoopTestResult loopTestResult = new LoopTestResult();

        for (int i = 0; i < 100; i++) {

            String xmlIn = XmlGenerator.generate("test/kz/gamma/webra/services/test/createPerson/docCreatePersonIn.xml", true).get(0);
            loopTestResult.start("Сгенерированный Ю.Л.", "Входные данные:","<xmp>" +xmlIn+ "</xmp>");
            DocCreatePersonIn docCreatePersonIn = (DocCreatePersonIn) XmlProcessor.getInstance().unmarshal(xmlIn.getBytes("UTF-8"), true, "UTF-8");
            DocCreatePersonOut docCreatePersonOut = (DocCreatePersonOut) TestUtils.callServer("createPerson", docCreatePersonIn);
            Assert.assertNotNull("getPersonId=null", docCreatePersonOut.getPersonId());
//                System.out.println("createPerson id==" + docCreatePersonOut.getPersonId());

        }

        throw loopTestResult;
    }

    @Test
    public void generateErrorPersons() throws Exception
    {
        LoopTestResult loopTestResult = new LoopTestResult();

        for (int i = 0; i < 100; i++) {

            List<String> gen = XmlGenerator.generate("test/kz/gamma/webra/services/test/createPerson/docCreatePersonIn.xml", true);
            String xmlIn = gen.get(0);
            String name = gen.get(1);
            loopTestResult.start("Сгенерированный с ошибками Ю.Л. ", "Ошибка в элементе:",name, "Входные параметры:", "<xmp>" +xmlIn+ "</xmp>");
            try {
                DocCreatePersonIn docCreatePersonIn = (DocCreatePersonIn) XmlProcessor.getInstance().unmarshal(xmlIn.getBytes("UTF-8"), true, "UTF-8");
                DocCreatePersonOut docCreatePersonOut = (DocCreatePersonOut) WSClient.getInstance().callServer("createPerson", docCreatePersonIn, "UTF-8");
            } catch (JAXBException e) {

            } catch (Throwable e) {
                loopTestResult.addError(e);
            }
        }
        throw loopTestResult;
    }

}
