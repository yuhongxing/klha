package com.czsy.ui.changzhan

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.InputFilter
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.Window
import android.widget.*
import com.czsy.CZBackTask
import com.czsy.Constant
import com.czsy.android.R
import com.czsy.bean.GangPingBean
import com.czsy.bean.IDNameBean
import com.czsy.ui.MainActivity
import mylib.app.BackFrontTask
import mylib.app.BackTask
import mylib.app.BaseActivity
import mylib.utils.Utils
import org.json.JSONObject

class PVCZUpdateGangping(a: MainActivity) : AbsPVCZNFC(a) {

    var guige_list = ArrayList<IDNameBean>()
    var qiti_list = ArrayList<IDNameBean>()
    var id: Int = 0

    //    var isXian: Boolean = true//多次扫瓶或查询时，dialog只显示一次
    var dialog: Dialog? = null

    override fun getMainViewRes(): Int {
        return R.layout.cz_abs_chadang//pv_changzhan_xiugai
    }

    override fun createMainView(ctx: Context?) {
        super.createMainView(ctx)
        tv_title.text = "修改气瓶信息"
    }

    override fun gpSearched(bp: GangPingBean?) {

        if (dialog != null && dialog?.isShowing!!) {
            dialog?.dismiss()
        }
        tv_content.text = "钢印号：" + bp?.gangPingHao + "\n" +
                "芯片号：" + bp?.xinPianHao + "\n" +
                "规格：" + bp?.guiGeName + "\n" +
                "气体类型：" + bp?.qiTiLeiXing
        id = bp?.id?.toInt()!!
        if (id != 0) {
            loadYvZhiInfo(bp)
        } else {
            Utils.toastSHORT("请重新扫描或查询气瓶")
        }
    }

    override fun gpSearchedError() {
        if (dialog != null && dialog?.isShowing!!) {
            dialog?.dismiss()
        }
    }

    fun subData(gph: String, guige: Int, cszl: String, qtlx: Int) {
        act.showProgress()
        BackTask.post(object : CZBackTask(act) {
            var isok = false
            override fun getURL(): String {
                return "gangPing/xiuGai"
            }

            override fun runFront2() {
                if (isok)
                    Utils.toastSHORT("修改成功")
                    act.main_pvc.pop()
            }

            override fun getInputParam(): String {
                var json = JSONObject()
                json.put("gangPingHao", gph)
                json.put("guiGe", guige)
                json.put("id", id)
                json.put("jingZhong", cszl)
                json.put("qiTiLeiXing", qtlx)
                return json.toString()
            }

            override fun parseResult(jdata: JSONObject?) {
                isok = jdata?.getInt("code") == 200
            }

        })
    }

    /**
     * 预置信息的展示、修改（dialog）
     */
    fun xinxiZhanshi(bp: GangPingBean?) {

        var guige: Int? = null
        var qitileixing: Int? = null

        var dialogView = View.inflate(act, R.layout.cz_yuzhi_dialog, null)
        dialogView.findViewById<LinearLayout>(R.id.line_yvzhi_changjia).visibility = GONE
        dialogView.findViewById<LinearLayout>(R.id.line_yvzhi_shengchanriqi).visibility = GONE
        dialogView.findViewById<LinearLayout>(R.id.line_yvzhi_changdu).visibility = GONE
        dialogView.findViewById<LinearLayout>(R.id.line_yvzhi_jianyanriqi).visibility = GONE
        dialogView.findViewById<TextView>(R.id.tv_title).text = "气瓶信息"
        var et_pizhong = dialogView.findViewById<EditText>(R.id.et_pizhong)
        et_pizhong.setText(bp?.jingZhong.toString())
        var et_prefix = dialogView.findViewById<EditText>(R.id.et_prefix)
        et_prefix.setText(bp?.gangPingHao)
        et_prefix.filters = arrayOf(InputFilter.LengthFilter(24))
        var sp_guige = dialogView.findViewById<Spinner>(R.id.sp_guige)//规格
        var et_qiti = dialogView.findViewById<Spinner>(R.id.et_qiti)//气体类型


        //规格
        sp_guige.adapter = ArrayAdapter<IDNameBean>(act, R.layout.gp_id_item, guige_list.toTypedArray())
        sp_guige.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                var bean: IDNameBean = parent?.selectedItem as IDNameBean
                guige = bean.id.toInt()
            }

        }

        for (i in guige_list.indices) {
            val cj = guige_list[i]

            if (cj.id != 0L && bp?.guiGe?.toLong() == cj.id) {
                sp_guige.setSelection(i)
            }
        }

        //气体类型
        et_qiti.adapter = ArrayAdapter<IDNameBean>(act, R.layout.gp_id_item, qiti_list.toTypedArray())
        et_qiti.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                var bean: IDNameBean = parent?.selectedItem as IDNameBean
                qitileixing = bean.id.toInt()
            }

        }

        for (i in qiti_list.indices) {
            val cj = qiti_list[i]
            if (cj.id != 0L && bp?.qiTiLeiXingId?.toLong() == cj.id) {
                et_qiti.setSelection(i)
            }
        }

        dialog = Dialog(act, android.R.style.Theme_DeviceDefault_Light_NoActionBar)
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setContentView(dialogView)
        dialog?.show()
        dialogView.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
            dialog?.dismiss()
        }
        dialogView.findViewById<Button>(R.id.btn_ok).setOnClickListener {
            dialog?.dismiss()
            subData(et_prefix.text.toString(), guige!!, et_pizhong.text.toString(), qitileixing!!)
        }


    }

    fun loadYvZhiInfo(bp: GangPingBean?) {
        act.showProgress()
        BackTask.post(object : BackFrontTask {
            override fun runFront() {
                act.hideProgress()
                xinxiZhanshi(bp)

            }

            override fun runBack() {
                Constant.getLX_GE(qiti_list, guige_list)
            }

        })
    }
}