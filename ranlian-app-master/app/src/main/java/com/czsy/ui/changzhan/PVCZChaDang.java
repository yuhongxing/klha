package com.czsy.ui.changzhan;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.czsy.android.R;
import com.czsy.bean.GangPingBean;
import com.czsy.ui.MainActivity;


public class PVCZChaDang extends AbsPVCZNFC implements View.OnClickListener {

    public PVCZChaDang(MainActivity a) {
        super(a);
    }

    @Override
    protected int getMainViewRes() {
        return R.layout.cz_abs_chadang;
    }


    @Override
    protected void createMainView(Context ctx) {
        super.createMainView(ctx);


    }

    @Override
    protected void gpSearched(GangPingBean bp) {
        tv_content.setText(bp.detailString());
    }


}
