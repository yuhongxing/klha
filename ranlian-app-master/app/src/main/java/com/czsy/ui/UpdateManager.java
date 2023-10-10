package com.czsy.ui;

import com.czsy.CZNetUtils;
import com.czsy.Constant;
import com.czsy.TheApp;
import com.czsy.android.BuildConfig;

import java.io.Serializable;
import java.lang.ref.WeakReference;

import mylib.utils.FileUtils;

import org.json.JSONObject;

class UpdateManager implements Runnable {
    private static UpdateManager sInst;
    private static final String update_file = "update_file";


    private UpdateManager() {
        up_info = (UpdateInfo) FileUtils.getObject(update_file, UpdateInfo.class);
    }

    public static UpdateManager get() {
        if (sInst == null) {
            sInst = new UpdateManager();
        }
        return sInst;
    }

    @Override
    public void run() {
        try {
            JSONObject j = new JSONObject();
//            j.put("package", BuildConfig.APPLICATION_ID);
//            j.put("vcode", BuildConfig.VERSION_CODE);
//            JSONObject json = CZNetUtils.postCZHttp("sapp/getAppPkgInfo", j.toString());
            JSONObject json = CZNetUtils.postCZHttp("config/queryThisVersion",null);
//            JSONObject json = CZNetUtils.getCZHttp("config/queryThisVersion");
            UpdateInfo ui = Constant.gson.fromJson(
                    json.getJSONObject("result").toString(), UpdateInfo.class);
            up_info = ui;
            up_info.last_update_time = System.currentTimeMillis();
//            FileUtils.saveObject(update_file, up_info);
        } catch (Throwable t) {
            up_info = null;
        }
        Runnable cb = up_callback == null ? null : up_callback.get();
        if (cb != null) {
            cb.run();
        }
    }

    public UpdateInfo up_info;

    public static class UpdateInfo implements Serializable {
        public String downloadUrl;
        public String log;
        public int versionCode = -1;
        //public int min_vcode = -1; // 支持的最小vcode

        public long last_update_time;
    }

    private WeakReference<Runnable> up_callback;

    public void checkUpdate(Runnable callback) {
        up_callback = new WeakReference<>(callback);
        TheApp.sInst.getBackHandler().removeCallbacks(this);
        TheApp.sInst.getBackHandler().post(this);
    }
}
