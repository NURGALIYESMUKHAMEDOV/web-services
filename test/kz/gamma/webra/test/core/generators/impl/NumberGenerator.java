package kz.gamma.webra.test.core.generators.impl;

import kz.gamma.webra.test.core.generators.Generator;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by i_nikulin
 * 21.04.2010 9:58:26
 */
public class NumberGenerator extends Generator {

    private Random rnd = new Random();
    private String invalid = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZАаБбВвГгДдЕеЁёЖжЗзИиЙйКкЛлМмНнОоПпРрСсТтУуФфХхЦцЧчШшЩщЪъЫыЬьЭэЮюЯяӨөӘәҒғҚқҢңҰұҮүҺһІі ,«»№!\"#$%&'()*+-./:;<=>?@[\\]^_`{|}~";

    @Override
    public String generate() {
        boolean nullable = Boolean.valueOf(params.get("nullable"));
        if(nullable && rnd.nextInt(5) == 1)
            return null;

        int max = Integer.valueOf(params.get("max"));
        int min = Integer.valueOf(params.get("min"));

        int res = rnd.nextInt(Math.abs(max - min) + 1) + min;

        if(res > max || res < min)
            throw new RuntimeException("Ошибка при генерации");

        return res + "";
    }

    @Override
    public String generateError() {

        boolean nullable = Boolean.valueOf(params.get("nullable"));
        if(!nullable && rnd.nextInt(5) == 1)
            return null;

        boolean isAll = Boolean.valueOf(params.get("all"));
        if(isAll)
        {
            StringBuilder sb = new StringBuilder();
            int len = rnd.nextInt(100);
            for (int i = 0; i < len; i++)
                sb.append(invalid.charAt(rnd.nextInt(invalid.length())));

            return sb.toString();
        }

        int max = Integer.valueOf(params.get("max"));
        int min = Integer.valueOf(params.get("min"));

        int abcMax = Math.max(Math.abs(min), Math.abs(max));

        int res;

        if (rnd.nextBoolean())
            res =  min - rnd.nextInt(abcMax * 2) - 1;
        else
            res = max + rnd.nextInt(abcMax * 2) + 1;

        if(res <= max && res >= min)
            throw new RuntimeException("Ошибка при генерации");

        return res + "";

    }

    public static void main(String[] args) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("max", "10");
        params.put("min", "5");
        Generator gen = new NumberGenerator();
        gen.init(params);

        System.out.println("generate");
        for (int i = 0; i < 10000; i++)
            System.out.println(gen.generate());

        System.out.println("generateError");
        for (int i = 0; i < 10000; i++)
            System.out.println(gen.generateError());
    }

}
