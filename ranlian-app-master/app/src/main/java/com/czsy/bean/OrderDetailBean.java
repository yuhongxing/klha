package com.czsy.bean;

import android.annotation.SuppressLint;

public class OrderDetailBean implements IBaseBean , IGPInfo{
    /**
     * {"dingDanHao":"2020031810230397753","gangPingId":"11073",
     * "gangPingHao":null,"guiGe":"10kg","gangPingJinE":0.10,
     * "shiShouJinE":130.00,"tuiQi":null,"tuiQiJinE":null,"yaJinJinE":130.00,
     * "shiShouYaJinJinE":130.00,"leiBie":1,"status":null,
     * "chuangJianRiQi":"2020-03-18 10:23:03","lastUpdateDate":"2020-03-18 10:59:48"}
     */

    public String dingDanHao;
    public String gangPinId;
    public String gangPingHao;
    public int guiGe;
    public String guiGeName;
    public float gangPingJinE;
    public float shiShouJinE;
    public String tuiQi;
    public float tuiQiJinE;
    public float yaJinJinE;
    public float shiShouYaJinJinE;
    public int leiBie;
    public String leiBieName;
    public String status;
    public String chuangJianRiQi;
    public String qiTiLeiXing;
    public String lastUpdateDate;
    public float youHuiJinE;
    public float fuJiaFei;

    @SuppressLint("DefaultLocale")
    public String toString() {
        return String.format("气瓶号: %s\n规格: %s\n气体类型：%s\n" +
                        "气瓶金额：%.2f\n实收金额: %.2f\n退气: %s\n"
                        + "优惠金额：%.2f\n附加金额: %.2f\n"
                        + "退气金额: %.2f\n押金金额: %.2f\n实收押金: %.2f\n" +
                        "创建日期：%s",
                gangPingHao==null?"":gangPingHao, guiGeName, qiTiLeiXing,
                gangPingJinE, shiShouJinE,
                tuiQi == null ? "" : tuiQi,
                youHuiJinE, fuJiaFei,
                tuiQiJinE, yaJinJinE,
                shiShouYaJinJinE, chuangJianRiQi);
    }
    public String toDetail() {
        return String.format("规格: %s\n" +
                        "气瓶金额：%.2f\n实收金额: %.2f\n退气: %s\n"
                        + "优惠金额：%.2f\n附加金额: %.2f\n"
                        + "退气金额: %.2f\n押金金额: %.2f\n实收押金: %.2f\n",
                guiGeName, gangPingJinE, shiShouJinE,
                tuiQi == null ? "" : tuiQi,
                youHuiJinE, fuJiaFei,
                tuiQiJinE, yaJinJinE,
                shiShouYaJinJinE);
    }


    @Override
    public String getGuiGeName() {
        return guiGeName;
    }

    @Override
    public double getJine() {
        return gangPingJinE;
    }
}
