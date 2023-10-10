package mylib.app;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import java.util.concurrent.atomic.AtomicInteger;

import cn.jpush.android.api.JPushInterface;
import mylib.app.EventHandler.Events;
import mylib.utils.FileUtils;
import mylib.utils.Utils;

@SuppressLint("DefaultLocale")
public abstract class AndroidApp extends Application {

    public static AtomicInteger showingActivities = new AtomicInteger(0);

    public static Intent pendingIntent = null;

    public static AndroidApp sInst;

    public static Handler sHandler;
    protected Handler backHandler;


    protected void handleBackMsg(Message msg) throws Throwable {

    }

    private void initBackThread() {
        if (backHandler != null) {
            return;
        }
        HandlerThread ht = new HandlerThread("BackThread");
        ht.start();
        backHandler = new Handler(ht.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                try {
                    handleBackMsg(msg);
                } catch (Throwable t) {
                    MyLog.LOGE(t);
                }
            }
        };
    }

    protected void addBroadcastIntent(String process, BroadcastReceiver br, IntentFilter ift) {
        ift.addAction(Intent.ACTION_MEDIA_MOUNTED);
        ift.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        ift.addAction(Intent.ACTION_MEDIA_REMOVED);
        ift.addAction(Intent.ACTION_MEDIA_EJECT);
        ift.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        ift.addAction(Intent.ACTION_SCREEN_OFF);
        ift.addAction(Intent.ACTION_SCREEN_ON);
        ift.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        // pkgs
        if (isRemoteProcess(process)) { // for remote process
            IntentFilter ift2 = new IntentFilter();
            ift2.addDataScheme("package");
            ift2.addAction(Intent.ACTION_PACKAGE_ADDED);
            ift2.addAction(Intent.ACTION_PACKAGE_REPLACED);
            ift2.addAction(Intent.ACTION_PACKAGE_CHANGED);
            ift2.addAction(Intent.ACTION_PACKAGE_REMOVED);
            ift2.addAction(Intent.ACTION_PACKAGE_RESTARTED);
            //ift.addAction(Intent.ACTION_PACKAGE_DATA_CLEARED);
            registerReceiver(br, ift2);
        }
    }

    protected boolean isRemoteProcess(String process) {
        return process != null && process.endsWith(":remote");
    }

    protected void init(String process) {
        // for all processes
        initBackThread();
        IntentFilter ift = new IntentFilter();
        BroadcastReceiver br = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent intent) {
                try {
                    handleBroadcastIntent(arg0, intent);
                } catch (Throwable t) {
                    MyLog.LOGE(t);
                }
            }
        };
        addBroadcastIntent(process, br, ift);
        this.registerReceiver(br, ift);


        boolean isLocal = getPackageName().equals(process);
        if (isLocal) { // only for local process
            initLocalProcess(process);
        } else { // for remote process
            initRemoteProcess(process);
        }
    }

    protected void initLocalProcess(String process) {
        FileUtils.checkDir();
        // jpush
        JPushInterface.setDebugMode(false); //BuildConfig.DEBUG); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);            // 初始化 JPush
    }

    protected void initRemoteProcess(String process) {
    }

    protected void handleBroadcastIntent(Context ctx, Intent intent) {

        String act = intent.getAction();
        if (TextUtils.isEmpty(act)) {
            return;
        }
        if (act.equals(Intent.ACTION_MEDIA_MOUNTED) || act.equals(Intent.ACTION_MEDIA_MOUNTED)
                || act.equals(Intent.ACTION_MEDIA_REMOVED) || act.equals(Intent.ACTION_MEDIA_EJECT)) {
            FileUtils.checkDir();
            return;
        } else if (Intent.ACTION_SCREEN_OFF.equals(act)) {
            EventHandler.notifyEvent(Events.onToggleScreen, false);
            return;
        } else if (Intent.ACTION_SCREEN_ON.equals(act)) {
            EventHandler.notifyEvent(Events.onToggleScreen, true);
            return;
        } else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(act)) {
            EventHandler.notifyEvent(Events.onConnChanged);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInst = this;
        sHandler = new Handler(this.getMainLooper());
        String pname = Utils.getProcessName();
        init(pname);
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        sInst = null;
        if (backHandler != null) {
            Looper l = backHandler.getLooper();
            l.quit();
            backHandler = null;
        }
    }

    public Handler getBackHandler() {
        if (backHandler == null) {
            initBackThread();
        }
        return backHandler;
    }


    public abstract Class<? extends EventHandler> getEventHandlerClass();

    public abstract int getAppNameRes();

    abstract public int getAppIconRes();


}
