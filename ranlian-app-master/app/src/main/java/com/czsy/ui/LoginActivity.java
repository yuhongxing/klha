package com.czsy.ui;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.czsy.CZBackTask;
import com.czsy.CZEvents;
import com.czsy.CZNetUtils;
import com.czsy.Constant;
import com.czsy.TheApp;
import com.czsy.android.R;
import com.czsy.bean.LoginUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import mylib.app.BackTask;
import mylib.app.BaseActivity;
import mylib.app.EventHandler;
import mylib.app.LocationService;
import mylib.app.MyLog;
import mylib.ui.PageViewContainer;
import mylib.utils.SPUtils;
import mylib.utils.Utils;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private String TAG = "Login";

    View mMainView;
    EditText et_name, et_pass;
    private LinearLayout line_test;
    private TextView tv_yvming;
    private Button btn_test0,btn_test1, btn_test2, btn_test3, btn_test4, btn_test5, btn_test6;
    private String versionName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.bg_blue));
        }

        LoginActivity act = this;
        mMainView = View.inflate(act, R.layout.pv_login1, null);
        et_pass = mMainView.findViewById(R.id.et_pass);
        et_name = mMainView.findViewById(R.id.et_name);
        line_test = mMainView.findViewById(R.id.line_test);
        btn_test0 = mMainView.findViewById(R.id.btn_test0);
        btn_test1 = mMainView.findViewById(R.id.btn_test1);
        btn_test2 = mMainView.findViewById(R.id.btn_test2);
        btn_test3 = mMainView.findViewById(R.id.btn_test3);
        btn_test4 = mMainView.findViewById(R.id.btn_test4);
        btn_test5 = mMainView.findViewById(R.id.btn_test5);
        btn_test6 = mMainView.findViewById(R.id.btn_test6);
        tv_yvming = mMainView.findViewById(R.id.tv_yvming);

        setContentView(mMainView);

        try {
            PackageInfo pi = act.getPackageManager().getPackageInfo(act.getPackageName(), 0);
            TextView tv_version = mMainView.findViewById(R.id.tv_version);
            versionName = pi.versionName;
            tv_version.setText(act.getString(R.string.fmt_version, versionName));
        } catch (PackageManager.NameNotFoundException e) {
        }

        mMainView.findViewById(R.id.tv_login).setOnClickListener(this);
        btn_test0.setOnClickListener(this);
        btn_test1.setOnClickListener(this);
        btn_test2.setOnClickListener(this);
        btn_test3.setOnClickListener(this);
        btn_test4.setOnClickListener(this);
        btn_test5.setOnClickListener(this);
        btn_test6.setOnClickListener(this);
        tv_yvming.setText(CZNetUtils.svr_host);
        String name = SPUtils.get().getString(Constant.login_name);
        if (!TextUtils.isEmpty(name)) {
            et_name.setText(name);
        }

        if (CZNetUtils.svr_host.equals("http://47.95.224.199/") || CZNetUtils.svr_host.equals("http://192.168.0.124:8088/")) {
            line_test.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected PageViewContainer getPVC() {
        return null;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.tv_login == id) {


            final String u_name = et_name.getText().toString().trim();
            final String u_pass = et_pass.getText().toString().trim();
            if (TextUtils.isEmpty(u_name) || TextUtils.isEmpty(u_pass)) {
                return;
            }
            loginS(u_name, u_pass);
        }
//        else if (id == R.id.btn_test1) {
//            loginS("tzc1", "88886666");
//        } else if (id == R.id.btn_test2) {
//            loginS("tzc2", "88886666");
//        } else if (id == R.id.btn_test3) {
//            loginS("tzc3", "88886666");
//        } else if (id == R.id.btn_test4) {
//            loginS("xjy", "88886666");
//        } else if (id == R.id.btn_test5) {
//            loginS("wxy", "88886666");
//        } else if (id == R.id.btn_test6) {
//            loginS("gongyingzhanzz", "88886666");
//        }


        else if (id == R.id.btn_test0) {
            loginS("hongqi001", "123456");
        } else if (id == R.id.btn_test1) {
            loginS("红旗充装站", "123456");
        } else if (id == R.id.btn_test2) {
            loginS("红旗充装站运输员", "123456");
        } else if (id == R.id.btn_test3) {
            loginS("红旗供应站配送员", "123456");
        } else if (id == R.id.btn_test4) {
            loginS("红旗巡检员", "123456");
        } else if (id == R.id.btn_test5) {
            loginS("红旗维修员", "123456");
        } else if (id == R.id.btn_test6) {
            loginS("红旗供应站", "123456");
        }
    }

    private void loginS(final String u_name, final String u_pass) {
        final LoginActivity act = this;
        act.showProgress();

        BackTask.post(new CZBackTask(act) {
            @Override
            protected void parseResult(JSONObject ret) throws Exception {
                JSONObject result = ret.getJSONObject("result");
                JSONObject juo = result.getJSONObject("userInfo");
                LoginUser lu = Constant.gson.fromJson(juo.toString(), LoginUser.class);
                if (!lu.validRole()) {
                    lu.appRoleType = result.optInt("userAppRole", -1);
                }
                if (!lu.validRole()) {
                    err_msg = act.getString(R.string.tip_not_auth);
                    return;
                }
                //if (TextUtils.isEmpty(lu.token)) {
                lu.token = result.getString("token");
                //}
                lu.save();
                err_msg = null;
            }

            @Override
            protected String getInputParam() throws Exception {
                JSONObject paras = new JSONObject();
                double[] pos = LocationService.myGP;
//                Log.d(TAG, "pos[0]-->" + pos[0] + "   pos[1]-->" + pos[1]);

                paras.put("userAccount", u_name);
                paras.put("password", u_pass);
                paras.put("latitude", pos[1]);
                paras.put("longitude", pos[0]);
                return paras.toString();
            }

            @Override
            protected String getURL() {
                return "user/login";
            }

            @Override
            protected void runFront2() {
                LoginUser lu = LoginUser.get();

                if (lu.valid()) {
                    subMobileModel(String.valueOf(lu.id));
                    act.startActivity(new Intent(act, MainActivity.class));
                    act.finish();
                } else {
                    Utils.toastLONG("登录失败");
                }
            }
        });
    }

    /**
     * 上传手机型号
     */
    private void subMobileModel(final String userNumber) {
        LoginActivity act = this;
        final String deviceBrand = Utils.getDeviceBrand();//手机厂商
        final String systemModel = Utils.getSystemModel();//手机型号
        final String systemVersion = Utils.getSystemVersion();//Android系统版本号

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        final String mTime = formatter.format(curDate);
        Log.e("xing", mTime);

//        act.showProgress();
        BackTask.post(new CZBackTask(act) {

            @Override
            public void runBack() {
                super.runBack();

                double lat = LocationService.lat;
                double lng = LocationService.lng;
                JSONObject paras = new JSONObject();

                try {
                    paras.put("lat", String.valueOf(lat));
                    paras.put("lng", String.valueOf(lng));

                    Log.d(TAG, "lat-->" + lat + "   lng-->" + lng);
                    CZNetUtils.postCZHttp("webservice/gps/faSongDingWei", paras.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void parseResult(JSONObject ret) throws Exception {
                Log.d("xing", "手机参数：" + ret.toString());
            }

            @Override
            protected String getInputParam() throws Exception {
                JSONObject paras = new JSONObject();
                paras.put("id", userNumber);
                paras.put("shouJiPinPai", deviceBrand);
                paras.put("shouJiXingHao", systemModel);
                paras.put("xiTongBanBen", systemVersion);
                paras.put("shangBaoShiJian", mTime);
                paras.put("ruanJianBanBen", versionName);
                return paras.toString();
            }

            @Override
            protected String getURL() {
                return "user/loginPutData";
            }

            @Override
            protected void runFront2() {

            }
        });
    }
}
