package com.czsy.bean.gongyingzhanzz

open class ShenPiBean<T> : ArrayList<ShenPiBeanItem>()

data class ShenPiBeanItem(
    val count: Int,
    val id: String,
    val kongPing: KongPing,
    val operationDate: String,
    val operatorId: String,
    val orgId: String,
    val orgName: String,
    val shenPiRen: String,
    val shenPiStatus: Int,//1:已审批，2：未审批，3：撤销
    val shenPiStatusName: String,
    val status: Int,
    val statusName : String,
    val userName: String,
    val zhongPing: ZhongPing
)

data class KongPing(
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

data class ZhongPing(
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