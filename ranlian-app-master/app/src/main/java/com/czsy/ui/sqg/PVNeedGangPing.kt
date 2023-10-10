package com.czsy.ui.sqg

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.czsy.CZBackTask
import com.czsy.android.databinding.PvNeedgangpingBinding
import com.czsy.other.DatePickerDialog
import com.czsy.ui.AbsPVBase
import com.czsy.ui.MainActivity
import mylib.app.BackTask
import mylib.utils.Utils
import org.json.JSONObject
import java.util.*

/**
 * 该配送员需要配送的钢瓶统计
 */
class PVNeedGangPing(a: MainActivity) : AbsPVBase(a), View.OnClickListener {

    lateinit var binding: PvNeedgangpingBinding

    var search_time = System.currentTimeMillis() - (1000 * 60 * 60 * 24 + 1000)
    var search_time2 = System.currentTimeMillis()
    var startDate = ""
    var endDate = ""

    override fun createMainView(ctx: Context?) {

        binding = PvNeedgangpingBinding.inflate(LayoutInflater.from(act))
        mMainView = binding.root

        binding.include.tvTitle.text = "需要配送的气瓶"
        binding.include.ivBack.setOnClickListener { act.pvc.pop() }

        binding.tvTime.setOnClickListener(this)
        binding.tvTime1.setOnClickListener(this)
        updateTitle()

    }

    fun updateTitle() {
        var c = Calendar.getInstance()
        c.timeInMillis = search_time

        var y = c[Calendar.YEAR]
        var m = c[Calendar.MONTH]
        var d = c[Calendar.DAY_OF_MONTH]
//        var h = c[Calendar.HOUR_OF_DAY]
//        var MM = c[Calendar.MINUTE]
//        var ss = c[Calendar.SECOND]
        binding.tvTime.setText(String.format("开始日期: %4d-%02d-%02d", y, m + 1, d))
        startDate = "${y}-${m+1}-${d} 00:00:00"


        c = Calendar.getInstance()
        c.timeInMillis = search_time2

        y = c[Calendar.YEAR]
        m = c[Calendar.MONTH]
        d = c[Calendar.DAY_OF_MONTH]
//        h = c[Calendar.HOUR_OF_DAY]
//        MM = c[Calendar.MINUTE]
//        ss = c[Calendar.SECOND]
        binding.tvTime1.setText(String.format("结束日期: %4d-%02d-%02d", y, m + 1, d))
        endDate = "${y}-${m+1}-${d} 23:59:59"
    }

    override fun onAttach(firstShow: Boolean) {
        super.onAttach(firstShow)

        act.showProgress()
        loadData()

    }

    override fun onClick(v: View?) {

        val d = DatePickerDialog(act)
        d.setOnDateSetListener(DatePickerDialog.OnDateSetListener { view, i, i1, i2 ->
            val c = Calendar.getInstance()
            c[Calendar.YEAR] = i
            c[Calendar.MONTH] = i1
            c[Calendar.DAY_OF_MONTH] = i2
            c[Calendar.HOUR_OF_DAY] = 0
            c[Calendar.MINUTE] = 0
            c[Calendar.SECOND] = 0
            val t = c.timeInMillis
            if (v === binding.tvTime) {
                if (t > search_time2) {
                    Utils.toastLONG("起始时间必须在结束时间之前")
                    return@OnDateSetListener
                }
                search_time = t
            } else {
                if (t < search_time) {
                    Utils.toastLONG("结束时间必须在起始时间之后")
                    return@OnDateSetListener
                }
                search_time2 = t
            }
            updateTitle()
            loadData()
        })
        d.show()

    }

    fun loadData() {

        BackTask.post(object : CZBackTask(act) {
            var result = JSONObject()
            override fun getURL(): String {
                return "dingDan/tongJiGangPingCountForAnZhuo"
            }

            override fun runFront2() {
                binding.tv5kg.text = result?.getString("gangPing5KGCount")
                binding.tv10kg.text = result?.getString("gangPing10KGCount")
                binding.tv15kg.text = result?.getString("gangPing15KGCount")
                binding.tv50kg.text = result?.getString("gangPing50KGCount")
                binding.tv50kg2.text = result?.getString("gangPing50KGIICount")
            }

            override fun getInputParam(): String {
                val json = JSONObject()
                json.put("status", 2)
                json.put("dingDanLeiXing", 1)
                json.put("startDate", startDate)
                json.put("endDate", endDate)

                return json.toString()
            }

            override fun parseResult(jdata: JSONObject?) {
                result = jdata?.getJSONObject("result")!!
            }

        })
    }

}