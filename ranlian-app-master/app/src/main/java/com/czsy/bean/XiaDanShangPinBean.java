package com.czsy.bean;

// 订单中的下单商品
public class XiaDanShangPinBean implements IBaseBean {
    /**
     * {"id":219,"dingDanHao":1001190421132551,
     * "shangPin":"液化气","guiGe":"100",
     * "danJia":3,"shuLiang":null}
     */
    public long id;
    public String dingDanHao;
    public String shangPin;
    public String guiGe;
    public double danJia;
    public int shuLiang;
}
