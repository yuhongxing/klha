package com.czsy.ui.changzhan;

import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.czsy.GPBackTask;
import com.czsy.android.R;
import com.czsy.bean.GangPingBean;
import com.czsy.ui.MainActivity;

import org.json.JSONObject;

import mylib.app.BackTask;
import mylib.utils.Utils;


public class PVCZJianYan extends AbsPVCZNFC implements View.OnClickListener {

    public PVCZJianYan(MainActivity a) {
        super(a);
    }

    AlertDialog alertDialog;

    @Override
    protected int getMainViewRes() {
        return R.layout.cz_abs_chadang;
    }


    @Override
    protected void createMainView(Context ctx) {
        super.createMainView(ctx);

        tv_title.setText("查档");
    }

    private void doIt(final GangPingBean gp) {
        act.showProgress();
        BackTask.post(new GPBackTask(act, gp) {
            @Override
            protected String getURL() {
                return "gangPing/checkGangPing";
            }

            String msg;

            @Override
            protected void parseResult(JSONObject jdata) throws Exception {
                super.parseResult(jdata);
                msg = jdata.optString("message");
                if ("null".equals(msg) || TextUtils.isEmpty(msg)) {
                    msg = null;
                    JSONObject j = jdata.getJSONObject("result");
                    boolean ok = j.optBoolean("ok", true);
                    if (!ok) {

                        err_msg = j.optString("msg");
                    }
                }
            }

            @Override
            public void runFront() {
                if (act != null) {
                    act.hideProgress();
                    if (act.isFinishing()) {
                        return;
                    }
                }
                if (!TextUtils.isEmpty(err_msg)) {
                    AlertDialog.Builder ab = new AlertDialog.Builder(act);
                    ab.setPositiveButton(android.R.string.ok, null);
                    if (alertDialog == null){
                        alertDialog = ab.create();
                    }

                    alertDialog.setMessage(err_msg);
                    alertDialog.setCancelable(true);

                    alertDialog.show();
                    // Utils.toast(err_msg);
                } else if (!TextUtils.isEmpty(msg)) {
                    Utils.toastLONG(msg);
                } else {
                    runFront2();
                }
                is_searching = false;
            }

        });
    }

    @Override
    protected void gpSearched(final GangPingBean bp) {
        tv_content.setText(bp.detailString());
        is_searching = true;
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        doIt(bp);

    }


}
