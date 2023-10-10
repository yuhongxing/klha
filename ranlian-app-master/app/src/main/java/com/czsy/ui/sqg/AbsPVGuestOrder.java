package com.czsy.ui.sqg;

import android.view.View;

import com.czsy.android.R;
import com.czsy.bean.CommonOrderBean;
import com.czsy.ui.AbsPVNFC;
import com.czsy.ui.MainActivity;

// 代客下单
public abstract class AbsPVGuestOrder extends AbsPVNFC implements View.OnClickListener {
    protected final CommonOrderBean client_order_bean;

    public AbsPVGuestOrder(MainActivity a, CommonOrderBean b) {
        super(a);
        this.client_order_bean = b;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.iv_back == id) {
            act.getPVC().pop();
        }
    }
}
