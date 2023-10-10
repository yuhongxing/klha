package com.czsy.bean;

import java.util.HashMap;
import java.util.Map;

// 押金信息
public class YaJinBean implements IBaseBean {
    public long id;
    public int guiGe;
    public double price;
    public String guiGeName;


    final public static Map<Integer, YaJinBean> yajin_map = new HashMap<>();
}
