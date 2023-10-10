package com.czsy.ui.changzhan;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.czsy.GPBackTask;
import com.czsy.android.R;
import com.czsy.bean.GangPingBean;
import com.czsy.bean.LoginUser;
import com.czsy.ui.MainActivity;

import org.json.JSONObject;

import mylib.app.BackTask;
import mylib.utils.Utils;

/**
 * 更换芯片
 */
public class PVCZGengHuan extends AbsPVCZNFC implements View.OnClickListener {


    private boolean is_new = false;
    private String gp_no, xp_no_old;
    private String xp_no_new;

    private AlertDialog alertDialog1;
    private AlertDialog alertDialog2;

    public PVCZGengHuan(MainActivity a) {
        super(a);
    }

    @Override
    protected int getMainViewRes() {
        return R.layout.cz_abs_chadang;
    }

    @Override
    protected void createMainView(Context ctx) {
        super.createMainView(ctx);

        tv_title.setText("更换芯片");

    }

    @Override
    protected void gpSearched(final GangPingBean bp) {
        gp_no = bp.gangPingHao;

        tv_content.setText(bp.detailString());

        AlertDialog.Builder ab = new AlertDialog.Builder(act);
        ab.setPositiveButton("更换芯片", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.dismiss();
                is_new = true;
                xp_no_old = bp.xinPianHao;
                tv_content.setText("");
                Toast.makeText(act, "请扫描新的芯片", Toast.LENGTH_LONG).show();
            }
        });
        ab.setNegativeButton(android.R.string.cancel, null);
        alertDialog1 = ab.create();
        alertDialog1.setMessage(bp.detailString());
        alertDialog1.show();
    }

    @Override
    public void onNFCIntent(NFCInfo i) {
        if (alertDialog1 != null){
            alertDialog1.dismiss();
        }
        if (alertDialog2 != null){
            alertDialog2.dismiss();
        }

        tv_xinpian.setText("芯片号: " + i.chip_sn);
        if (!is_new) {
            doSearch(i.chip_sn, false);
        } else {

            xp_no_new = i.chip_sn;
            if (xp_no_old.equals(xp_no_new)){
                Utils.toastSHORT("芯片相同，请重新扫描");
                return;
            }

            Log.d("xing", "钢瓶号：" + gp_no + " ; 旧的芯片号：" + xp_no_old + " ; 新的芯片号：" + xp_no_new);
            doIt();
        }
    }

    private void doIt() {
        final int[] code = new int[1];
        AlertDialog.Builder ab = new AlertDialog.Builder(act);
        ab.setPositiveButton("确定更换", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                act.showProgress();
                BackTask.post(new GPBackTask(act, null) {
                    @Override
                    protected String getURL() {
                        return "gangPing/updateXinPianHao";
                    }

                    @Override
                    protected String getInputParam() throws Exception {
                        JSONObject j = new JSONObject();
                        j.put("gangPingHao", gp_no);
                        j.put("xinPianHao", xp_no_new);
                        return j.toString();
                    }
                    @Override
                    protected void parseResult(JSONObject jdata) throws Exception {
                         code[0] = jdata.getInt("code");

                    }
                    @Override
                    protected void runFront2() {
                        Utils.toast(R.string.tip_op_ok);
                        if (code[0] == 200){
                            act.getPVC().pop();
                        }
                    }
                });
            }
        });
        ab.setNegativeButton(android.R.string.cancel, null);
        alertDialog2 = ab.create();
        alertDialog2.setMessage("气瓶号：" + gp_no + "\n旧的芯片号：" + xp_no_old + "\n新的芯片号：" + xp_no_new);
        alertDialog2.show();
    }
}
