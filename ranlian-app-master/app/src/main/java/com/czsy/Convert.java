package com.czsy;

import java.math.BigDecimal;

public class Convert {
    public static String bytesToHexString(byte[] var0) {
        StringBuilder var2 = new StringBuilder("");
        if (var0 != null && var0.length > 0) {
            for (int var1 = 0; var1 < var0.length; ++var1) {
                String var3 = Integer.toHexString(var0[var1] & 255);
                if (var3.length() < 2) {
                    var2.append(0);
                }

                var2.append(var3);
            }

            return var2.toString().toUpperCase();
        } else {
            return null;
        }
    }

    public static String getASCIIString(byte[] var0) {
        StringBuilder var2 = new StringBuilder();

        for (int var1 = 0; var1 < var0.length; ++var1) {
            if (var0[var1] > 0) {
                var2.append((char) var0[var1]);
            }
        }

        return var2.toString();
    }

    public static double round(Double var0, int var1) {
        return var1 < 0
                ? new Double(0.0D)
                : new Double((new BigDecimal(var0.toString())).divide(new BigDecimal("1"), var1, 4).doubleValue());
    }

    public static float toFloat(String var0, float var1, int var2) {
        return var0 != null && !var0.equalsIgnoreCase("") ? (float) round(Double.parseDouble(var0), var2) : var1;
    }

    public static int toInt(String var0) {
        return toInt(var0, 0);
    }

    public static int toInt(String var0, int var1) {
        return var0 != null && !var0.equalsIgnoreCase("") && RegExpValidator.IsNumber(var0)
                ? Integer.parseInt(var0)
                : var1;
    }

}
