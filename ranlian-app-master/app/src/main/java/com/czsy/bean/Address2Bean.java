package com.czsy.bean;


public class Address2Bean implements IBaseBean { //implements Serializable {
    // {"provinceId":"110","cityId":null,"countyId":null,"townId":null,"villageId":null,
    // "provinceName":"北京市","cityName":null,
    // "countyName":null,"townName":null,"villageName":null}
    public String provinceId, cityId, countyId, townId, villageId;
    public String provinceName, cityName, countyName, townName, villageName;
    public String detailAddress;


    /* public IDNameBean sheng, shi, xiang, cun, qu;*/

    public String getSheng() {
        //return sheng == null ? "?省" : sheng.toString();
        return provinceName == null ? "?省" : provinceName.toString();
    }

    public String getShi() {
        // return shi == null ? "?市" : shi.toString();
        return cityName == null ? "?市" : cityName.toString();
    }

    public String getQu() {
        // return qu == null ? "?区" : qu.toString();
        return countyName == null ? "?区" : countyName.toString();
    }

    public String getXiang() {
        // return xiang == null ? "?乡" : xiang.toString();
        return townName == null ? "?乡" : townName.toString();
    }

    public String getCun() {
        //  return cun == null ? "?村" : cun.toString();
        return villageName == null ? "?村" : villageName.toString();
    }

    @Override
    public String toString() {
        return getSheng() + getShi() + getQu() + getXiang() + getCun() + detailAddress;
    }
}
