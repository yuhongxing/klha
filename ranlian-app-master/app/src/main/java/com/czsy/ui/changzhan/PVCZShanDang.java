package com.czsy.ui.changzhan;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;

import com.czsy.GPBackTask;
import com.czsy.android.R;
import com.czsy.bean.GangPingBean;
import com.czsy.ui.MainActivity;

import mylib.app.BackTask;


public class PVCZShanDang extends AbsPVCZNFC implements View.OnClickListener {

    AlertDialog alertDialog;

    public PVCZShanDang(MainActivity a) {
        super(a);
    }

    @Override
    protected int getMainViewRes() {
        return R.layout.cz_abs_chadang;
    }

    private boolean is_doing = false;

    @Override
    protected void createMainView(Context ctx) {
        super.createMainView(ctx);
        tv_title.setText("删档");

    }

    @Override
    protected void gpSearched(final GangPingBean bp) {
        if (alertDialog != null){
            alertDialog.dismiss();
        }

        tv_content.setText(bp.detailString());
        final AlertDialog.Builder ab = new AlertDialog.Builder(act);
        ab.setNegativeButton(android.R.string.cancel, null);
        ab.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                act.showProgress();
                BackTask.post(new GPBackTask(act, bp) {


                    @Override
                    protected String getURL() {
                        return "gangPing/shanDang";
                    }

                    @Override
                    protected void runFront2() {
                        super.runFront2();
                        tv_content.setText("");
                        tv_xinpian.setText("");
                        et_input.setText("");
                        et_input.setHint("输入气瓶号或扫描芯片");
                    }
                });
            }
        });

        alertDialog = ab.create();
        alertDialog.setMessage("真的要删档吗？");
        alertDialog.setTitle(R.string.app_name);
        alertDialog.show();
    }

    @Override
    protected void gpSearchedError() {
        if (alertDialog != null){
            alertDialog.dismiss();
        }
    }
}
