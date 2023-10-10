package com.czsy.ui.yunshuyuan;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import com.czsy.ReturnRunable;
import com.czsy.android.R;
import com.czsy.bean.CommonOrderBean;
import com.czsy.bean.GangPingBean;
import com.czsy.ui.AbsPVScanEmpty;
import com.czsy.ui.MainActivity;

import java.util.LinkedList;
import java.util.List;

import mylib.utils.Utils;

//  yunshuyuan 扫瓶
abstract public class AbsYSYPVScan extends AbsPVScanEmpty {


    public AbsYSYPVScan(MainActivity a) {
        super(a, null, false);
    }

    @Override
    protected void createMainView(Context ctx) {
        super.createMainView(ctx);
        View v = mMainView.findViewById(R.id.bottom_container);
        v.setVisibility(View.GONE);
        btn_next.setText(android.R.string.ok);
        tv_title.setText(R.string.title_scan_ping);
    }

    @Override
    protected void onClickScanHeavy() {
        final int size = gp_id_list.size() + nfc_gp_list.size();
        if (size == 0) {
            Utils.toastLONG("没有输入气瓶");
            return;
        }
        // 出库或入库
        getIdFromServer(new ReturnRunable<Boolean>() {
            @Override
            public void run() {
                if (!ret) {
                    return;
                }

                AlertDialog.Builder ab = new AlertDialog.Builder(act);
                ab.setTitle(R.string.app_name);
                ab.setMessage("输入了" + size + "个气瓶，确定提交？");
                ab.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        doIt();
                    }
                });
                ab.setNegativeButton(android.R.string.cancel, null);
                ab.show();
            }
        });
    }

    @Override
    protected void onClickNoEmpty() {
        throw new RuntimeException();
    }

    private void doIt() {
        List<GangPingBean> list = new LinkedList<>();
        for (CommonOrderBean.GangPingInfo info : nfc_gp_list) {
            list.add(info.gp_bean);
        }
        for (CommonOrderBean.GangPingInfo info : gp_id_list) {
            list.add(info.gp_bean);
        }
        onGP(list);
    }

    protected abstract void onGP(final List<GangPingBean> list);
}
