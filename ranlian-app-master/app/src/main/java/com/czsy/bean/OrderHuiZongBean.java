package com.czsy.bean;

// 配送汇总返回的结果
public class OrderHuiZongBean implements IBaseBean {
    public long ts;
    public long product_id;
    public String product_name;
    public double price;
    public int heavy_ping;
    public double money_wanted, money_real;

}
