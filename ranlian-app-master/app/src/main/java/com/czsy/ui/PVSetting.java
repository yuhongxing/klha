package com.czsy.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.czsy.CZBackTask;
import com.czsy.CZConfig;
import com.czsy.CZNetUtils;
import com.czsy.android.R;
import com.czsy.bean.LoginUser;

import mylib.app.BackFrontTask;
import mylib.app.BackTask;
import mylib.utils.Utils;

import org.json.JSONObject;

public class PVSetting extends AbsPVBase implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {
    public PVSetting(MainActivity a) {
        super(a);
    }

    public PVSetting(MainActivity a, int type) {
        super(a);
        this.type = type;
    }

    Switch cb_vibrat, cb_sound;
    private ConstraintLayout line_xiaoxi;
    private TextView tv_name, tv_zhiwu;
    private int type = -1;//-1时正常显示，0时隐藏

    @Override
    protected void createMainView(Context ctx) {
        mMainView = View.inflate(act, R.layout.pv_setting1, null);
        mMainView.findViewById(R.id.btn_logout).setOnClickListener(this);
        mMainView.findViewById(R.id.btn_changepass).setOnClickListener(this);
        tv_name = mMainView.findViewById(R.id.tv_name);
        tv_zhiwu = mMainView.findViewById(R.id.tv_zhiwu);
        line_xiaoxi = mMainView.findViewById(R.id.line_xiaoxi);
        cb_vibrat = mMainView.findViewById(R.id.cb_vibrat);
        cb_sound = mMainView.findViewById(R.id.cb_sound);
        cb_vibrat.setChecked(CZConfig.getMsgVibrate());
        cb_sound.setChecked(CZConfig.getMsgSound());
        cb_vibrat.setOnCheckedChangeListener(this);
        cb_sound.setOnCheckedChangeListener(this);

        LoginUser lu = LoginUser.get();
        tv_name.setText(lu.userName);
        tv_zhiwu.setText(lu.jobName);
        if (type == 0) {
            line_xiaoxi.setVisibility(View.GONE);
        }

        try {
            PackageInfo pi = act.getPackageManager().getPackageInfo(act.getPackageName(), 0);
            TextView tv_version = mMainView.findViewById(R.id.tv_version);
            tv_version.setText(act.getString(R.string.fmt_version, pi.versionName));
        } catch (PackageManager.NameNotFoundException e) {
        }

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.btn_changepass == id) {
            final Dialog d = new Dialog(act);
            d.requestWindowFeature(Window.FEATURE_NO_TITLE);
            Window window = d.getWindow();
            window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(layoutParams);

            window.setBackgroundDrawableResource(android.R.color.transparent);
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

            d.setContentView(R.layout.dialog_changepass);
            d.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView et_old_pass = d.findViewById(R.id.et_old_pass);
                    TextView et_new_pass = d.findViewById(R.id.et_new_pass);
                    TextView et_new_pass2 = d.findViewById(R.id.et_new_pass2);
                    final String old_pass = et_old_pass.getText().toString().trim();
                    final String new_pass = et_new_pass.getText().toString().trim();
                    final String new_pass2 = et_new_pass2.getText().toString().trim();
                    if (TextUtils.isEmpty(old_pass)
                            || TextUtils.isEmpty(new_pass)
                            || TextUtils.isEmpty(new_pass2)) {
                        Utils.toastLONG("请填写完整");
                        return;
                    }
                    if (!new_pass.equals(new_pass2)) {
                        Utils.toastLONG("2次新密码不一致");
                        return;
                    }
                    act.showProgress();
                    BackTask.post(new CZBackTask(act) {
                        @Override
                        protected void parseResult(JSONObject jdata) throws Exception {

                        }

                        @Override
                        protected String getInputParam() throws Exception {
                            JSONObject j = new JSONObject();
                            LoginUser lu = LoginUser.get();
                            j.put("oldPassword", old_pass);
                            j.put("userAccount", lu.userAccount);
                            j.put("newFirstPassword", new_pass);
                            j.put("newSecondPassword", new_pass2);
                            return j.toString();
                        }

                        @Override
                        protected String getURL() {
                            return "user/xiuGaiMiMa";
                        }

                        @Override
                        protected void runFront2() {
                            Utils.toastLONG("密码修改成功，请重新登录");
                            d.dismiss();
                            LoginUser.get().logout();
                        }
                    });
                }
            });

            d.show();
        } else if (R.id.btn_logout == id) {
            AlertDialog.Builder ab = new AlertDialog.Builder(act);
            ab.setTitle(R.string.app_name).setMessage("真的要退出吗？");
            ab.setNegativeButton(android.R.string.cancel, null);
            ab.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    LoginUser lu = LoginUser.get();
                    if (lu.valid()) {
                        lu.logout();
                    }
                    Intent ii = new Intent(act, LoginActivity.class);
                    act.startActivity(ii);
                    act.finish();
                }
            });
            ab.show();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (compoundButton == cb_sound) {
            CZConfig.setMsgSound(b);
        } else if (compoundButton == cb_vibrat) {
            CZConfig.setMsgVibrate(b);
        }
    }
}
