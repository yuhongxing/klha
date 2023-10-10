package com.czsy.ui.sqg.details

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import com.czsy.CZBackTask
import com.czsy.android.databinding.PvOrderDetailBinding
import com.czsy.bean.CommonOrderBean
import com.czsy.ui.AbsPVBase
import com.czsy.ui.MainActivity
import com.czsy.ui.sqg.order.PVClientOrder_Empty
import mylib.app.BackTask
import mylib.utils.Utils
import org.json.JSONObject

/**
 * 退瓶单--详情
 */
class TuipingDetails(a: MainActivity, val bean: CommonOrderBean) : AbsPVBase(a), View.OnClickListener {

    lateinit var bind: PvOrderDetailBinding
    var qiWangShiJian: String? = null

    override fun createMainView(ctx: Context?) {

        bind = PvOrderDetailBinding.inflate(LayoutInflater.from(act))
        mMainView = bind.root

        bind.tvOrderOkTime.visibility = View.GONE
        bind.zhifufangshi.visibility = GONE
        bind.tvOrderMoney.visibility = GONE

        bind.tvRight.visibility = View.VISIBLE
        bind.tvRight.setOnClickListener(this)
        bind.ivBack.setOnClickListener(this)

    }

    override fun onAttach(firstShow: Boolean) {
        super.onAttach(firstShow)

        act.showProgress()
        BackTask.post(object : CZBackTask(act) {
            override fun getURL(): String {
                return "dingDan/chaXunDingDanXiangQingForAnZhuo"
            }

            override fun runFront2() {

                bind.tvOrdernum.text = bean.dingDanLeiXingName + "：" + bean.dingDanHao
                bind.tvOrderTime.text = """
                    下单时间：${bean.chuangJianRiQi}
                    期望日期：$qiWangShiJian
                    """.trimIndent()
                bind.tvKehuNum.text = "客户编号：" + bean.keHuBianHao
                bind.tvName.text = "客        户：" + bean.keHuMing
                bind.tvTel.text = ""
                Utils.setTextSpan("电        话：", bean.keHuDianHua, "", bind.tvTel, Utils.TextClickListener { Utils.toCall(bean.keHuDianHua) })
                val louceng = if (bean.louCeng == null) "" else "; 楼层：" + bean.louCeng
                bind.tvAddress.text = "地        址：" + bean.diZhi + louceng
                bind.tvGyz.text = "供  应  站：" + bean.gongYingZhan
                bind.tvLaiyuan.text = "来        源：" + bean.laiYuanName
                bind.tvBeizhu.text = "备        注：" + bean.beiZhu

            }

            override fun getInputParam(): String {

                return "{\"dingDanHao\":${bean.dingDanHao}}"
            }

            override fun parseResult(jdata: JSONObject?) {
                qiWangShiJian = jdata?.getJSONObject("result")?.getString("qiWangShiJian")
            }

        })

    }

    override fun onClick(v: View?) {
        when (v) {
            bind.ivBack ->
                act.pvc.pop()
            bind.tvRight ->
                act.pvc.push(PVClientOrder_Empty(act, bean))
        }
    }


}