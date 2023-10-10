package com.czsy.ui;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.czsy.Constant;
import com.czsy.TheApp;
import com.czsy.android.R;
import com.czsy.bean.CommonOrderBean;
import com.czsy.bean.GangPingBean;
import com.czsy.bean.LoginUser;

import mylib.app.MyLog;
import mylib.utils.Utils;

// 客户下单，扫重瓶
abstract public class AbsPVScanHeavy extends AbsPVScanEmpty {
    public AbsPVScanHeavy(MainActivity a, CommonOrderBean b) {
        super(a, b, false,false,true);
    }

    @Override
    protected boolean isEmpty() {
        return false;
    }

    @Override
    protected void createMainView(Context ctx) {
        super.createMainView(ctx);
        tv_title.setText(R.string.title_scan_heavy);
        btn_next.setVisibility(View.GONE);
        btn_no_empty.setText(R.string.title_scan_done);
        btn_no_empty.setTextColor(Color.WHITE);
        btn_no_empty.setBackgroundResource(R.drawable.sel_btn_blue);
    }
}
