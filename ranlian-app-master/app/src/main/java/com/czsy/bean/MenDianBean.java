package com.czsy.bean;

public class MenDianBean implements IBaseBean {

    /**
     * {"id":"2","danWeiId":"2","shangJiDanWeiId":"3",
     * "bianMa":"0101001001","mingCheng":"张庄供应站",
     * "leiXing":"自营","leiXingId":1,"fuZeRen":"","fuZeRenId":null,"lianXiDianHua":"",
     * "diZhi":"公司地址","chuangJianRiQi":"2019-07-26 20:28:48",
     * "lastUpdateDate":"2019-08-09 21:19:54",
     * "fanWei":{"id":"0","shengFen":"北京市","shengFenId":"110","diShi":"市辖区","diShiId":"110100000000","quXian":"东城区","quXianId":"110101000000","xiangZhen":"东华门街道办事处","xiangZhenId":"110101001000","cun":"多福巷社区","cunId":"110101001001"}
     */
    public long id;
    public long danWeiId, shangJiDanWeiId;
    public String bianMa;
    public String mingCheng;
    public String leiXing;
    public long leiXingId;
    public String diZhi;

    @Override
    public String toString() {
        return mingCheng;
    }
}
