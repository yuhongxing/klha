package com.czsy.bean;

import com.czsy.TheApp;
import com.czsy.android.R;

public enum WeiXiuType implements IBaseBean {


    qi_ta("其他原因", 5),
    lou_qi("气瓶漏气", 1),
    zhao_ju("炉具问题", 2),
    hei_yan("黑烟", 3),
    da_bu_zhe("打不着", 4);

    public String name;
    public int id;

    WeiXiuType(String resid, int id) {
        name = (resid);
        this.id = id;
    }

    @Override
    public String toString() {
        return name;
    }
}
