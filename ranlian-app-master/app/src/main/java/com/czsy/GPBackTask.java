package com.czsy;

import com.czsy.android.R;
import com.czsy.bean.GangPingBean;

import org.json.JSONObject;

import mylib.app.BaseActivity;
import mylib.utils.Utils;

public abstract class GPBackTask extends CZBackTask {
    final GangPingBean gp;

    public GPBackTask(BaseActivity act, GangPingBean b) {
        super(act);
        gp = b;
    }

    @Override
    protected void parseResult(JSONObject jdata) throws Exception {

    }

    @Override
    protected String getInputParam() throws Exception {
        JSONObject j = new JSONObject();
        //j.put("gangPingHao", gp.gangPingHao);
        j.put("id", gp.id);
        return j.toString();
    }


    @Override
    protected void runFront2() {
        Utils.toast(R.string.tip_op_ok);
    }
}
