package com.czsy.bean;

import com.czsy.TheApp;
import com.czsy.android.R;

public enum PayMethodBean implements IBaseBean {
    /**
     * XIAN_JIN(new Byte("1"), "现金"),
     * WEI_XIN(new Byte("2"), "微信"),
     * RAN_QI_KA(new Byte("3"), "燃气卡"),
     * QIAN_KUAN(new Byte("4"), "欠款")
     */
    xian_jin(R.string.title_xianjin, 1),
    wei_xin(R.string.title_weixin, 2),
    chong_zhi_ka(R.string.title_chognzhika, 3),
    qian_kuang(R.string.title_qiankuang, 4),
    ji_fen(R.string.title_jifen, 5),
    yin_hang_ka(R.string.title_yinHang,7);//微信小程序占了 6

    private String name;
    final public int id;

    private PayMethodBean(int resid, int id) {
        name = TheApp.sInst.getString(resid);
        this.id = id;
    }
}
