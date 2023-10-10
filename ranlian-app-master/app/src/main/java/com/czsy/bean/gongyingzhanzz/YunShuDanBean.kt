package com.czsy.bean.gongyingzhanzz

open class YunShuDanBean<T>:ArrayList<ItemBean>()

data class ItemBean(
    val changZhan: String,
    val changZhanId: String,
    val chePai: String,
    val chuangJianRiQi: String,
    val gangPingCount: Int,
    val gangPingIdList: Any,
    val gangPingTongJi: GangPingTongJi,
    val gongYingZhan: String,
    val gongYingZhanId: String,
    val id: String,
    val shenPiRen: String,
    val statistics: Statistics,
    val status: Int,
    val statusName: String,
    val type: Int,
    val typeName: String,
    val yuQi: Double,
    val yunShuYuanId: String,
    val yunShuYuanName: String
)

data class GangPingTongJi(
    val gangPing10KGCount: Int,
    val gangPing15KGCount: Int,
    val gangPing50KGCount: Int,
    val gangPing50KGIICount: Int,
    val gangPing50KGYeCount: Int,
    val gangPing5KGCount: Int,
    val shiShouYaJin: Int,
    val sum: Int,
    val xiaoShouE: Int
)

data class Statistics(
    val anJian: AnJian,
    val baoFei: BaoFei,
    val kongPing: KongPing,
    val zhongPing: ZhongPing
)

data class AnJian(
    val gangPing10KGCount: Int,
    val gangPing15KGCount: Int,
    val gangPing50KGCount: Int,
    val gangPing50KGIICount: Int,
    val gangPing50KGYeCount: Int,
    val gangPing5KGCount: Int,
    val shiShouYaJin: Int,
    val sum: Int,
    val xiaoShouE: Int
)

data class BaoFei(
    val gangPing10KGCount: Int,
    val gangPing15KGCount: Int,
    val gangPing50KGCount: Int,
    val gangPing50KGIICount: Int,
    val gangPing50KGYeCount: Int,
    val gangPing5KGCount: Int,
    val shiShouYaJin: Int,
    val sum: Int,
    val xiaoShouE: Int
)
