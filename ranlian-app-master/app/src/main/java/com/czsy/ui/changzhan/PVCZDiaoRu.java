package com.czsy.ui.changzhan;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.czsy.GPBackTask;
import com.czsy.android.R;
import com.czsy.ui.MainActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import mylib.app.BackTask;
import mylib.utils.Utils;


public class PVCZDiaoRu extends AbsPVCZPiLiangKt {


    public PVCZDiaoRu(MainActivity a, boolean isNext) {
        super(a, isNext);
    }

    @Override
    protected int getMainViewRes() {
        return R.layout.cz_abs_piliang;
    }


    @Override
    protected void createMainView(Context ctx) {
        super.createMainView(ctx);

        getTv_title().setText("调入");

    }

    private void doIt(final List<Long> lists) {
        act.showProgress();
        BackTask.post(new GPBackTask(act, null) {
            @Override
            protected String getURL() {
                return "gangPing/diaoRuGangPing";
            }

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
            public void runFront() {
                super.runFront();
                act.getPVC().pop();
            }

        });
    }

    @Override
    protected void subData(final List<Long> lists) {

        if (lists.size() == 0){

            Utils.toastSHORT("请扫描要调入的气瓶");
            return;
        }

        AlertDialog.Builder ab = new AlertDialog.Builder(act);
        ab.setMessage("确定将 " + lists.size() + " 个气瓶调入吗？");
        ab.setPositiveButton("确定调入", new DialogInterface.OnClickListener() {
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
