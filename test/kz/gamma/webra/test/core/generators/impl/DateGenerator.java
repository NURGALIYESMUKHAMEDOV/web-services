package kz.gamma.webra.test.core.generators.impl;

//import kz.gamma.webra.core.Utils;
import kz.gamma.webra.test.core.generators.Generator;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by i_nikulin
 * 21.04.2010 10:40:08
 */
public class DateGenerator extends Generator {

    private Random rnd = new Random();
    public static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private String all = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZАаБбВвГгДдЕеЁёЖжЗзИиЙйКкЛлМмНнОоПпРрСсТтУуФфХхЦцЧчШшЩщЪъЫыЬьЭэЮюЯяӨөӘәҒғҚқҢңҰұҮүҺһІі0123456789 ,«»№!\"#$%&'()*+-./:;<=>?@[\\]^_`{|}~";
           

    @Override
    public String generate() {

        boolean nullable = Boolean.valueOf(params.get("nullable"));
        if(nullable && rnd.nextInt(5) == 1)
            return null;

        Date max, min;
        try {
            max = dateFormat.parse(params.get("max"));
            min = dateFormat.parse(params.get("min"));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        Long inDay = 365L/*Utils.getDifferenceInDay(min, max)*/;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(min);

        calendar.add(Calendar.DAY_OF_YEAR, rnd.nextInt(inDay.intValue()));

        Date date = calendar.getTime();

        if (date.getTime() > max.getTime() || date.getTime() < min.getTime())
            throw new RuntimeException("Ошибка при генерации");

        return dateFormat.format(date);

    }

    @Override
    public String generateError() {

        boolean nullable = Boolean.valueOf(params.get("nullable"));
        if(!nullable && rnd.nextInt(5) == 1)
            return null;

       StringBuilder sb = new StringBuilder();
        int len = rnd.nextInt(100);

        for (int i = 0; i < len; i++)
            sb.append(all.charAt(rnd.nextInt(all.length())));

        return sb.toString();


    }

    public static void main(String[] args) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("max", "2006-08-13");
        params.put("min", "2000-08-13");
        Generator gen = new DateGenerator();
        gen.init(params);

        System.out.println("generate");
        for (int i = 0; i < 10000; i++)
            System.out.println(gen.generate());

        System.out.println("generateError");
        for (int i = 0; i < 10000; i++)
            System.out.println(gen.generateError());
    }

}
