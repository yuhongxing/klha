package com.czsy.bean;

import java.util.LinkedList;
import java.util.List;

public class AnJianOrderBean extends CommonOrderBean {
    public static class QA implements IBaseBean {
        public int id;
        public String wenTi;
        public List<String> daAn = new LinkedList<>();
    }

    public String anJianQianMing;
    public String anJianTuPian;

    public long id;
    public List<QA> anJianWenTiList;

    public AnJianOrderBean() {
        dingDanLeiXingName = "安检单";
    }

    @Override
    public String toString() {
        return String.format("%s: %s\n" +
                        "客户: %s\n" +
                        "地址: %s\n" +
                        "下单日期: %s\n" +
                        "完成时间: %s\n" +
                        "楼层: %s\n" +
                        "供应站: %s\n" +
                        "报修原因： %s\n" +
                        "备注: %s",
                dingDanLeiXingName, dingDanHao, keHuMing, diZhi, chuangJianRiQi,
                this.wanChengRiQi==null?"":this.wanChengRiQi,
                this.louCeng == null ? "" : louCeng,
                this.gongYingZhan,
                this.tuiDanYuanYin != null ? this.tuiDanYuanYin : "",
                (this.beiZhu != null ? this.beiZhu : "")
        );
    }
}

