package com.czsy.bean;

import java.util.List;

public class YunShuDanBean implements IBaseBean {


    /**
     * {"id":"2","changZhanId":"3","changZhan":null,"gongYingZhanId":"4",
     * "gongYingZhan":null,"gangPingCount":null,"gangPingIdList":null,
     * "yunShuYuanId":"12","yunShuYuanName":"运输大队长","chePai":"",
     * "chuangJianRiQi":"2019-09-22 22:47:49","type":1,"typeName":"供应站-》厂站"}
     */
    public long id;
    public long  changZhanId;
    public String changZhan;
    public long gongYingZhanId;
    public Integer gangPingCount;
    public String gongYingZhan;
    //public List<Long> gangPingIdList;
    public long yunShuYuanId;
    public String yunShuYuanName;
    public String chePai; // 车牌
    public String chuangJianRiQi;
    public int type;
    public String typeName;
    public String statusName;


}
