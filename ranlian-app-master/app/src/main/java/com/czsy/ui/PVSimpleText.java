package com.czsy.ui;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.czsy.CZBackTask;
import com.czsy.android.R;
import com.czsy.bean.OrderHuiZongBean;
import com.czsy.other.DatePickerDialog;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;
import java.util.Random;

import mylib.app.BackTask;
import mylib.app.MyLog;
import mylib.ui.list.AbstractAdapter;

public class PVSimpleText extends AbsPVBase implements View.OnClickListener {
    protected final String tip_txt;
    protected TextView tv_title;
    protected TextView tv_content;

    protected Button btn_shenpi, btn_chexiao;

    public PVSimpleText(MainActivity a, String txt) {
        super(a);
        tip_txt = txt;
    }


    public void setTitle(String title) {
        if (tv_title != null) {
            tv_title.setText(title);
        }
    }

    @Override
    protected void createMainView(Context ctx) {
        mMainView = View.inflate(ctx, R.layout.pv_simple_text, null);
        tv_content = mMainView.findViewById(R.id.tv_content);
        tv_content.setText(tip_txt);
        tv_title = mMainView.findViewById(R.id.tv_title);
        btn_shenpi = mMainView.findViewById(R.id.btn_shenpi);
        btn_chexiao = mMainView.findViewById(R.id.btn_chexiao);

        mMainView.findViewById(R.id.iv_back).setOnClickListener(this);

        btn_shenpi.setOnClickListener(this);
        btn_chexiao.setOnClickListener(this);
        initView();
    }


    protected boolean is_loading = false;

    protected void initView(){}

    protected void shenPi() {
    }

    protected void cheXiao() {
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (R.id.iv_back == id) {
            act.getPVC().pop();
        } else if (btn_shenpi == view) {
            shenPi();
        } else if (btn_chexiao == view) {
            cheXiao();
        }
    }

}
