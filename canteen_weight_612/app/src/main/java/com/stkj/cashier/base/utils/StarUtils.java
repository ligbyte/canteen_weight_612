package com.stkj.cashier.base.utils;

public class StarUtils {



    public static String nameStar(String input) {
        if (input == null || input.length() <= 1) {
            return input;
        }
        StringBuilder maskedString = new StringBuilder(input.length());
        maskedString.append(input.charAt(0));
        for (int i = 1; i < input.length(); i++) {
            maskedString.append('*');
        }
        return maskedString.toString();
    }




    public static String phoneStar(String input) {
        if (input == null || input.length() < 6) {
            return input;
        } else if (input.length() >= 6 && input.length() < 14) {
            StringBuilder maskedString = new StringBuilder(input);
            for (int i = 3; i < 7 && i < input.length(); i++) {
                maskedString.setCharAt(i, '*');
            }
            return maskedString.toString();
        } else if (input.length() >= 14) {
            StringBuilder maskedString = new StringBuilder(input);
            for (int i = 6; i < input.length() - 4; i++) {
                maskedString.setCharAt(i, '*');
            }
            return maskedString.toString();
        }
        return input;
    }


}
