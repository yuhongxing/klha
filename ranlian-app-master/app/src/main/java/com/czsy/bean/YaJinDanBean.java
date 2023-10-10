package com.czsy.bean;

import com.czsy.TheApp;
import com.czsy.android.R;

// 押金单bean
public class YaJinDanBean implements IBaseBean {
    /**
     * {"id":"5","guiGe":1,"guiGeName":"5kg","yaJin":2.0}
     */
    public long id;
    public int guiGe;
    public String guiGeName;
    public double yaJin;
    public String date;
    public String zhiZhiDanHao;
    public String shiShouYaJin;

    public String getZhiZhiDanHao() {
        return zhiZhiDanHao;
    }

    public void setZhiZhiDanHao(String zhiZhiDanHao) {
        this.zhiZhiDanHao = zhiZhiDanHao;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getGuiGe() {
        return guiGe;
    }

    public void setGuiGe(int guiGe) {
        this.guiGe = guiGe;
    }

    public String getGuiGeName() {
        return guiGeName;
    }

    public void setGuiGeName(String guiGeName) {
        this.guiGeName = guiGeName;
    }

    public double getYaJin() {
        return yaJin;
    }

    public String getShiShouYaJin() {
        return shiShouYaJin;
    }

    public void setShiShouYaJin(String shiShouYaJin) {
        this.shiShouYaJin = shiShouYaJin;
    }

    public void setYaJin(double yaJin) {
        this.yaJin = yaJin;
    }

    @Override
    public String toString() {
        return String.format("\n%s\n%s \n%s: %.2f %s\n%s",
                "纸质单号："+zhiZhiDanHao,
                date,
                guiGeName, yaJin, TheApp.sInst.getString(R.string.title_yuan),
                "实收押金："+shiShouYaJin+"元");
    }
}
