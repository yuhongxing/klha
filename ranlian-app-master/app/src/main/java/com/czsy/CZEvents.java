package com.czsy;

import java.util.LinkedList;
import java.util.List;

import mylib.app.EventHandler;

public class CZEvents extends EventHandler {
    public static enum Event {
        onLoginUserChanged,
        onPushDataChanged,
    }

    protected List<Class<? extends Enum<?>>> getEventClass() {
        List<Class<? extends Enum<?>>> ret = super.getEventClass();
        ret.add(Event.class);
        return ret;
    }

    public void onLoginUserChanged() {
    }

    public void onPushDataChanged() {
    }


}
