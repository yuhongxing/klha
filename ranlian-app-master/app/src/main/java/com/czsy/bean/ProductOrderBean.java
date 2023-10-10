package com.czsy.bean;

// 订单中的商品信息
public class ProductOrderBean implements IBaseBean {
    public ProductManager.ProductBean product; // 商品信息，never null !

    public double price; // 单价, 可以改，缺省为product price
    public int count = 1; // 数量
    public double pei_song_price; // 配送费
    public double shang_lou_price; // 上楼价
    public double real_money; // 实际收金额

    public double getAllPrice() {
        double all_price = price * count + pei_song_price + shang_lou_price;
        return all_price;
    }

}
