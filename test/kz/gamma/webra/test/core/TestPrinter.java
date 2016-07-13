package kz.gamma.webra.test.core;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Ivan.Nikulin
 * Date: 03.01.2008
 * Time: 11:09:51
 */
public class TestPrinter extends RunListener
{
    protected static final Logger log = Logger.getLogger(TestPrinter.class);

    private PrintStream left;
    private PrintStream main;
    private PrintStream subleft;
    private boolean isStarted = false;
    private String className;
    private String subName;
    private int successfulCount = 0;
    private int warningCount = 0;
    private int failturesCount = 0;

    private int mainSuccessfulCount = 0;
    private int mainFailturesCount = 0;

    private StringBuilder total = new StringBuilder();
    private boolean wasSuccessful = true;
    private Date startDate;

    public TestPrinter() throws FileNotFoundException
    {

        main = new PrintStream(new PrintStream(new File("./test/report/~main.htm")));
        main.println("<html>");
        main.println("<head>");
        main.println("<link href='style.css' rel='StyleSheet' type='text/css'>");
        main.println("<meta http-equiv='content-type' content='text/html; charset=utf-8'>");

        main.println("</head>");
        main.println("<body>");
        main.println("<br><b>Report of WebRA auto testing</b>");
        DateFormat df = new SimpleDateFormat();
        startDate = new Date();
        main.println("<br>Start time = " + df.format(startDate));

        left = new PrintStream(new PrintStream(new File("./test/report/~left.htm")));
        left.println("<html>");
        left.println("<head>");
        left.println("<link href='style.css' rel='StyleSheet' type='text/css'>");
        left.println("<meta http-equiv='content-type' content='text/html; charset=utf-8'>");
        left.println("</head>");
        left.println("<body>");
        left.println("<a href='~main.htm' target=main>All info</a><br>");
        left.println("<br><b>Test classes:</b>");

        total.append("\n========================================");
        total.append("RESULT OF TESTING");
        total.append("========================================\n");
    }

    public void end()
    {
        DateFormat df = new SimpleDateFormat();
        Date endDate = new Date();
        main.println("<br>End time = " + df.format(endDate));
        main.println("<br>Elapsed time = " + elapsedTimeAsString(endDate.getTime() - startDate.getTime()));
        main.println("<br>All count = " + (mainSuccessfulCount + mainFailturesCount));
        main.println("<br>Successful count = " + mainSuccessfulCount);
        main.println("<br>Failure count = " + mainFailturesCount);
        if (mainFailturesCount == 0)
            main.println("<br>Result = <font color=green>Successful</font>");
        else
            main.println("<br>Result = <font color=red>Failed</font>");

        main.println("</body>");
        main.println("</html>");

        left.println("</body>");
        left.println("</html>");
        left.close();

        total.append("\nsee log for details in 'test/report.htm'");

        log.info(total.toString());
        if (!wasSuccessful)
            throw new RuntimeException(total.toString());
    }

    public void testRunFinished(Result result) throws Exception
    {

        if (isStarted)
            ok();

        String simpleName = className.substring(className.lastIndexOf('.') + 1, className.length());

        subleft.println("</body>");
        subleft.println("</html>");
        subleft.close();
        subleft = null;

        left.println("<br>");
        left.print(failturesCount == 0 ? (warningCount == 0 ? "<font color=green><a class=ok" : "<font color=orange><a class=warning") : "<font color=red><a class=error");
        left.print(" href='~main_" + (mainSuccessfulCount + mainFailturesCount + 1) + "." + className + ".htm' target='main'>" + (mainSuccessfulCount + mainFailturesCount + 1) + "." + simpleName + "</a>");
        left.println("</font>");


        PrintStream main = new PrintStream("./test/report/~main_" + (mainSuccessfulCount + mainFailturesCount + 1) + "." + className + ".htm");
        main.println("<html>");
        main.println("<frameset rows='40%,*'>");
        main.println("<frame src='~subleft_" + className + ".htm' name='subleft'>");
        main.println("<frame src='~top_" + className + ".htm' name='data'>");
        main.println("</frameset>");
        main.println("</html>");
        main.close();

        PrintStream top = new PrintStream("./test/report/~top_" + className + ".htm");
        top.println("<html>");
        top.println("<head>");
        top.println("<link href='style.css' rel='StyleSheet' type='text/css'>");
        top.println("<meta http-equiv='content-type' content='text/html; charset=utf-8'>");
        top.println("</head>");
        top.println("<body>");
        top.println("<br>class = " + className);
        top.println("<br>Time = " + elapsedTimeAsString(result.getRunTime()));
        top.println("<br>All count = " + (successfulCount + failturesCount + warningCount));
        top.println("<br>Successful count = " + successfulCount);
        top.println("<br>Warning count = " + warningCount);
        top.println("<br>Failure count = " + failturesCount);
        top.println("</body>");
        top.println("</html>");
        if (failturesCount == 0)
        {
            top.println("<br>Result = <font color=green>Successful</font>");
            total.append("\nresult for test '").append(className).append("':Successful");
            mainSuccessfulCount++;
        } else
        {
            top.println("<br>Result = <font color=red>Failed</font>");
            total.append("\nresult for test '").append(className).append("':Failed(").append(failturesCount).append(")");
            wasSuccessful = false;
            mainFailturesCount++;
        }
        top.close();

        successfulCount = 0;
        failturesCount = 0;
        warningCount = 0;
    }

