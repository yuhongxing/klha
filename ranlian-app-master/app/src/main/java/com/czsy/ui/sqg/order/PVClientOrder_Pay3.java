package com.czsy.ui.sqg.order;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.czsy.CZBackTask;
import com.czsy.CZNetUtils;
import com.czsy.Constant;
import com.czsy.INFCHandler;
import com.czsy.android.R;
import com.czsy.bean.CommonOrderBean;
import com.czsy.bean.PayMethodBean;
import com.czsy.bean.YaJinDanBean;
import com.czsy.bean.YuJieSuanBean;
import com.czsy.print.PrintManager;
import com.czsy.ui.MainActivity;
import com.czsy.ui.PayMethodObject;
import com.czsy.ui.sqg.AbsPVGuestOrder;

import mylib.app.BackFrontTask;
import mylib.app.BackTask;
import mylib.app.LocationService;
import mylib.app.MyLog;
import mylib.utils.Global;
import mylib.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

// 订单下单 - 收钱
public class PVClientOrder_Pay3 extends AbsPVGuestOrder implements INFCHandler {
    ImageView iv_scan;
    TextView tv_tip;
    private LinearLayout pay_type_container;
    PayMethodObject pay_method;
    final boolean is_shangping_bean;
    double shishou_ya_jin = 0;

    @Override
    public void onNFCIntent(INFCHandler.NFCInfo data) {
        if (pay_method != null) {
            pay_method.onNFCIntent(data, client_order_bean.keHuId, act, null);
        }
    }

    public PVClientOrder_Pay3(MainActivity a, CommonOrderBean b, double shishou_ya_jin) {
        super(a, b);
        is_shangping_bean = b.dingDanLeiXing == Constant.dingdan_type_shangpin;
        this.shishou_ya_jin = shishou_ya_jin;


        Log.d("xing", "shishou_ya_jin-->" + shishou_ya_jin);
        for (CommonOrderBean.GangPingInfo info : b.heavy_list) {
            Log.d("xing", "heavy_list--------------" + info.getGuiGeName() + "  " + info.yajin_number + "  " + info.yajin_file);
        }
        for (CommonOrderBean.GangPingInfo info : b.empty_list) {
            Log.d("xing", "empty_list--------------" + info.getGuiGeName() + "  " + info.yajin_number + "  " + info.yajin_file);
        }
    }

    private boolean hasYaJinInput() {
        return !is_shangping_bean && !client_order_bean.isTuiPingDan();
    }

    @Override
    protected void createMainView(Context ctx) {
        mMainView = View.inflate(act, R.layout.pv_guest_order_pay3, null);

        mMainView.findViewById(R.id.iv_back).setOnClickListener(this);
        mMainView.findViewById(R.id.btn_ok).setOnClickListener(this);
        mMainView.findViewById(R.id.btn_print).setOnClickListener(this);

        iv_scan = mMainView.findViewById(R.id.iv_scan);
        tv_tip = mMainView.findViewById(R.id.tv_tip);
        pay_type_container = mMainView.findViewById(R.id.pay_type_container);

        if (client_order_bean.laiYuan == 3 && client_order_bean.zhiFuFangShi == 6) {
            pay_type_container.setVisibility(View.GONE);
        }
        pay_method = new PayMethodObject(pay_type_container, true);
        if (client_order_bean.client_bean != null &&
                client_order_bean.client_bean.shiFouYunXuQianKuan) {
            pay_method.rb_qiankuan.setVisibility(View.VISIBLE);
        }
        boolean is_tuipin = client_order_bean.isTuiPingDan();
        if (is_tuipin) {
            pay_method.rb_wei_xin.setVisibility(View.GONE);
            pay_method.rb_chong_zhi_ka.setVisibility(View.GONE);
            pay_method.rb_qiankuan.setVisibility(View.GONE);
            pay_method.rb_yinHangKa.setVisibility(View.GONE);
        }

        //是否积分单
        if (client_order_bean.isJiFenDan() && shishou_ya_jin == 0.0) {//积分单没有押金 只显示积分收款
            pay_method.rb_wei_xin.setVisibility(View.GONE);
            pay_method.rb_chong_zhi_ka.setVisibility(View.GONE);
            pay_method.rb_qiankuan.setVisibility(View.GONE);
            pay_method.rb_xin_jin.setVisibility(View.GONE);
            pay_method.rb_yinHangKa.setVisibility(View.GONE);
            pay_method.rb_jifen.setVisibility(View.VISIBLE);

            pay_method.rb_jifen.setChecked(true);

        } else if (client_order_bean.isJiFenDan() && shishou_ya_jin > 0) {//积分单 有押金 只显示现金收款
            pay_method.rb_wei_xin.setVisibility(View.GONE);
            pay_method.rb_chong_zhi_ka.setVisibility(View.GONE);
            pay_method.rb_qiankuan.setVisibility(View.GONE);
            pay_method.rb_yinHangKa.setVisibility(View.GONE);
            pay_method.rb_jifen.setVisibility(View.GONE);
            pay_method.rb_xin_jin.setVisibility(View.VISIBLE);
        }
    }

