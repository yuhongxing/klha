package com.czsy.bean;

import com.czsy.Constant;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

// 商品信息,
public class ProductManager {
    public static class ProductBean implements IBaseBean { // 钢瓶
        public int id;
        public String name;
        public double price; // 单价
        public TypeWithName type; // type

        @Override
        public String toString() {
            return name;
        }
    }

    private final static List<TypeWithName> product_types = new LinkedList<>();
    private final static Map<Integer, List<ProductBean>> product_map = new HashMap<>();

    private static void testData() {
        if (!Constant.DO_TEST) {
            return;
        }
        if (product_types.isEmpty()) {
            TypeWithName t1 = new TypeWithName("测试1", 1);
            TypeWithName t2 = new TypeWithName("测试2", 2);
            //TypeWithName t3 = new TypeWithName("测试3", 3);
            //TypeWithName t4 = new TypeWithName("测试4", 4);

            product_types.add(t1);
            product_types.add(t2);
            //product_types.add(t3);
            //product_types.add(t4);

            List<ProductBean> list = new LinkedList<>();
            ProductBean pb = new ProductBean();
            pb.name = "pb1";
            pb.id = 1;
            pb.price = 1.0;
            pb.type = t1;
            list.add(pb);
            pb = new ProductBean();
            pb.name = "pb2";
            pb.id = 2;
            pb.price = 2.0;
            pb.type = t1;
            list.add(pb);
            product_map.put(t1.type, list);

            list = new LinkedList<>();
            pb = new ProductBean();
            pb.name = "pb3";
            pb.id = 3;
            pb.price = 3.0;
            pb.type = t2;
            list.add(pb);
            pb = new ProductBean();
            pb.name = "pb4";
            pb.id = 4;
            pb.price = 4.0;
            pb.type = t2;
            list.add(pb);
            product_map.put(t2.type, list);
        }
    }

    // 获取缺省商品
    public static ProductBean getDefaultProduct() {
        testData();
        if (product_types.isEmpty()) {
            return null;
        }
        List<ProductBean> pb = product_map.get(product_types.get(0).type);
        if (pb == null || pb.isEmpty()) {
            return null;
        }
        return pb.get(0);
    }

    public static List<ProductBean> getProductListByType(int type_id) {
        return product_map.get(type_id);
    }

    // get all product info
    public static List<TypeWithName> getAllProductTypes() {
        testData();
        return product_types;
    }
}
