package com.czsy.ui.sqg.order;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.czsy.*;
import com.czsy.android.R;
import com.czsy.bean.*;
import com.czsy.ui.AbsPVScanHeavy;
import com.czsy.ui.MainActivity;
import com.czsy.ui.sqg.PVAnJianDan;

import java.util.*;

import mylib.app.BackTask;
import mylib.app.MyLog;
import mylib.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

// 代客下单 - 扫重瓶子
public class PVClientOrder_Heavy extends AbsPVScanHeavy implements INFCHandler {
    protected final CommonOrderBean client_order_bean;

    public PVClientOrder_Heavy(MainActivity a, CommonOrderBean b) {
        super(a, b);
        client_order_bean = b;
    }

    protected boolean existGangPing(String data) {
        Log.d("existGangPing", "PVClientOrder_Heavy--" + data);
        if (super.existGangPing(data)) {
            return true;
        }
        for (CommonOrderBean.GangPingInfo gp : client_order_bean.empty_list) {
            Log.d("existGangPing", "PVClientOrder_Heavy--empty_list-->" + gp.gp_bean.xinPianHao);
            if (Constant.sameString(gp.gp_bean.xinPianHao, data) ||
                    Constant.sameString(gp.gp_bean.gangPingHao, data)) {
                Utils.toast(R.string.tip_exist_gp);
                return true;
            }
        }
        for (CommonOrderBean.GangPingInfo gp : client_order_bean.heavy_list) {
            Log.d("existGangPing", "PVClientOrder_Heavy--heavy_list-->" + gp.gp_bean.xinPianHao);
            if (Constant.sameString(gp.gp_bean.xinPianHao, data) ||
                    Constant.sameString(gp.gp_bean.gangPingHao, data)) {
                Utils.toast(R.string.tip_exist_gp);
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onClickScanHeavy() {
        // never happen
    }

    @Override
    protected void createMainView(Context ctx) {
        super.createMainView(ctx);
        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //解决扫重瓶后点击提交，在清空重扫重瓶提示已存在问题
                for (CommonOrderBean.GangPingInfo bean : client_order_bean.empty_list) {
                    bean.isShowImage = true;
                }
                nfc_gp_list.clear();
                gp_id_list.clear();
                gp_adapter.notifyDataSetChanged();
                client_order_bean.heavy_list.clear();
            }
        });
    }

    private boolean addToList() {
        // 重瓶 重>空
        int cnt_heavy = nfc_gp_list.size() + gp_id_list.size();
        int cnt_empty = client_order_bean.empty_list.size();
        if (cnt_empty > cnt_heavy) {
            Utils.toastLONG("重瓶数量需大于等于空瓶数量！");
            return false;
        }
        // 检测空重瓶规格是否一致？
        Map<Integer, Integer> kp_guige = new HashMap<>();
        for (CommonOrderBean.GangPingInfo kp_info : client_order_bean.empty_list) {
            int ge = kp_info.gp_bean.guiGe;
            Integer cnt = kp_guige.get(ge);
            if (cnt == null) {
                kp_guige.put(ge, 1);
            } else {
                kp_guige.put(ge, cnt + 1);
            }
        }
        Map<Integer, Integer> zp_guige = new HashMap<>();
        List<CommonOrderBean.GangPingInfo> ll = new LinkedList<>();
        ll.addAll(nfc_gp_list);
        ll.addAll(gp_id_list);
        for (CommonOrderBean.GangPingInfo kp_info : ll) { //client_order_bean.heavy_list) {
            int ge = kp_info.gp_bean.guiGe;
            Integer cnt = zp_guige.get(ge);
            if (cnt == null) {
                zp_guige.put(ge, 1);
            } else {
                zp_guige.put(ge, cnt + 1);
            }
        }
        for (Integer kp_ge_key : kp_guige.keySet()) {
            int cnt = kp_guige.get(kp_ge_key);
            Integer zp_cnt = zp_guige.get(kp_ge_key);
            if (zp_cnt == null || cnt > zp_cnt) {
                AlertDialog.Builder ab = new AlertDialog.Builder(act);
                ab.setTitle(R.string.app_name).setMessage("空瓶和重瓶规格不一致").
                        setPositiveButton(android.R.string.ok, null).show();
                return false;
            }
        }

        client_order_bean.heavy_list.clear();
        client_order_bean.heavy_list.addAll(nfc_gp_list);
        client_order_bean.heavy_list.addAll(gp_id_list);
        return true;
    }

