package com.czsy;


import android.text.TextUtils;

public abstract class ReturnRunable<T> implements Runnable {
    public T ret;
    public String msg;

    public String getMsg(String def) {
        return TextUtils.isEmpty(msg) ? def : msg;
    }
}
