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
import mylib.utils.Utils;


public class PVCZBaoFei extends AbsPVCZNFC implements View.OnClickListener {

    AlertDialog alertDialog;

    public PVCZBaoFei(MainActivity a) {
        super(a);
    }

    @Override
    protected int getMainViewRes() {
        return R.layout.cz_abs_chadang;
    }


    @Override
    protected void createMainView(Context ctx) {
        super.createMainView(ctx);

        tv_title.setText("报废");
    }

    private void doIt(final GangPingBean gp) {
        act.showProgress();
        BackTask.post(new GPBackTask(act, gp) {
            @Override
            protected String getInputParam() throws Exception {
                return String.format("{\"idList\":[%d]}", gp.id);
            }

            @Override
            protected String getURL() {
                return "gangPing/baoFei";
            }

        });
    }

    @Override
    protected void gpSearched(final GangPingBean bp) {

        if (alertDialog != null){
            alertDialog.dismiss();
        }

        tv_content.setText(bp.detailString());

        if (bp.status == GangPingBean.gp_status_baofei) {
            Utils.toastSHORT("此气瓶已报废");
        } else {

            AlertDialog.Builder ab = new AlertDialog.Builder(act);

            ab.setPositiveButton("确定报废", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    doIt(bp);
                }
            });
            ab.setNegativeButton(android.R.string.cancel, null);
            alertDialog = ab.create();
            alertDialog.setMessage(bp.detailString());
            alertDialog.show();
        }

    }

    @Override
    protected void gpSearchedError() {
        if (alertDialog != null){
            alertDialog.dismiss();
        }
    }


}
