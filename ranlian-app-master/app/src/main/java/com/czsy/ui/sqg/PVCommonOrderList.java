package com.czsy.ui.sqg;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;

import com.czsy.CZNetUtils;
import com.czsy.Constant;
import com.czsy.TheApp;
import com.czsy.android.R;
import com.czsy.bean.ClientBean;
import com.czsy.bean.CommonOrderBean;
import com.czsy.ui.AbsPVOrderList;
import com.czsy.ui.MainActivity;
import com.czsy.ui.PVOrderDetail;
import com.czsy.ui.sqg.details.TuipingDetails;
import com.czsy.ui.sqg.huishou.HsSeleteAct;
import com.czsy.ui.sqg.huishou.PVZheJiuDan2;
import com.czsy.ui.sqg.order.PVClientOrder_Client;
import com.czsy.ui.sqg.order.PVClientOrder_Empty;
import com.czsy.ui.sqg.order.PVSQG_Order;

import mylib.app.MyLog;
import mylib.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;

/**
 * 订单列表页面。 “回收” “退瓶” “商品”
 */
public class PVCommonOrderList extends AbsPVOrderList<CommonOrderBean> {
    public final int order_type;
    private boolean isFromMsg = false;//是否消息列表点进来的
    private String dingDanHao;

    public PVCommonOrderList(MainActivity a, boolean has_date, int type) {
        super(a, has_date);
        order_type = type;
    }

