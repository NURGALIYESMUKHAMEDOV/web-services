package kz.gamma.webra.test.core;

/**
 * Created by Ivan.Nikulin
 * Date: 27.02.2008
 * Time: 17:55:39
 */

/**
 * варнинг будет красится желтым цветом
 */
public class Warning extends Exception
{
    public Warning()
    {
    }

    public Warning(String message)
    {
        super(message);
    }

    public Warning(String message, Throwable cause)
    {
        super(message, cause);
    }

    public Warning(Throwable cause)
    {
        super(cause);
    }
}
