package com.czsy.ui.sqg;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.czsy.CZBackTask;
import com.czsy.CZEvents;
import com.czsy.CZNetUtils;
import com.czsy.Constant;
import com.czsy.INFCHandler;
import com.czsy.android.R;
import com.czsy.bean.ClientBean;
import com.czsy.bean.IDNameBean;
import com.czsy.bean.LoginUser;
import com.czsy.bean.WeiXiuBean;
import com.czsy.bean.WeiXiuType;
import com.czsy.bean.WeiXiuType1;
import com.czsy.ui.AbsPVOrderList;
import com.czsy.ui.MainActivity;
import com.czsy.ui.PVOrderDetail;
import com.czsy.ui.PVPushMsg;
import com.czsy.ui.sqg.order.PVClientOrder_Client;
import com.czsy.ui.sqg.order.PVClientOrder_Empty;
import com.czsy.ui.sqg.order.PVClientOrder_Pay3;
import com.czsy.ui.sqg.order.PVSQG_Order;
import com.czsy.ui.weixiuyuan.PVWeixiuMain;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.LinkedList;

import mylib.app.BackFrontTask;
import mylib.app.BackTask;
import mylib.app.BaseActivity;
import mylib.app.EventHandler;
import mylib.app.MyLog;
import mylib.utils.Utils;

/**
 * 维修单
 */
public class PVWeiXiuList extends AbsPVOrderList<WeiXiuBean> implements INFCHandler {
    final PVWeiXiuList pv = this;
    TextView tvOK, tvNO;
//    PVPushMsg pv_pushmsg;
    boolean isWeixiuyuan = LoginUser.get().appRoleType == Constant.role_wei_xiu;

    private boolean isFromMsg = false;
    private boolean OK_or_NO = false;//true: 已完成的，false：未完成的；
    private String dingDanHao;
    private WeiXiuBean weiXiuBean;
    private boolean isJiancha = false;

    public PVWeiXiuList(MainActivity a, boolean has_date) {
        super(a, has_date);
    }

    public PVWeiXiuList(MainActivity a, boolean isFromMsg, String dingDanHao) {
        super(a, false);
        this.isFromMsg = isFromMsg;
        Log.d("xiaoxi", "isFromMsg--》" + isFromMsg);
        this.dingDanHao = dingDanHao;
    }

//    private CZEvents evt = new CZEvents() {
//        @Override
//        public void onPushDataChanged() {
//
//            pv_pushmsg.reload();
//
//        }
//    };
//
//    private BaseActivity.EventTypes getEvents() {
//        return new BaseActivity.EventTypes(new Enum[]{
//
//                CZEvents.Event.onPushDataChanged,
//        }, evt);
//    }

    @Override
    protected void bindItem(final int i, final WeiXiuBean bean, PVSQG_Order.ViewHolder viewHolder) {

        viewHolder.tv_name.setText(bean.keHuMing + "  (编号" + bean.keHuBianHao + ")");
//        viewHolder.tv_phone.setText("联系电话："+bean.keHuDianHua);
        viewHolder.tv_phone.setText("");
        Utils.setTextSpan("联系电话：", bean.keHuDianHua, "", viewHolder.tv_phone, new Utils.TextClickListener() {
            @Override
            public void onClick() {
                Utils.toCall(bean.keHuDianHua);
            }
        });
        if (bean.status != 4) {
            viewHolder.btn_reject_order.setVisibility(View.VISIBLE);
        } else {
            viewHolder.btn_reject_order.setVisibility(View.GONE);
        }
        viewHolder.tv_address.setText("地址："+bean.diZhi);
        viewHolder.tv_beizhu.setText("备注：" + bean.beiZhu);
        viewHolder.tv_time.setText(bean.chuangJianRiQi);
    }


