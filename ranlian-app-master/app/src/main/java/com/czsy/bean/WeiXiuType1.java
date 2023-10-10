package com.czsy.bean;

import java.util.LinkedList;
import java.util.List;

public class WeiXiuType1 {

    final public static List<IDNameBean> clientTypes;

    static {
        clientTypes = new LinkedList<>();
        IDNameBean b = new IDNameBean();
        b.id = 1;
        b.name = "气瓶漏气";
        clientTypes.add(b);

        b = new IDNameBean();
        b.id = 2;
        b.name = "炉具问题";
        clientTypes.add(b);

        b = new IDNameBean();
        b.id = 3;
        b.name = "黑烟";
        clientTypes.add(b);

        b = new IDNameBean();
        b.id = 4;
        b.name = "打不着";
        clientTypes.add(b);

        b = new IDNameBean();
        b.id = 5;
        b.name = "其他原因";
        clientTypes.add(b);
    }

    private String name;
    private int id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return name;
    }

    public static WeiXiuType1 getData(int id) {
        WeiXiuType1 weiXiuType1 = new WeiXiuType1();
        weiXiuType1.setId(id);
        switch (id) {
            case 1:
                weiXiuType1.setName("气瓶漏气");
                return weiXiuType1;
            case 2:
                weiXiuType1.setName("炉具问题");
                return weiXiuType1;
            case 3:
                weiXiuType1.setName("黑烟");
                return weiXiuType1;
            case 4:
                weiXiuType1.setName("打不着");
                return weiXiuType1;
            case 5:
                weiXiuType1.setName("其他原因");
                return weiXiuType1;
        }
        return null;
    }

}
