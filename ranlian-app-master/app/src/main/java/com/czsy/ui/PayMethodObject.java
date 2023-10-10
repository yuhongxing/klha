package com.czsy.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.czsy.CZBackTask;
import com.czsy.Constant;
import com.czsy.INFCHandler;
import com.czsy.ReturnRunable;
import com.czsy.android.R;
import com.czsy.bean.ChongZhiKaBean;
import com.czsy.bean.PayMethodBean;

import mylib.app.BackTask;
import mylib.app.BaseActivity;
import mylib.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

public class PayMethodObject { //implements INFCHandler {
    public final RadioButton rb_xin_jin, rb_wei_xin, rb_chong_zhi_ka, rb_qiankuan, rb_jifen, rb_yinHangKa;
    public final View pay_type_container;
    public String czk_id;
    public String nfc_id;
    String password;
    public TextView tv_czk;
    boolean isShowDialog;

    /**
     * @param mMainView
     * @param isShowDialog 是否显示 输入充值卡密码弹框；（结算时，选择充值卡支付，需要弹框；充值时 不需要）
     */
    public PayMethodObject(View mMainView, boolean isShowDialog) {
        this.isShowDialog = isShowDialog;
        pay_type_container = mMainView;
        CompoundButton.OnCheckedChangeListener li = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton != rb_chong_zhi_ka) {
                    czk_id = null;
                    tv_czk.setText("");
                    return;
                }
                if (b) {
                    Utils.toastLONG("请扫描充值卡");
                }
            }
        };

        rb_xin_jin = mMainView.findViewById(R.id.rb_xin_jin);
        rb_wei_xin = mMainView.findViewById(R.id.rb_wei_xin);
        rb_chong_zhi_ka = mMainView.findViewById(R.id.rb_chong_zhi_ka);
        rb_qiankuan = mMainView.findViewById(R.id.rb_qiankuan);
        rb_jifen = mMainView.findViewById(R.id.rb_jifen);
        rb_yinHangKa = mMainView.findViewById(R.id.rb_yinHangKa);

        rb_wei_xin.setOnCheckedChangeListener(li);
        rb_xin_jin.setOnCheckedChangeListener(li);
        rb_chong_zhi_ka.setOnCheckedChangeListener(li);
        rb_qiankuan.setOnCheckedChangeListener(li);
        rb_jifen.setOnCheckedChangeListener(li);
        rb_yinHangKa.setOnCheckedChangeListener(li);

        tv_czk = mMainView.findViewById(R.id.tv_czk);
    }

    public PayMethodBean getPayMethod() {
        PayMethodBean pb = null;
        if (rb_chong_zhi_ka.isChecked()) {
            pb = PayMethodBean.chong_zhi_ka;
        } else if (rb_qiankuan.isChecked()) {
            pb = PayMethodBean.qian_kuang;
        } else if (rb_wei_xin.isChecked()) {
            pb = PayMethodBean.wei_xin;
        } else if (rb_xin_jin.isChecked()) {
            pb = PayMethodBean.xian_jin;
        } else if (rb_jifen.isChecked()) {
            pb = PayMethodBean.ji_fen;
        } else if (rb_yinHangKa.isChecked()) {
            pb = PayMethodBean.yin_hang_ka;
        } else {
            Utils.toastLONG("请选择支付方式");
        }
        return pb;
    }

    protected boolean isCZKChecked() {
        return rb_chong_zhi_ka.isChecked();
    }

    //@Override
    public void onNFCIntent(INFCHandler.NFCInfo data, final Long kehuId,
                            final BaseActivity act,
                            final ReturnRunable<ChongZhiKaBean> task) {
        if (!isCZKChecked()) {
            return;
        }
        nfc_id = data.chip_sn;

        // 卡
        BackTask.post(new CZBackTask(act) {
            //String tip = null;
            ChongZhiKaBean czk;
//            String title = "";

            @Override
            protected void parseResult(JSONObject jdata) throws Exception {

//                title = jdata.getString("message");
                JSONArray ja = jdata.optJSONArray("result");
                if (ja == null) {
                    //tip = "找不到充值卡信息";
                } else if (ja.length() == 0) {
                    czk = new ChongZhiKaBean();
                    czk.kaHao = nfc_id;
                } else {
                    czk = Constant.gson.fromJson(
                            ja.getJSONObject(0).toString(),
                            ChongZhiKaBean.class);
                }
            }

            @Override
            protected String getInputParam() throws Exception {
                JSONObject j = new JSONObject();
                j.put("kaHao", nfc_id);
                j.put("keHuId", kehuId);
                return j.toString();
            }

            @Override
            protected String getURL() {
                return "chongZhiKa/chaXunChuZhiKa/1/1";
            }

            @Override
            protected void runFront2() {


                czk_id = czk == null ? null : czk.id;//.chip_sn;
                if (tv_czk != null) {

                    if (isShowDialog) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(act);
                        builder.setTitle("充值卡");
                        final EditText et = new EditText(act);
                        et.setHint("请输入密码");
                        et.setSingleLine(true);
                        et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        builder.setView(et);
                        builder.setNegativeButton("取消", null);
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                password = et.getText().toString();

                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }

                    tv_czk.setText(czk == null ? null : String.format("充值卡余额: %.2f", czk.yuE));
                }
                if (czk == null) {
                    Utils.toastLONG("充值卡不正确！");
                }
                if (task != null) {
                    task.ret = czk;
                    task.run();
                }

            }
        });
    }

    public void preparePayParameter(JSONObject j) throws Exception {
        PayMethodBean pb = getPayMethod();
        j.put("zhiFuFangShi", pb.id);
        if (pb == PayMethodBean.chong_zhi_ka) {
            j.put("chongZhiKaXinPianHao", czk_id);
            j.put("chuZhiKaId", czk_id);
            j.put("cardPassword", password);
        }

    }
}
