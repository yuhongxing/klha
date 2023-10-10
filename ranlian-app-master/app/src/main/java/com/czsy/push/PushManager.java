package com.czsy.push;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.czsy.CZEvents;
import com.czsy.CZNetUtils;
import com.czsy.Constant;
import com.czsy.KAService;
import com.czsy.TheApp;
import com.czsy.android.R;
import com.czsy.bean.CommonOrderBean;
import com.czsy.ui.AbsPVBase;
import com.czsy.ui.MainActivity;
import com.czsy.ui.PVOrderDetail;
import com.czsy.ui.PVPushMsg;
import com.czsy.ui.gongyingzhan.GYZZhanZhangMain;
import com.czsy.ui.gongyingzhan.yunying.dingdanlist.OrderFeipeiList;
import com.czsy.ui.gongyingzhan.yunying.dingdanlist.YunShuDanList;
import com.czsy.ui.gongyingzhan.yunying.manageSqg.LingqvShenpiList;
import com.czsy.ui.sqg.PVAnJianList;
import com.czsy.ui.sqg.PVCommonOrderList;
import com.czsy.ui.sqg.PVMainSQG;
import com.czsy.ui.sqg.PVSQGApp;
import com.czsy.ui.sqg.PVSQGApp1;
import com.czsy.ui.sqg.PVWeiXiuList;
import com.czsy.ui.sqg.order.PVSQG_Order;
import com.czsy.ui.weixiuyuan.PVWeixiuMain;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import mylib.app.BackFrontTask;
import mylib.app.BackTask;
import mylib.app.BaseActivity;
import mylib.app.EventHandler;
import mylib.utils.FileUtils;
import mylib.utils.Utils;

public class PushManager {
    static private PushManager sInst;
    static final public int max_push_bean = 100;
    private int unread_cnt = 0;

    private boolean isZZ = false;

    static public PushManager get() {
        if (sInst == null) {
            sInst = new PushManager();
        }
        return sInst;
    }

    private PushManager() {
        push_list = (List<PushBean>)
                FileUtils.getObject(push_file, List.class);
        if (push_list != null) {

            while (push_list.size() > max_push_bean) {
                push_list.remove(push_list.get(push_list.size() - 1));
            }
            for (PushBean pb : push_list) {
                if (!pb.is_read) {
                    unread_cnt++;
                }
            }
        }

    }

    private List<PushBean> push_list;
    private final static String push_file = "push_list";


    public int getUnreadCnt() {
        return unread_cnt;
    }


    //配送员的
    public void readMsg(MessageBean pb, PVSQGApp1 pv, PVPushMsg pvPushMsg) {
        if (pb.type == PushBean.msgtype_order || pb.type == PushBean.msgtype_cuiDan) {
//            return;
//        }
        final MainActivity act = pv.act;
        // to views ...
        if (pb.dingDanType == PushBean.type_gouqi
                || pb.dingDanType == PushBean.type_shangpin) {

            seleteDetailGoPage(pv, pb.dingDanHao);
        } else if (pb.dingDanType == PushBean.type_tuiping
                || pb.dingDanType == PushBean.type_zhejiu) {
            act.getPVC().push(new PVCommonOrderList(act, pb.dingDanType, true, pb.dingDanHao));
        } else if (pb.dingDanType == PushBean.type_weixiu) {
            act.getPVC().push(new PVWeiXiuList(act, true, pb.dingDanHao));
        } else if (pb.dingDanType == PushBean.type_anjian) {
            act.getPVC().push(new PVAnJianList(act, true, pb.dingDanHao));
        }}
    }