    public PVCommonOrderList(MainActivity a, int type, boolean isFromMsg, String dingDanHao) {
        super(a, false);
        order_type = type;
        this.isFromMsg = isFromMsg;
        this.dingDanHao = dingDanHao;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void bindItem(int i, final CommonOrderBean bean, PVSQG_Order.ViewHolder viewHolder) {
        viewHolder.tv_name.setText(bean.keHuMing + "  (编号" + bean.keHuBianHao + ")");
//        viewHolder.tv_phone.setText("联系电话：" + bean.keHuDianHua);
        viewHolder.tv_phone.setText("");
        Utils.setTextSpan("联系电话：", bean.keHuDianHua, "", viewHolder.tv_phone, new Utils.TextClickListener() {
            @Override
            public void onClick() {
                Utils.toCall(bean.keHuDianHua);
            }
        });
        if (bean.status != 6) {
            viewHolder.btn_reject_order.setVisibility(View.VISIBLE);
        } else {
            viewHolder.btn_reject_order.setVisibility(View.GONE);
        }
        viewHolder.tv_address.setText("地址：" + bean.diZhi);
        if (bean.beiZhu == null) {
            viewHolder.tv_beizhu.setText("备注：无");
        } else {
            viewHolder.tv_beizhu.setText("备注：" + bean.beiZhu);
        }
        viewHolder.tv_time.setText(bean.chuangJianRiQi);
//        vh.btn_tuidan.setVisibility(
//                o.status == null || o.status != Constant.status_yifenpei ? // Constant.status_daifukuan ?
//                        View.INVISIBLE : View.VISIBLE);
////        StringBuffer sb = new StringBuffer(b.toString());
////
////        vh.tv_content.setText(sb.toString());
//
//
////        vh.tv_ordernum.setText("订  单  号："+o.dingDanHao);
//        vh.tv_ordernum.setText(o.dingDanLeiXingName + "：" + o.dingDanHao);
//        vh.tv_order_time.setText("下单时间：" + o.chuangJianRiQi);
//        vh.tv_orderOk_time.setVisibility(o.status == 6 ? View.VISIBLE : View.GONE);
//        vh.tv_orderOk_time.setText("完成时间：" + o.wanChengRiQi);
//        vh.zhifufangshi.setVisibility(o.status == 6 ? View.VISIBLE : View.GONE);
//        vh.zhifufangshi.setText("支付方式：" + o.zhiFuFangShiName);
//        vh.tv_kehu_num.setText("客户编号：" + o.keHuBianHao);
//        vh.tv_name.setText("客        户：" + o.keHuMing);
////        vh.tv_tel.setText("电        话：" + o.keHuDianHua);
//
//        vh.tv_tel.setText("");
//        Utils.setTextSpan("电        话：", o.keHuDianHua, "", vh.tv_tel, new Utils.TextClickListener() {
//            @Override
//            public void onClick() {
//                Utils.toCall(o.keHuDianHua);
//            }
//        });
//
//        String louceng = o.louCeng == null ? "" : "; 楼层：" + o.louCeng;
//        vh.tv_address.setText("地        址：" + o.diZhi + louceng);
//        vh.tv_gyz.setText("供  应  站：" + o.gongYingZhan);
//        vh.tv_order_money.setText("订单金额：" + Math.abs(o.yingShouJinE));
//        vh.tv_laiyuan.setText("来        源：" + o.laiYuanName);
//
//        if (o.callerPhone != null && o.callerPhone.length() > 0) {
//            vh.tv_laidianTel.setVisibility(View.VISIBLE);
//
//            vh.tv_laidianTel.setText("");
//            Utils.setTextSpan("来电号码：", o.callerPhone, "", vh.tv_laidianTel, new Utils.TextClickListener() {
//                @Override
//                public void onClick() {
//                    Utils.toCall(o.callerPhone);
//                }
//            });
//        } else {
//            vh.tv_laidianTel.setVisibility(View.GONE);
//        }
//
//        vh.tv_beizhu.setVisibility(o.beiZhu != null ? View.VISIBLE : View.GONE);
//        vh.tv_beizhu.setText("备        注：" + o.beiZhu);
//
//        vh.gridLayout_tuiping.setVisibility(o.dingDanLeiXing == Constant.dingdan_type_tuipin ? View.VISIBLE : View.GONE);
//        vh.tv_shiTuiYaJin.setText(Math.abs(o.yaJinJinE) + "");
//        vh.tv_tuiQi.setText(o.yuQi + "");
//        vh.tv_gangpingNum.setText(o.zheJiuGangPing + "");
//
//        String good = "", jiage = "", num = "", money = "";
//        if (o.details != null && !o.details.isEmpty()) {
//            vh.gridLayout_goods.setVisibility(o.status != Constant.status_done ? View.VISIBLE : View.GONE);
//            for (CommonOrderBean.DetailInfo detailInfo : o.details) {
//                good = good + detailInfo.shangPingMingCheng + "\n";
//                jiage = jiage + detailInfo.price + "\n";
//                num = num + detailInfo.count + "\n";
//                money = money + detailInfo.zongJinE + "\n";
//            }
//        }
//        vh.tv_good.setText(good);
//        vh.tv_jiage.setText(jiage);
//        vh.tv_num.setText(num);
//        vh.tv_money.setText(money);

    }

    @Override
    protected void createMainView(Context ctx) {
        super.createMainView(ctx);

        if (order_type == Constant.dingdan_type_tuipin) {
            tv_title.setText(R.string.title_tuipingdan);
        } else if (order_type == Constant.dingdan_type_zhejiu) {
            tv_title.setText(R.string.title_zhejiudan);
        } else if (order_type == Constant.dingdan_type_shangpin) {
            tv_title.setText(R.string.title_lingshoudan);
        } else {
            tv_title.setText(R.string.title_dingqidan);
        }
        tv_right.setVisibility(View.VISIBLE);
        list_view.setOnItemClickListener(this);
    }

    @Override
    public void runBack() {
        try {
            JSONObject in = new JSONObject();
            int st = Constant.status_yifenpei; //status_daifukuan;

            //判断是否为消息列表进来的
            //是：只查询该（一个）订单；
            //否：正常流程
            if (isFromMsg) {
                in.put("sid", dingDanHao);
            } else {

                in.put("status", st);
                in.put("dingDanLeiXing", order_type);
                if (has_date) {
                    Pair<String, String> p = Constant.getTimeDuration(cur_date, cur_date + Constant._1day);
                    in.put("startDate ", p.first);
                    in.put("endData", p.second);

                }
            }

            JSONObject j = CZNetUtils.postCZHttp("dingDan/chaXun/100/1", in.toString());

            JSONArray jr = j.getJSONArray("result");
            ret_data = new LinkedList<>();
            boolean set_title = false;
            for (int i = 0; i < jr.length(); i++) {
                final CommonOrderBean cb = Constant.gson.fromJson(
                        jr.getJSONObject(i).toString(), CommonOrderBean.class);
//                cb.status = st;
                ret_data.add(cb);
                if (!set_title) {
                    set_title = true;
                    TheApp.sHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            tv_title.setText(cb.dingDanLeiXingName);
                        }
                    });
                }
            }

        } catch (Exception e) {
            MyLog.LOGE(e);
            err_msg = "无法获取订单列表";
        }
    }

    private void create(ClientBean cb) {
        CommonOrderBean b = new CommonOrderBean();
        b.fromClientBean(cb);
        if (order_type == Constant.dingdan_type_zhejiu) {
            b.dingDanLeiXing = Constant.dingdan_type_zhejiu;
            act.getPVC().replace(new PVZheJiuDan2(act, b));
        } else if (order_type == Constant.dingdan_type_tuipin) {
            b.dingDanLeiXing = Constant.dingdan_type_tuipin;
            act.getPVC().replace(new PVClientOrder_Empty(act, b));
        } else if (order_type == Constant.dingdan_type_shangpin) {
            b.dingDanLeiXing = Constant.dingdan_type_shangpin;
            act.getPVC().replace(new PVShangPin2(act, cb));
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.tv_right == id) {
            // find a user
            final PVClientOrder_Client client = new PVClientOrder_Client(act, null) {
                @Override
                public void onClickOk() {
                    if (!client_pv.validClient()) {
                        return;
                    }
                    ClientBean cb = client_pv.client_bean;
                    create(cb);
                }
            };
            act.getPVC().push(client);

        } else {
            super.onClick(v);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        final CommonOrderBean b = adapter.getItem(i);
        if (b.status != Constant.status_yifenpei) { //status_daifukuan) {
            return;
        }
        if (order_type == Constant.dingdan_type_tuipin) {

            act.getPVC().push(new TuipingDetails(act, b));
//            act.getPVC().push(new PVClientOrder_Empty(act, b));
        } else if (order_type == Constant.dingdan_type_zhejiu) {

            act.getPVC().push(new HsSeleteAct(act, b));

        } else if (order_type == Constant.dingdan_type_shangpin) {
            act.getPVC().push(new PVOrderDetail(act, b, false));
            //act.getPVC().push(new PVClientOrder_Pay3(act, b, null, null));
        }
    }

    @Override
    protected String getTuiDanURL() {
        return "dingDan/tuiDan";
    }

    @Override
    public void onAttach(boolean firstShow) {
        Log.d("xiaofa", "onAttach");
        super.onAttach(firstShow);
    }
}
