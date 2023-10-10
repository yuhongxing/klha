package com.czsy;

import android.text.TextUtils;

import com.czsy.android.R;

import org.json.JSONObject;

import java.io.IOException;
import java.net.ConnectException;

import mylib.app.BackFrontTask;
import mylib.app.BaseActivity;
import mylib.app.MyLog;
import mylib.utils.Utils;

abstract public class CZBackTask implements BackFrontTask {

    final BaseActivity act;
    protected String err_msg;

    public CZBackTask(BaseActivity act) {
        this.act = act;
    }

    @Override
    public void runFront() {
        if (act != null) {
            act.hideProgress();
            if (act.isFinishing()) {
                return;
            }
        }
        if (!TextUtils.isEmpty(err_msg)) {
            Utils.toastLONG(err_msg);
            return;
        }
        runFront2();
    }

    @Override
    public void runBack() {
        try {
            String url = getURL();
            if (url == null) {
                return;
            }
            String s = getInputParam();
            JSONObject ret = CZNetUtils.postCZHttp(url, s);
            err_msg = ret.optString("message");
            int code = ret.optInt("code", 200);
            if (code != 200) {
                throw new CZNetUtils.CZNetErr(code, ret);
            }
            //JSONObject result = ret.isNull("result") ? null : ret.getJSONObject("result");
            err_msg = null; // ok
            parseResult(ret);

        } catch (Exception e) {
            MyLog.LOGE(e);
            if (err_msg == null && (e instanceof IOException)) {
                err_msg = TheApp.sInst.getString(R.string.tip_network_error);
            }
            onError(e);
            if (TextUtils.isEmpty(err_msg)) {
                err_msg = TheApp.sInst.getString(R.string.tip_common_err);
            }
        }
    }

    protected void onError(Exception e) {

    }

    /**
     * 解析数据
     * @param jdata 返回的数据
     * @throws Exception
     */
    abstract protected void parseResult(JSONObject jdata) throws Exception;

    /**
     * 请求的参数：json格式
     * @return
     * @throws Exception
     */
    abstract protected String getInputParam() throws Exception;

    /**
     * 请求地址
     * @return
     */
    abstract protected String getURL();

    abstract protected void runFront2();
}
