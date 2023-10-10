package com.czsy.ui.changzhan;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import com.czsy.GPBackTask;
import com.czsy.android.R;
import com.czsy.ui.MainActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import mylib.app.BackTask;
import mylib.utils.Utils;


public class PVCZSongJian extends AbsPVCZPiLiangKt {

    public PVCZSongJian(MainActivity a, boolean isNext) {
        super(a, isNext);
    }

    @Override
    protected int getMainViewRes() {
        return R.layout.cz_abs_piliang;
    }

    @Override
    protected void subData(final List<Long> lists) {

        if (lists.size()==0){
            Utils.toastSHORT("请扫描要送检的气瓶");
            return;
        }

        AlertDialog.Builder ab = new AlertDialog.Builder(act);
        ab.setMessage("确定将 " + lists.size() + " 个气瓶送检吗？");
        ab.setPositiveButton("确定送检", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                doIt(lists);
            }
        });
        ab.setNegativeButton("取消", null);
        ab.setNegativeButton(android.R.string.cancel, null);
        ab.show();
    }


    @Override
    protected void createMainView(Context ctx) {
        super.createMainView(ctx);

        getTv_title().setText("送检");
        getTv_xinpian().setVisibility(View.GONE);
    }

    private void doIt(final List<Long> lists) {
        act.showProgress();
        BackTask.post(new GPBackTask(act, null) {
            @Override
            protected String getInputParam() throws Exception {

                JSONObject j = new JSONObject();
                JSONArray array = new JSONArray();
                for (long id : lists){
                    array.put(id);
                }
                j.put("idList",array);
                return j.toString();
            }

            @Override
            protected String getURL() {
                return "gangPing/chuKuNianJian";
            }

            @Override
            public void runFront() {
                super.runFront();
                act.getPVC().pop();
            }
        });
    }




}
