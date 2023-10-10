package com.czsy.bean;

public class GangPingChaXunBean {


    /**
     * code : 200
     * message :
     * result : {"gangPingHao":"HD000001","xinPianHao":"CAASD2","guiGe":3,"guiGeName":"15kg","status":1,"statusName":"使用中","changZhan":"充装站","anJianRiQi":"","jianDangRiQi":"2020-11-05","baoFeiRiQi":"2028-11-05","yuQiStatus":2,"yuQiStatusName":"重瓶","zuiHouWeiZhi":"","zuiHouWeiZhiName":"","keHuXingMing":"","keHuLeiXing":"","mobile":"","gongYingZhan":"","kaiHuRiQi":"","address":"","detailedAddress":""}
     * ext :
     */

    private int code;
    private String message;
    private ResultDTO result;
    private String ext;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ResultDTO getResult() {
        return result;
    }

    public void setResult(ResultDTO result) {
        this.result = result;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public static class ResultDTO {
        /**
         * gangPingHao : HD000001
         * xinPianHao : CAASD2
         * guiGe : 3
         * guiGeName : 15kg
         * status : 1
         * statusName : 使用中
         * changZhan : 充装站
         * anJianRiQi :
         * jianDangRiQi : 2020-11-05
         * baoFeiRiQi : 2028-11-05
         * yuQiStatus : 2
         * yuQiStatusName : 重瓶
         * zuiHouWeiZhi :
         * zuiHouWeiZhiName :
         * keHuXingMing :
         * keHuLeiXing :
         * mobile :
         * gongYingZhan :
         * kaiHuRiQi :
         * address :
         * detailedAddress :
         */

        private String gangPingHao;
        private String xinPianHao;
        private int guiGe;
        private double jingZhong;
        private String guiGeName;
        private int status;
        private String statusName;
        private String changZhan;
        private String anJianRiQi;
        private String jianDangRiQi;
        private String baoFeiRiQi;
        private int yuQiStatus;
        private String yuQiStatusName;
        private String qiTiLeiXing;
        private String keHuBianHao;
        private String lastUpdateWeiZhiRiQi;

        public String getKeHuBianHao() {
            return keHuBianHao;
        }

        public void setKeHuBianHao(String keHuBianHao) {
            this.keHuBianHao = keHuBianHao;
        }

        public String getLastUpdateWeiZhiRiQi() {
            return lastUpdateWeiZhiRiQi;
        }

        public void setLastUpdateWeiZhiRiQi(String lastUpdateWeiZhiRiQi) {
            this.lastUpdateWeiZhiRiQi = lastUpdateWeiZhiRiQi;
        }

        /**
         * CHANG_ZHAN(new Byte("1"), "充装站"),
         * YUN_SHU_YUAN(new Byte("2"), "运输员"),
         * GONG_YING_ZHAN(new Byte("3"), "供应站"),
         * PEI_SONG_YUAN(new Byte("4"), "配送员"),
         * KE_HU(new Byte("5"), "客户"),
         * PEI_SONG_YUAN_DAI_CHU_KU(new Byte("6"), "配送员待出库"),
         */
        private String zuiHouWeiZhi;
        private String zuiHouWeiZhiName;
        private String zuiHouWeiZhiHolderName;
        private String keHuXingMing;
        private String keHuLeiXing;
        private String mobile;
        private String gongYingZhan;
        private String kaiHuRiQi;
        private String address;
        private String detailedAddress;

        public String getGangPingHao() {
            return gangPingHao;
        }

        public String getZuiHouWeiZhiHolderName() {
            return zuiHouWeiZhiHolderName;
        }

        public void setZuiHouWeiZhiHolderName(String zuiHouWeiZhiHolderName) {
            this.zuiHouWeiZhiHolderName = zuiHouWeiZhiHolderName;
        }

        public void setGangPingHao(String gangPingHao) {
            this.gangPingHao = gangPingHao;
        }

        public String getXinPianHao() {
            return xinPianHao;
        }

        public void setXinPianHao(String xinPianHao) {
            this.xinPianHao = xinPianHao;
        }

        public int getGuiGe() {
            return guiGe;
        }

        public void setGuiGe(int guiGe) {
            this.guiGe = guiGe;
        }

        public String getGuiGeName() {
            return guiGeName;
        }

        public void setGuiGeName(String guiGeName) {
            this.guiGeName = guiGeName;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getStatusName() {
            return statusName;
        }

        public void setStatusName(String statusName) {
            this.statusName = statusName;
        }

        public String getChangZhan() {
            return changZhan;
        }

        public void setChangZhan(String changZhan) {
            this.changZhan = changZhan;
        }

        public String getAnJianRiQi() {
            return anJianRiQi;
        }

        public void setAnJianRiQi(String anJianRiQi) {
            this.anJianRiQi = anJianRiQi;
        }

        public String getJianDangRiQi() {
            return jianDangRiQi;
        }

        public void setJianDangRiQi(String jianDangRiQi) {
            this.jianDangRiQi = jianDangRiQi;
        }

        public String getBaoFeiRiQi() {
            return baoFeiRiQi;
        }

        public void setBaoFeiRiQi(String baoFeiRiQi) {
            this.baoFeiRiQi = baoFeiRiQi;
        }

        public int getYuQiStatus() {
            return yuQiStatus;
        }

        public void setYuQiStatus(int yuQiStatus) {
            this.yuQiStatus = yuQiStatus;
        }

        public String getYuQiStatusName() {
            return yuQiStatusName;
        }

        public void setYuQiStatusName(String yuQiStatusName) {
            this.yuQiStatusName = yuQiStatusName;
        }

        public double getJingZhong() {
            return jingZhong;
        }

        public void setJingZhong(double jingZhong) {
            this.jingZhong = jingZhong;
        }

        public String getZuiHouWeiZhi() {
            return zuiHouWeiZhi;
        }

        public void setZuiHouWeiZhi(String zuiHouWeiZhi) {
            this.zuiHouWeiZhi = zuiHouWeiZhi;
        }

        public String getZuiHouWeiZhiName() {
            return zuiHouWeiZhiName;
        }

        public void setZuiHouWeiZhiName(String zuiHouWeiZhiName) {
            this.zuiHouWeiZhiName = zuiHouWeiZhiName;
        }

        public String getKeHuXingMing() {
            return keHuXingMing;
        }

        public void setKeHuXingMing(String keHuXingMing) {
            this.keHuXingMing = keHuXingMing;
        }

        public String getKeHuLeiXing() {
            return keHuLeiXing;
        }

        public void setKeHuLeiXing(String keHuLeiXing) {
            this.keHuLeiXing = keHuLeiXing;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getGongYingZhan() {
            return gongYingZhan;
        }

        public void setGongYingZhan(String gongYingZhan) {
            this.gongYingZhan = gongYingZhan;
        }

        public String getKaiHuRiQi() {
            return kaiHuRiQi;
        }

        public void setKaiHuRiQi(String kaiHuRiQi) {
            this.kaiHuRiQi = kaiHuRiQi;
        }

        public String getAddress() {
            return address;
        }

        public String getQiTiLeiXing() {
            return qiTiLeiXing;
        }

        public void setQiTiLeiXing(String qiTiLeiXing) {
            this.qiTiLeiXing = qiTiLeiXing;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getDetailedAddress() {
            return detailedAddress;
        }

        public void setDetailedAddress(String detailedAddress) {
            this.detailedAddress = detailedAddress;
        }
    }
}
