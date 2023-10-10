package com.czsy.bean;

public class CongzhuangzhanBean {

    private String id;
    private String danWeiId;
    private String shangJiDanWeiId;
    private String shangJiDanWei;
    private String bianMa;
    private String mingCheng;
    private String leiXing;
    private String leiXingId;
    private String fuZeRen;
    private String fuZeRenId;
    private String lianXiDianHua;
    private String  diZhi;
    private String  chuangJianRiQi;
    private String  lastUpdateDate;
    private FanWei fanWei;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDanWeiId() {
        return danWeiId;
    }

    public void setDanWeiId(String danWeiId) {
        this.danWeiId = danWeiId;
    }

    public String getShangJiDanWeiId() {
        return shangJiDanWeiId;
    }

    public void setShangJiDanWeiId(String shangJiDanWeiId) {
        this.shangJiDanWeiId = shangJiDanWeiId;
    }

    public String getShangJiDanWei() {
        return shangJiDanWei;
    }

    public void setShangJiDanWei(String shangJiDanWei) {
        this.shangJiDanWei = shangJiDanWei;
    }

    public String getBianMa() {
        return bianMa;
    }

    public void setBianMa(String bianMa) {
        this.bianMa = bianMa;
    }

    public String getMingCheng() {
        return mingCheng;
    }

    public void setMingCheng(String mingCheng) {
        this.mingCheng = mingCheng;
    }

    public String getLeiXing() {
        return leiXing;
    }

    public void setLeiXing(String leiXing) {
        this.leiXing = leiXing;
    }

    public String getLeiXingId() {
        return leiXingId;
    }

    public void setLeiXingId(String leiXingId) {
        this.leiXingId = leiXingId;
    }

    public String getFuZeRen() {
        return fuZeRen;
    }

    public void setFuZeRen(String fuZeRen) {
        this.fuZeRen = fuZeRen;
    }

    public String getFuZeRenId() {
        return fuZeRenId;
    }

    public void setFuZeRenId(String fuZeRenId) {
        this.fuZeRenId = fuZeRenId;
    }

    public String getLianXiDianHua() {
        return lianXiDianHua;
    }

    public void setLianXiDianHua(String lianXiDianHua) {
        this.lianXiDianHua = lianXiDianHua;
    }

    public String getDiZhi() {
        return diZhi;
    }

    public void setDiZhi(String diZhi) {
        this.diZhi = diZhi;
    }

    public String getChuangJianRiQi() {
        return chuangJianRiQi;
    }

    public void setChuangJianRiQi(String chuangJianRiQi) {
        this.chuangJianRiQi = chuangJianRiQi;
    }

    public String getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(String lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public FanWei getFanWei() {
        return fanWei;
    }

    public void setFanWei(FanWei fanWei) {
        this.fanWei = fanWei;
    }

    private class FanWei{
        private String id;
        private String shengFen;
        private String shengFenId;
        private String diShi;
        private String diShiId;
        private String quXian;
        private String quXianId;
        private String xiangZhen;
        private String xiangZhenId;
        private String cun;
        private String cunId;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getShengFen() {
            return shengFen;
        }

        public void setShengFen(String shengFen) {
            this.shengFen = shengFen;
        }

        public String getShengFenId() {
            return shengFenId;
        }

        public void setShengFenId(String shengFenId) {
            this.shengFenId = shengFenId;
        }

        public String getDiShi() {
            return diShi;
        }

        public void setDiShi(String diShi) {
            this.diShi = diShi;
        }

        public String getDiShiId() {
            return diShiId;
        }

        public void setDiShiId(String diShiId) {
            this.diShiId = diShiId;
        }

        public String getQuXian() {
            return quXian;
        }

        public void setQuXian(String quXian) {
            this.quXian = quXian;
        }

        public String getQuXianId() {
            return quXianId;
        }

        public void setQuXianId(String quXianId) {
            this.quXianId = quXianId;
        }

        public String getXiangZhen() {
            return xiangZhen;
        }

        public void setXiangZhen(String xiangZhen) {
            this.xiangZhen = xiangZhen;
        }

        public String getXiangZhenId() {
            return xiangZhenId;
        }

        public void setXiangZhenId(String xiangZhenId) {
            this.xiangZhenId = xiangZhenId;
        }

        public String getCun() {
            return cun;
        }

        public void setCun(String cun) {
            this.cun = cun;
        }

        public String getCunId() {
            return cunId;
        }

        public void setCunId(String cunId) {
            this.cunId = cunId;
        }
    }

}
