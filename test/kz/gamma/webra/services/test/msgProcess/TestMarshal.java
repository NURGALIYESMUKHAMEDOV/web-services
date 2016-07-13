package kz.gamma.webra.services.test.msgProcess;

import kz.gamma.webra.services.common.entities.DocCreatePersonOut;
import kz.gamma.webra.services.common.msgProcess.XmlProcessor;
import org.junit.Test;

import javax.xml.bind.MarshalException;

/**
 * Created by i_nikulin
 * 23.04.2010 13:40:59
 */
public class TestMarshal {

    /**
     * маршализуем пустой объект без валидации
     */
    @Test
    public void testErrorMarshal() throws Exception
    {
        DocCreatePersonOut personOut = new  DocCreatePersonOut();
        XmlProcessor.getInstance().marshal(personOut, false);
    }

    /**
     * маршализуем пустой объект с валидаций, ждем исключение
     */
    @Test(expected = MarshalException.class)
    public void testMarshal() throws Exception
    {
        DocCreatePersonOut personOut = new  DocCreatePersonOut();
        XmlProcessor.getInstance().marshal(personOut, true);
    }

}