    public void testStarted(Description description) throws Exception
    {


        if (isStarted)
            ok();


        className = description.getDisplayName();
        subName = className.substring(0, className.indexOf('('));
        className = className.substring(className.indexOf('(') + 1, className.indexOf(')'));
//        Class clazz = Class.forName(className);
//        boolean isLoop = clazz.isAssignableFrom(IsLoop.class);

        if (subleft == null)
        {
            subleft = new PrintStream(new PrintStream(new File("./test/report/~subleft_" + className + ".htm")));
            subleft.println("<html>");
            subleft.println("<head>");
            subleft.println("<link href='style.css' rel='StyleSheet' type='text/css'>");
            subleft.println("<meta http-equiv='content-type' content='text/html; charset=utf-8'>");
            subleft.println("</head>");
            subleft.println("<body>");
            subleft.println("<a href='~top_" + className + ".htm' target=data>All info</a><br>");
            subleft.println("<br><b>Sub tests:</b>");
        }

        isStarted = true;

    }

    public void testFailure(Failure failure) throws Exception
    {

        isStarted = false;
        Throwable throwable = failure.getException();
        if (throwable instanceof LoopTestResult)
        {
            LoopTestResult loop = (LoopTestResult) throwable;
            for (Map.Entry<String, Throwable> entry : loop.getRuns().entrySet())
            {
                subName = entry.getKey();
                datas(entry.getValue(),loop.getRunsData().get(subName));

            }
        } else
        {
            datas(throwable, null);
        }
    }

    public void testIgnored(Description description)
    {
    }

    protected String elapsedTimeAsString(long runTime)
    {
        long sec = Math.round(runTime / 1000D);
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb, Locale.US);
        formatter.format("%1$2d:%2$2d", sec/60, sec%60);
        return sb.toString();
    }

    protected void ok() throws Exception
    {
        subleft.println("<br><a class=ok href='ok.htm' target='data'>" + (successfulCount + failturesCount + warningCount + 1) + "." + subName + "</a>");
        successfulCount++;
        isStarted = false;
    }

    protected void datas(Throwable throwable, String dataStr) throws Exception
    {
        if(throwable == null)
        {
            subleft.println("<br><a class=ok href='~data_" + (successfulCount + failturesCount + warningCount + 1) + "." + className + "-" + subName + ".htm' target='data'>" + (successfulCount + failturesCount + warningCount + 1) + "." + subName + "</a>");
            successfulCount++;
            isStarted = false;

        } else if(throwable instanceof Warning)
        {
            subleft.println("<br><a class=warning href='~data_" + (successfulCount + failturesCount + warningCount + 1) + "." + className + "-" + subName + ".htm' target='data'>" + (successfulCount + failturesCount + warningCount + 1) + "." + subName + "</a>");
            warningCount++;
        }  else
        {
            subleft.println("<br><a class=error href='~data_" + (successfulCount + failturesCount + warningCount + 1) + "." + className + "-" + subName + ".htm' target='data'>" + (successfulCount + failturesCount + warningCount + 1) + "." + subName + "</a>");
            failturesCount++;
        }

        PrintStream data = new PrintStream(new File("./test/report/~data_" + (successfulCount + failturesCount + warningCount) + "." + className + "-" + subName + ".htm"));
        data.println("<html>");
        data.println("<head>");
        data.println("<link href='style.css' rel='StyleSheet' type='text/css'>");
        data.println("<meta http-equiv='content-type' content='text/html; charset=utf-8'>");
        data.println("</head>");
        data.println("<body>");
        data.println("<br/>");

        if(dataStr != null)
        {
            data.println(dataStr);
            data.println("<br/>");
        }

        if(throwable != null)
        {

            data.println("<font color=red>");
            data.println("<xmp>");
            throwable.printStackTrace(data);
            data.println("</xmp>");
            data.println("</font>");
        }
        data.println("</body>");
        data.println("</html>");
        data.close();

    }


}

