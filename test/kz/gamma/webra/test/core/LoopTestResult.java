package kz.gamma.webra.test.core;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

/**
 * Created by Ivan.Nikulin
 * Date: 03.01.2008
 * Time: 9:52:51
 */
public class LoopTestResult extends Exception
{
    protected static final Logger log = Logger.getLogger(LoopTestResult.class);

    private Map<String, Throwable> runs = new TreeMap<String, Throwable>();
    private Map<String, String> runsData = new HashMap<String, String>();
    private String lastName;

    public void addError(Throwable th)
    {
        log.error(th);
        runs.put(lastName, th);
    }

    public void start(String name,String ... datas)
    {
        lastName = name;
        int i = 0;
        while (runs.containsKey(lastName))
            lastName = name + " " + ++i;

        runs.put(lastName, null);
        StringBuilder sb = new StringBuilder();

        for(String par : datas)
            sb.append(par).append("<br/>");

        runsData.put(lastName, sb.toString());
    }

    public Map<String, Throwable> getRuns()
    {
        return runs;
    }

    public Map<String, String> getRunsData() {
        return runsData;
    }
}
