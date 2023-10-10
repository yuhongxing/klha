package com.czsy.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.Window;
import android.view.WindowManager;
import com.czsy.android.R;
import mylib.app.BaseActivity;
import mylib.utils.Utils;

import java.util.LinkedList;
import java.util.List;

abstract public class AbsCZBaseActvity extends BaseActivity {

    private List<String> getBadPermission() {
        List<String> bad_permission = new LinkedList<>();

        String[] perms = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
        };
        for (String s : perms) {
            int p = ActivityCompat.checkSelfPermission(this, s);
            //int p = Ac checkSelfPermission(s);
            if (p != PackageManager.PERMISSION_GRANTED) {
                bad_permission.add(s);
            }
        }
        return bad_permission;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.bg_blue));
        }

        // permission check
        List<String> bad_permission = getBadPermission();
        if (!bad_permission.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    bad_permission.toArray(new String[0]), 1222);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1222) {
            if (!getBadPermission().isEmpty()) {
                Utils.toastLONG("权限未通过，无法使用！");
                finish();

                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
