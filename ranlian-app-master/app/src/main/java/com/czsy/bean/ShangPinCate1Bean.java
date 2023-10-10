package com.czsy.bean;

public class ShangPinCate1Bean implements IBaseBean {
    // {"id":"1","level":1,"name":"厨具类",
    // "chuangJianRiQi":"2019-07-26 07:22:21",
    // "lastUpdateDate":"2019-07-26 07:22:21","pid":"0"}

    public long id;
    public int level;
    public String name;
    public String chuangJianRiQi;
    public String lastUpdateDate;
    public long pid;

    @Override
    public String toString() {
        return name;
    }
}
