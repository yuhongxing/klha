package com.czsy.ui.xunjian

import android.util.Log
import android.widget.*
import com.czsy.CZBackTask
import com.czsy.ui.MainActivity
import com.czsy.ui.changzhan.AbsPVCZSeleteTipSub
import mylib.app.BackTask
import mylib.utils.Utils
import org.json.JSONArray
import org.json.JSONObject

/**
 * 巡检-选择问题提交
 */
class PVXunjianJianceKt(a: MainActivity) : AbsPVCZSeleteTipSub(a) {


    override fun rightBtnOnClick() {
        if(!et_input?.text.toString().isEmpty()){
            act.pvc.push(PVXunjianJiluKt(act, et_input?.text.toString()))
        }else{
            Utils.toastSHORT("请扫描芯片")
        }
    }

    override fun initView() {
        super.initView()
        et_input?.hint = "请扫描芯片"
    }

    override fun nextBtnOnClick() {
        if (picUrl == null){
            Utils.toastSHORT("请拍照")
            return
        }
        submitData()
    }

    override fun getTipUrl(): String {
        return "xunjianshuju/queryXunJianNeiRongGuanLiByXinPianHao"
    }

    /**
     * 提交
     */
    fun submitData() {

        act.showProgress()
        BackTask.post(object : CZBackTask(act) {
            var json = JSONObject()
            override fun getURL(): String {
                return "xunjianshuju/insertXunJianJiLuAndXiangQing"
            }

            override fun runFront2() {
                if (json.getInt("code") == 200) {
                    Toast.makeText(act, "操作完成", Toast.LENGTH_SHORT).show()
                    act.pvc.pop()
                } else {
                    Toast.makeText(act, "" + json.getString("message"), Toast.LENGTH_SHORT).show()
                }
            }

            override fun getInputParam(): String {

                val j = JSONObject()
                j.put("beiZhu", et_comment?.text.toString())
                j.put("tuPian", picUrl)
                j.put("xinPianHao", et_input?.text.toString())
                val anJianDaAnList = JSONArray()
                j.put("xunJianJiLuAndXiangQingParams", anJianDaAnList)
                for (vh in anjian_list!!) {
                    val o = JSONObject()
                    anJianDaAnList.put(o)
                    o.put("wenti", vh.item?.wenTi)
                    val huida = JSONArray()
                    o.put("opthions", huida)
                    for (cb in vh.cbs!!) {
                        if (cb.isChecked) {
                            huida.put(cb.text)
                        }
                    }
                }
                Log.d("xing", "sub-->" + j.toString())
                return j.toString()

            }

            override fun parseResult(jdata: JSONObject?) {
                if (jdata != null) {
                    json = jdata
                }
                Log.d("xing", "巡检问题提交-->" + jdata.toString())

            }

        })

    }

}