package com.czsy;

import android.text.TextUtils;
import android.util.Log;

import cn.jpush.android.api.JPushInterface;
import com.czsy.android.BuildConfig;
import com.czsy.android.R;
import com.czsy.bean.LoginUser;
import com.czsy.push.JPushReceiver;
import com.czsy.push.PushManager;
import com.czsy.ui.sqg.PVSQGApp;
import mylib.app.AndroidApp;
import mylib.app.EventHandler;
import mylib.app.LocationService;
import mylib.app.MyLog;
import org.json.JSONObject;

public class TheApp extends AndroidApp {
    // private static final int MSG_LOC = 100;

    private Thread.UncaughtExceptionHandler mDefaultCrashHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
        //将当前实例设为系统默认的异常处理器
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {
                MyLog.LOGE(throwable);
                try { // wait for log all to file
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                }
                mDefaultCrashHandler.uncaughtException(thread, throwable);
            }
        });
    }

    @Override
    public Class<? extends EventHandler> getEventHandlerClass() {
        return CZEvents.class;
    }

    @Override
    public int getAppNameRes() {
        return R.string.app_name;
    }

    @Override
    public int getAppIconRes() {
        return 0;
    }

    private int loc_err_cnt = 0;

    @Override
    protected void initLocalProcess(String process) {
        super.initLocalProcess(process);

//        final LoginUser lu = LoginUser.get();
//        if (null == lu || !lu.valid()) {
//            PushManager.get().clearPushData();
//        }
        evtHandler = new CZEvents() {

            @Override
            public void onPushDataChanged() {
                LoginUser lu = LoginUser.get();
                if (lu.valid()){
                    PushManager.get().updateNotification(true);
                    Log.d("TongZhi","in TheAPP--onPushDataChanged   有效的");
                }else {
                    Log.d("TongZhi","in TheAPP--onPushDataChanged   没有账号");
                }

            }

            @Override
            public void onLocationChanged(final double lng, final double lat) {
                super.onLocationChanged(lng, lat);
                LoginUser lu = LoginUser.get();
                if (lu == null || !lu.valid()) {
                    return;
                }

                backHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject paras = new JSONObject();
                            paras.put("lat", String.valueOf(loc_service.myGP[1]));
                            paras.put("lng", String.valueOf(loc_service.myGP[0]));
                            CZNetUtils.postCZHttp("webservice/gps/faSongDingWei", paras.toString());
                            //skip result
                        } catch (Exception e) {
                            //MyLog.LOGE(e);
                            loc_err_cnt++;
                            if (loc_err_cnt >= 5) {
                                loc_err_cnt = 0;
                                try {
                                    loc_service.stopService();
                                    TheApp.sHandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                loc_service.start();
                                            } catch (Throwable ex2) {
                                                MyLog.LOGE(ex2);
                                            }
                                        }
                                    }, 1000 * 30);
                                } catch (Exception ex) {
//                                    MyLog.LOGE(ex);
                                    try {
                                        loc_service.start();
                                    } catch (Exception ex2) {
                                        MyLog.LOGE(ex2);
                                    }
                                }
                            }
                        }
                    }
                });
            }

            @Override
            public void onConnChanged() {
                Log.e("JPush","-->onConnChanged");
                super.onConnChanged();
                // rebind jpush
                JPushReceiver.bindJPush();
//                JPushReceiverCustom.bindJPush();
                PVSQGApp pv = PVSQGApp.thiz == null ? null : PVSQGApp.thiz.get();
                if (pv != null) {
                    pv.reloadHuiZong();
                }
            }

            @Override
            public void onLoginUserChanged() {
                Log.e("JPush","-->onLoginUserChanged");
                JPushReceiver.bindJPush();
//                JPushReceiverCustom.bindJPush();
            }
        };
        EventHandler.addEventHandler(new Enum[]{
                        EventHandler.Events.onLocationChanged,
                        EventHandler.Events.onConnChanged,
                        CZEvents.Event.onLoginUserChanged,
                        CZEvents.Event.onPushDataChanged,
                }, evtHandler
        );
        sHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sHandler.removeCallbacks(this);
                if (loc_service == null) {
                    loc_service = new LocationService(Constant.GPS_INTERVAL);
                }
                try {
                    loc_service.start();
                } catch (Exception e) {
                    // sHandler.postDelayed(this, 3000);
                }
            }
        }, 3000);

        initJPush();
//        StatService.start(this); // baidu stat
    }

    private EventHandler evtHandler;
    public LocationService loc_service;

    private void initJPush() {
        Log.e("JPush","-->initJPush");
        JPushInterface.setDebugMode(BuildConfig.DEBUG);
        JPushInterface.init(this);
        //JPushInterface.resumePush(sInst);
        Runnable push_check_task = new Runnable() {
            @Override
            public void run() {
                sHandler.removeCallbacks(this);
                // if not login to jpush, restart it
                String regid = JPushInterface.getRegistrationID(sInst);
                Log.e("JPush","regid-->"+regid);
                if (TextUtils.isEmpty(regid)) {
                    JPushInterface.init(sInst);
                    sHandler.postDelayed(this, PUSH_INTERVAL);
                }
            }
        };
        sHandler.postDelayed(push_check_task, PUSH_INTERVAL);
    }

    private final static long PUSH_INTERVAL = 10 * 1000;


}
