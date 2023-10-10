package com.czsy.bean;

import org.json.JSONObject;

public class YuJieSuanBean implements IBaseBean {
    /*
     *"yingShouJinE":1127.0,"zongShangPinJinE":1125.0,
     * "louCengJinE":0.0,
     * "zongYaJin":2.0,"zongYuQiJinE":0.0,"zongYuQi":0.0,"youHuiJinE":0.0,"youHuiJuanId":null,
     * "zhongPingList":[{"gangPingId":"10186","gangPingHao":"HD222222","guiGe":1,"guiGeName":"5kg",
     * "gangPingJinE":1125.0,"yaJinJinE":2.0,"yuQi":null,"yuQiJinE":null,"yaJinTiaoId":null}],
     * "kongPingList":[],"shangPingList":null}
     */
    public double yingShouJinE;
    public double zongShangPinJinE;
    public double louCengJinE;
    public double zongYaJin;
    public double zongYuQiJinE;
    public double zongYuQi;
    public double youHuiJinE;
    public Long youHuiJuanId;
    public Long yaJinTiaoId;
    public int jiFen;
    public double yiZhiFu;

    transient public JSONObject json;
}
