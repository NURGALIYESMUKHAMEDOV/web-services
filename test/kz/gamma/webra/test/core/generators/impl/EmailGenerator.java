package kz.gamma.webra.test.core.generators.impl;

import kz.gamma.webra.test.core.generators.Generator;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by i_nikulin
 * 21.04.2010 11:32:35
 */
public class EmailGenerator extends Generator {

    private Random rnd = new Random();
    private String all = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZАаБбВвГгДдЕеЁёЖжЗзИиЙйКкЛлМмНнОоПпРрСсТтУуФфХхЦцЧчШшЩщЪъЫыЬьЭэЮюЯяӨөӘәҒғҚқҢңҰұҮүҺһІі0123456789 ,«»№!\"#$%&'()*+-./:;<=>?@[\\]^_`{|}~";
    private String valid1 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789._%+-";
    private String valid2 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_";
    private String domain = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private String invalid = "";

    @Override
    public void init(Map<String, String> params) {
        super.init(params);
        
        for (int i = 0; i < all.length(); i++)
            if (valid1.indexOf(all.charAt(i)) == -1)
                invalid += all.charAt(i);
    }


    @Override
    public String generate() {

        boolean nullable = Boolean.valueOf(params.get("nullable"));
        if(nullable && rnd.nextInt(5) == 1)
            return null;

        StringBuilder sb = new StringBuilder();

        int len = rnd.nextInt(10) + 1;
        for (int i = 0; i < len; i++)
            sb.append(valid1.charAt(rnd.nextInt(valid1.length())));

        sb.append("@");

        len = rnd.nextInt(10) + 1;
        for (int i = 0; i < len; i++)
            sb.append(valid2.charAt(rnd.nextInt(valid2.length())));

        sb.append(".");

        len = rnd.nextInt(3) + 2;
        for (int i = 0; i < len; i++)
            sb.append(domain.charAt(rnd.nextInt(domain.length())));

        return sb.toString();
    }

    @Override
    public String generateError() {

        boolean nullable = Boolean.valueOf(params.get("nullable"));
        if(!nullable && rnd.nextInt(5) == 1)
            return null;

        params.put("nullable", "false");

        StringBuilder sb = new StringBuilder(generate());


        if (rnd.nextBoolean())
            sb.replace(sb.indexOf("@"), sb.indexOf("@") + 1, "");

        int count = rnd.nextInt(sb.length()) + 1;

        for (int i = 0; i< count; i++)
        {
            int start = rnd.nextInt(sb.length());
            sb.replace(start, start + 1, invalid.charAt(rnd.nextInt(invalid.length())) + "");
        }

        return sb.toString();
    }


    public static void main(String[] args) {

        Map<String, String> params = new HashMap<String, String>();
        Generator gen = new EmailGenerator();
        gen.init(params);

        System.out.println("generate");
        for (int i = 0; i < 10000; i++)
            System.out.println(gen.generate());

        System.out.println("generateError");
        for (int i = 0; i < 10000; i++)
            System.out.println(gen.generateError());
    }

}
