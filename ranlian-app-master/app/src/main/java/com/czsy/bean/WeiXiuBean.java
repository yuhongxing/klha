package com.czsy.bean;

import com.czsy.Constant;

public class WeiXiuBean implements IBaseBean {
    /**
     * /**
     * {"id":"106","dingDanHao":"2019092517414002636","keHuId":"2",
     * "laiYuan":null,"baoXiuReason":"hghjkiy","diZhi":"ooo","keHuMing":"ttt",
     * "keHuDianHua":"13121548755","peiSongYuan":"配送王二","peiSongYuanId":"2",
     * "gongYingZhan":"xxx供应站","gongYingZhanId":"4","changZhan":"沧州场站",
     * "changZhanId":"3","keFuId":null,"keFu":null,"qiWangPeiSongRiQi":null,
     * "wanChengRiQi":null,"beiZhu":null,"tuiDanYuanYin":null,"status":4,
     * "statusName":"完成","chuangJianRiQi":"2019-09-25 17:41:40",
     * "lastUpdateDate":"2019-09-25 17:41:40","enableFenPei":false,"enableCancel":false}
     */
    public long id;
    public String dingDanHao;
    public String chuangJianRiQi;
    public String diZhi;
    public long keHuId;
    public int laiYuan;
    public String keHuBianHao;
    public String baoXiuReason;
    public String peiSongYuan;
    public long peiSongYuanId;
    public String keHuMing;
    public String changZhan;
    public long changZhanId;
    public String qiWangPeiSongRiQi;
    public String wanChengRiQi;
    public long gongYingZhanId;
    public long keFuId;
    public String keFu;
    public String tuiDanYuanYin;
    public int status;
    public String keHuDianHua;
    public String beiZhu;
    public int type;
    public String typeName;
    public String statusName;
    public boolean isCoerceSwipeCard;//是否检测巡检卡

    @Override
    public String toString() {
        final WeiXiuBean b = this;
        return String.format(
                "维修订单号：%s\n客户(%s): %s\n" +
                        "地址: %s\n" +
                        "下单日期: %s\n" +
                        (b.status == 4 ? ("完成日期: %s\n") : "%s") +
                        "类型: %s\n" +
                        "原因: %s\n" +
                        (b.status == 4 ? ("备注: %s\n") : "%s")
                , b.dingDanHao,
                b.keHuMing, keHuDianHua, b.diZhi
                , b.chuangJianRiQi == null ? "??" : b.chuangJianRiQi,
                (b.status == 4 ? (b.wanChengRiQi == null ? "??" : b.wanChengRiQi) : ""),
                b.typeName,
                b.baoXiuReason,
                (b.status == 4 ? (b.beiZhu == null ? "" : b.beiZhu) : "")
        );
    }


}
