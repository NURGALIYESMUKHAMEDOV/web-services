package kz.gamma.webra.test.core.generators.impl;

import kz.gamma.webra.test.core.generators.Generator;

import java.util.*;

/**
 * Created by i_nikulin
 * 21.04.2010 9:49:16
 */
public class EnumGenerator extends Generator{

    private Random rnd = new Random();
    private String all = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZАаБбВвГгДдЕеЁёЖжЗзИиЙйКкЛлМмНнОоПпРрСсТтУуФфХхЦцЧчШшЩщЪъЫыЬьЭэЮюЯяӨөӘәҒғҚқҢңҰұҮүҺһІі0123456789 ,«»№!\"#$%&'()*+-./:;<=>?@[\\]^_`{|}~";
    private String invalid = "";

    @Override
    public void init(Map<String, String> params) {
        super.init(params);
        

        String valid = params.get("enums").replace(",","");

        for (int i = 0; i < all.length(); i++)
            if (valid.indexOf(all.charAt(i)) == -1)
                invalid += all.charAt(i);
    }


    @Override
    public String generate() {
        boolean nullable = Boolean.valueOf(params.get("nullable"));
        if(nullable && rnd.nextInt(5) == 1)
            return null;

        List<String> enums = Arrays.asList(params.get("enums").split(","));
        String res = enums.get(rnd.nextInt(enums.size()));

        if(!enums.contains(res))
             throw new RuntimeException("Ошибка при генерации");

        return res;
    }

    @Override
    public String generateError() {
        boolean nullable = Boolean.valueOf(params.get("nullable"));
        if(!nullable && rnd.nextInt(5) == 1)
            return null;

        List<String> enums = Arrays.asList(params.get("enums").split(","));

        StringBuilder sb = new StringBuilder();
        int len = rnd.nextInt(100);

        for (int i = 0; i < len; i++)
            sb.append(invalid.charAt(rnd.nextInt(invalid.length())));

        String res = sb.toString();

        if(enums.contains(res))
             throw new RuntimeException("Ошибка при генерации");

        return res;
    }


    public static void main(String[] args) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("enums", "BIN,RNN,IIN");

        Generator gen = new EnumGenerator();
        gen.init(params);

        System.out.println("generate");
        for (int i = 0; i < 10000; i++)
            System.out.println(gen.generate());

        System.out.println("generateError");
        for (int i = 0; i < 10000; i++)
            System.out.println(gen.generateError());
    }

}
