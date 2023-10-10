package com.czsy.bean;

import android.text.TextUtils;
import com.czsy.CZEvents;
import com.czsy.Constant;
import com.czsy.push.PushManager;
import mylib.app.EventHandler;
import mylib.utils.FileUtils;
import mylib.utils.SPUtils;

public class LoginUser implements IBaseBean {

    public String userAccount; //
    public String userName; //
    public long id;
    public String token;
    public String telNum;
    public int sex;
    public String email;
    public long orgId;
    //public long jobId;
    public int workStatus;
    public long roleId;
    public int isDel;
    public int status;
    public String accountState;
    public String userState;
    public String address;
    public int appRoleType = -1;
    //public UserOrgBean userOrg;
    public String lastPanDianShiJian;
    public String jobName;

    public boolean validRole() {
        return appRoleType <= Constant.role_max &&
                appRoleType >= Constant.role_min;
    }

    private final static String local_file = "login_user";
    private static LoginUser sInst;

    public void save() {
        SPUtils.get().setString(Constant.login_name, this.userAccount);
        FileUtils.saveObject(local_file, this);
        sInst = this;
        EventHandler.notifyEvent(CZEvents.Event.onLoginUserChanged);

    }

    public boolean valid() {
        return validRole() &&
                !TextUtils.isEmpty(userAccount) && id > 0 && !TextUtils.isEmpty(token);
    }

    public static LoginUser get() {
        if (sInst == null) {
            sInst = (LoginUser) FileUtils.getObject(local_file, LoginUser.class);
            if (sInst == null) {
                sInst = new LoginUser();
            }
        }
        return sInst;
    }

    public void logout() {
        token = null;
        userName = null;
        id = -1;
        save();
//        PushManager.get().clearPushData();
    }
}
