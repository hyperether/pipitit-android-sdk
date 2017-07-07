package com.hyperether.pipitit.util;

/**
 * Temporary class for read bytes
 *
 * @author Nebojsa Brankovic
 * @version 1.0 - 4/29/2017
 */
public class ByteUtils {

    public static String byteArrayToString(byte[] bytes) {
        if (bytes == null)
            return "Array is null !!!";
        try {
            StringBuilder answer = new StringBuilder();
            for (byte aByte : bytes) {
                String hexString = Integer.toHexString(aByte & 0xff);
                if (hexString.length() == 1) answer.append("0");
                answer.append(hexString);
            }
            return hexToAscii(answer.toString());
        } catch (Exception e) {
            return "Something is wrong!! " + e.getMessage();
        }
    }

    private static String hexToAscii(String s) {
        int n = s.length();
        StringBuilder sb = new StringBuilder(n / 2);
        for (int i = 0; i < n; i += 2) {
            char a = s.charAt(i);
            char b = s.charAt(i + 1);
            sb.append((char) ((hexToInt(a) << 4) | hexToInt(b)));
        }
        return sb.toString();
    }

    private static int hexToInt(char ch) {
        if ('a' <= ch && ch <= 'f') {
            return ch - 'a' + 10;
        }
        if ('A' <= ch && ch <= 'F') {
            return ch - 'A' + 10;
        }
        if ('0' <= ch && ch <= '9') {
            return ch - '0';
        }
        throw new IllegalArgumentException(String.valueOf(ch));
    }
}
