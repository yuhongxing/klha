package com.czsy;

import android.nfc.NdefRecord;
import android.nfc.Tag;

import java.nio.charset.Charset;
import java.util.Locale;

public class NFCReader {

    private static String getHex(byte[] var0) {
        StringBuilder var2 = new StringBuilder();

        for (int var1 = 0; var1 <= var0.length - 1; ++var1) {
            var2.append(Integer.toString(var0[var1] & 255));
        }

        return var2.toString();
    }

    //@SuppressLint({"NewApi"})
    public static String getXPBM(Tag var0) {
        return getHex(((Tag) var0).getId()).toUpperCase();
    }

    //@SuppressLint({"NewApi"})
    public static NdefRecord newTextRecord(String var0, Locale var1, boolean var2) {
        byte[] var4 = var1.getLanguage().getBytes(Charset.forName("US-ASCII"));
        Charset var6;
        if (var2) {
            var6 = Charset.forName("UTF-8");
        } else {
            var6 = Charset.forName("UTF-16");
        }

        byte[] var5 = var0.getBytes(var6);
        short var3;
        if (var2) {
            var3 = 0;
        } else {
            var3 = 128;
        }

        char var8 = (char) (var3 + var4.length);
        byte[] var7 = new byte[var4.length + 1 + var5.length];
        var7[0] = (byte) var8;
        System.arraycopy(var4, 0, var7, 1, var4.length);
        System.arraycopy(var5, 0, var7, var4.length + 1, var5.length);
        return new NdefRecord((short) 1, NdefRecord.RTD_TEXT, new byte[0], var7);
    }

}
