package cn.wizzer.modules.controllers.open.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Random;

public class RandomCode {

    private static String genCode(int totalLen, int intLen) {
        Random random = new Random();
        int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
        String val = "";
        for (int i = 0; i < (totalLen - intLen); i++) {
            val += (char) (random.nextInt(26) + temp);
        }
        for (int i = 0; i < intLen; i++) {
            val += random.nextInt(10);
        }
        return val.toUpperCase();
    }

    public static String genIntCode(int len){
        return genCode(len,len);
    }

    public static String genStringCode(int len){
        return genCode(len,0);
    }

    public static String genStrAndIntCode(int intLen,int strLen){
        return genCode(intLen+strLen,intLen);
    }

    public static String genTimeCode(int randomLen){
        String timeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss", Locale.getDefault()));
        return timeStr+genIntCode(randomLen);
    }
}