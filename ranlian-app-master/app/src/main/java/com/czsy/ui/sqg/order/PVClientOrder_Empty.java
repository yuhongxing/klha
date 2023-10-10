package com.czsy.ui.sqg.order;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.preference.DialogPreference;
import android.util.Log;
import android.view.View;

import com.czsy.*;
import com.czsy.android.R;
import com.czsy.bean.CommonOrderBean;
import com.czsy.bean.GangPingBean;
import com.czsy.bean.KuCunBean;
import com.czsy.bean.LoginUser;
import com.czsy.ui.AbsPVScanEmpty;
import com.czsy.ui.MainActivity;
import com.czsy.ui.MainPVC;

import com.czsy.ui.PVMyKunCun2;
import mylib.app.BackFrontTask;
import mylib.app.BackTask;
import mylib.app.MyLog;
import mylib.utils.Utils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

// 代客下单 - 扫空瓶
public class PVClientOrder_Empty extends AbsPVScanEmpty implements INFCHandler {
    protected final CommonOrderBean client_order_bean;

    public PVClientOrder_Empty(MainActivity a, CommonOrderBean b) {
        super(a, b, true);
        client_order_bean = b;
        Log.d("xing"," 代客下单 - 扫空瓶"+ client_order_bean.dingDanHao);
    }

    private void addToEmptyList() {
        client_order_bean.empty_list.clear();
        client_order_bean.empty_list.addAll(nfc_gp_list);
        client_order_bean.empty_list.addAll(gp_id_list);
    }

    @Override
    protected void onClickScanHeavy() {
        Log.d("xing","onClickScanHeavy");
        getIdFromServer(new ReturnRunable<Boolean>() {
            @Override
            public void run() {
                // check if is ok
                if (ret) {
                    addToEmptyList();
                    act.getPVC().push(
                            new PVClientOrder_Heavy(act, client_order_bean)
                    );
                } else {
                    Utils.toastLONG(getMsg("扫描到非法气瓶！"));
                }
            }
        });
        //act.getPVC().push(new PVClientOrder_Client(act, client_order_bean));

    }

    @Override
    protected void createMainView(Context ctx) {
        super.createMainView(ctx);
        if (is_tuipin) {
            btn_next.setVisibility(View.GONE);
            btn_clear.setVisibility(View.GONE);
            btn_no_empty.setText(android.R.string.ok);
            btn_no_empty.setTextColor(Color.WHITE);
            btn_no_empty.setBackgroundResource(R.drawable.sel_btn_blue);
        }
    }

    @Override
    protected void onClickNoEmpty() {
        if (is_tuipin) {

            Log.d("xing","onClickNoEmpty");

            // 退瓶单。直接预处理
            final int size = gp_id_list.size() + nfc_gp_list.size();
            if (size == 0) {
                Utils.toastLONG("没有输入气瓶");
                return;
            }
            this.getIdFromServer(new ReturnRunable<Boolean>() {
                @Override
                public void run() {
                    if (!ret) {
                        Utils.toastLONG(getMsg("扫描到非法气瓶！"));
                        return;
                    }
                    addToEmptyList();
                    client_order_bean.heavy_list.clear();
                    createOrderIfNeeded(new ReturnRunable<Boolean>() {
                        @Override
                        public void run() {
                            if (this.ret) {
                                MainPVC pvc = act.getPVC();
                                pvc.push(new PVClientOrder_Confirm3(act, client_order_bean));
                            } else {

                            }
                        }
                    });

                    return;


                }
            });
            return;
        }
        client_order_bean.empty_list.clear();
        act.getPVC().push(new PVClientOrder_Heavy(act, client_order_bean));
    }

    @Override
    public void onAttach(boolean firstShow) {
        if (firstShow) {

            if (client_order_bean != null) {
                client_order_bean.empty_list.clear();
            }
        }else {
            /**
             * 此解决：
             * 处理订单，有空瓶的情况下，重大于空时，
             * 扫完重瓶返回上级页面，再进入扫重瓶页面（或清空重扫），
             * 扫瓶还是显示全部都有押金条上传按钮。
             */
            for (CommonOrderBean.GangPingInfo bean : client_order_bean.empty_list) {
                bean.isShowImage = true;
            }
        }
        super.onAttach(firstShow);
    }


    @Override
    public String checkGanPing(GangPingBean gp) {
        /**
         * (1)钢瓶在此配送员不可操作（提示：此钢瓶已在库存）
         * (2)钢瓶不属于此用户后台记录异常
         */
        LoginUser lu = LoginUser.get();

        if (gp.zuiHouWeiZhi == GangPingBean.zhwz_psy
                && gp.peiSongYuanId == lu.id) {
            return "此气瓶已在库存";
        }

        if (gp.zuiHouWeiZhi != GangPingBean.zhwz_kh){
            return TheApp.sInst.getString(R.string.err_gp_bad_user);
        }

        if (gp.keHuId != client_order_bean.keHuId){
            return TheApp.sInst.getString(R.string.err_gp_not_user);
        }

//        if (gp.zuiHouWeiZhi != GangPingBean.zhwz_kh
////                || gp.keHuId != client_order_bean.keHuId
//        ) {
////            Utils.toast(TheApp.sInst.getString(R.string.err_gp_not_user));
//            return TheApp.sInst.getString(R.string.err_gp_not_user);
//        }
        return null;
    }
}
