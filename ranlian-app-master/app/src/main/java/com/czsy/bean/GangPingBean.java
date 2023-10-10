package com.czsy.bean;

import com.czsy.INFCHandler;

// 钢瓶
public class GangPingBean implements IBaseBean, IGPInfo {

    public final static int gp_status_using = 1; //
    public final static int gp_status_unknown = 0; // 使用中
    public final static int gp_status_anjian = 2; // 安检中
    public final static int gp_status_baofei = 3; // 报废
    public final static int gp_status_shandang = 4; // 删档

    public String getStatusName() {
        if (status == gp_status_using) {
            return "使用中";
        } else if (status == gp_status_anjian) {
            return "安检中";
        } else if (status == gp_status_baofei) {
            return "已报废";
        } else if (status == gp_status_shandang) {
            return "已删档";
        }
        return "未知";
    }

    public GangPingBean() {
    }

    public GangPingBean(INFCHandler.NFCInfo info) {
        xinPianHao = info.chip_sn;
        //xpbm = info.xpbm;
    }

    public String detailString() {
        return String.format("气瓶号： %s\n芯片号： %s\n气体类型: %s\n生产厂家: %s" +
                        "\n建档日期: %s\n生产日期: %s\n上次检验日期：%s\n规格： %s\n空瓶重量：%skg\n状态: %s , %s\n" +
                        "当前充装站：%s\n建档充装站: %s\n最后位置: %s",
                gangPingHao, xinPianHao,qiTiLeiXing,
                shengChanChangJia,
                chuangJianRiQi, shengChanRiQi,
                shangCiJianXiuRiQi == "" ? "" : shangCiJianXiuRiQi,
                guiGeName,
                jingZhong,
                isEmpty() ? "空瓶" : "重瓶",
                statusName, changZhan, jianDangDanWei,
                zuiHouWeiZhiName + " / " + zuiHouWeiZhiHolderName);
    }

    public long id; //	int	数据库主键
    public int yuQiStatus; //
    public String yuQiStatusName;
    public String shengChanRiQi;

    public boolean isEmpty() {
        return 1 == yuQiStatus;
    }

    public double jingZhong;
    public double zhongLiang;//	Float	钢瓶重量（不含钢瓶），单位：千克，保留2位小数
    public String gangPingHao; //	Varchar	钢瓶号
    public String xinPianHao; //	Varchar	芯片号
    //public String xpbm; //	Varchar	芯片号2
    public String erWeiMaHao; //	Varchar	二维码号

    public int guiGe;
    public String guiGeName;
    public int qiTiLeiXingId;//气体类型的id
    public String qiTiLeiXing;//气体类型。如："液化石油气"
    public String chuangJianRiQi;
    public String jianDangRiQi;
    public String baoFeiRiQi;
    public int status;
    public String statusName;
    //public String leiXing;//	Varchar	钢瓶类型，承载的气体类型，取自钢瓶类型表

    public double yaJin; //	Int	钢瓶押金，单位：元
    public long peiSongYuanId; //	Int	配送员ID，配送员领取后，关联这个字段
    public long yunShuYuanId;
    //public long yuQiLuRuRenYaunId; //	Int	余气录入的人员ID
    public long keHuId; //	Int	客户ID，销售给客户后关联这个字段

    public String changZhan;
    public long changZhanId; //	Int	单位ID，当钢瓶属于场站、供应站时，这个字段关联单位表。
    public long gongYingZhanId;

    //对应操作：配送员接收空瓶、供应站接收、场站接收后，关联这个字段
    public String shengChanChangJia; // 厂家

    /**
     * /**
     * * 最后位置,1(厂站)，2（运输员），3（配送站），4（配送员），5（客户）
     * * <p>
     * * t_gangping.zui_hou_wei_zhi
     */
    public final static int zhwz_cz = 1;
    public final static int zhwz_ysy = 2;
    public final static int zhwz_psz = 3;
    public final static int zhwz_psy = 4;
    public final static int zhwz_kh = 5;
    public int zuiHouWeiZhi;
    public String zuiHouWeiZhiHolderName;
    public String zuiHouWeiZhiName;

    public String xiaCiJianXiuRiQi;
    public String shangCiJianXiuRiQi; // Timestamp 上次检修日期

    @Override
    public String getGuiGeName() {
        return guiGeName;
    }

    @Override
    public double getJine() {
        return 0.0f;
    }

    public String jianDangDanWei;

}
