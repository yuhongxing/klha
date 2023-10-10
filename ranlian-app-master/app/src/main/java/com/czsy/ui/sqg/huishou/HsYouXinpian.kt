package com.czsy.ui.sqg.huishou

import android.R
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import com.czsy.CZBackTask
import com.czsy.Constant
import com.czsy.INFCHandler
import com.czsy.bean.CommonOrderBean
import com.czsy.bean.PayMethodBean
import com.czsy.ui.AbsPVScanEmpty
import com.czsy.ui.MainActivity
import mylib.app.BackTask
import mylib.utils.Utils
import org.json.JSONArray
import org.json.JSONObject

/**
 * 回收-有芯片
 */
class HsYouXinpian(a: MainActivity, b: CommonOrderBean, has_yuqi: Boolean) : AbsPVScanEmpty(a, b, has_yuqi, true), INFCHandler {

    var b: CommonOrderBean = b

    override fun createMainView(ctx: Context?) {
        super.createMainView(ctx)
        tv_title.text = "扫描回收瓶"
        btn_next.visibility = View.GONE
        btn_clear.visibility = View.GONE
        btn_no_empty.setText(R.string.ok)
        btn_no_empty.setTextColor(Color.WHITE)
        btn_no_empty.setBackgroundResource(com.czsy.android.R.drawable.sel_btn_blue)
    }

    override fun onClickScanHeavy() {}

    //确定
    override fun onClickNoEmpty() {
        Log.d("xing", "onClickNoEmpty")

        val size = gp_id_list.size + nfc_gp_list.size
        if (size == 0) {
            Utils.toastLONG("没有输入气瓶")
            return
        }

        val jdata = JSONObject()
        val jsonArray = JSONArray()

        for (bean in gp_id_list) {
            val json = JSONObject()
            json.put("gangPingHao", bean.gp_bean.gangPingHao)
            json.put("yvQi", bean.yu_qi)
            json.put("yvQiJinE", bean.yin_tui_kuan)
            json.put("gangPingJinE", bean.gangping_jine)
            jsonArray.put(json)
//            Log.d("xing", "钢瓶号：：\n钢瓶号-->${bean.gp_bean.gangPingHao}\n余气-->${bean.yu_qi}\n余气金额-->${bean.yin_tui_kuan}\n钢瓶金额-->${bean.gangping_jine}")
        }

        for (bean in nfc_gp_list) {
            val json = JSONObject()
            json.put("gangPingHao", bean.gp_bean.gangPingHao)
            json.put("yvQi", bean.yu_qi)
            json.put("yvQiJinE", bean.yin_tui_kuan)
            json.put("gangPingJinE", bean.gangping_jine)
            jsonArray.put(json)
//            Log.d("xing", "NFC：：\n钢瓶号-->${bean.gp_bean.gangPingHao}\n余气-->${bean.yu_qi}\n余气金额-->${bean.yin_tui_kuan}\n钢瓶金额-->${bean.gangping_jine}")
        }

        Log.d("xing", "json-->${jsonArray.toString()}")

        BackTask.post(object :CZBackTask(act){
            override fun getURL(): String {
                return if (b == null ||
                        b.isNewOrder()) "dingDan/chuangJianByPeiSongYuan" else "dingDan/jieSuanZheJiuDan"

            }

            override fun runFront2() {
                Utils.toast(com.czsy.android.R.string.tip_op_ok)
                act.main_pvc.popTo1()
            }

            override fun getInputParam(): String {
                if (b !=null && !b.isNewOrder){
                    jdata.put("sid", b.dingDanHao)
//                    jdata.put("feiYongZongJi", fyzj)
//                    jdata.put("gangPingShuLiang", gpsl)
                }else{
                    jdata.put("keHuId", b.keHuId)
                    jdata.put("dingDanType", Constant.dingdan_type_zhejiu)
//                    jdata.put("zheJiuDanFeiYongZongJi", fyzj)
//                    jdata.put("zheJiuDanHuiShouGangPingCount", gpsl)
                    jdata.put("diZhi", b.diZhi)
                    jdata.put("zhiFuFangShi", PayMethodBean.xian_jin.id)
                }
//                jdata.put("beiZhu", beizhu)
//                jdata.put("louCeng", louCeng)
                jdata.put("gpInfo",jsonArray)
                return jdata.toString()
            }

            override fun parseResult(jdata: JSONObject?) {
                val code = jdata!!.optInt("code", 0)
                if (code != 200) {
                    throw Exception()
                }
            }

        })


    }

    override fun onAttach(firstShow: Boolean) {
        if (firstShow) {
            client_order_bean?.empty_list?.clear()
        }
        super.onAttach(firstShow)
    }
}