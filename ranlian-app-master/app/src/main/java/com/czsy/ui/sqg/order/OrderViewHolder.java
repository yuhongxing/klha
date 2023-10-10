package com.czsy.ui.sqg.order;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.czsy.CZBackTask;
import com.czsy.CZNetUtils;
import com.czsy.Constant;
import com.czsy.TheApp;
import com.czsy.android.R;
import com.czsy.bean.CommonOrderBean;
import com.czsy.bean.XiaDanShangPinBean;
import com.czsy.ui.MainActivity;

import mylib.app.BackTask;
import mylib.utils.Utils;

import org.json.JSONObject;

class OrderViewHolder implements View.OnClickListener {
    CommonOrderBean order;
    final View root;
    TextView tv_ordernum, tv_order_time, tv_order_oktime, tv_kehu_num, tv_name, tv_tel, tv_address,
            tv_gyz, tv_order_money, tv_goods_money, tv_laiyuan, tv_beizhu, tv_good,
            tv_jiage, tv_num, tv_money, tv_laidianTel;
    final View btn_reject_order;//btn_call
    final TextView btn_favo;

    final PVSQG_Order pv_sqg;

    OrderViewHolder(View r, PVSQG_Order pv) {
        root = r;
        pv_sqg = pv;
        tv_ordernum = r.findViewById(R.id.tv_ordernum);
        tv_order_time = r.findViewById(R.id.tv_order_time);
        tv_order_oktime = r.findViewById(R.id.tv_order_oktime);
        tv_kehu_num = r.findViewById(R.id.tv_kehu_num);
        tv_name = r.findViewById(R.id.tv_name);
        tv_tel = r.findViewById(R.id.tv_tel);
        tv_address = r.findViewById(R.id.tv_address);
        tv_gyz = r.findViewById(R.id.tv_gyz);
        tv_order_money = r.findViewById(R.id.tv_order_money);
        tv_goods_money = r.findViewById(R.id.tv_goods_money);
        tv_laiyuan = r.findViewById(R.id.tv_laiyuan);
        tv_beizhu = r.findViewById(R.id.tv_beizhu);
        tv_good = r.findViewById(R.id.tv_good);
        tv_jiage = r.findViewById(R.id.tv_jiage);
        tv_num = r.findViewById(R.id.tv_num);
        tv_money = r.findViewById(R.id.tv_money);
        tv_laidianTel = r.findViewById(R.id.tv_laidianTel);
        btn_favo = r.findViewById(R.id.btn_favo);
        btn_favo.setOnClickListener(this);
        btn_reject_order = r.findViewById(R.id.btn_reject_order);
        btn_reject_order.setOnClickListener(this);

        r.setTag(this);
    }

    void bind(CommonOrderBean o) {
        order = o;
        tv_ordernum.setText("订  单  号：" + o.dingDanHao);
        tv_order_time.setText("下单时间：" + o.chuangJianRiQi);
        if (o.status == 6) {
            tv_order_oktime.setText("完成时间：" + o.wanChengRiQi);
            tv_order_oktime.setVisibility(View.VISIBLE);
        } else {
            tv_order_oktime.setVisibility(View.GONE);
        }

        tv_kehu_num.setText("客户编号：" + o.keHuBianHao);
        tv_name.setText("客        户：" + o.keHuMing);
        tv_tel.setText("");
        Utils.setTextSpan("电        话：", o.keHuDianHua, "", tv_tel, new Utils.TextClickListener() {
            @Override
            public void onClick() {
                Utils.toCall(order.keHuDianHua);
            }
        });

        tv_address.setText("地        址：" + o.diZhi);
        tv_gyz.setText("供  应  站：" + o.gongYingZhan);
        tv_order_money.setText("订单金额：" + o.shiShouJinE);//yingShouJinE
        tv_goods_money.setText("商品金额：" + o.shangPinJinE);
        tv_laiyuan.setText("来        源：" + (o.laiYuan == 3 ? o.laiYuanName + "(" + o.zhiFuFangShiName + ")" : o.laiYuanName));
        if (o.callerPhone != null && o.callerPhone.length() > 0) {
            tv_laidianTel.setVisibility(View.VISIBLE);

            tv_laidianTel.setText("");
            Utils.setTextSpan("来电号码：", o.callerPhone, "", tv_laidianTel, new Utils.TextClickListener() {
                @Override
                public void onClick() {
                    Utils.toCall(order.callerPhone);
                }
            });
        } else {
            tv_laidianTel.setVisibility(View.GONE);
        }
        tv_beizhu.setText("备        注：" + o.beiZhu);

        String good = "", jiage = "", num = "", money = "";
        for (CommonOrderBean.DetailInfo detailInfo : o.details) {
            good = good + detailInfo.shangPingMingCheng + "\n";
            jiage = jiage + detailInfo.price + "\n";
            num = num + detailInfo.count + "\n";
            money = money + detailInfo.zongJinE + "\n";

        }
        tv_good.setText(good);
        tv_jiage.setText(jiage);
        tv_num.setText(num);
        tv_money.setText(money);
        if (Constant.status_done == (o.status)) {
            btn_reject_order.setVisibility(View.GONE);
//            btn_call.setVisibility(View.GONE);
            btn_favo.setVisibility(View.GONE);
        } else {
            btn_reject_order.setVisibility(View.VISIBLE);
//            btn_call.setVisibility(View.VISIBLE);
        }
        if (pv_sqg.fav_orders.contains(o.dingDanHao)) {
            btn_favo.setText(R.string.tip_remove_favo);
        } else {
            btn_favo.setText(R.string.tip_add_favo);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (R.id.btn_reject_order == id) {
            Context ctx = view.getContext();
            final Dialog d = new Dialog(ctx, android.R.style.Theme_Translucent_NoTitleBar);
            d.setContentView(R.layout.dialog_input);
            final EditText et_input = d.findViewById(R.id.et_input);
            d.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String reason = et_input.getText().toString();
                    if (TextUtils.isEmpty(reason)) {
                        Utils.toastLONG("请输入退单原因");
                        return;
                    }
                    final MainActivity act = (MainActivity) pv_sqg.act;
                    act.showProgress();
                    BackTask.post(new CZBackTask(act) {
                        @Override
                        protected void parseResult(JSONObject jdata) throws Exception {
                            int code = jdata.optInt("code", 0);
                            if (code != 200) {
                                throw new CZNetUtils.CZNetErr(code, jdata);
                            }
                        }

                        @Override
                        protected String getInputParam() throws Exception {
                            JSONObject j = new JSONObject();
                            j.put("sid", order.dingDanHao);
                            j.put("yuanYin", reason);
                            return j.toString();
                        }

                        @Override
                        protected String getURL() {
                            return "dingDan/tuiDan";
                        }

                        @Override
                        protected void runFront2() {
                            d.dismiss();
                            Utils.toastLONG("退单成功！");
                            pv_sqg.onTuiDan(order);
                        }
                    });
                }
            });
            d.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    d.dismiss();
                }
            });
            d.show();

        } else if (R.id.btn_favo == id) {
            if (Constant.status_done == (order.status)) {
                return;
            }
            pv_sqg.favoClick(order, btn_favo);
        }
    }
}
