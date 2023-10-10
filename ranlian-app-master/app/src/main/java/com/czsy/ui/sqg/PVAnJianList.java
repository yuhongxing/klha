package com.czsy.ui.sqg;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.czsy.CZNetUtils;
import com.czsy.Constant;
import com.czsy.INFCHandler;
import com.czsy.android.R;
import com.czsy.bean.AnJianOrderBean;
import com.czsy.bean.ClientBean;
import com.czsy.bean.CommonOrderBean;
import com.czsy.ui.AbsPVOrderList;
import com.czsy.ui.MainActivity;
import com.czsy.ui.sqg.order.PVClientOrder_Client;
import com.czsy.ui.sqg.order.PVSQG_Order;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;


import mylib.app.BackFrontTask;
import mylib.app.BackTask;
import mylib.app.MyLog;
import mylib.utils.Utils;

public class PVAnJianList extends AbsPVOrderList<AnJianOrderBean> implements INFCHandler {

    private boolean isFromMsg = false;
    private String dingDanHao;
    private boolean isJiancha = false;
    private AnJianOrderBean anJianOrderBean;

    public PVAnJianList(MainActivity a, boolean has_date) {
        super(a, has_date);
    }

    public PVAnJianList(MainActivity a, boolean isFromMsg, String dingDanHao) {
        super(a, false);
        this.isFromMsg = isFromMsg;
        this.dingDanHao = dingDanHao;
    }

    @Override
    protected void bindItem(int i, final AnJianOrderBean bean, PVSQG_Order.ViewHolder viewHolder) {
//        vh.btn_tuidan.setVisibility(b.status != 2 ? View.INVISIBLE
//                : View.VISIBLE);
//        vh.tv_content.setText(b.toString());
////        vh.tv_content.setVisibility(View.VISIBLE);
////        vh.top_container.setVisibility(View.GONE);
//
//
//        vh.tv_ordernum.setText(b.dingDanLeiXingName + "：" + b.dingDanHao);
//        vh.tv_order_time.setText("下单时间：" + b.chuangJianRiQi);
//        vh.tv_orderOk_time.setText("完成时间：" + b.wanChengRiQi);
//        vh.zhifufangshi.setVisibility(View.GONE);
//        vh.tv_kehu_num.setVisibility(View.GONE);
//        vh.tv_order_money.setVisibility(View.GONE);
//        vh.tv_name.setText("客        户：" + b.keHuMing);
//        vh.tv_tel.setText("电        话：" + b.keHuDianHua);
//        String louceng = b.louCeng == null ? "" : "; 楼层：" + b.louCeng;
//        vh.tv_address.setText("地        址：" + b.diZhi + louceng);
//        vh.tv_gyz.setText("供  应  站：" + b.gongYingZhan);
//        vh.tv_laiyuan.setVisibility(b.tuiDanYuanYin != null ? View.VISIBLE : View.GONE);
//        vh.tv_laiyuan.setText("报修原因：" + b.tuiDanYuanYin);
//        vh.tv_beizhu.setVisibility(b.beiZhu != null ? View.VISIBLE : View.GONE);
//        vh.tv_beizhu.setText("备        注：" + b.beiZhu);
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
        viewHolder.tv_beizhu.setText("备注：" + bean.beiZhu);
        viewHolder.tv_time.setText(bean.chuangJianRiQi);

    }

    @Override
    protected String getTuiDanURL() {
        return "anJian/tuiDan";
    }


    @Override
    protected void createMainView(Context ctx) {
        super.createMainView(ctx);
        tv_title.setText(R.string.title_anjian_dan);
        tv_right.setVisibility(View.VISIBLE);
    }


    @Override
    public void runBack() {
        try {
            /**
             * 保修单，安检单都是这2种
             * YI_FEN_PEI(new Byte("2"), "已分配"),
             *     FINISH(new Byte("4"), "完成"),
             *
             * 购瓶单，商品单，退瓶单，折旧单是这三种
             * YI_FEN_PEI(new Byte("2"), "已分配"),
             *     DAI_FU_KUAN(new Byte("4"), "待付款"),
             *     FINISH(new Byte("6"), "完成"),
             */
            JSONObject in = new JSONObject();
            if (isFromMsg) {
                in.put("sid", dingDanHao);
            } else {
                in.put("status", 2);

            }

//            Pair<String, String> p = Constant.getTimeDuration(cur_date);
//            in.put("anJianRiQiKaiShi", p.first);
//            in.put("anJianRiQiJieZhi", p.second);
            JSONObject j = CZNetUtils.postCZHttp("anJian/query/200/1", in.toString());

            JSONArray jr = j.getJSONArray("result");
            ret_data = new LinkedList<>();
            for (int i = 0; i < jr.length(); i++) {
                ret_data.add(Constant.gson.fromJson(
                        jr.getJSONObject(i).toString(), AnJianOrderBean.class));
            }

        } catch (Exception e) {
            MyLog.LOGE(e);
            if (TextUtils.isEmpty(err_msg)) {
                err_msg = act.getString(R.string.tip_common_err);
            }
        }
    }

    private void create(ClientBean cb) {
        act.getPVC().replace(new PVAnJianDan(act, cb, null) {
            protected void onAnJianOk() {
                act.getPVC().pop();
                Utils.toast(R.string.tip_op_ok);
            }

            protected void onAnJianSkip() {
            }
        });
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        if (R.id.tv_right == id) {
            // new an jian
            final CommonOrderBean client_order_bean = new CommonOrderBean();
            final PVClientOrder_Client client = new PVClientOrder_Client(act, client_order_bean) {
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
        final AnJianOrderBean ab = adapter.getItem(i);
        anJianOrderBean = ab;
        if (ab.status != 2) {
            return;
        }

        if (ab.isCoerceSwipeCard) {

            act.showProgress("请扫描巡检卡");
            Log.d("xing", "---请扫描巡检卡");
            isJiancha = true;

        } else {
            isJiancha = false;

            ClientBean cb = ab.toClientBean();
            act.getPVC().push(new PVAnJianDan(act, cb, ab) {
                @Override
                protected void onAnJianOk() {

                    Utils.toastSHORT("登记成功");
                    act.getPVC().pop();
                    loadData();
                }
            });
        }

    }

    @Override
    public void onNFCIntent(NFCInfo i) {

        if (isJiancha) {
            seleteKehu(i.chip_sn, String.valueOf(anJianOrderBean.keHuId), anJianOrderBean.keHuMing);
        }

    }


    /**
     * 根据巡检卡号查询对比客户信息
     *
     * @param xinpianhao 芯片号
     */
    private void seleteKehu(final String xinpianhao, final String keHuId, final String keHuName) {

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

                            if (keHuId.equals(id) && keHuName.equals(userName)) {

                                act.getPVC().push(new PVAnJianDan(act, anJianOrderBean.toClientBean(), anJianOrderBean) {
                                    @Override
                                    protected void onAnJianOk() {

                                        Utils.toastSHORT("登记成功");
                                        act.getPVC().pop();
                                        loadData();
                                    }
                                });

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

}
