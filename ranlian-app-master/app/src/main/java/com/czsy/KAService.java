package com.czsy;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;

import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.czsy.android.R;
import com.czsy.push.PushManager;
import com.czsy.ui.MainActivity;

import mylib.utils.Global;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

public class KAService extends Service {

    //    private static final String TAG = KAService.class.getSimpleName();
    private static final String TAG = "TongZhi";
    public static String CALENDAR_ID = "channel_01";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public int onStartCommand(Intent ii, int flags, int startId) {
        String content;

        int unread = PushManager.get().getUnreadCnt();
        Log.d(TAG, "unread-->" + unread);
        if (unread <= 0) {
            content = getString(R.string.app_name);
        } else {
            content = String.format("你有%d条未读消息", unread);
        }
        Log.d(TAG, "content-->" + content);

        Intent intent = new Intent(TheApp.sInst,
                MainActivity.class);
        intent.putExtra("tab", 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pi = PendingIntent.getActivity(
                TheApp.sInst, 0, intent, FLAG_UPDATE_CURRENT);

        NotificationManager manager = (NotificationManager)
                TheApp.sInst.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification;
        NotificationCompat.Builder builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CALENDAR_ID, "123",
                    NotificationManager.IMPORTANCE_DEFAULT);
            // 设置渠道描述
            notificationChannel.setDescription("测试通知组");
            // 是否绕过请勿打扰模式
            notificationChannel.canBypassDnd();
            // 设置绕过请勿打扰模式
            notificationChannel.setBypassDnd(true);
            // 桌面Launcher的消息角标
            notificationChannel.canShowBadge();
            // 设置显示桌面Launcher的消息角标
            notificationChannel.setShowBadge(true);
            // 设置通知出现时声音，默认通知是有声音的
            notificationChannel.setSound(null, null);
            // 设置通知出现时的闪灯（如果 android 设备支持的话）
            notificationChannel.enableLights(true);
//            notificationChannel.setLightColor(Color.RED);
            // 设置通知出现时的震动（如果 android 设备支持的话）
            notificationChannel.enableVibration(false);
//            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400,
//                    300, 200, 400});
            manager.createNotificationChannel(notificationChannel);
        }
        builder = new NotificationCompat.Builder(this, CALENDAR_ID);

        boolean noti = ii != null && ii.getBooleanExtra("notify", false);
        boolean isJPush = ii != null && ii.getBooleanExtra("isJPush", true);
        Log.d(TAG, "noti-->" + noti);
        Log.d(TAG, "isJPush-->" + isJPush);

        if (noti && isJPush) {
            if ( CZConfig.getMsgSound()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Uri uri = Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.tishiyin);
                    Ringtone rt = RingtoneManager.getRingtone(this, uri);
                    rt.play();
                    Log.d(TAG, "Build.VERSION.SDK_INT >= Build.VERSION_CODES.O");
                } else {
                    builder.setSound(Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.tishiyin));

                    Log.d(TAG, "Build.VERSION.SDK_INT < Build.VERSION_CODES.O");
                }


            }
            if ( CZConfig.getMsgVibrate()) {
                //震动器，builder.setDefaults();不管用
                //设置震动周期，数组表示时间：等待+执行，单位是毫秒，下面操作代表:等待100，执行500，等待200，执行500，
                //后面的数字如果为-1代表不重复，之执行一次，其他代表会重复，0代表从数组的第0个位置开始
                long[] patter = {100, 500, 200, 500};
                Vibrator vibrator = (Vibrator) this.getSystemService(this.VIBRATOR_SERVICE);
                vibrator.vibrate(patter, -1);
            }
        }
        notification = builder.setSmallIcon(R.drawable.ic_msg)
                .setContentIntent(pi)
                .setContentTitle(getString(R.string.app_name))
                .setSmallIcon(android.R.drawable.ic_popup_reminder)
                .setContentText(content)
                .setChannelId(CALENDAR_ID)
                .build();

        manager.notify(PushManager.notification_id, notification);

        return super.onStartCommand(ii, flags, startId);
    }
}