    //维修员 和 供应站站长
    public void readMsg(MessageBean pb, AbsPVBase absPVBase, PVPushMsg pvPushMsg) {
        final MainActivity act = absPVBase.act;
        if (pb.type == PushBean.msgtype_order && absPVBase instanceof PVWeixiuMain) {

            if (pb.dingDanType == PushBean.type_weixiu){
                act.getPVC().push((new PVWeiXiuList(act, true, pb.dingDanHao)));
            }else if (pb.dingDanType == PushBean.type_anjian){
                act.getPVC().push(new PVAnJianList(act,false));
            }
        } else if (absPVBase instanceof GYZZhanZhangMain) {

//            GYZZhanZhangMain aa = (GYZZhanZhangMain) absPVBase;
//            MainActivity act = aa.act;

            if (pb.dingDanType == PushBean.type_gouqi
                    || pb.dingDanType == PushBean.type_shangpin
                    || pb.dingDanType == PushBean.type_tuiping
                    || pb.dingDanType == PushBean.type_zhejiu
                    || pb.dingDanType == PushBean.type_weixiu
                    || pb.dingDanType == PushBean.type_anjian) {
                act.getPVC().push(new OrderFeipeiList(act, pb.dingDanHao, pb.dingDanType,true));
            } else if (pb.dingDanType == PushBean.type_tousu) {
                Log.d("xing", "点击了：投诉订单");
            } else if (pb.dingDanType == PushBean.type_psy_gpck) {
                act.getPVC().push(new LingqvShenpiList(act, false));
                Log.d("xing", "点击了：配送员领取钢瓶");
            } else if (pb.dingDanType == PushBean.type_gyz_ck || pb.dingDanType == PushBean.type_gyz_rk) {
                act.getPVC().push(new YunShuDanList(act, pb.dingDanHao, pb.dingDanType));
                Log.d("xing", "点击了：供应站出入库单");
            }

        }

    }

    public final static int notification_id = 13452;

    /**
     * 推送消息服务
     *
     * @param isJPush 判断是否是极光推送的，true:有提示音，false：消息列表点击的更新，不提示声音和振动；
     */
    public void updateNotification(boolean isJPush) {
        Intent intent = new Intent(TheApp.sInst, KAService.class);
        intent.putExtra("notify", true);
        intent.putExtra("isJPush", isJPush);
        TheApp.sInst.startService(intent);
    }

    //查询订单详情,如果该订单正常（未退单）就跳转到详情
    private void seleteDetailGoPage(final AbsPVBase absPVBase, final String dingDanHao) {

        absPVBase.act.showProgress();
        BackTask.post(new BackFrontTask() {

            CommonOrderBean bean;

            @Override
            public void runFront() {
                absPVBase.act.hideProgress();
                if (bean != null) {
                    absPVBase.act.getPVC().push(new PVOrderDetail(absPVBase.act, bean, true));
                } else {
                    Utils.toastSHORT("该订单不存在");
                }
            }

            @Override
            public void runBack() {

                try {

                    JSONObject j = new JSONObject();
                    j.put("sid", dingDanHao);

                    JSONObject ret = CZNetUtils.postCZHttp(
                            "dingDan/chaXun/1/1",
                            j.toString());
                    JSONArray result = ret.getJSONArray("result");
                    if (result.length() > 0) {

                        bean = Constant.gson.fromJson(result.getJSONObject(0).toString()
                                , CommonOrderBean.class);
                    } else {
                        bean = null;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }


    /**
     * //消息变为已读
     *
     * @param id         消息id
     * @param dingDanHao 订单号
     * @param isAllRead  是否全部已读；1-->全部
     */
    public void UpdataMsgStatus(final long id, final String dingDanHao, final long isAllRead) {
        BackTask.post(new BackFrontTask() {
            @Override
            public void runFront() {

            }

            @Override
            public void runBack() {
                JSONObject dJ = new JSONObject();
                try {

                    if (isAllRead == 1) {
                        dJ.put("isAllRead", isAllRead);
                    } else {
                        dJ.put("id", id);
                        dJ.put("dingDanHao", Long.parseLong(dingDanHao));
                    }
                    CZNetUtils.postCZHttp("notSendMsg/updateRead", dJ.toString());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


}
