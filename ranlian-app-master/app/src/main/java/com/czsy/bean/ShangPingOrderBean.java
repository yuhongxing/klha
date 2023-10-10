package com.czsy.bean;

import com.czsy.Constant;
import org.json.JSONException;
import org.json.JSONObject;

// 商品订单bean
public class ShangPingOrderBean extends CommonOrderBean { //implements IBaseBean {

    public ShangPingOrderBean() {
        dingDanLeiXing = Constant.dingdan_type_shangpin;
    }

    public JSONObject toPrepayOrderJson() throws JSONException {
        JSONObject ret = new JSONObject();
        ret.put("dingDanHao", dingDanHao);
        ret.put("sid", dingDanHao);
        return ret;
    }
}

