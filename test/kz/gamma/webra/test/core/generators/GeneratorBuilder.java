package kz.gamma.webra.test.core.generators;

import kz.gamma.webra.test.core.generators.impl.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by i_nikulin
 * 19.04.2010 17:32:40
 */
public class GeneratorBuilder {

    protected static Map<String, Class<? extends Generator>> map = new HashMap<String, Class<? extends Generator>>();

    static {

        map.put("pattern", PatternStringGenerator.class);
        map.put("enum", EnumGenerator.class);
        map.put("number", NumberGenerator.class);
        map.put("date", DateGenerator.class);
        map.put("email", EmailGenerator.class);

    }





    //--------------------------------------------------------------------------------------------------------------------


    public static Generator build(String name, boolean canError, Map<String, String> params) throws Exception {
        Class<? extends Generator> genClass = map.get(name);

        if (genClass == null)
            throw new Exception("Для имени '" + name + "' не определен генератор");

        Generator generator = genClass.newInstance();
        generator.init(params);
        generator.setCanError(canError);        

        return generator;
    }


}
