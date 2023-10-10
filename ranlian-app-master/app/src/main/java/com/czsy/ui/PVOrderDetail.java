package com.czsy.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.czsy.CZBackTask;
import com.czsy.CZNetUtils;
import com.czsy.Constant;
import com.czsy.INFCHandler;
import com.czsy.android.R;
import com.czsy.bean.CommonOrderBean;
import com.czsy.bean.OrderDetailBean;
import com.czsy.bean.PayMethodBean;
import com.czsy.bean.YaJinDanBean;
import com.czsy.ui.sqg.order.FavOrderManager;
import com.czsy.ui.sqg.order.PVClientOrder_Empty;
import com.czsy.ui.sqg.order.PVClientOrder_Pay3;
import com.czsy.ui.sqg.order.PVWeiXin;

import mylib.app.BackFrontTask;
import mylib.app.BackTask;
import mylib.app.LocationService;
import mylib.ui.list.AbstractAdapter;
import mylib.ui.list.MyListView;
import mylib.utils.Global;
import mylib.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class PVOrderDetail extends AbsPVBase implements View.OnClickListener, INFCHandler {

    protected TextView tv_title, tv_right;
    protected TextView tv_content;
    final CommonOrderBean order;
    private String anJianRenYuan, shangCiAnJianRiQi, qiWangShiJian;
    protected TextView tv_ordernum, tv_order_time, tv_orderOk_time, zhifufangshi,
            tv_kehu_num, tv_name, tv_tel, tv_address, tv_gyz, tv_order_money, tv_laiyuan, tv_beizhu, tv_anjian,
            tv_good, tv_jiage, tv_num, tv_money, tv_laidianTel, btn_favo, btn_reject_order, tv_order_yajin_xianshang,
            tv_gp_good, tv_gp_jiage, tv_gp_num, tv_gp_money;
    private RelativeLayout layout_bottom;//, layout_bottom_zz;
    protected MyListView list_item;
    private LinearLayout line_gpList, line_spList;
    private boolean hasSP = false;
    //判断是否是“消息列表”点进来的，如果是：显示底部按钮（收藏，退单）；不是：隐藏
    private boolean isFromMsg = false;
    //    private boolean isZZ = false;//是否是站长
    private boolean isCoerceSwipeCard = false;//是否强制扫巡检卡
    private boolean isOK = false;//对比巡检卡信息是否通过
    private String good = "", jiage = "", num = "", money = "";
    private String xinpianhao = "";

    private Set<String> fav_orders;

    public PVOrderDetail(MainActivity a, CommonOrderBean order, Boolean isFromMsg) {
        super(a);
        this.order = order;
        this.isFromMsg = isFromMsg;
//        this.isZZ = isZZ;

        fav_orders = FavOrderManager.getFavOrderIds();
        if (fav_orders == null) {
            fav_orders = new HashSet<>();
        }
    }


    public void setTitle(String title) {
        if (tv_title != null) {
            tv_title.setText(title);
        }
    }

    //钢瓶
    private AbstractAdapter<OrderDetailBean> adapter = new AbstractAdapter<OrderDetailBean>() {

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            if (view == null) {
                view = View.inflate(viewGroup.getContext(), R.layout.order_detail_item, null);
                new ViewHolder(view);
            }
            ViewHolder vh = (ViewHolder) view.getTag();
            OrderDetailBean data = adapter.getItem(i);
            setInfo(data, vh);
            return vh.root;
        }
    };

    @Override
    protected void createMainView(Context ctx) {

        mMainView = View.inflate(ctx, R.layout.pv_order_detail, null);
        tv_content = mMainView.findViewById(R.id.tv_content);

        mMainView.findViewById(R.id.iv_back).setOnClickListener(this);
//        btn_shenpi = mMainView.findViewById(R.id.btn_shenpi);
//        btn_chexiao = mMainView.findViewById(R.id.btn_chexiao);
        btn_favo = mMainView.findViewById(R.id.btn_favo);
        btn_reject_order = mMainView.findViewById(R.id.btn_reject_order);
        tv_title = mMainView.findViewById(R.id.tv_title);
        tv_right = mMainView.findViewById(R.id.tv_right);
        tv_content = mMainView.findViewById(R.id.tv_content);
        tv_order_yajin_xianshang = mMainView.findViewById(R.id.tv_order_yajin_xianshang);
        tv_ordernum = mMainView.findViewById(R.id.tv_ordernum);
        tv_order_time = mMainView.findViewById(R.id.tv_order_time);
        tv_orderOk_time = mMainView.findViewById(R.id.tv_orderOk_time);
        zhifufangshi = mMainView.findViewById(R.id.zhifufangshi);
        tv_kehu_num = mMainView.findViewById(R.id.tv_kehu_num);
        tv_name = mMainView.findViewById(R.id.tv_name);
        tv_tel = mMainView.findViewById(R.id.tv_tel);
        tv_laidianTel = mMainView.findViewById(R.id.tv_laidianTel);
        tv_address = mMainView.findViewById(R.id.tv_address);
        tv_gyz = mMainView.findViewById(R.id.tv_gyz);
        tv_order_money = mMainView.findViewById(R.id.tv_order_money);
        tv_laiyuan = mMainView.findViewById(R.id.tv_laiyuan);
        tv_beizhu = mMainView.findViewById(R.id.tv_beizhu);
        line_gpList = mMainView.findViewById(R.id.line_gpList);
        tv_good = mMainView.findViewById(R.id.tv_good);
        tv_jiage = mMainView.findViewById(R.id.tv_jiage);
        tv_num = mMainView.findViewById(R.id.tv_num);
        tv_money = mMainView.findViewById(R.id.tv_money);
        tv_gp_good = mMainView.findViewById(R.id.tv_gp_good);
        tv_gp_jiage = mMainView.findViewById(R.id.tv_gp_jiage);
        tv_gp_num = mMainView.findViewById(R.id.tv_gp_num);
        tv_gp_money = mMainView.findViewById(R.id.tv_gp_money);
        layout_bottom = mMainView.findViewById(R.id.layout_bottom);
        tv_anjian = mMainView.findViewById(R.id.tv_anjian);
//        layout_bottom_zz = mMainView.findViewById(R.id.layout_bottom_zz);
        line_spList = mMainView.findViewById(R.id.line_spList);
        list_item = mMainView.findViewById(R.id.list_item);
        list_item.setFocusable(false);
        list_item.setAdapter(adapter);
//        btn_shenpi.setOnClickListener(this);
//        btn_chexiao.setOnClickListener(this);
        btn_favo.setOnClickListener(this);
        btn_reject_order.setOnClickListener(this);

        if (
                (
                        order.dingDanLeiXing == Constant.dingdan_type_gouqi ||
                                order.dingDanLeiXing == Constant.dingdan_type_tuipin ||
                                order.dingDanLeiXing == Constant.dingdan_type_shangpin) &&
                        (order.status == Constant.status_yifenpei || order.status == Constant.status_daifukuan)) {
            tv_right.setVisibility(View.VISIBLE);
            tv_right.setOnClickListener(this);
        }

        if (isFromMsg) {
            layout_bottom.setVisibility(View.VISIBLE);

            if (order.dingDanLeiXing != Constant.dingdan_type_gouqi) {
                btn_favo.setVisibility(View.GONE);
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private void updateInfo() {
        if (fav_orders.contains(order.dingDanHao)) {
            btn_favo.setText(R.string.tip_remove_favo);
        } else {
            btn_favo.setText(R.string.tip_add_favo);
        }
        tv_ordernum.setText(order.dingDanLeiXingName + "：" + order.dingDanHao);
        tv_order_time.setText("下单时间：" + order.chuangJianRiQi + "\n" + "期望配送日期：" + qiWangShiJian);
        tv_orderOk_time.setVisibility(order.status == 6 ? View.VISIBLE : View.GONE);
        tv_orderOk_time.setText("完成时间：" + order.wanChengRiQi);
        zhifufangshi.setVisibility(order.status == 6 ? View.VISIBLE : View.GONE);
        zhifufangshi.setText("支付方式：" + order.zhiFuFangShiName);
        tv_kehu_num.setText("客户编号：" + order.keHuBianHao);
        tv_name.setText("客        户：" + order.keHuMing);
//        tv_tel.setText("电        话：" + order.keHuDianHua);
        tv_tel.setText("");
        Utils.setTextSpan("电        话：", order.keHuDianHua, "", tv_tel, new Utils.TextClickListener() {
            @Override
            public void onClick() {
                Utils.toCall(order.keHuDianHua);
            }
        });
        String louceng = order.louCeng == null ? "" : "; 楼层：" + order.louCeng;
        tv_address.setText("地        址：" + order.diZhi + louceng);
        tv_gyz.setText("供  应  站：" + order.gongYingZhan);
        tv_order_money.setVisibility(order.status == 6 ? View.VISIBLE : View.GONE);
        tv_order_money.setText("订单金额：" + Math.abs(order.shiShouJinE));
        if (order.xianShang != 0) {
            tv_order_yajin_xianshang.setVisibility(View.VISIBLE);
            tv_order_yajin_xianshang.setText("线上收取：" + Math.abs(order.xianShang));
        }


        tv_laiyuan.setText("来        源：" + order.laiYuanName + "   " + order.zhiFuFangShiName);

        if (order.callerPhone != null && order.callerPhone.length() > 0) {
            tv_laidianTel.setVisibility(View.VISIBLE);

            tv_laidianTel.setText("");
            Utils.setTextSpan("来电号码：", order.callerPhone, "", tv_laidianTel, new Utils.TextClickListener() {
                @Override
                public void onClick() {
                    Utils.toCall(order.callerPhone);
                }
            });
        } else {
            tv_laidianTel.setVisibility(View.GONE);
        }

        tv_beizhu.setVisibility(order.beiZhu != null ? View.VISIBLE : View.GONE);
        tv_beizhu.setText("备        注：" + order.beiZhu);
        tv_anjian.setText("安检人员：" + anJianRenYuan + "\n" + "安检日期：" + shangCiAnJianRiQi);

        line_gpList.setVisibility(order.order_gp != null && order.order_gp.size() > 0 ? View.VISIBLE : View.GONE);
        adapter.setData(order.order_gp);
        adapter.notifyDataSetChanged();

        line_spList.setVisibility(hasSP ? View.VISIBLE : View.GONE);

        tv_good.setText(good);
        tv_jiage.setText(jiage);
        tv_num.setText(num);
        tv_money.setText(money);


        String good = "", jiage = "", num = "", money = "";
        for (CommonOrderBean.DetailInfo detailInfo : order.details) {

            if (detailInfo.type.equals("购瓶")) {


                good = good + detailInfo.shangPingMingCheng + "\n";
                jiage = jiage + detailInfo.price + "\n";
                num = num + detailInfo.count + "\n";
                money = money + detailInfo.zongJinE + "\n";
            }

        }
        tv_gp_good.setText(good);
        tv_gp_jiage.setText(jiage);
        tv_gp_num.setText(num);
        tv_gp_money.setText(money);


    }

    @Override
    public void onAttach(boolean firstShow) {
        super.onAttach(firstShow);
        Log.d("xing", "onAttach");
        if (firstShow){
        act.showProgress();
        if (firstShow) {
            order.order_gp.clear();
            BackTask.post(new CZBackTask(null) {
                @Override
                protected void parseResult(JSONObject jdata) throws Exception {
                    JSONObject jr = jdata.getJSONObject("result");

                    anJianRenYuan = jr.getString("anJianRenYuan");
                    shangCiAnJianRiQi = jr.getString("shangCiAnJianRiQi");
                    qiWangShiJian = jr.getString("qiWangShiJian");

                    isCoerceSwipeCard = jr.getBoolean("isCoerceSwipeCard");

                    if (jr.has("dingDanGangPing")) {
                        JSONArray ja = jr.getJSONArray("dingDanGangPing");
                        if (ja.length() > 0) {
                            for (int i = 0; i < ja.length(); i++) {
                                OrderDetailBean detail = Constant.gson.fromJson(
                                        ja.getJSONObject(i).toString(), OrderDetailBean.class);
                                order.order_gp.add(detail);
                            }

                        }

                    }
                    /*
                     * ,"shangPingDingDanList":[{"id":"69","dingDanHao":"2019120517302715656","shangPin":"煤气灶（单）","shangPinId":"1",
                     * "danJia":10.50,"shuLiang":1,"shangPingJinE":10.50,
                     * "chuangJianRiQi":"2019-12-05 17:30:27"}]
                     */
                    if (jr.has("shangPingDingDanList")) {
                        JSONArray ja = jr.getJSONArray("shangPingDingDanList");
                        if (ja.length() > 00) {
                            hasSP = true;
                            for (int i = 0; i < ja.length(); i++) {
                                JSONObject jo = ja.getJSONObject(i);
                                good = good + jo.getString("shangPin") + "\n";
                                jiage = jiage + jo.optDouble("danJia") + "\n";
                                num = num + jo.getInt("shuLiang") + "\n";
                                money = money + jo.optDouble("shangPingJinE") + "\n";

                            }
                        }
                    }
                }

                @Override
                protected String getInputParam() throws Exception {
                    return "{\"dingDanHao\":\"" + order.dingDanHao + "\"}";
                }

                @Override
                protected String getURL() {
                    return "dingDan/chaXunDingDanXiangQingForAnZhuo";
                }

                @Override
                protected void runFront2() {
                    act.hideProgress();
                    updateInfo();
                }
            });
        }}
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (R.id.iv_back == id) {
            act.getPVC().pop();
        } else if (R.id.tv_right == id) {

            //强制扫巡检卡

            if (isCoerceSwipeCard) {
                act.showProgress("请扫描巡检卡");

            } else {
                //status=4 是待付款状态，直接调取微信二维码支付
                if (order.status == 4) {
                    getParam();
//                    act.getPVC().push(new PVWeiXin(act,
//                            order.dingDanHao,
//                            "https://qr.api.cli.im/newqr/create?data=%25E4%25BD%25A0%25E5%25BE%2588%2B%2B%25E4%25B8%2589%25E5%2588%2586%25E6%25AF%2592&level=H&transparent=false&bgcolor=%23FFFFFF&forecolor=%23000000&blockpixel=12&marginblock=1&logourl=&logoshape=no&size=500&kid=cliim&key=bc38ccba8354316dc4d61d9ccedb4a06",
//                            PVWeiXin.from_order));
                } else {

                    if (order.dingDanLeiXing == Constant.dingdan_type_tuipin) {
                        act.getPVC().replace(new PVClientOrder_Empty(act, order));
                    } else if (order.dingDanLeiXing == Constant.dingdan_type_shangpin) {
                        act.getPVC().replace(new PVClientOrder_Pay3(act, order, 0));
                    } else if (order.dingDanLeiXing == Constant.dingdan_type_gouqi) {
                        act.getPVC().replace(new PVClientOrder_Empty(act, order));
                    }
                }
            }

        } else if (R.id.btn_favo == id) {//收藏

            if (!fav_orders.contains(order.dingDanHao)) {
                FavOrderManager.saveFavOrder(order.dingDanHao);
                addFavo(order, true);
            } else {
                FavOrderManager.removeFavOrder(order.dingDanHao);
                addFavo(order, false);
            }
        } else if (R.id.btn_reject_order == id) {//退单
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
//                    final MainActivity act = ;
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

                            act.getPVC().pop();
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
        }

    }

    //收藏与取消收藏
    private void addFavo(CommonOrderBean b, boolean add) {
        if (add) {
            fav_orders.add(b.dingDanHao);
            btn_favo.setText(R.string.tip_remove_favo);

        } else {
            fav_orders.remove(b.dingDanHao);
            btn_favo.setText(R.string.tip_add_favo);

        }
    }

    @Override
    public void onNFCIntent(NFCInfo i) {
//        xinpianhao = i.chip_sn;

        seleteKehu(i.chip_sn);
    }

    private class ViewHolder {
        public final View root;
        public TextView tv_gangpingNum, tv_time, tv_leiBieName, tv_gp_jine, tv_yh_jine,
                tv_fujia_jine, tv_ss_jine, tv_tuiQi, tv_tq_jine, tv_yajin, tv_ss_yajin;


        public ViewHolder(View r) {
            root = r;
            tv_gangpingNum = r.findViewById(R.id.tv_gangpingNum);
            tv_time = r.findViewById(R.id.tv_time);
            tv_leiBieName = r.findViewById(R.id.tv_leiBieName);
            tv_gp_jine = r.findViewById(R.id.tv_gp_jine);
            tv_yh_jine = r.findViewById(R.id.tv_yh_jine);
            tv_fujia_jine = r.findViewById(R.id.tv_fujia_jine);
            tv_ss_jine = r.findViewById(R.id.tv_ss_jine);
            tv_tuiQi = r.findViewById(R.id.tv_tuiQi);
            tv_tq_jine = r.findViewById(R.id.tv_tq_jine);
            tv_yajin = r.findViewById(R.id.tv_yajin);
            tv_ss_yajin = r.findViewById(R.id.tv_ss_yajin);

            root.setTag(this);
        }

    }

    @SuppressLint("SetTextI18n")
    private void setInfo(OrderDetailBean data, ViewHolder vh) {

        if (data.gangPingHao != null && !data.gangPingHao.equals("")) {
            vh.tv_gangpingNum.setText("气瓶号：" + data.gangPingHao + "    规格：" + data.guiGeName + "\n气体类型：" + data.qiTiLeiXing);

        } else {
            vh.tv_gangpingNum.setText("规格：" + data.guiGeName + "    气体类型：" + data.qiTiLeiXing);

        }

        vh.tv_time.setText("创建日期：" + data.chuangJianRiQi);
        if (data.leiBie == 1) {
            vh.tv_leiBieName.setText("操作类别：购瓶");

        } else if (data.leiBie == 2) {
            vh.tv_leiBieName.setText("操作类别：退瓶");

        } else {
            vh.tv_leiBieName.setText("");
            vh.tv_leiBieName.setVisibility(View.GONE);
        }
        vh.tv_gp_jine.setText(data.gangPingJinE + "");
        vh.tv_yh_jine.setText(data.youHuiJinE + "");
        vh.tv_fujia_jine.setText(data.fuJiaFei + "");
        vh.tv_ss_jine.setText(data.shiShouJinE + "");
        vh.tv_tuiQi.setText(data.tuiQi);
        vh.tv_tq_jine.setText(data.tuiQiJinE + "");
        vh.tv_yajin.setText(data.yaJinJinE + "");
        vh.tv_ss_yajin.setText(data.shiShouYaJinJinE + "");

    }

    private void seleteKehu(final String xinpianhao) {

//        act.showProgress();
        BackTask.post(new BackFrontTask() {
            JSONObject ret = new JSONObject();

            @Override
            public void runFront() {
                act.hideProgress();

                try {
                    if (ret.getInt("code") == 200) {
                        if (ret.optJSONObject("result") != null) {


                            String id = ret.getJSONObject("result").getString("id");
                            String userName = ret.getJSONObject("result").getString("userName");

                            if (order.keHuId == Long.parseLong(id) && order.keHuMing.equals(userName)) {

                                if (order.status == 4){
                                    getParam();
                                    return;
                                }

                                if (order.dingDanLeiXing == Constant.dingdan_type_tuipin) {
                                    act.getPVC().replace(new PVClientOrder_Empty(act, order));
                                } else if (order.dingDanLeiXing == Constant.dingdan_type_shangpin) {
                                    act.getPVC().replace(new PVClientOrder_Pay3(act, order, 0));
                                } else if (order.dingDanLeiXing == Constant.dingdan_type_gouqi) {
                                    act.getPVC().replace(new PVClientOrder_Empty(act, order));
                                }
                            } else {
                                Utils.toastSHORT("巡检卡信息不匹配");
                            }

                        } else {
                            Utils.toastSHORT("该巡检卡信息为空");
                        }

                    } else {
                        Utils.toastSHORT(ret.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void runBack() {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("data", xinpianhao);
                    jsonObject.put("type", 4);
                    ret = CZNetUtils.postCZHttp("keHu/chaXunKeHuAnZhuo", jsonObject.toString());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //获取订单结算接口需要的参数
    private void getParam() {
        act.showProgress();
        BackTask.post(new BackFrontTask() {
            JSONObject jsonObject;

            @Override
            public void runFront() {

                try {
                    if (jsonObject != null && jsonObject.getInt("code") == 200) {
                        jieSuan(jsonObject.getJSONObject("result"));
                    } else {
                        act.hideProgress();
                        Utils.toastSHORT("请重试");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void runBack() {
                try {
                    jsonObject = CZNetUtils.postCZHttp("dingDan/getWeiXinDingDanParam", "{\"dingDanHao\":\""+order.dingDanHao+"\"}");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //调结算订单接口
    private void jieSuan(final JSONObject param) {
        act.showProgress();
        BackTask.post(new CZBackTask(act) {

            @Override
            protected void runFront2() {
                if (ret_code != 200) { // ok
                    Utils.toastLONG("支付失败！ " + err_msg);
                    return;
                }

                act.getPVC().push(new PVWeiXin(act,
                        order.dingDanHao,
                        wx_url, PVWeiXin.from_order));

            }

            int ret_code = 0;
            String wx_url;

            @Override
            protected void parseResult(JSONObject data) throws Exception {

                ret_code = data.getInt("code");
                if (ret_code != 200) { // ok
                    err_msg = data.optString("message");
                } else {
                    JSONObject j_res = data.getJSONObject("result");
                    wx_url = j_res.getString("weiXinPayCodeUrl");

                }
            }

            @Override
            protected String getInputParam() throws Exception {

                return param.toString();
            }

            @Override
            protected String getURL() {
                return "dingDan/jieSuan";
            }

        });
    }

}
