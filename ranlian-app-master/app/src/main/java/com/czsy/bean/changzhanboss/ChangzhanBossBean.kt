package com.czsy.bean.changzhanboss

//气体出入库
class ChuRuKuBeans<T> : ArrayList<ChuRuKuBeanItem>()

//气体出入库详情
class ChuRuKuDetailsBeans<T> : ArrayList<ChuRuKuDetailsBeanItem>()

//储罐列表
class ChuGuanBeans<T> : ArrayList<ChuGuanBeanItem>()

//储罐气体出入库记录
class ChuguanQitiChurukuBeans<T> : ArrayList<ChuguanQitiChurukuBeanItem>()

//充装记录
class ChongzhuangLogBeans<T> : ArrayList<ChongzhuangLogBeanItem>()

//运输员
class YunshuyuanKucunBeans<T> : ArrayList<YunshuyuanKucunBeanItem>()


/**
 *气体出入库列表item
 */
data class ChuRuKuBeanItem(
        val beiZhu: String,
        val changZhan: String,
        val changZhanId: String,
        val chePai: String,
        val chuangJianRiQi: String,
        val gangPingCount: Int,
        val gangPingIdList: String,
        val gongYingZhan: String,
        val gongYingZhanId: String,
        val id: String,
        val isDel: Int,
        val lastUpdateDate: String,
        val status: Int,// 审批状态  1：已审批；2：未审批； 3：驳回
        val statusName: String,
        val type: Int,
        val yuQi: Double,
        val yuQiList: String,
        val yuanYin: String,
        val yunShuYuanId: String,
        val yunShuYuanName: String
)

/**
 *气体出入库详情bean
 */
data class ChuRuKuDetailsBeanItem(
        val gangPingHao: String,
        val xinPianHao: String,
        val yuQiStatusName: String,
        val guiGeName: String,
        val qiTiLeiXing: String


)

/**
 *钢瓶库存bean
 */
data class TongJiGPKucun(
        val `10kg`: Kg,
        val `15kg`: KgX,
        val `50kg`: Kg,
        val `50kgII`: KgII,
        val `5kg`: KgXX
)

data class Kg(
        val kong: Int,
        val zhong: Int
)

data class KgX(
        val kong: Int,
        val zhong: Int
)

data class KgII(
        val kong: Int,
        val zhong: Int
)

data class KgXX(
        val kong: Int,
        val zhong: Int
)

/**
 * 气体统计bean
 */
data class TongJiQitiBean(
        val chuGuanAll: Double,//储罐内气体汇总
        val sum: Double,//总和
        val yuqiAll: Double,//空瓶内余气汇总
        val zhongPingAll: Double //重瓶内余气汇总

)

/**
 * 储罐列表item
 */
data class ChuGuanBeanItem(
        val bianHao: String,
        val changZhan: String,
        val changZhanId: String,
        val chuGuanChangDu: Double,
        val chuGuanZhiJing: Any,
        val chuangJianRiQi: String,
        val dangQianRongLiang: Double,
        val dangQianWenDu: Double,
        val dangQianYaLi: Double,
        val dangQianYeWei: Double,
        val fengTouBanJing: Any,
        val id: String,
        val isDel: Int,
        val lastUpdateDate: String,
        val qiTiType: Int,
        val qiTiTypeName: String,
        val qiYongRiQi: String,
        val shengChanNianXian: String,
        val status: Int,
        val statusName: String,
        val type: Any,
        val xiaCiXunJianRiQi: String,
        val zuiDaRongLiang: Double


) {
    override fun toString(): String {
        return "$bianHao"
    }
}

/**
 * 储罐气体出入库记录bean
 */
data class ChuguanQitiChurukuBeanItem(
        val caoZuoRen: String,
        val chePai: String,
        val chuangJianRiQi: String,
        val leiXing: Int,
        val leiXingName: String,
        val shiJiJiaGe: Double,
        val shiJiZhongLiang: Double
)

/**
 * 充装记录bean
 */
data class ChongzhuangLogBeanItem(
        val chengHao: Any,
        val chongZhuangZhan: Any,
        val chongZhuangZhanId: Any,
        val chuGuan: Any,
        val chuangJianRiQi: String,
        val endStartWeight: Double,
        val gangPingQiTi: Any,
        val ranQiLeiXing: Any,
        val remark: Any,
        val startWeight: Any,
        val useWeight: Double,
        val user: String
)

data class YunshuyuanKucunBeanItem(
        val changZhan: String,
        val changZhanId: Any,
        val gongYingZhan: Any,
        val gongYingZhanId: Any,
        val keHuId: Any,
        val keHuMing: Any,
        val peiSongYuan: Any,
        val peiSongYuanId: Any,
        val sum: Sum,
        val yunShuYuan: String,
        val yunShuYuanId: String
)


/**
 * 运输员库存item
 */
data class Sum(
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
