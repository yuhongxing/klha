package com.czsy.bean;

import android.text.TextUtils;

import com.czsy.Constant;

import mylib.app.MyLog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class CommonOrderBean implements IBaseBean {

    //public long id;
    public String keHuBianHao;
    public String dingDanHao;
    public int dingDanLeiXing;
    public String dingDanLeiXingName;
    public long keHuId;
    public int laiYuan;
    public String laiYuanName;
    //public String peiSongGangPing;
    //public String huiShouGangPing;
    public double yuQi;
    public int zheJiuGangPing;
    public double tuiQiJinE;
    public double yaJinJinE;
    public double shiShouJinE;
    public double shouYaJinJinE;
    public double shangPinJinE;
    public double yunFei;
    public double fuJiaFei;
    public double yingShouJinE;
    public double xianShang;
    public boolean isCoerceSwipeCard;
    public int zhiFuFangShi;
    public String zhiFuFangShiName;
    public String louCeng;
    public boolean isShowSeleteLouCeng = false;
    public String diZhi;
    public String keHuMing;
    public String keHuDianHua;
    public String callerPhone;
    public String peiSongYuan;
    public long peiSongYuanId;
    public String gongYingZhan;
    public long gongYingZhanId;
    public String changZhan;
    public long changZhanId;
    public long keFuId;
    public String keFu;
    public String wanChengRiQi;
    public String shiFouKaiFaPiao;
    public String shiFouCuiDan;
    public Boolean qiangZhiAnJian;
    public long anJianDanSid;
    public String beiZhu;
    public String tuiDanYuanYin;
    public Integer status;
    public String statusName;
    public String chuangJianRiQi;
    public String lastUpdateDate;
    public String qiWangShiJian;

    public boolean isForceAnJian() {
        return qiangZhiAnJian != null && qiangZhiAnJian;
    }

    public boolean isNewClient() { //新用户 ？M
        return keHuId <= 0l;
    }

    public boolean isNewOrder() { // 待客下单？
//        if (TextUtils.isEmpty(dingDanHao) || dingDanHao == null) {
//            return true;
//        }
//        return false;
        return TextUtils.isEmpty(dingDanHao) || dingDanHao == null;
    }

    public boolean isTuiPingDan() {
        return dingDanLeiXing == Constant.dingdan_type_tuipin;
    }

    public boolean isJiFenDan() {

        if (zhiFuFangShi == 5) {
            return true;
        }
        return false;
    }

    public JSONObject toPrepayOrderJson() throws Exception {
        // 预结算
        JSONObject ret = new JSONObject();
        if (!empty_list.isEmpty()) {
            JSONArray ja = new JSONArray();
            for (GangPingInfo gpi : empty_list) {
                JSONObject tmp = new JSONObject();
                tmp.put("id", gpi.gp_bean.id);
                tmp.put("yuQi", gpi.yu_qi);
                tmp.put("yuQiJinE", gpi.yin_tui_kuan);
                ja.put(tmp);
            }
            ret.put("kongPingParamList", ja);

        }
        if (!heavy_list.isEmpty()) {
            JSONArray ja = new JSONArray();
            for (GangPingInfo gpi : heavy_list) {
                JSONObject tmp = new JSONObject();
                tmp.put("id", gpi.gp_bean.id);
                tmp.put("yaJinNumber", gpi.yajin_number);
                tmp.put("yaJinFile", gpi.yajin_file);
                ja.put(tmp);
            }
            ret.put("zhongPingParamList", ja);
        }
        ret.put("sid", dingDanHao);
        ret.put("beiZhu", beiZhu);
        if (isTuiPingDan()) {
            // 押金条
            JSONArray ja = new JSONArray();
            for (YaJinDanBean yj : yajindan_list) {
                ja.put(yj.id);
            }
            ret.put("yaJinTiaoIdList", ja);
        }
        return ret;
    }

    public ClientBean toClientBean() {
        ClientBean b = new ClientBean();
        b.diZhi = diZhi;
        b.id = keHuId;
        b.userName = keHuMing;
        b.telNum = keHuDianHua;
        b.shiFouXuYaoAnJian = isForceAnJian();
        b.shiFouYunXuQianKuan = false; //ClientBean.no_qian_fei;
        return b;
    }

    public void fromClientBean(ClientBean cb) {
        if (cb == null) {
            MyLog.LOGW("fromClientBean with null ClientBean");
            return;
        }
        diZhi = cb.diZhi;
        keHuId = cb.id;
        keHuMing = cb.userName;
        keHuDianHua = cb.telNum;
        qiangZhiAnJian = cb.shiFouXuYaoAnJian;
        this.client_bean = cb;
    }

    // transient, set at fly
    public transient ClientBean client_bean;
    public transient AnJianOrderBean an_jian_ben;

    ////================ otehrs
    public static class GangPingInfo implements IGPInfo {
        public GangPingBean gp_bean;
        public double yu_qi; //余气
        public double jingZhong; //气瓶净重
        public double yin_tui_kuan; // 应退款
        public double gangping_jine; // 钢瓶金额
        public final boolean is_empty; // 属于空瓶还是重瓶？
        public String yajin_file; // 押金照片
        public String yajin_number; // 押金纸质单号
        public boolean isShowImage = true;//购气订单-->扫重瓶时判断是否显示添加押金图片

        public GangPingInfo(boolean is_empty) {
            this.is_empty = is_empty;
        }

        @Override
        public String getGuiGeName() {
            return gp_bean.getGuiGeName();
        }

        @Override
        public double getJine() {
            return gp_bean.getJine();
        }
    }

    // 重瓶
    public transient final List<GangPingInfo> heavy_list = new LinkedList<>();
    // 空瓶
    public transient final List<GangPingInfo> empty_list = new LinkedList<>();
    //    //// 2019.6.7 add
    public List<XiaDanShangPinBean> xiaDanShangPinList;
    //    // 要退押金单的空瓶，
    public transient final Set<YaJinDanBean> yajindan_list = new HashSet<>();

    @Override
    public String toString() {
        String s = String.format("%s: %s\n" +
                        "客户: %s  电话: %s\n" +
                        "地址: %s, 楼层: %s\n" +
                        "下单日期: %s\n" +
                        (status == Constant.status_done ? "完成日期: %s\n" : "%s") +
                        "供应站: %s\n" +
                        "订单金额: %.2f\n" +
                        (status == Constant.status_done ? "支付方式: %s\n" : "%s") +
                        "来源： %s\n备注: %s\n",
                dingDanLeiXingName, dingDanHao, keHuMing, keHuDianHua, diZhi,
                this.louCeng == null ? "" : louCeng,
                chuangJianRiQi,
                (status == 6 ? wanChengRiQi : "")
                , this.gongYingZhan
                , Math.abs(this.yingShouJinE),
                (status == 6 ? this.zhiFuFangShiName : ""),
                this.laiYuanName,
                (this.beiZhu != null ? this.beiZhu.trim() : "")
        );
        if (dingDanLeiXing == Constant.dingdan_type_tuipin) {
            s += String.format("实退押金： %.2f\n退气: %.2f\n气瓶数量: %d",
                    Math.abs(yaJinJinE), yuQi, zheJiuGangPing);
            return s;
        } else  //if (dingDanLeiXing == Constant.dingdan_type_shangpin) {
            if (status != Constant.status_done) {
                // 商品单
                if (details != null && !details.isEmpty()) {
                    StringBuilder sb = new StringBuilder("\n------------订单详情\n");
                    for (CommonOrderBean.DetailInfo sp : details) {
                        sb.append(sp.toString());
                        sb.append('\n');
                    }
                    s += sb.toString();
                }
            }

        return s;
    }

    public static class DetailInfo {
        public String shangPingMingCheng;
        public String type;
        public double price;
        public int count;
        public double zongJinE;

        @Override
        public String toString() {

            return String.format("%s, 单价: %.2f, 数量: %d, 金额: %.2f",
                    shangPingMingCheng,
                    price, count, zongJinE);
        }
    }

    public List<DetailInfo> details;

    // 订单钢瓶
    final public transient List<OrderDetailBean> order_gp = new LinkedList<>();

    public List<GoodsInfo> goodsInfos;

    public static class GoodsInfo {
        public String shangPin;
        public int shuLiang;
        public double shangPingJinE;
    }
}
