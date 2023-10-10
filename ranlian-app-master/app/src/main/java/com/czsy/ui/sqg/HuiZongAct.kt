package com.czsy.ui.sqg

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import com.czsy.CZBackTask
import com.czsy.android.databinding.ActSqgHuizongBinding
import com.czsy.ui.AbsPVBase
import com.czsy.ui.MainActivity
import mylib.app.BackTask
import org.json.JSONObject

/**
 * 配送员汇总
 */
class HuiZongAct(a: MainActivity) : AbsPVBase(a) {

    lateinit var binding: ActSqgHuizongBinding

    var stat_json:JSONObject = JSONObject()

    override fun createMainView(ctx: Context?) {
        Log.d("xing","createMainView")
        binding = ActSqgHuizongBinding.inflate(LayoutInflater.from(act))
        mMainView = binding.root

        binding.include.tvTitle.text = "配送员汇总"

        binding.include.ivBack.setOnClickListener {
            act.main_pvc.pop()
        }

//        chuliContext()
    }

    override fun onAttach(firstShow: Boolean) {
        super.onAttach(firstShow)
        Log.d("xing","onAttach")

        act.showProgress("正在加载")
        BackTask.post(object : CZBackTask(act) {
            override fun getURL(): String {
                return "dingDan/peiSongYuanHuiZong"
            }

            override fun runFront2() {

//                Log.d("xing",stat_json.toString())
//                val xsJsonObject = stat_json.getJSONArray("xiaoShou")
//                Log.d("xing",xsJsonObject.toString())
                chuliContext()
            }

            override fun getInputParam(): String {
                return ""
            }

            override fun parseResult(jdata: JSONObject?) {
                if (jdata != null) {
                    val j = jdata.getJSONObject("result")
                    stat_json = j
                }
            }

        })

    }

