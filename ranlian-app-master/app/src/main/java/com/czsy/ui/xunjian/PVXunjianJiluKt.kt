package com.czsy.ui.xunjian

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.czsy.CZNetUtils
import com.czsy.android.R
import com.czsy.ui.AbsPVBase
import com.czsy.ui.MainActivity
import com.czsy.ui.PicFullImageView
import mylib.app.BackFrontTask
import mylib.app.BackTask
import org.json.JSONObject

/**
 * 巡检记录
 */
class PVXunjianJiluKt(a: MainActivity, _xinPianHao: String) : AbsPVBase(a), View.OnClickListener {

    val xinPianHao = _xinPianHao
    var picUrl: String? = null
    var tv_content: TextView? = null
    var iv_content: ImageView? = null

    override fun createMainView(ctx: Context?) {
        mMainView = View.inflate(act, R.layout.pv_xj_jilu, null)
        tv_content = mMainView.findViewById(R.id.tv_content)
        iv_content = mMainView.findViewById(R.id.iv_content)

        mMainView.findViewById<TextView>(R.id.tv_title).setText("记录")
        mMainView.findViewById<ImageView>(R.id.iv_back).setOnClickListener(this)
        iv_content?.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_back -> act.pvc.pop()
            R.id.iv_content -> act.pvc.push(PicFullImageView(act, CZNetUtils.svr_host + picUrl))
        }
    }

    override fun onAttach(firstShow: Boolean) {
        super.onAttach(firstShow)
        /**
         * 巡检记录最近一条数据
         */
        var sb = StringBuilder()
        BackTask.post(object : BackFrontTask {

            override fun runFront() {

                tv_content?.text = sb
                Glide.with(act).load(CZNetUtils.svr_host + picUrl).into(iv_content!!)
            }

            override fun runBack() {
                val jsonObject = CZNetUtils.postCZHttp("xunJianJiLu/chaXunZuiJinXunJianJiLu", "{ \"xinPianHao\":\"$xinPianHao\"}")
                val ret = jsonObject.optJSONObject("result") ?: return
                picUrl = jsonObject.getJSONObject("result").getString("tupian")

                sb.append("芯片号：${ret.getString("xinpianhao")}\n" +
                        "设备名称：${ret.getString("shebeimingcheng")}\n" +
                        "设备编号：${ret.getString("shebeibianhao")}\n" +
                        "设备规格：${ret.getString("guige")}\n" +
                        "设备类型：${ret.getString("leixing")}\n" +
                        "单位名称：${ret.getString("danweimingcheng")}\n" +
                        "操作人：${ret.getString("caozuorenmingcheng")}\n" +
                        "上次巡检时间：${ret.getString("lastUpdateDate")}\n" +
                        "备注：${ret.getString("beizhu")}\n"
                )
            }
        })


    }
}