package com.czsy.ui.changzhan.chongzhuangcheck

import android.util.Log
import android.view.View
import com.czsy.CZBackTask
import com.czsy.CZNetUtils
import com.czsy.ui.MainActivity
import com.czsy.ui.changzhan.AbsPVCZSeleteTipSub
import mylib.app.BackFrontTask
import mylib.app.BackTask
import mylib.utils.Utils
import org.json.JSONArray
import org.json.JSONObject

/**
 * 充装前后检测问题提交
 */
class PVCZSeleteTipSub(a: MainActivity, _status: Int, _idList: List<Long>) : AbsPVCZSeleteTipSub(a) {

    var idList: List<Long>? = null

    init {
        status = _status
        idList = _idList
    }

    override fun initView() {
        when (status) {
            1 -> tv_title?.setText("充装前检测")
            2 -> tv_title?.setText("充装后检测")
        }
        line_top?.visibility = View.GONE
        tv_right?.visibility = View.GONE
    }

    override fun rightBtnOnClick() {
    }

    override fun nextBtnOnClick() {

        BackTask.post(object : CZBackTask(act) {
            var code = 500
            override fun getURL(): String {
                var url = ""
                when (status) {
                    1 -> url = "gangPing/beforeCheck"
                    2 -> url = "gangPing/afterCheck"
                }
                return url
            }

            override fun runFront2() {
                if (code == 200)
                    Utils.toastSHORT("提交成功")
                    act.pvc.popTo1()

            }

            override fun getInputParam(): String {
                val j = JSONObject()
                j.put("beiZhu", et_comment?.text.toString())
                j.put("picture", picUrl)
                val anJianDaAnList = JSONArray()
                val idListJSON = JSONArray()
                for (id in idList!!) {
                    idListJSON.put(id)
                }
                j.put("list", anJianDaAnList)
                j.put("idList", idListJSON)
                for (vh in anjian_list!!) {
                    val o = JSONObject()
                    anJianDaAnList.put(o)
                    o.put("problemId", vh.item?.id)
//            o.put("wenti", vh.item?.wenTi)
                    val huida = JSONArray()
                    o.put("value", huida)
                    for (cb in vh.cbs!!) {
                        if (cb.isChecked) {
                            huida.put(cb.text)
                        }
                    }
                }
                return j.toString()
            }

            override fun parseResult(jdata: JSONObject?) {

                code = jdata?.getInt("code")!!
            }

        })

    }

    override fun getTipUrl(): String {
        return "gangPing/queryChongZhuangQuestion"
    }


}