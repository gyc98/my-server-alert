package cn.billycloud.myserveralert.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashHelper {
    public static String md5(String input) throws MyException, NoSuchAlgorithmException {
        if(input == null || input.length() == 0) {
            throw new MyException("md5输入数据为空");
        }
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(input.getBytes());
        byte[] byteArray = md5.digest();

        char[] hexDigits = {'0','1','2','3','4','5','6','7','8','9', 'a','b','c','d','e','f'};
        // 一个字节对应两个16进制数，所以长度为字节数组乘2
        char[] charArray = new char[byteArray.length * 2];
        int index = 0;
        for (byte b : byteArray) {
            charArray[index++] = hexDigits[b>>>4 & 0xf];
            charArray[index++] = hexDigits[b & 0xf];
        }
        return new String(charArray);
    }
}