    @Override
    protected void createMainView(Context ctx) {
        super.createMainView(ctx);

        tv_title.setText(R.string.title_baoxiu);
        tv_right.setVisibility(View.VISIBLE);


        if (isWeixiuyuan) {
//            iv_back.setVisibility(View.GONE);
//            tv_left.setVisibility(View.VISIBLE);
//            tv_msg_cnt.setVisibility(View.VISIBLE);

//            pv_pushmsg = (new PVPushMsg(act, pv, tv_msg_cnt));
//            tv_left.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    act.getPVC().push(pv_pushmsg);
//                }
//            });
            if (isFromMsg) {
                Log.d("xiaoxi", "设置隐藏数值--》");
                tv_left.setVisibility(View.GONE);
//                tv_msg_cnt.setVisibility(View.GONE);
                tv_right.setVisibility(View.GONE);

            } else {
                LinearLayout line_tab = mMainView.findViewById(R.id.line_tab);
                line_tab.setVisibility(View.VISIBLE);

                tvOK = mMainView.findViewById(R.id.tv_ok);
                tvNO = mMainView.findViewById(R.id.tv_no);
                tvOK.setOnClickListener(this);
                tvNO.setOnClickListener(this);


            }
        }


    }


    @Override
    public void onClick(View v) {
        if (v == tv_right) {
            final PVClientOrder_Client client = new PVClientOrder_Client(act, null) {
                @Override
                public void onClickOk() {
                    if (!client_pv.validClient()) {
                        return;
                    }
                    Log.d("xing", "onClickOk");
                    ClientBean cb = client_pv.client_bean;
                    create(cb);
                }
            };
            act.getPVC().push(client);
        } else if (v == tvOK) {

            changeUI(true);
            loadData();
        } else if (v == tvNO) {
            changeUI(false);
            loadData();
        } else {
            super.onClick(v);
        }
    }

    private void create(ClientBean cb) {
        WeiXiuBean b = new WeiXiuBean();
        b.keHuId = cb.id;
        b.diZhi = cb.diZhi;
        b.keHuMing = cb.userName;
        doWeiXiuDan(act, b, true, new Runnable() {
            @Override
            public void run() {
                act.getPVC().pop(); //To(PVWeiXiuList.this);
                // loadData();
            }
        });
    }

    @Override
    public void runBack() {
        try {
            JSONObject in = new JSONObject();
            if (isFromMsg) {
                in.put("sid", dingDanHao);
            } else {
                if (OK_or_NO) {//已完成
                    in.put("status", 4);
//                    in.put("startDate",);
//                    in.put("endDate",);
                } else {//未完成
                    in.put("status", 2);

                }
            }
            JSONObject j = CZNetUtils.postCZHttp("baoXiuTouSu/queryBaoXiu/200/1",
                    in.toString());
            JSONArray jr = j.getJSONArray("result");
            ret_data = new LinkedList<>();
            for (int i = 0; i < jr.length(); i++) {
                ret_data.add(Constant.gson.fromJson(
                        jr.getJSONObject(i).toString(), WeiXiuBean.class));
            }

        } catch (Exception e) {
            MyLog.LOGE(e);
            if (TextUtils.isEmpty(err_msg)) {
                err_msg = act.getString(R.string.tip_common_err);
            }
        }
    }

