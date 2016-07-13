package kz.gamma.webra.test.core.generators.impl;

import kz.gamma.webra.test.core.generators.Generator;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by i_nikulin
 * 19.04.2010 16:52:20
 */
public class PatternStringGenerator extends Generator {

    private Random rnd = new Random();
    private String all = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZАаБбВвГгДдЕеЁёЖжЗзИиЙйКкЛлМмНнОоПпРрСсТтУуФфХхЦцЧчШшЩщЪъЫыЬьЭэЮюЯяӨөӘәҒғҚқҢңҰұҮүҺһІі0123456789 ,«»№!\"#$%&'()*+-./:;<=>?@[\\]^_`{|}~";
    private String invalid = "";


    @Override
    public void init(Map<String, String> params) {
        super.init(params);

        String valid = params.get("valid");
        boolean isAll = Boolean.valueOf(params.get("all"));

        if (!isAll)
            for (int i = 0; i < all.length(); i++)
                if (valid.indexOf(all.charAt(i)) == -1)
                    invalid += all.charAt(i);
    }

    @Override
    public String generateError() {
        boolean nullable = Boolean.valueOf(params.get("nullable"));
        if (!nullable && rnd.nextInt(5) == 1)
            return null;

        String valid = params.get("valid");
        boolean isAll = Boolean.valueOf(params.get("all"));
        if(isAll)
            valid = all;
        int max = Integer.valueOf(params.get("max"));
        int min = Integer.valueOf(params.get("min"));
        int len = rnd.nextInt(max - min + 1) + min - 1;


        boolean hasError = false;
        boolean needInvalid = false;
        while (!hasError) {
            //ошибка по min
            if (min > 0 && rnd.nextBoolean()) {
                len = rnd.nextInt(min);
                hasError = true;
            } else
            //ошибка по max
            {
                len = rnd.nextInt(max) + max + 1;
                hasError = true;
            }

            //ошибка по valid
            if (!isAll && rnd.nextBoolean()) {
                needInvalid = true;
                hasError = true;
            }

            if (len == 0 && nullable) {                
                hasError = false;
            }
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < len; i++)
            sb.append(valid.charAt(rnd.nextInt(valid.length())));

        if (sb.length() > 0 && needInvalid || !hasError) {
            len = rnd.nextInt(sb.length());
            for (int i = 0; i < len; i++) {
                int start = rnd.nextInt(sb.length());
                sb.replace(start, start + 1, invalid.charAt(rnd.nextInt(invalid.length())) + "");
            }
        }


        return sb.toString();
    }


    @Override
    public String generate() {
        boolean nullable = Boolean.valueOf(params.get("nullable"));
        if (nullable && rnd.nextInt(5) == 1)
            return null;

        String valid = params.get("valid");
        boolean isAll = Boolean.valueOf(params.get("all"));
        if(isAll)
            valid = all;

        int max = Integer.valueOf(params.get("max"));
        int min = Integer.valueOf(params.get("min"));
        int len = rnd.nextInt(max - min + 1) + min;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++)
            sb.append(valid.charAt(rnd.nextInt(valid.length())));

        return sb.toString();
    }


    public static void main(String[] args) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("max", "10");
        params.put("min", "5");
        params.put("valid", "*");
        Generator gen = new PatternStringGenerator();
        gen.init(params);

        for (int i = 0; i < 10000; i++)
            System.out.println(gen.generate());
        for (int i = 0; i < 10000; i++)
            System.out.println(gen.generateError());
    }
}
