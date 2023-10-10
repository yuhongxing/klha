package com.czsy.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;

import com.czsy.*;
import com.czsy.android.BuildConfig;
import com.czsy.android.R;
import com.czsy.bean.LoginUser;
import com.czsy.ui.changzhan.PVCZMain;
import com.czsy.ui.gongyingzhan.GYZZhanZhangMain;
import com.czsy.ui.sqg.PVMainSQG;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import mylib.app.BackFrontTask;
import mylib.app.BackTask;
import mylib.app.EventHandler;
import mylib.app.MyLog;
import mylib.ui.AbstractPageView;
import mylib.utils.Global;
import mylib.utils.Utils;

public class MainActivity extends AbsCZBaseActvity {
    public MainPVC main_pvc;

    @Override
    public MainPVC getPVC() {
        return main_pvc;
    }

    protected boolean checkUser() {
        LoginUser lu = LoginUser.get();
        if (!lu.valid()) {
            //main_pvc.push(new PVLogin(this));
            finish();
            startActivity(new Intent(this, LoginActivity.class));
            return false;
        }
        return true;
    }

    private CZEvents evt = new CZEvents() {
        @Override
        public void onLocationChanged(double lng, double lat) {
            super.onLocationChanged(lng, lat);
        }

        @Override
        public void onLocationError(String errString) {
            super.onLocationError(errString);
        }

        @Override
        public void onLoginUserChanged() {
            super.onLoginUserChanged();
            LoginUser lu = LoginUser.get();
            if (lu == null || !lu.valid()) {
                finish();
                Intent i = new Intent();
                i.setClass(TheApp.sInst, LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                TheApp.sInst.startActivity(i);
            }
        }


    };

    @Override
    protected EventTypes getEvents() {
        return new EventTypes(new Enum[]{
                EventHandler.Events.onLocationChanged,
                EventHandler.Events.onLocationError,
                CZEvents.Event.onLoginUserChanged,
        }, evt);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
    }

    protected void init() {
        main_pvc = new MainPVC(this);
        setContentView(main_pvc);
        LoginUser lu = LoginUser.get();
        Log.e("token", lu.token);
        if (lu.appRoleType == Constant.role_song_qi_gong) {//送气工
            main_pvc.push(new PVMainSQG(this));
        } else if (lu.appRoleType == Constant.role_chang_zhan) {//场站
            main_pvc.push(new PVCZMain(this));
        } else if (lu.appRoleType == Constant.role_yun_shu_yuan//运输员
                || lu.appRoleType == Constant.role_xun_jian//巡检员
                || lu.appRoleType == Constant.role_wei_xiu//维修员
                || lu.appRoleType == Constant.role_cz_zhanzhang)//场站--站长/经理
        {
            main_pvc.push(new PVMainKt(this, lu.appRoleType));
        } else if (lu.appRoleType == Constant.role_gyz_zhanzhang) {//供应站-站长
            main_pvc.push(new GYZZhanZhangMain(this));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, KAService.class);
        intent.putExtra("notify", false);
        intent.putExtra("isJPush", false);
        startService(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        Bundle b = i.getExtras();
        MyLog.LOGD("extra: " + (b == null ? "" : b.toString()));
        LoginUser lu = LoginUser.get();
        if (!checkUser()) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            init();
        }


        // check permission
        if (Build.VERSION.SDK_INT >= 23) {
            Log.e("权限", "Build.VERSION.SDK_INT >= 23");
            PackageManager packageManager = getPackageManager();
            String packageName = getPackageName();
            String[] perm = new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            };//READ_PHONE_STATE
            boolean need_perm = false;
            for (String permission : perm) {
                int per = packageManager.checkPermission(permission, packageName);
                if (PackageManager.PERMISSION_DENIED == per) {
                    Log.e("权限", "per-->" + per);
                    need_perm = true;
                    break;
                }
            }
            if (need_perm) {
                requestPermissions(perm, req_perm);
            }
        }

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()), 0);

    }

    protected PendingIntent mPendingIntent;
    protected NfcAdapter mNfcAdapter;

    @Override
    public void onResume() {
        super.onResume();
        checkNFC(true);
        // 检查更新
        checkUpdata();
    }

    @Override
    public void onPause() {
        super.onPause();
        // checkNFC(false);
    }

    private void checkNFC() {
        if (mNfcAdapter != null && !mNfcAdapter.isEnabled()) {
            main_pvc.post(new Runnable() {
                @Override
                public void run() {
                    final Context act = MainActivity.this;
                    AlertDialog.Builder ab = new AlertDialog.Builder(act);
                    ab.setTitle(R.string.app_name).setMessage(R.string.tip_use_nfc);
                    ab.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
                            act.startActivity(intent);

                        }
                    });
                    ab.setCancelable(false);
                    ab.show();
                }
            });

        }
    }

    public void checkNFC(boolean en) {
        if (mNfcAdapter == null) {
            return;
        }
        AbstractPageView cur_pv = main_pvc.getCurrentPage();
        try {
            if (en && cur_pv != null && (cur_pv instanceof INFCHandler || cur_pv instanceof AbsPVNFC)) {
                checkNFC();
                mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
            } else {
                mNfcAdapter.disableForegroundDispatch(this);
            }
        } catch (Exception e) {
            MyLog.LOGE(e);
            Utils.toastLONG("NFC设备故障，请重启程序");
            finish();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        //super.onNewIntent(intent);
        if (null == intent) {
            return;
        }
        AbstractPageView cur_pv = main_pvc.getCurrentPage();
        if (cur_pv != null && (cur_pv instanceof INFCHandler)) {
            INFCHandler nfc = (INFCHandler) cur_pv;
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if (tag == null) {
                MyLog.LOGE("bad nfc tag");
                return;
            }
            String xpbm = NFCReader.getXPBM(tag);
            String chip_sn = Convert.bytesToHexString(tag.getId());

            nfc.onNFCIntent(new INFCHandler.NFCInfo(xpbm, chip_sn));

        }
    }

    private final static int req_storage = 1123;
    private final static int req_perm = 1124;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == req_storage && resultCode == Activity.RESULT_OK) {
            showUpdateInfo(UpdateManager.get().up_info);
        }
    }

    //检查更新
    private void checkUpdata() {
        BackTask.post(new BackFrontTask() {
            UpdateManager.UpdateInfo updateInfo = null;

            @Override
            public void runFront() {

                if (updateInfo != null) {
                    showUpdateInfo(updateInfo);
                }
            }

            @Override
            public void runBack() {
                try {
                    JSONObject jsonObject = CZNetUtils.postCZHttp("config/queryThisVersion", null);
                    updateInfo = Constant.gson.fromJson(
                            jsonObject.getJSONObject("result").toString(), UpdateManager.UpdateInfo.class);
                } catch (IOException e) {
                    e.printStackTrace();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //显示更新提示框
    private void showUpdateInfo(final UpdateManager.UpdateInfo ui) {
        if (ui == null) {
            checkUpdata();
            return;
        }
        PackageInfo pi = null;
        try {
            pi = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            finish();
            return;
        }
        Log.d("checkUpdate", "ui.versionCode==>" + ui.versionCode + "  |  my.versionCode==>" + pi.versionCode);

        int my_ver = pi.versionCode;
        if (my_ver == ui.versionCode) {
            return;
        }
        final boolean force = true; //my_ver > ui.versionCode || my_ver < ui.min_vcode;
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setCancelable(!force);
        ab.setMessage(ui.log);
        ab.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Activity act = MainActivity.this;
                String s = Manifest.permission.WRITE_EXTERNAL_STORAGE;
                if (!Constant.hasPermission(act, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    ActivityCompat.requestPermissions((Activity) act,
                            new String[]{s}, req_storage);
                    return;
                }
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                i.setData(Uri.parse(CZNetUtils.svr_host + ui.downloadUrl));
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                MainActivity.this.startActivity(i);
                finish();
            }
        });

        ab.setTitle("更新提示").show();
    }

}