    public static void doWeiXiuDan(final BaseActivity act, final WeiXiuBean b, boolean isCreate, final Runnable done_task) {
        final Dialog d = new Dialog(act, android.R.style.Theme_Translucent_NoTitleBar);
        d.setContentView(R.layout.dialog_new_weixiu);
        final EditText et_input = d.findViewById(R.id.et_input);//维修备注
        et_input.setText(b.beiZhu);
        final EditText et_input_yuanyin = d.findViewById(R.id.et_input_yuanyin);
        et_input_yuanyin.setText(b.baoXiuReason);
        if (!isCreate){
            et_input_yuanyin.setKeyListener(null);
        }

        final Spinner sp_weixiu_lx = d.findViewById(R.id.sp_weixiu_lx);
        sp_weixiu_lx.setAdapter(new ArrayAdapter<>(act,
                R.layout.gp_id_item,
                WeiXiuType1.clientTypes));

        if (b.type == 0) {
            sp_weixiu_lx.setSelection(4);
        } else {
            sp_weixiu_lx.setSelection(b.type - 1);
        }

        final boolean is_new = b.id <= 0;
        d.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String ss_yuanyin = et_input_yuanyin.getText().toString().trim();//维修原因
                final String ss = et_input.getText().toString().trim();//维修备注
                if (TextUtils.isEmpty(ss)) {
                    Utils.toastLONG("请填写维修备注");
                    return;
                }
                d.dismiss();
                act.showProgress();
                BackTask.post(new CZBackTask(act) {
                    @Override
                    protected void parseResult(JSONObject jdata) throws Exception {

                    }

                    @Override
                    protected String getInputParam() throws Exception {
                        JSONObject j = new JSONObject();
                        j.put("keHuId", b.keHuId);
                        IDNameBean wx = (IDNameBean) sp_weixiu_lx.getSelectedItem();
                        j.put("type", wx.id);
                        j.put("yuanYin", ss_yuanyin);
                        j.put("beiZhu", ss);
                        if (is_new) {
                            j.put("diZhi", b.diZhi);
                        } else {
                            j.put("sid", b.dingDanHao);
                        }
                        return j.toString();
                    }

                    @Override
                    protected String getURL() {
                        return is_new ?
                                "baoXiuTouSu/chuangJianBaoXiu" :
                                "baoXiuTouSu/baoXiuJieDan";
                    }

                    @Override
                    protected void runFront2() {
                        Utils.toast(R.string.tip_op_ok);
                        if (done_task != null) {
                            done_task.run();
                        }
                    }
                });

            }
        });
        d.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                d.dismiss();

                IDNameBean wx = (IDNameBean) sp_weixiu_lx.getSelectedItem();
                Log.d("xing", wx.id + "");

            }
        });
        d.show();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        final WeiXiuBean b = adapter.getItem(i);
        weiXiuBean = b;
        if (b.status != 2) {
            return;
        }
        if (b.isCoerceSwipeCard) {
            act.showProgress("请扫描巡检卡");
            isJiancha = true;
        } else {
            isJiancha = false;
            doWeiXiuDan(act, b, false,new Runnable() {
                @Override
                public void run() {
                    loadData();
                }
            });
        }


    }

    @Override
    protected String getTuiDanURL() {
        return "baoXiuTouSu/tuiDan";
    }

//    @Override
//    public void onAttach(boolean firstShow) {
//        super.onAttach(firstShow);
//        if (firstShow) {
//
//            if (isWeixiuyuan) {
//                pv_pushmsg.getView(act);
//                if (isFromMsg) {
//                    pv_pushmsg.reload(true);
//                } else {
//                    pv_pushmsg.reload();
//                }
//                BaseActivity.EventTypes et = getEvents();
//                EventHandler.addEventHandler(et.mEvts, et.mHandler);
//
//            }
//        }
//    }

    private void changeUI(boolean isOK) {
        OK_or_NO = isOK;

        if (isOK) {
            tvOK.setTextColor(act.getResources().getColor(R.color.bg_blue));
            tvOK.setTextSize(16);

            tvNO.setTextColor(act.getResources().getColor(R.color.bg_gray_dark));
            tvNO.setTextSize(14);
        } else {
            tvNO.setTextColor(act.getResources().getColor(R.color.bg_blue));
            tvNO.setTextSize(16);

            tvOK.setTextColor(act.getResources().getColor(R.color.bg_gray_dark));
            tvOK.setTextSize(14);
        }

    }

    @Override
    public void onNFCIntent(NFCInfo i) {

        if (isJiancha) {
            seleteKehu(i.chip_sn, String.valueOf(weiXiuBean.keHuId), weiXiuBean.keHuMing);

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

                                doWeiXiuDan(act, weiXiuBean, false,new Runnable() {
                                    @Override
                                    public void run() {
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