    @Override
    protected void onClickNoEmpty() {
        if (nfc_gp_list.isEmpty() && gp_id_list.isEmpty()) {
            Utils.toastLONG("请扫重瓶");
            return;
        }

        if (TextUtils.isEmpty(client_order_bean.louCeng) || client_order_bean.isShowSeleteLouCeng) {
            AlertDialog.Builder ab = new AlertDialog.Builder(act);
            ab.setTitle("请选择楼层").setItems(Constant.getLouCeng(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    client_order_bean.louCeng = String.valueOf(i + 1);
                    client_order_bean.isShowSeleteLouCeng = true;
                    checkGPIds();
                }
            }).show();
        } else {
            checkGPIds();
        }
    }

    void checkGPIds() {
        getIdFromServer(new ReturnRunable<Boolean>() {
            @Override
            public void run() {
                if (!ret) {
                    Utils.toastLONG(getMsg("扫描到非法气瓶！"));
                    return;
                }
                if (!addToList()) {
                    return;
                }

                // 检测是否与订单中的规格一致？
                if (!client_order_bean.order_gp.isEmpty()) {//分配的订单
                    Set<Integer> guige = new HashSet<>();
                    for (CommonOrderBean.GangPingInfo info : client_order_bean.heavy_list) {
                        guige.add(info.gp_bean.guiGe);
                    }
                    for (OrderDetailBean b : client_order_bean.order_gp) {
                        if (!guige.contains(b.guiGe)) {
                            Utils.toastLONG("未加入气瓶：" + b.guiGeName);
                            return;
                        }
                    }
                    guige.clear();
                    for (OrderDetailBean info : client_order_bean.order_gp) {
                        guige.add(info.guiGe);
                    }
                    for (CommonOrderBean.GangPingInfo info : client_order_bean.heavy_list) {
                        if (!guige.contains(info.gp_bean.guiGe)) {
                            Utils.toastLONG("订单中没有：" + info.gp_bean.guiGeName);
                            return;
                        }
                    }

//                    Log.d("xing","guige.size()-->"+guige.size());
//                    Log.d("xing","heavy_list-->"+client_order_bean.heavy_list.size());
//                    Log.d("xing","order_gp.size()-->"+client_order_bean.order_gp.size());

                    if (client_order_bean.heavy_list.size() != client_order_bean.order_gp.size()) {
                        Utils.toastSHORT("与订单中数量不符");
                        return;
                    }

                    //检测押金图片和纸质单号是否拍全
                    for (CommonOrderBean.GangPingInfo info : client_order_bean.heavy_list) {

                        if (info.isShowImage) {
                            if (info.yajin_file.isEmpty() || info.yajin_number.isEmpty()) {
                                Utils.toastSHORT("请补全押金图片或纸质单号");
                                return;
                            }
                        }
                    }


                } else {//代客下单的

                    //检测押金图片和纸质单号是否拍全
                    for (CommonOrderBean.GangPingInfo info : client_order_bean.heavy_list) {

                        if (info.isShowImage) {
                            if (info.yajin_file.isEmpty() || info.yajin_number.isEmpty()) {
                                Utils.toastSHORT("请补全押金图片或纸质单号");
                                return;
                            }
                        }
                    }
                }

                act.getPVC().push(new PVAnJianDan(act, client_order_bean.toClientBean(),
                        client_order_bean.isForceAnJian(),
                        client_order_bean.dingDanHao) {

                    void done() {
                        createOrderIfNeeded(new ReturnRunable<Boolean>() {
                            @Override
                            public void run() {
                                if (this.ret) {
                                    act.getPVC().push(new PVClientOrder_Confirm3(act, client_order_bean));
                                }
                            }
                        });
                    }

                    @Override
                    protected void onAnJianOk() {
                        client_order_bean.an_jian_ben = anjian_order_bean;
                        done();
                    }

                    @Override
                    protected void onAnJianSkip() {
                        client_order_bean.an_jian_ben = null; // no an jian
                        done();
                    }
                });
            }


        });
    }

    @Override
    public String checkGanPing(GangPingBean gp) {
        // ~~判断当前钢瓶是否属于当前配送员~~

        // ③、订单中扫重瓶
        //(1)钢瓶不在操作送气工不可操作（提示：此钢瓶不在库存）
        //(2)钢瓶是空瓶不可操作（提示：此钢瓶是空瓶）
        //(3)超期、报废不可操作（提示：此钢瓶超期未检、此钢瓶已报废）
//        if(!kucun_set.contains(gp.xinPianHao)) {
//            return "钢瓶不在库存";
//        }
        LoginUser lu = LoginUser.get();
        if (gp.gongYingZhanId != lu.orgId
                || gp.peiSongYuanId != lu.id
                || gp.zuiHouWeiZhi != Constant.zhihouweizhi_psy
        ) {
            return "气瓶不在库存";
        } else if (gp.isEmpty()) {
            return "此气瓶是空瓶";
        } else if (gp.status != GangPingBean.gp_status_using) {
            return "气瓶状态: " + gp.getStatusName();
        }
        return null;
    }

    @Override
    public void onAttach(boolean firstShow) {
        if (firstShow) {

            if (client_order_bean != null) {
                client_order_bean.heavy_list.clear();
            }

        }
        super.onAttach(firstShow);

    }
}
