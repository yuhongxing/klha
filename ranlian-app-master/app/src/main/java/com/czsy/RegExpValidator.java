package com.czsy;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class RegExpValidator {
//    public static boolean IsChinese(String var0) {
//        return match("^[一-龥],{0,}$", var0);
//    }

    public static boolean IsDay(String var0) {
        return match("^((0?[1-9])|((1|2)[0-9])|30|31)$", var0);
    }

    public static boolean IsDecimal(String var0) {
        return match("^[0-9]+(.[0-9]{2})?$", var0);
    }

    public static boolean IsDepositCode(String var0) {
        return match("^[A-Z0-9-]+$", var0);
    }

    public static boolean IsHandset(String var0) {
        return match("^[1]+[3,5]+\\d{9}$", var0);
    }

    public static boolean IsIDcard(String var0) {
        return match("(^\\d{18}$)|(^\\d{15}$)", var0);
    }

    public static boolean IsIntNumber(String var0) {
        return match("^\\+?[1-9][0-9]*$", var0);
    }

    public static boolean IsLetter(String var0) {
        return match("^[A-Za-z]+$", var0);
    }

    public static boolean IsLowChar(String var0) {
        return match("^[a-z]+$", var0);
    }

    public static boolean IsMonth(String var0) {
        return match("^(0?[[1-9]|1[0-2])$", var0);
    }

    public static boolean IsNumber(String var0) {
        return match("^[0-9]*$", var0);
    }

    public static boolean IsPostalcode(String var0) {
        return match("^\\d{6}$", var0);
    }

    public static boolean IsUpChar(String var0) {
        return match("^[A-Z]+$", var0);
    }

    public static boolean IsUrl(String var0) {
        return match("http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?", var0);
    }

    public static boolean isCard(String var0) {
        return match("^\\d{19}$", var0);
    }

    public static boolean isDate(String var0) {
        return match(
                "^((((1[6-9]|[2-9]\\d)\\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-)) (20|21|22|23|[0-1]?\\d):[0-5]?\\d:[0-5]?\\d$",
                var0);
    }

    public static boolean isEmail(String var0) {
        return match(
                "^([\\w-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([\\w-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$",
                var0);
    }

//    public static boolean isIDCardAddress(String var0) {
//        return match("^(?=.*?[\一-\龥])[\\dA-Za-z\\-\一-\龥]+", var0);
//    }
//
//    public static boolean isIDCardName(String var0) {
//        return match("[\一-\龥]{2,5}(?:·[\一-\龥]{2,5})*", var0);
//    }

    public static boolean isIDCardNumber(String var0) {
        return match("^[1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$",
                var0);
    }

    public static boolean isIP(String var0) {
        return match("^" + "(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)" + "\\."
                + "(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)" + "\\." + "(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)"
                + "\\." + "(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)" + "$", var0);
    }

    public static boolean isLicitScanCode(String var0) {
        return match("^[a-zA-Z0-9\\-]{1,20}$", var0);
    }

    public static boolean isMobile(String var0) {
        return match("^(0|86|17951)?(13[0-9]|15[012356789]|17[0-9]|18[0-9]|14[57])[0-9]{8}$", var0);
    }

    public static boolean isMoney(String var0) {
        return Pattern.compile("^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?$").matcher(var0).matches();
    }

    public static boolean isPassword(String var0) {
        return match("^[\\@A-Za-z0-9\\!\\#\\$\\%\\^\\&\\*\\.\\~\\,\\|\\?]{6,14}", var0);
    }

    public static boolean isTel(String var0) {
        return match("^(\\d{3,4}-?)?\\d{6,8}$", var0);
    }

    public static boolean isUsername(String var0) {
        return match("^[0-9a-zA-Z_]{6,18}$", var0);
    }

    public static boolean isWildMobile(String var0) {
        return match("^(0|86|17951)?(13[0-9]|15[012356789]|17[0-9]|18[0-9]|14[57])[0-9]{8}-[0-9]{1,2}$", var0);
    }

    public static boolean isWildTel(String var0) {
        return match("^(\\d{3,4}-?)?\\d{6,8}-[0-9]{1,2}$", var0);
    }

    public static boolean match(String var0, String var1) {
        try {
            boolean var2 = Pattern.compile(var0).matcher(var1).matches();
            return var2;
        } catch (PatternSyntaxException var3) {
            return false;
        }
    }

    public static boolean regexString(String var0) {
        return match("[一-龥]", var0);
    }

}
