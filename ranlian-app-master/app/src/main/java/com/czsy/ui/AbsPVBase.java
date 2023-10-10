package com.czsy.ui;

import android.content.Intent;
import android.util.Log;

import com.czsy.bean.LoginUser;

import mylib.ui.BaseInOutAnimPageView;

public abstract class AbsPVBase extends BaseInOutAnimPageView {
    public final MainActivity act;

    public AbsPVBase(MainActivity a) {
        act = a;
    }
//    public AbsPVBase(MainActivity a,String s) {
//        act = a;
//    }

    @Override
    public void onAttach(boolean firstShow) {
        super.onAttach(firstShow);
        LoginUser lu = LoginUser.get();
        if (!lu.valid()) {
            act.startActivity(new Intent(act, LoginActivity.class));
            act.finish();
            return;
        }
    }
}
