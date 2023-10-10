package com.czsy.bean;

public class ShangPinBean implements IBaseBean {
//    /**
//     * "id": 3,
//     * "mingCheng": "天然气煤气灶",
//     * "pinLei": "厨具类",
//     * "pinLeiId": 1,
//     * "danJia": 10.5,
//     * "jiFenJiaZhi": 10050,
//     * "tuPian": "/xxx/xx.jpg",
//     * "chuangJianRiQi": "2019-06-09 12:47:26",
//     * "lastUpdateDate": "2019-06-09 04:51:42",
//     * "isDel": 0,
//     * "xiangQing": null
//     */
//    public long id;
//    public String mingCheng;
//    public String pinLei;
//    public long pinLeiId;
//    public double danJia;
//    public long jiFenJiaZhi;
//    public String tuPian;
//    public String chuangJianRiQi;
//    public String lastUpdateDate;
//    public int isDel;
//    public String xiangQing;
//
//    public String getDescription() {
//        return pinLei + " - " + mingCheng;
//    }
//
//    public boolean isDel() {
//        return 0 != isDel;
//    }
    /*
    {"id":"1","name":"煤气灶（单）(10.5)"}
     */
    public long id;
    public String name;

    @Override
    public String toString() {
        return name;
    }
}