    fun chuliContext() {

        val sb = """一、上缴额统计：
今日（14:00前）需上交金额：${String.format("%.2f元\n", stat_json.optDouble("jinrShangJiaoBefore14", 0.0))}明日（14:00后）需上交金额：${String.format("%.2f元\n", stat_json.optDouble("jingrShangJiaoAfter14", 0.0))}
二、订单收付款统计

14:00前：
订单收入金额：${String.format("%.2f元\n", stat_json.optDouble("dingDanShouRuBefore14", 0.0))}现金收入：${String.format("%.2f元\n", stat_json.optDouble("xianJinBefore14", 0.0))}微信收入：${String.format("%.2f元\n", stat_json.optDouble("weiXinBefore14", 0.0))}欠款金额：${String.format("%.2f元\n", stat_json.optDouble("qianKuanBefore14", 0.0))}押金收取：${String.format("%.2f元\n", stat_json.optDouble("shouYaJinBefore14", 0.0))}押金退还：${String.format("%.2f元\n", stat_json.optDouble("tuiYaJinBefore14", 0.0))}现金充值：${String.format("%.2f元\n", stat_json.optDouble("xianJinChongZhiBefore14", 0.0))}微信充值：${String.format("%.2f元\n", stat_json.optDouble("weiXinChongZhiBefore14", 0.0))}余额消费：${String.format("%.2f元\n", stat_json.optDouble("ranQiKaShouRuBefore14", 0.0))}楼层费  ：${String.format("%.2f元\n", stat_json.optDouble("louCengBefore14", 0.0))}退气金额：${String.format("%.2f元\n", stat_json.optDouble("tuiQiJinEBefore14", 0.0))}回收气瓶金额：${String.format("%.2f元\n", stat_json.optDouble("huiShouJinEBefore14", 0.0))}优惠金额：${String.format("%.2f元\n", stat_json.optDouble("youHuiJinEBefore14", 0.0))}余气重量：${String.format("%.2fKG\n", stat_json.optDouble("yuQiBefore14", 0.0))}积分数量：${String.format("%s\n", stat_json.optString("jiFenBefore14", "0"))}小程序订单气瓶金额：${String.format("%.2f元\n", stat_json.optDouble("xiaoChengXuJinEBefore14", 0.0))}
14:00后：
订单收入金额：${String.format("%.2f元\n", stat_json.optDouble("dingDanShouRuAfter14", 0.0))}现金收入：${String.format("%.2f元\n", stat_json.optDouble("xianJinAfter14", 0.0))}微信收入：${String.format("%.2f元\n", stat_json.optDouble("weiXinAfter14", 0.0))}欠款金额：${String.format("%.2f元\n", stat_json.optDouble("qianKuanAfter14", 0.0))}押金收取：${String.format("%.2f元\n", stat_json.optDouble("shouYaJinAfter14", 0.0))}押金退还：${String.format("%.2f元\n", stat_json.optDouble("tuiYaJinAfter14", 0.0))}现金充值：${String.format("%.2f元\n", stat_json.optDouble("xianJinChongZhiAfter14", 0.0))}微信充值：${String.format("%.2f元\n", stat_json.optDouble("weiXinChongZhiAfter14", 0.0))}余额消费：${String.format("%.2f元\n", stat_json.optDouble("ranQiKaShouRuAfter14", 0.0))}楼层费  ：${String.format("%.2f元\n", stat_json.optDouble("louCengAfter14", 0.0))}退气金额：${String.format("%.2f元\n", stat_json.optDouble("tuiQiJinEAfter14", 0.0))}回收气瓶金额：${String.format("%.2f元\n", stat_json.optDouble("huiShouJinEAfter14", 0.0))}优惠金额：${String.format("%.2f元\n", stat_json.optDouble("youHuiJinEAfter14", 0.0))}余气重量：${String.format("%.2fKG\n", stat_json.optDouble("yuQiAfter14", 0.0))}积分数量：${String.format("%s\n", stat_json.optString("jiFenAfter14", "0"))}小程序订单气瓶金额：${String.format("%.2f元\n", stat_json.optDouble("xiaoChengXuJinEAfter14", 0.0))}
            """

        //销售
        val xsJsonObject = stat_json.getJSONArray("xiaoShou").get(0) as JSONObject
        val xsJA_tianRanQi = xsJsonObject.getJSONArray("tianRanQi")
        var tv_tianRanQi = "天然气\n"
        for (i in 0 until xsJA_tianRanQi.length()) {
            val obj = xsJA_tianRanQi.get(i) as JSONObject
            val name = obj.getString("name")
            val v = obj.getString("val")
            tv_tianRanQi += "${"$name:$v"}\n"
        }
        binding.tvXsTianranqi.text = tv_tianRanQi

        val xsJA_bingWan = xsJsonObject.getJSONArray("bingWan")
        var tv_bingWan = "丙烷\n"
        for (i in 0 until xsJA_bingWan.length()) {
            val obj = xsJA_bingWan.get(i) as JSONObject
            val name = obj.getString("name")
            val v = obj.getString("val")
            tv_bingWan += "${"$name:$v"}\n"
        }
        binding.tvXsBingwan.text = tv_bingWan

        val xsJA_dingWan = xsJsonObject.getJSONArray("dingWan")
        var tv_dingWan = "丁烷\n"
        for (i in 0 until xsJA_dingWan.length()) {
            val obj = xsJA_dingWan.get(i) as JSONObject
            val name = obj.getString("name")
            val v = obj.getString("val")
            tv_dingWan += "${"$name:$v"}\n"
        }
        binding.tvXsDingwan.text = tv_dingWan

        val xsJA_yeHuaQi = xsJsonObject.getJSONArray("yeHuaQi")
        var tv_yeHuaQi = "液化石油气\n"
        for (i in 0 until xsJA_yeHuaQi.length()) {
            val obj = xsJA_yeHuaQi.get(i) as JSONObject
            val name = obj.getString("name")
            val v = obj.getString("val")
            tv_yeHuaQi += "${"$name:$v"}\n"
        }
        binding.tvXsYehuashiyouqi.text = tv_yeHuaQi


        //回收
        val hsJsonObject = stat_json.getJSONArray("huiShou").get(0) as JSONObject
        val hsJA_tianRanQi = hsJsonObject.getJSONArray("tianRanQi")
        var tvhs_tianRanQi = "天然气\n"
        for (i in 0 until hsJA_tianRanQi.length()) {
            val obj = hsJA_tianRanQi.get(i) as JSONObject
            val name = obj.getString("name")
            val v = obj.getString("val")
            tvhs_tianRanQi += "${"$name:$v"}\n"
        }
        binding.tvHsTianranqi.text = tvhs_tianRanQi

        val hsJA_bingWan = hsJsonObject.getJSONArray("bingWan")
        var tvhs_bingWan = "丙烷\n"
        for (i in 0 until hsJA_bingWan.length()) {
            val obj = hsJA_bingWan.get(i) as JSONObject
            val name = obj.getString("name")
            val v = obj.getString("val")
            tvhs_bingWan += "${"$name:$v"}\n"
        }
        binding.tvHsBingwan.text = tvhs_bingWan

        val hsJA_dingWan = hsJsonObject.getJSONArray("dingWan")
        var tvhs_dingWan = "丁烷\n"
        for (i in 0 until hsJA_dingWan.length()) {
            val obj = hsJA_dingWan.get(i) as JSONObject
            val name = obj.getString("name")
            val v = obj.getString("val")
            tvhs_dingWan += "${"$name:$v"}\n"
        }
        binding.tvHsDingwan.text = tvhs_dingWan

        val hsJA_yeHuaQi = hsJsonObject.getJSONArray("yeHuaQi")
        var tvhs_yeHuaQi = "液化石油气\n"
        for (i in 0 until hsJA_yeHuaQi.length()) {
            val obj = hsJA_yeHuaQi.get(i) as JSONObject
            val name = obj.getString("name")
            val v = obj.getString("val")
            tvhs_yeHuaQi += "${"$name:$v"}\n"
        }
        binding.tvHsYehuashiyouqi.text = tvhs_yeHuaQi


        binding.tvContent.text = sb

    }
}