package com.czsy.bean;

import java.util.LinkedList;
import java.util.List;

// 客户
public class ClientBean implements IBaseBean {

    //用户类型
    final public static List<IDNameBean> clientTypes;

    static {
        clientTypes = new LinkedList<>();
        IDNameBean b = new IDNameBean();
        b.id = 1;
        b.name = "工业用户";
        clientTypes.add(b);

        b = new IDNameBean();
        b.id = 2;
        b.name = "商业用户";
        clientTypes.add(b);

        b = new IDNameBean();
        b.id = 3;
        b.name = "居民用户";
        clientTypes.add(b);

        b = new IDNameBean();
        b.id = 4;
        b.name = "VIP用户";
        clientTypes.add(b);
    }

    //商业用户--类型
    final public static List<IDNameBean> clientShangYeTypes;

    static {
        clientShangYeTypes = new LinkedList<>();

        IDNameBean b = new IDNameBean();
        b.id = 0;
        b.name = "默认";
        clientShangYeTypes.add(b);

        b = new IDNameBean();
        b.id = 1;
        b.name = "流动摊";
        clientShangYeTypes.add(b);

        b = new IDNameBean();
        b.id = 2;
        b.name = "固定门市";
        clientShangYeTypes.add(b);

        b = new IDNameBean();
        b.id = 3;
        b.name = "机关单位食堂";
        clientShangYeTypes.add(b);

        b = new IDNameBean();
        b.id = 4;
        b.name = "学校幼儿园食堂";
        clientShangYeTypes.add(b);
    }

    /////////////// --->
    public String getDiZhi() {
        return diZhi;
    }

    public String diZhi;
    public long id = 0l;
    public String telNum;
    public String userName; // name
    public String keHuBianHao; // 客户编号
    public long type = 3;
    public String typeName;
    public long orgId;

    public final static int can_qian_fei = 1;
    public final static int no_qian_fei = 2;

    public Boolean shiFouYunXuQianKuan = false; //no_qian_fei; // 是否欠款？ 1: 可以欠费, 2: 不能欠费
    public boolean shiFouXuYaoAnJian = false;  // 安检
    public String shangCiAnJianRiQi;


    public String toString() {
        return userName;
    }
}