    private YuJieSuanBean yu_jie_suan_bean;

    @Override
    public void onAttach(boolean firstShow) {
        super.onAttach(firstShow);

        if (firstShow) {
            // 获取到订单预结算
            tv_tip.setText(null);
            act.showProgress();
            BackTask.post(new CZBackTask(act) {
                @SuppressLint("DefaultLocale")
                @Override
                protected void runFront2() {
                    if (yu_jie_suan_bean != null) {
                        StringBuilder tip_string = new StringBuilder();

//                    if (client_order_bean.laiYuan == 3 && client_order_bean.zhiFuFangShi == 6) {
//
//                        tip_string.append(String.format(
//                                "楼层金额: %.2f %s\n"
//                                        + "应收押金: %.2f %s\n"
//                                        + "优惠金额: %.2f %s\n"
//                                        + "退气金额: %.2f %s\n"
//                                        + "总商品金额: %.2f %s\n"
//                                , yu_jie_suan_bean.louCengJinE, act.getString(R.string.title_yuan)
//                                , yu_jie_suan_bean.zongYaJin, act.getString(R.string.title_yuan)
//                                , yu_jie_suan_bean.youHuiJinE, act.getString(R.string.title_yuan)
//                                , yu_jie_suan_bean.zongYuQiJinE, act.getString(R.string.title_yuan)
//                                , yu_jie_suan_bean.zongShangPinJinE, act.getString(R.string.title_yuan)
//                        ));
//                    } else
                        if (is_shangping_bean) {
                            tip_string.append(String.format(
                                    "应收金额: %.2f %s\n"
                                            + "商品金额: %.2f %s\n"
                                            + "楼层金额: %.2f %s\n"
                                            + "优惠金额: %.2f %s\n"
                                            + "总商品金额: %.2f %s\n"
                                    , yu_jie_suan_bean.yingShouJinE, act.getString(R.string.title_yuan)
                                    , yu_jie_suan_bean.zongShangPinJinE, act.getString(R.string.title_yuan)
                                    , yu_jie_suan_bean.louCengJinE, act.getString(R.string.title_yuan)
                                    , yu_jie_suan_bean.youHuiJinE, act.getString(R.string.title_yuan)
                                    , yu_jie_suan_bean.zongShangPinJinE, act.getString(R.string.title_yuan)

                            ));
                        } else if (client_order_bean.isTuiPingDan()) {
                            tip_string.append(String.format(
                                    "应收金额: %.2f %s\n"
                                            + "楼层金额: %.2f %s\n"
                                            + "应退押金: %.2f %s\n"
                                            + "退气金额: %.2f %s\n"
                                    , yu_jie_suan_bean.yingShouJinE, act.getString(R.string.title_yuan)
                                    , yu_jie_suan_bean.louCengJinE, act.getString(R.string.title_yuan)
                                    , yu_jie_suan_bean.zongYaJin, act.getString(R.string.title_yuan)
                                    , yu_jie_suan_bean.zongYuQiJinE, act.getString(R.string.title_yuan)
                            ));
                        } else if (client_order_bean.isJiFenDan()) {

                            if (shishou_ya_jin == 0.0) {
                                tip_string.append(String.format(
                                        "退气金额: %.2f %s\n"
                                                + "积分: %d \n"
                                        , yu_jie_suan_bean.zongYuQiJinE, act.getString(R.string.title_yuan)
                                        , yu_jie_suan_bean.jiFen
                                ));
                            } else { //有押金的，需要收取押金
                                tip_string.append(String.format(
                                        "应收金额: %.2f %s\n"
                                                + "楼层金额: %.2f %s\n"
                                                + "应收押金: %.2f %s\n"
                                                + "优惠金额: %.2f %s\n"
                                                + "退气金额: %.2f %s\n"
                                                + "积分: %d \n"
                                        , yu_jie_suan_bean.yingShouJinE, act.getString(R.string.title_yuan)
                                        , yu_jie_suan_bean.louCengJinE, act.getString(R.string.title_yuan)
                                        , yu_jie_suan_bean.zongYaJin, act.getString(R.string.title_yuan)
                                        , yu_jie_suan_bean.youHuiJinE, act.getString(R.string.title_yuan)
                                        , yu_jie_suan_bean.zongYuQiJinE, act.getString(R.string.title_yuan)
                                        , yu_jie_suan_bean.jiFen)
                                );
                            }


                        } else {
                            tip_string.append(String.format(
                                    "应收金额: %.2f %s\n"
                                            + "楼层金额: %.2f %s\n"
                                            + "应收押金: %.2f %s\n"
                                            + "实收押金: %.2f %s\n"
                                            + "优惠金额: %.2f %s\n"
                                            + "退气金额: %.2f %s\n"
                                            + "总商品金额: %.2f %s\n"
                                    , yu_jie_suan_bean.yingShouJinE, act.getString(R.string.title_yuan)
                                    , yu_jie_suan_bean.louCengJinE, act.getString(R.string.title_yuan)
                                    , yu_jie_suan_bean.zongYaJin, act.getString(R.string.title_yuan)
                                    , shishou_ya_jin, act.getString(R.string.title_yuan)
                                    , yu_jie_suan_bean.youHuiJinE, act.getString(R.string.title_yuan)
                                    , yu_jie_suan_bean.zongYuQiJinE, act.getString(R.string.title_yuan)
                                    , yu_jie_suan_bean.zongShangPinJinE, act.getString(R.string.title_yuan)
                            ));
                        }
//                    if (client_order_bean.laiYuan == 3 && client_order_bean.zhiFuFangShi == 6) {
//                        tip_string.append("\n小程序已收：" + yu_jie_suan_bean.yiZhiFu + "元");
//                        tip_string.append('\n');
//
//                    } else
                        if (!client_order_bean.isTuiPingDan() && (!client_order_bean.isJiFenDan() || (client_order_bean.isJiFenDan() && shishou_ya_jin != 0.0))) {

                            BigDecimal yingShouJinE = new BigDecimal(Double.toString(yu_jie_suan_bean.yingShouJinE));
                            BigDecimal zongYaJin = new BigDecimal(Double.toString(yu_jie_suan_bean.zongYaJin));
                            BigDecimal shishouYaJin = new BigDecimal(Double.toString(shishou_ya_jin));
//                        BigDecimal tuiQiJinE = new BigDecimal(Double.toString(yu_jie_suan_bean.zongYuQiJinE));
                            BigDecimal ret = yingShouJinE.subtract(zongYaJin).add(shishouYaJin);

//                        tip_string.append("\n实收总金额：" + (yu_jie_suan_bean.yingShouJinE - yu_jie_suan_bean.zongYaJin + shishou_ya_jin) + "元");
                            tip_string.append("\n实收总金额：" + ret + "元");
                            tip_string.append('\n');
                        }
                        tv_tip.setText(tip_string);

                    } else {
                        Utils.toastLONG("订单已完成！");
                        act.getPVC().pop();
                    }
                }

                @Override
                protected void parseResult(final JSONObject data) throws Exception {
                    JSONObject result = data.getJSONObject("result");

                    yu_jie_suan_bean = Constant.gson.fromJson(result.toString(), YuJieSuanBean.class);
                    yu_jie_suan_bean.json = result;

                    if (client_order_bean.isNewOrder()) {
                        client_order_bean.yingShouJinE = yu_jie_suan_bean.yingShouJinE;
                        client_order_bean.dingDanHao = result.getString("dingDanHao");
                    }

                    // print
                    //PrintManager.get().printInBack(client_order_bean);
                }

                @Override
                protected String getInputParam() throws Exception {

                    JSONObject j = client_order_bean.toPrepayOrderJson();
                    return j.toString();
                }

                @Override
                protected String getURL() {
                    return "dingDan/dingDanYuJieSuan";
                }

            });
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.btn_print == id) {
            BackTask.post(new BackFrontTask() {
                @Override
                public void runFront() {
                    // print done
                }

                @Override
                public void runBack() {
                    PrintManager.get().printInBack(client_order_bean);
                }
            });
        } else if (R.id.btn_ok == id) {
            if (TextUtils.isEmpty(client_order_bean.dingDanHao)) {
                Utils.toastLONG("错误的订单，没有订单号！");
                return;
            }

            final PayMethodBean fpb = pay_method.getPayMethod();
            if (fpb == null) {
                Utils.toastLONG("请选择支付方式");
                return;
            }

            if (fpb.id == PayMethodBean.chong_zhi_ka.id && TextUtils.isEmpty(pay_method.czk_id)) {
                Utils.toastLONG("请扫描充值卡");
                return;
            }


            final double f_shishou_ya_jin = shishou_ya_jin;

            if (!hasYaJinInput()) {
                Log.d("xing", "!hasYaJinInput()");
                if (fpb.id == PayMethodBean.yin_hang_ka.id) {
                    getShouKuanUrl(yu_jie_suan_bean.zongYaJin, fpb);
                    return;
                }
                submit(yu_jie_suan_bean.zongYaJin, fpb);
            } else {
                AlertDialog.Builder ab = new AlertDialog.Builder(act);
                ab.setTitle(R.string.app_name).setMessage(
                        String.format("实收押金：%.2f, 确定提交吗？", f_shishou_ya_jin))
                        .setNegativeButton(android.R.string.cancel, null)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (fpb.id == PayMethodBean.yin_hang_ka.id) {
                                    getShouKuanUrl(f_shishou_ya_jin, fpb);
                                    return;
                                }
                                submit(f_shishou_ya_jin, fpb);
                            }
                        }).show();

            }
        } else {
            super.onClick(v);
        }
    }


    private void submit(final double f_shishou_ya_jin, final PayMethodBean fpb) {
        act.showProgress();
        BackTask.post(new CZBackTask(act) {

            @Override
            protected void runFront2() {
                if (ret_code != 200) { // ok
                    Utils.toastLONG("支付失败！ " + err_msg);
                    return;
                }
                Log.d("xing", "fpb-->" + fpb);
                if (fpb == PayMethodBean.xian_jin
                        || fpb == PayMethodBean.chong_zhi_ka
                        || fpb == PayMethodBean.qian_kuang
                        || fpb == PayMethodBean.ji_fen) {
                    Utils.toastLONG("订单完成！");
                    act.getPVC().popTo1();
                } else if (fpb == PayMethodBean.wei_xin) {
                    act.getPVC().push(new PVWeiXin(act,
                            client_order_bean.dingDanHao,
                            wx_url, PVWeiXin.from_order));
                }
//                else if (fpb == PayMethodBean.yin_hang_ka) {
//                    FrameLayout fl = new FrameLayout(act);
//                    ImageView img = new ImageView(act);
//                    img.setPadding(0, 20, 0, 0);
//                    Glide.with(act).load(CZNetUtils.svr_host + yin_hang_ka_url).into(img);
//                    int size = Global.sWidth * 3 / 4;
//                    FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
//                            size, size,
//                            Gravity.CENTER);
//                    fl.addView(img, lp);
//                    new AlertDialog.Builder(act)
//                            .setTitle("请确认收款后点击完成")
//                            .setView(fl)
//                            .setPositiveButton("完成", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    Utils.toastLONG("订单完成！");
//                                    act.getPVC().popTo1();
//                                }
//                            })
//                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                }
//                            })
//                            .show();
//
//                }
            }

            int ret_code = 0;
            String wx_url;
            String yin_hang_ka_url;

            @Override
            protected void parseResult(JSONObject data) throws Exception {
                //JSONObject result = data.getJSONObject("result");
                ret_code = data.getInt("code");
                if (ret_code != 200) { // ok
                    err_msg = data.optString("message");
                } else {

                    if (fpb == PayMethodBean.wei_xin) {
                        JSONObject j_res = data.getJSONObject("result");
                        wx_url = j_res.getString("weiXinPayCodeUrl");
                    }
//                    else if (fpb == PayMethodBean.yin_hang_ka) {
//                        JSONObject j_res = data.getJSONObject("result");
//                        yin_hang_ka_url = j_res.getString("yinHangPayUrl");
//                    }
                }
            }

            @Override
            protected String getInputParam() throws Exception {
                JSONObject j = new JSONObject(yu_jie_suan_bean.json.toString());
                Log.d("xing", "预结算Bean-->" + yu_jie_suan_bean.json.toString());
                //j.put("zhiFuFangShi", fpb.id);
                if (client_order_bean.laiYuan == 3 && client_order_bean.zhiFuFangShi == 6) {
                    Log.d("xing", "getInputParam-->client_order_bean.laiYuan == 3 && client_order_bean.zhiFuFangShi == 6");
                    j.put("zhiFuFangShi", 6);
                } else {
                    Log.d("xing", "getInputParam-->else");
                    pay_method.preparePayParameter(j);
                }

                j.put("zhongPingParamList", j.optJSONArray("zhongPingList"));
                j.put("kongPingParamList", j.optJSONArray("kongPingList"));
                if (j.remove("zhongPingList") != null && j.has("zhongPingParamList")) {
                    JSONArray ja = j.getJSONArray("zhongPingParamList");
                    for (int i = 0; i < ja.length(); i++) {
                        JSONObject jo = ja.getJSONObject(i);
                        jo.put("id", jo.getString("gangPingId"));
                        jo.put("gangPingJiaGe", jo.getDouble("gangPingJinE"));
                        jo.put("yaJin", jo.getDouble("yaJinJinE"));
                    }
                }
                if (j.remove("kongPingList") != null && j.has("kongPingParamList")) {
                    JSONArray ja = j.getJSONArray("kongPingParamList");
                    for (int i = 0; i < ja.length(); i++) {
                        JSONObject jo = ja.getJSONObject(i);
                        jo.put("id", jo.getString("gangPingId"));
                        jo.put("gangPingJiaGe", jo.getDouble("gangPingJinE"));
                        jo.put("yaJin", jo.getDouble("yaJinJinE"));
                    }
                }

                j.put("sid", client_order_bean.dingDanHao);
                j.put("shiShouYaJin", f_shishou_ya_jin);

                /**
                 * zongYingShouJinE (number): 总应收金额 =
                 * 总商品金额 + 实收押金金额 + 楼层金额 - 优惠金额 - 总余气金额，2位精度 ,
                 */
                j.put("zongYingShouJinE", yu_jie_suan_bean.zongShangPinJinE + f_shishou_ya_jin
                        + yu_jie_suan_bean.louCengJinE - yu_jie_suan_bean.youHuiJinE - yu_jie_suan_bean.zongYuQiJinE);

                if (client_order_bean.isTuiPingDan()) {
                    // 押金条
                    JSONArray ja = new JSONArray();
                    for (YaJinDanBean yj : client_order_bean.yajindan_list) {
                        ja.put(yj.id);
                        Log.d("xing", "押金条yj.id：" + yj.id);
                        Log.d("xing", "押金条yj.getId()：" + yj.getId());
                    }
                    j.put("yaJinTiaoIdList", ja);
                }

                double[] myGP = LocationService.myGP;
                if (myGP != null) {
                    j.put("lat", String.valueOf(myGP[1]));
                    j.put("lng", String.valueOf(myGP[0]));
                }
                return j.toString();
            }

            @Override
            protected String getURL() {
                return "dingDan/jieSuan";
            }

        });
    }

    //获取收款码
    private void getShouKuanUrl(final double f_shishou_ya_jin, final PayMethodBean fpb) {
        act.showProgress();
        BackTask.post(new BackFrontTask() {
            JSONObject jsonObject;

            @Override
            public void runFront() {

                act.hideProgress();
                try {
                    if (jsonObject != null && jsonObject.getInt("code") == 200) {

                        String yin_hang_ka_url = jsonObject.getString("result");

                        FrameLayout fl = new FrameLayout(act);
                        ImageView img = new ImageView(act);
                        img.setPadding(0, 20, 0, 0);
                        Glide.with(act).load(CZNetUtils.svr_host + yin_hang_ka_url).into(img);
                        int size = Global.sWidth * 3 / 4;
                        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                                size, size,
                                Gravity.CENTER);
                        fl.addView(img, lp);
                        new AlertDialog.Builder(act)
                                .setTitle("请确认收款后点击完成")
                                .setView(fl)
                                .setPositiveButton("完成", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        submit(f_shishou_ya_jin, fpb);

                                        Utils.toastLONG("订单完成！");
                                        act.getPVC().popTo1();
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();

                    } else {
                        Utils.toastSHORT(jsonObject.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void runBack() {
                try {
                    jsonObject = CZNetUtils.postCZHttp("user/getUserQrCode", null);
                } catch (IOException e) {

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

}
