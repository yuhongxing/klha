package com.czsy.ui.sqg.order;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.czsy.CZNetUtils;
import com.czsy.Constant;
import com.czsy.QRCodeUtil;
import com.czsy.TheApp;
import com.czsy.android.R;
import com.czsy.ui.AbsPVBase;
import com.czsy.ui.MainActivity;
import com.czsy.ui.sqg.PVSQGApp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.zip.Inflater;

import mylib.app.BackFrontTask;
import mylib.app.BackTask;
import mylib.app.MyLog;
import mylib.ui.AbstractPageView;
import mylib.utils.Global;
import mylib.utils.Utils;

public class PVWeiXin extends AbsPVBase implements Runnable {
    public final static int from_charge = 1;
    public final static int from_order = 2;

    final String url, sid;
    private ImageView iv;
    private Button button;
    final Handler handler;
    private final int size;
    //final ReturnRunable<Boolean> call_back;
    final int from;

    public PVWeiXin(MainActivity a, String sid, String url, int from) { //} CommonOrderBean order_bean) {
        super(a);
        this.from = from;
        handler = TheApp.sInst.getBackHandler();
        this.url = url;
        this.sid = sid;
        size = Global.sWidth * 3 / 4;
    }

    @Override
    protected void createMainView(Context ctx) {
        mMainView = View.inflate(act,R.layout.layout_weixincode,null);
        iv = mMainView.findViewById(R.id.imageView8);
        button = mMainView.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelOrder(sid);
            }
        });
    }

    @Override
    public void onAttach(boolean firstShow) {
        super.onAttach(firstShow);
        if (firstShow) {
            iv.setImageBitmap(QRCodeUtil.createQRCodeBitmap(
                    url //"weixin://wxpay/bizpayurl?pr=7Z3iQaa"
                    , size, size));
            handler.postDelayed(this, delay_time);
        }
    }

    @Override
    public void onDetach(boolean lastShow) {
        super.onDetach(lastShow);
        if (lastShow) {
            handler.removeCallbacks(this);
        }
    }

    @Override
    public void run() {
        handler.removeCallbacks(this);
        try {
            String the_url = from == from_order ? "dingDan/checkPayStatus" : "chongZhiKa/checkPayStatus";
            JSONObject j = CZNetUtils.postCZHttp(the_url,
                    "{\"sid\":\"" + sid + "\"}");
            JSONObject j_res = j.getJSONObject("result");
            int status = j_res.getInt("status");
            final String msg = j_res.optString("msg");
            if (status == Constant.wx_pay_ok) {
                button.setVisibility(View.GONE);
                // "status":2,"msg":"","kaYuE":2.0,"zongJinE":2.0,"zhiFuJinE":2.0,"zengSongJinE":0.0}
                final double kaYuE = j_res.optDouble("kaYuE");
                final double zongJinE = j_res.optDouble("zongJinE");
                final double zhiFuJine = j_res.optDouble("zhiFuJinE");
                final double zengSongJinE = j_res.optDouble("zengSongJinE");
                String tip;
                if (from == from_charge) {

                    tip = String.format("微信支付成功：\n" +
                            "本次支付金额: %.2f\n" +
                            "本次赠送金额: %.2f\n" +
                            "本次充值金额: %.2f\n" +
                            "卡余额: %.2f", zhiFuJine, zengSongJinE, zongJinE, kaYuE);

                } else {
                    tip = String.format("微信支付成功：\n" +
                            "本次支付金额: %.2f\n", zhiFuJine);

                }
                final String finalTip = tip;
                TheApp.sHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        AlertDialog.Builder ab = new AlertDialog.Builder(act);
                        ab.setMessage(finalTip).setTitle(R.string.app_name)
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        act.getPVC().popTo1();

                                    }
                                }).show();
                    }
                });
                return;
            } else if (status == Constant.wx_pay_err) {
                TheApp.sHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Utils.toastLONG(TextUtils.isEmpty(msg) ? "微信支付失败！" : msg);
                        act.getPVC().popMe(PVWeiXin.this);
                    }
                });
                return;
            }
            handler.postDelayed(this, delay_time);
        } catch (Exception e) {
            MyLog.LOGE(e);
            TheApp.sHandler.post(new Runnable() {
                @Override
                public void run() {
                    Utils.toastLONG("微信支付失败！");
                    act.getPVC().popMe(PVWeiXin.this);
                }
            });
        }
    }

    private final static long delay_time = 3000;

    @Override
    public boolean doBackPressed() {
        AlertDialog.Builder ab = new AlertDialog.Builder(act);
        ab.setTitle(R.string.app_name).setMessage("确定要取消微信支付吗？")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        act.getPVC().popTo1();
                    }
                }).setNegativeButton(android.R.string.cancel, null).show();
        return true;
    }

    //取消订单
    private void cancelOrder(final String sid){
        BackTask.post(new BackFrontTask() {
            JSONObject jsonObject;
            @Override
            public void runFront() {
                try {
                    if (jsonObject!=null && jsonObject.getInt("code") == 200){
                        Utils.toastSHORT("取消成功");
                        act.getPVC().popTo1();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void runBack() {
                try {
                    jsonObject = CZNetUtils.postCZHttp("dingDan/cancel","{\"sid\":\"" + sid + "\"}");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
