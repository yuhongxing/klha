package com.czsy.ui.sqg;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.czsy.CZBackTask;
import com.czsy.CZNetUtils;
import com.czsy.Constant;
import com.czsy.INFCHandler;
import com.czsy.ReturnRunable;
import com.czsy.android.R;
import com.czsy.bean.ChongZhiKaBean;
import com.czsy.bean.ClientBean;
import com.czsy.bean.PayMethodBean;
import com.czsy.ui.AbsPVBase;
import com.czsy.ui.MainActivity;
import com.czsy.ui.PayMethodObject;
import com.czsy.ui.fingerpaint.SignDialog;
import com.czsy.ui.sqg.order.PVWeiXin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import mylib.app.BackFrontTask;
import mylib.app.BackTask;
import mylib.app.MyLog;
import mylib.utils.Utils;

/**
 * 客户充值
 */
public class PVCharge extends AbsPVBase implements INFCHandler, View.OnClickListener {

    final ClientBean client_bean;
    View btn_ok;
    TextView tv_nfc, tv_tip;
    //RadioButton rb_wei_xin, rb_xin_jin;
    PayMethodObject pay_obj;


    public PVCharge(MainActivity a, ClientBean b) {
        super(a);
        client_bean = b;
    }

    NFCInfo info;

    @Override
    public void onNFCIntent(NFCInfo i) {

        info = i;
        // Utils.toast("读取到充值卡信息！");
        btn_ok.setVisibility(View.VISIBLE);
        tv_nfc.setText("充值卡: " + i.chip_sn);

        // 卡
        pay_obj.onNFCIntent(i, client_bean.id, act, new ReturnRunable<ChongZhiKaBean>() {
            @Override
            public void run() {
                ChongZhiKaBean czk = this.ret;
                if (czk != null) {
                    tv_nfc.setText(
                            String.format("充值卡: %s, 余额: %.2f, 状态: %s",
                                    info.chip_sn, czk.yuE, czk.statusName));
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (R.id.iv_back == id || R.id.btn_cancel == id) {
            act.getPVC().pop();
            return;
        } else if (R.id.btn_ok == id) {
            goChongzhi();
        }
    }

    EditText et_pass,et_pass_czk;
    EditText et_money;

    @Override
    protected void createMainView(Context ctx) {
        mMainView = View.inflate(act, R.layout.dialog_charge, null);
        mMainView.findViewById(R.id.btn_ok).setOnClickListener(this);
        mMainView.findViewById(R.id.btn_cancel).setOnClickListener(this);
        mMainView.findViewById(R.id.iv_back).setOnClickListener(this);
        tv_nfc = mMainView.findViewById(R.id.tv_nfc);
        tv_nfc.setText("请靠近充值卡");

        //rb_wei_xin = mMainView.findViewById(R.id.rb_wei_xin);
        //rb_xin_jin = mMainView.findViewById(R.id.rb_xin_jin);
        pay_obj = new PayMethodObject(mMainView.findViewById(R.id.pay_type_container),false) {
            protected boolean isCZKChecked() {
                return true; //
            }
        };
        pay_obj.rb_chong_zhi_ka.setVisibility(View.GONE);
        et_pass = mMainView.findViewById(R.id.et_pass);
        et_pass_czk = mMainView.findViewById(R.id.et_pass_czk);
        et_money = mMainView.findViewById(R.id.et_money);
        btn_ok = mMainView.findViewById(R.id.btn_ok);
        tv_tip = mMainView.findViewById(R.id.tv_tip);

        TextView tv_client_info = mMainView.findViewById(R.id.tv_client_info);
        tv_client_info.setText(String.format("客户：%s (%s)\n地址: %s",
                client_bean.userName, client_bean.telNum, client_bean.getDiZhi()));
    }

    @Override
    public void onAttach(boolean firstShow) {
        super.onAttach(firstShow);
        if (firstShow) {
            // 优惠信息
            BackTask.post(new CZBackTask(act) {
                String tip = null;
                String msg = "该用户不享有充值活动";

                @Override
                protected void parseResult(JSONObject jdata) throws Exception {
                    JSONArray j_res = jdata.getJSONArray("result");
                    StringBuffer sb = new StringBuffer("充值优惠！\n");
                    for (int i = 0; i < j_res.length(); i++) {
                        JSONObject j = j_res.getJSONObject(i);
                        String guiZeMingCheng = j.getString("guiZeMingCheng");
                        String guoQiShiJian = j.getString("guoQiShiJian");
                        int chongZhiJinE = j.getInt("chongZhiJinE");
                        int zengSongJinE = j.getInt("zengSongJinE");
                        sb.append(String.format("%d: %s\n    充 %d 元赠 %d 元 (有效期至：%s)\n", i + 1, guiZeMingCheng, chongZhiJinE, zengSongJinE, guoQiShiJian));
                    }
                    if (j_res.length() != 0) {
                        tip = sb.toString();
                    }
                }

                @Override
                protected String getInputParam() throws Exception {
                    return "{\"id\":\"" + client_bean.id + "\"}";
                }

                @Override
                protected String getURL() {
//                    return "chongZhiKa/getChongZhiHuoDongList";
                    return "chongZhiZengSong/keYongChaXun";
                }

                @Override
                protected void runFront2() {
                    if (tip != null) {
                        tv_tip.setText(tip);

                    } else {
                        AlertDialog.Builder d = new AlertDialog.Builder(act);
                        d.setTitle(msg);
                        d.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        d.show();
                    }
                }
            });
        }
    }

    private void goChongzhi() {
        final Bitmap[] bmp = new Bitmap[1];
        if (info == null) {
            Utils.toastLONG("未读取到充值卡！");
            return;
        }

        String pass = et_pass.getText().toString().trim();
        String pass_czk = et_pass_czk.getText().toString().trim();
        if (TextUtils.isEmpty(pass)) {
            et_pass.requestFocus();
            Utils.toastSHORT("请输入配送员密码");
            return;
        }

        if (TextUtils.isEmpty(pass_czk)) {
            et_pass_czk.requestFocus();
            Utils.toastSHORT("请输入充值卡密码");
            return;
        }

        int money = -1;
        try {
            money = Integer.valueOf(et_money.getText().toString().trim());
        } catch (Exception e) {
            money = -1;
        }
        if (money <= 0) {
            et_money.requestFocus();
            Utils.toastLONG("请输入金额");
            return;
        }
        final PayMethodBean fpb = pay_obj.getPayMethod();
        if (fpb == null) {
            Utils.toastLONG("请输充值类型");
            return;
        }
        final String f_pass = pass;
        final String f_pass_czk = pass_czk;
        final int f_money = money;

        BackTask.post(new CZBackTask(act) {
            @Override
            protected void parseResult(JSONObject jdata) throws Exception {

            }

            @Override
            protected String getInputParam() throws Exception {
                JSONObject j = new JSONObject();
                // 充值
                try {
                    j.put("kaHao", info.chip_sn);
                    j.put("chongZhiJinE", f_money);
                    j.put("password", f_pass);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return j.toString();
            }

            @Override
            protected String getURL() {
                return "chongZhiKa/chongZhiBefor";
            }

            @Override
            protected void runFront2() {

                final SignDialog d = new SignDialog(act);
                d.setClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int id = view.getId();
                        if (id == R.id.iv_back) {
                            d.dismiss();
                        } else if (R.id.btn_ok == id) {
                            d.dismiss();
                            bmp[0] = d.finger_view.getBitmap();
                            Log.d("sign", "是否签字了" + d.finger_view.isSign());

                            if (d.finger_view.isSign()) {

                                act.showProgress();
                                BackTask.post(new BackFrontTask() {
                                    @Override
                                    public void runFront() {
                                        act.hideProgress();
                                        if (!TextUtils.isEmpty(msg)) {
                                            Utils.toastLONG(msg);
                                            return;
                                        } else { // ok


                                            if (fpb == PayMethodBean.xian_jin || fpb == PayMethodBean.yin_hang_ka) {
                                                Utils.toastLONG("充值成功！");
                                            } else if (fpb == PayMethodBean.wei_xin) {
                                                act.getPVC().push(new PVWeiXin(
                                                        act, wx_sid, wx_url, PVWeiXin.from_charge));
                                                return;
                                            }
                                            //
                                            act.getPVC().popTo1();
                                        }
                                    }

                                    String msg, wx_sid, wx_url;

                                    @Override
                                    public void runBack() {
                                        try {
                                            String bmp_url = CZNetUtils.upload(bmp[0]);

                                            JSONObject j = new JSONObject();
                                            // 充值
                                            j.put("kaHao", info.chip_sn);
                                            j.put("chongZhiJinE", f_money);
                                            j.put("password", f_pass);
                                            j.put("cardPassword", f_pass_czk);
                                            j.put("keHuId", client_bean.id);
                                            j.put("zhiFuFangShi", fpb.id);
                                            j.put("sign", bmp_url);//签字图片
                                            JSONObject ret = CZNetUtils.postCZHttp("chongZhiKa/chongZhi", j.toString());

                                            int code = ret.optInt("code", -1);
                                            if (code != CZNetUtils.code_ok) {
                                                msg = ret.optString("message");
                                                if (TextUtils.isEmpty(msg)) {
                                                    msg = act.getString(R.string.tip_common_err);
                                                }
                                                return;
                                            }
                                            JSONObject j_res = ret.getJSONObject("result");
                                            wx_sid = j_res.getString("sid");
                                            wx_url = j_res.getString("weiXinPayCodeUrl");
                                            // parse ...
                                            msg = null;

                                        } catch (Exception e) {
                                            MyLog.LOGE(e);
                                            msg = act.getString(R.string.tip_common_err);
                                        }
                                    }
                                });

                            } else {
                                Utils.toastSHORT("客户没有签字");
                            }
                        } else if (R.id.btn_reset == id) {
                            d.finger_view.clear();
                        }

                    }

                });
                d.show();

            }
        });


    }

}
