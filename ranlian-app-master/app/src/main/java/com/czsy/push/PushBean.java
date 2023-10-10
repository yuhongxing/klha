package com.czsy.push;

import java.io.Serializable;

public class PushBean implements Serializable {

    public final static int msgtype_order = 1; //分配订单配送员
    public final static int msgtype_cuiDan = 2; //催单
    public final static int msgtype_keFuXiaDan = 3; //客服下单
    public final static int msgtype_psyChuKu = 4; //配送员领取钢瓶
    public final static int msgtype_gyzChuKu = 5; //供应站出库
    public final static int msgtype_gyzRuKu = 6; //供应站入库

    //1(购气订单),2(商品订单),3(退瓶订单),4(回收订单)(折旧),6(维修单)，7（安检单）
    public final static int type_gouqi = 1;
    public final static int type_shangpin = 2;
    public final static int type_tuiping = 3;
    public final static int type_zhejiu = 4;
    public final static int type_chognzhika = 5;
    public final static int type_weixiu = 6;
    public final static int type_anjian = 7;
    //投诉
    public final static int type_tousu = 8;
    //配送员领取钢瓶
    public final static int type_psy_gpck = 9;
    //供应站出库
    public final static int type_gyz_ck = 10;
    //供应站入库
    public final static int type_gyz_rk = 11;


    public String getTypeString() {

        if (ding_dan_type == type_gouqi) {
            return "购气单";
        } else if (ding_dan_type == type_shangpin) {
            return "商品单";
        } else if (ding_dan_type == type_tuiping) {
            return "退瓶单";
        } else if (ding_dan_type == type_zhejiu) {
            return "回收单";
        } else if (ding_dan_type == type_weixiu) {
            return "维修单";
        } else if (ding_dan_type == type_anjian) {
            return "安检单";
        } else if (ding_dan_type == type_tousu) {
            return "投诉";
        } else if (ding_dan_type == type_psy_gpck) {
            return "气瓶领取";
        } else if (ding_dan_type == type_gyz_ck) {
            return "供应站出库";
        } else if (ding_dan_type == type_gyz_rk) {
            return "供应站入库";
        }
        return "";
    }

    public String msg_id;
    public long msg_target_uid;
    public int msg_type;
    public String msg_data;
    public int ding_dan_type;

    public String ding_dan_hao;
    public long timestamp;
    public boolean is_read = false;
}
