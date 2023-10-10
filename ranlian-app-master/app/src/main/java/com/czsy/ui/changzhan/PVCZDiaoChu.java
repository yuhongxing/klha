package com.czsy.ui.changzhan;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.czsy.GPBackTask;
import com.czsy.android.R;
import com.czsy.ui.MainActivity;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import mylib.app.BackTask;
import mylib.utils.Utils;


public class PVCZDiaoChu extends AbsPVCZPiLiangKt {


    public PVCZDiaoChu(@Nullable MainActivity a, boolean _isNext) {
        super(a, _isNext);
    }

    @Override
    protected int getMainViewRes() {
        return R.layout.cz_abs_piliang;
    }


    @Override
    protected void createMainView(Context ctx) {
        super.createMainView(ctx);

        getTv_title().setText("调出");

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
                return "gangPing/diaoChuGangPing";
            }

            @Override
            public void runFront() {
                super.runFront();
                act.getPVC().pop();
            }
        });
    }

    @Override
    protected void subData(final List<Long> lists) {
        if (lists.size() == 0){

            Utils.toastSHORT("请扫描要调出的气瓶");
            return;
        }

        AlertDialog.Builder ab = new AlertDialog.Builder(act);
        ab.setMessage("确定将 " + lists.size() + " 个气瓶调出吗？");
        ab.setPositiveButton("确定调出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                doIt(lists);
                Log.d("xing", lists.toString());
            }
        });
        ab.setNegativeButton("取消", null);
        ab.setNegativeButton(android.R.string.cancel, null);
        ab.show();
    }


}
