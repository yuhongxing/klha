package com.czsy.ui.changzhan;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.czsy.CZBackTask;
import com.czsy.Constant;
import com.czsy.INFCHandler;
import com.czsy.android.R;
import com.czsy.bean.GangPingBean;
import com.czsy.ui.AbsPVBase;
import com.czsy.ui.MainActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import mylib.app.BackTask;
import mylib.utils.FileUtils;


abstract public class AbsPVCZNFC extends AbsPVBase implements INFCHandler, View.OnClickListener {
    public AbsPVCZNFC(MainActivity a) {
        super(a);

    }

    protected TextView tv_content;
    protected TextView tv_xinpian;
    protected EditText et_input;
    protected TextView tv_title;
    protected String xinPianHao;

    protected abstract int getMainViewRes();

    @Override
    protected void createMainView(Context ctx) {

        mMainView = View.inflate(ctx, getMainViewRes(), null);
        tv_xinpian = mMainView.findViewById(R.id.tv_xinpian);
        et_input = mMainView.findViewById(R.id.et_input);
        tv_title = mMainView.findViewById(R.id.tv_title);
        mMainView.findViewById(R.id.iv_back).setOnClickListener(this);
        mMainView.findViewById(R.id.btn_search).setOnClickListener(this);
        tv_content = mMainView.findViewById(R.id.tv_content);

        PVCZJianDang.CZJianDangYuZhi yuzhi_info = PVCZJianDang.getYuZhiInfo();
//        et_input.setText(yuzhi_info.prefix);
        et_input.setHint("输入气瓶号或扫描芯片");
    }

    protected boolean is_searching = false;

    protected void doSearch(final String no, final boolean is_gp_no) {
        if (is_searching) {
            return;
        }

        is_searching = true;
        act.showProgress();
        // search gangping by nfc
        BackTask.post(new CZBackTask(act) {
            GangPingBean ret_gp;

            @Override
            protected void parseResult(JSONObject jdata) throws Exception {

                JSONArray ja = jdata.getJSONArray("result");
                if (ja.length() == 1) {
                    ret_gp = Constant.gson.fromJson(ja.getJSONObject(0).toString(), GangPingBean.class);
                    return;
                }
                ret_gp = null;
                err_msg = "没有找到气瓶!";
                gpSearchedError();
                throw new IOException();
            }

            @Override
            protected String getInputParam() throws Exception {
                JSONObject j = new JSONObject();
                if (is_gp_no) {
                    j.put("gangPingHao", no);
                } else {
                    j.put("xinPianHao", no);
                }
                return j.toString();
            }

            @Override
            protected String getURL() {
                return "gangPing/chaXun/1000/1";
            }

            @Override
            public void runFront() {
                is_searching = false;
                if (ret_gp == null) {
                    if (tv_xinpian != null) {
                        tv_xinpian.setText(null);
                    }
                    if (tv_content != null) {
                        tv_content.setText(null);
                    }
                }
                super.runFront();
            }

            @Override
            protected void runFront2() {
                // update
                if (ret_gp != null) {
                    gpSearched(ret_gp);
                    if (is_gp_no){
                        tv_xinpian.setText("");
                    }else {
                        et_input.setText("");
                        et_input.setHint("输入气瓶号或扫描芯片");
                    }
                }
            }
        });

    }

    @Override
    public void onNFCIntent(NFCInfo i) {
        xinPianHao = i.chip_sn;
        tv_xinpian.setText("芯片号: " + i.chip_sn);
        doSearch(i.chip_sn, false);
    }

    protected abstract void gpSearched(final GangPingBean bp);

    protected void gpSearchedError() { }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (R.id.iv_back == id) {
            act.getPVC().pop();
        } else if (id == R.id.btn_search) {
            final String no = et_input.getText().toString().trim();
            if (TextUtils.isEmpty(no)) {
                et_input.requestFocus();
                return;
            }
            doSearch(no, true);
        }
    }

}
