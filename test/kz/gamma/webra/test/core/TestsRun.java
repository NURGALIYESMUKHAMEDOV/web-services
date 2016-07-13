package kz.gamma.webra.test.core;


import kz.gamma.webra.services.test.createPerson.GeneratePersonToDB;
import kz.gamma.webra.services.test.msgProcess.TestMarshal;
import kz.gamma.webra.services.test.testXml.TestCreatePerson;
import org.junit.runner.JUnitCore;
import org.apache.log4j.Logger;

import java.io.FileNotFoundException;

/**
 * Created by Ivan.Nikulin
 * Date: 21.12.2007
 * Time: 13:49:56
 */
public class TestsRun
{
    protected static final Logger log = Logger.getLogger(TestsRun.class);

    private JUnitCore unitCore;
    private TestPrinter printer;

    public TestsRun()
    {
        unitCore = new JUnitCore();
        try
        {
            printer = new TestPrinter();
            unitCore.addListener(printer);
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void run() throws Exception
    {
        log.info("TestsRun start junit testing");
        run(TestCreatePerson.class);
        run(GeneratePersonToDB.class);
        run(TestMarshal.class);



        printer.end();
    }


    private void run(Class test)
    {
        log.info("====================================================================");
        log.info(test.getName());
        unitCore.run(test);
    }

    public static void main(String[] args) throws Exception
    {
        new TestsRun().run();
    }


}
