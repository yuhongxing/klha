package com.czsy;

import mylib.utils.SPUtils;

public class CZConfig {
    public final static String config_msg_sound = "config_msg_sound";
    public final static String config_msg_vibrate = "config_msg_vibrate";

    public static boolean getMsgVibrate() {
        return SPUtils.get().getBool(config_msg_vibrate, true);
    }

    public static boolean getMsgSound() {
        return SPUtils.get().getBool(config_msg_sound, true);
    }

    public static void setMsgVibrate(boolean v) {
        SPUtils.get().setBool(config_msg_vibrate, v);
    }

    public static void setMsgSound(boolean v) {
        SPUtils.get().setBool(config_msg_sound, v);
    }
}
