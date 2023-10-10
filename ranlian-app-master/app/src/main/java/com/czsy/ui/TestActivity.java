package com.czsy.ui;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.czsy.CZBackTask;
import com.czsy.Constant;
import com.czsy.Convert;
import com.czsy.NFCReader;
import com.czsy.android.R;
import com.czsy.bean.GangPingBean;

import org.json.JSONArray;
import org.json.JSONObject;

import mylib.app.BackTask;
import mylib.app.MyLog;
import mylib.utils.Utils;

public class TestActivity extends MainActivity implements View.OnClickListener {
    @Override
    public MainPVC getPVC() {
        return null;
    }

    private Button b1, b2;
    TextView tv_info;
    TextView tv_content;

    @Override
    protected EventTypes getEvents() {
        return null;
    }

    @Override
    protected void init() {
        //super.init();
        tv_info = new TextView(this);
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.addView(tv_info);
        tv_info.setText("请扫卡");
        b1 = new Button(this);
        b1.setText("空瓶变重瓶");
        b2 = new Button(this);
        b2.setText("重瓶变空瓶");
        ll.addView(b1);
        ll.addView(b2);
        tv_content = new TextView(this);
        ll.addView(tv_content);
        setContentView(ll);

        b1.setOnClickListener(this);
        b2.setOnClickListener(this);
    }


    @Override
    public void onClick(final View view) {
        if (null == gp_info) {
            Utils.toastLONG("请扫卡");
            return;
        }
        BackTask.post(new CZBackTask(this) {
            @Override
            protected void parseResult(JSONObject jdata) throws Exception {
                if (jdata.getInt("code") != 200) {
                    throw new RuntimeException(jdata.toString());
                }
            }

            @Override
            protected String getInputParam() throws Exception {
                JSONObject j = new JSONObject();
                j.put("id", gp_info.id);
                j.put("yuQiStatus", view == b1 ? "2" : "1");
                return j.toString();
            }

            @Override
            protected String getURL() {
                return "gangPing/resetYuQiStatus";
            }

            @Override
            protected void runFront2() {
                tv_info.setText("");
                gp_info = null;
                tv_content.setText("");
                Utils.toastLONG("ok");
            }
        });
    }

    public void checkNFC(boolean en) {
        if (mNfcAdapter == null) {
            return;
        }
        try {
            if (en) {
                mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
            } else {
                mNfcAdapter.disableForegroundDispatch(this);
            }
        } catch (Exception e) {
            MyLog.LOGE(e);
            Utils.toastLONG("NFC设备故障，请重启程序");
            finish();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        //super.onNewIntent(intent);
        if (null == intent) {
            return;
        }
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag == null) {
            MyLog.LOGE("bad nfc tag");
            return;
        }
        final String xpbm = NFCReader.getXPBM(tag);
        final String chip_sn = Convert.bytesToHexString(tag.getId());


        BackTask.post(new CZBackTask(this) {
            @Override
            protected void parseResult(JSONObject ret) throws Exception {
                JSONArray result = ret.getJSONArray("result");

                if (result.length() == 0) {
                    Utils.toastLONG(getString(R.string.tip_gp_not_found));
                } else {
                    gp_info = Constant.gson.fromJson(
                            result.getJSONObject(0).toString(),
                            GangPingBean.class);

                }
            }

            @Override
            protected String getInputParam() throws Exception {
                JSONArray ja = new JSONArray();
                JSONObject jo = new JSONObject();
                ja.put(jo);
                jo.put("xinPianHao", chip_sn);
                return ja.toString();
            }

            @Override
            protected String getURL() {
                return "gangPing/piLiangChaXun";
            }

            @Override
            protected void runFront2() {
                if (gp_info == null) {
                    Utils.toastLONG("没有找到气瓶");
                } else {
                    tv_info.setText("" + gp_info.id);
                    tv_content.setText(gp_info.detailString());
                }
            }
        });
    }

    @Override
    protected boolean checkUser() {

        return true;
    }

    private GangPingBean gp_info;
}
