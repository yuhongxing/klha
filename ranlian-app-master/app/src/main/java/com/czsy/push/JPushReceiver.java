package com.czsy.push;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.czsy.CZEvents;
import com.czsy.Constant;
import com.czsy.TheApp;
import com.czsy.android.R;
import com.czsy.bean.LoginUser;
import com.czsy.ui.MainActivity;
import com.czsy.ui.changzhan.PVChangZhanMain;
import com.czsy.ui.sqg.PVMainSQG;
import com.czsy.ui.yunshuyuan.PVYSYMain;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.helper.c;
import cn.jpush.android.service.JPushMessageReceiver;
import mylib.app.EventHandler;
import mylib.app.MyLog;
import mylib.utils.Utils;

import static android.content.ContentValues.TAG;
import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * <!-- User defined. 用户自定义的广播接收器-->JPushMessageReceiver
 */
public class JPushReceiver extends JPushMessageReceiver {
    private static int seq_num = 1;

    public static void bindJPush() {

        LoginUser lu = LoginUser.get();
        if (lu == null || !lu.valid()) {
            JPushInterface.deleteAlias(TheApp.sInst, seq_num);
            Log.e("JPush","bindJPush--> lu == null");
            return;
        }
        String jtoken = JPushInterface.getRegistrationID(TheApp.sInst);
        Log.e("JPush","bindJPush--> jtoken == "+jtoken);
        if (TextUtils.isEmpty(jtoken)) {
            Log.e("JPush","bindJPush--> jtoken == null");
            return;
        }
        // BIND ...
        int i = seq_num++;
        Set<String> s = new HashSet<>();
        if (lu.appRoleType == Constant.role_song_qi_gong) {
            s.add("peisongyuan");
        } else if (lu.appRoleType == Constant.role_chang_zhan) {
            s.add("changzhan");
        } else if (lu.appRoleType == Constant.role_yun_shu_yuan) {
            s.add("yunshuyuan");
        }
        if (!s.isEmpty()) {
            JPushInterface.setTags(TheApp.sInst, i, s);
        }
        JPushInterface.setAlias(TheApp.sInst, i, String.valueOf(lu.id));
    }

    public void onMessage(Context var1, CustomMessage var2) {
        super.onMessage(var1, var2);
        try {
            final String message = var2.message;
            MyLog.LOGD("Push: " + message);
            final LoginUser lu = LoginUser.get();
            if (lu == null || !lu.valid()) {
                return;
            }
            try {
                final PushBean pb = Constant.gson.fromJson(
                        message, PushBean.class);
                if (pb.msg_target_uid != 0l &&
                        pb.msg_target_uid != lu.id) {
                    MyLog.LOGE("push msg not for me");
                    return;
                }

                EventHandler.notifyEvent(CZEvents.Event.onPushDataChanged);//声音提示播放
            } catch (Exception e) {
                MyLog.LOGE(e);
            }
        } catch (Exception e) {
            MyLog.LOGE(e);
        }
    }

    public void onNotifyMessageArrived(Context var1, NotificationMessage var2) {
        super.onNotifyMessageArrived(var1, var2);
        MyLog.LOGD("Push: " + "onNotifyMessageArrived-->"+var2.toString());

    }
}