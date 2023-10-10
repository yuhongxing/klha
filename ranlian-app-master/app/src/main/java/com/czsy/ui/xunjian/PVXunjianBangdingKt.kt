package com.czsy.ui.xunjian

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.*
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.bigkoo.pickerview.view.OptionsPickerView
import com.czsy.CZBackTask
import com.czsy.CZNetUtils
import com.czsy.INFCHandler
import com.czsy.android.R
import com.czsy.bean.XunjianShebeiLeixingBean
import com.czsy.other.DatePickerDialog
import com.czsy.ui.AbsPVBase
import com.czsy.ui.MainActivity
import com.google.gson.Gson
import mylib.app.BackFrontTask
import mylib.app.BackTask
import mylib.utils.DateUtils
import mylib.utils.Utils
import org.json.JSONObject

/**
 * 巡检设备绑定
 */
class PVXunjianBangdingKt(a: MainActivity) : AbsPVBase(a), INFCHandler, View.OnClickListener {

    private var tvXinpian: TextView? = null
    private var tvYouxiaoTime: TextView? = null
    private var tvShebeiType: TextView? = null
    private var tvBaofeiTime: TextView? = null
    private var etSBName: EditText? = null
    private var etSBGuige: EditText? = null
    private var etSBNum: EditText? = null
    private var etSBChangjia: EditText? = null
    private var etSBWeizhi: EditText? = null
    var isOK: Boolean = false //判断芯片是否可用

    var o1Items: MutableList<String>? = mutableListOf()
    var o2Items: MutableList<MutableList<XunjianShebeiLeixingBean.ResultDTO.ErJiListDTO>>? = mutableListOf()
    var sb_id = "0"

    override fun createMainView(ctx: Context?) {

        mMainView = View.inflate(act, R.layout.pv_xj_bangding, null)

        mMainView.findViewById<TextView>(R.id.tv_title).setText("巡检设备绑定")
        tvXinpian = mMainView.findViewById(R.id.tv_xinpian)
        tvShebeiType = mMainView.findViewById(R.id.tv_shebeiType)
        tvYouxiaoTime = mMainView.findViewById(R.id.tv_youxiaoTime)
        tvBaofeiTime = mMainView.findViewById(R.id.tv_baofeiTime)
        etSBName = mMainView.findViewById(R.id.et_name)
        etSBGuige = mMainView.findViewById(R.id.et_guige)
        etSBNum = mMainView.findViewById(R.id.et_num)
        etSBChangjia = mMainView.findViewById(R.id.et_changjia)
        etSBWeizhi = mMainView.findViewById(R.id.et_weizhi)

        tvShebeiType?.setOnClickListener(this)
        tvYouxiaoTime?.setOnClickListener(this)
        tvBaofeiTime?.setOnClickListener(this)
        mMainView.findViewById<ImageView>(R.id.iv_back).setOnClickListener(this)
        mMainView.findViewById<Button>(R.id.btn_sub).setOnClickListener(this)


    }

    override fun onAttach(firstShow: Boolean) {
        super.onAttach(firstShow)
        seleteType()
    }

    override fun onNFCIntent(i: INFCHandler.NFCInfo?) {
        tvXinpian?.setText(i?.chip_sn)
        chaXunXinPianStatus(i?.chip_sn)
    }

    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.iv_back ->
                act.pvc.pop()
            R.id.tv_youxiaoTime ->
                selectTime(tvYouxiaoTime)
            R.id.tv_baofeiTime ->
                selectTime(tvBaofeiTime)
            R.id.btn_sub -> {
//                val aa =  DateUtils.shijian1(tvYouxiaoTime?.text.toString(), tvBaofeiTime?.text.toString())
//               Log.d("xing","aa--${aa}")
                if (isOK) submitData() else Utils.toastSHORT("请重新选择芯片")
            }
            R.id.tv_shebeiType -> selectType()
        }
    }

    /**
     * 选择时间
     */
    fun selectTime(tv: TextView?) {
        val dpd = DatePickerDialog(act)
        dpd.setOnDateSetListener(object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                tv?.setText("$year-${month + 1}-$dayOfMonth")
            }

        })
        dpd.show()
    }

    /**
     * 选择类型
     */
    fun selectType() {

        val pv: OptionsPickerView<Any> = object : OptionsPickerBuilder(act, OnOptionsSelectListener { options1, options2, options3, v ->

            var o2size = o2Items?.get(options1)?.size

            Log.d("xing", "o2Items:" + o2Items?.size + "\n options1:" + options1 + "\n" + "options2:" + options2 + "\n o2size:" + o2size)
            if (o2size!! > 0) sb_id = o2Items?.get(options1)?.get(options2)?.getItemId()!! else sb_id = "-1"
            tvShebeiType?.text =
                    if (o2size > 0)
                        o1Items?.get(options1) + "--" + o2Items?.get(options1)?.get(options2)?.getMingcheng()
                    else o1Items?.get(options1)
        }) {
        }.setTitleText("设备类型").build()

        pv.setPicker(o1Items as List<Any>?, o2Items as List<MutableList<Any>>?)

        pv.show()

    }

    /**
     * 绑定芯片
     */
    fun submitData() {

        if (!checkNull()) {
            return
        }
        act.showProgress()
        BackTask.post(object : CZBackTask(act) {
            override fun getURL(): String {
                return "xunJianSheBei/bangDingXinPian"
            }

            override fun runFront2() {
                act.pvc.pop()
                Toast.makeText(act, "绑定成功", Toast.LENGTH_SHORT).show()
            }

            override fun getInputParam(): String {
                val jsonObject: JSONObject = JSONObject()
                jsonObject?.put("mingCheng", etSBName?.text)
                jsonObject?.put("guiGe", etSBGuige?.text)
                jsonObject?.put("sheBeiBianHao", etSBNum?.text)
                jsonObject?.put("sheiBeiChangJia", etSBChangjia?.text)
                jsonObject?.put("xinPianHao", tvXinpian?.text)
                jsonObject?.put("sheBeiWeiZhi", etSBWeizhi?.text)
                jsonObject?.put("youXiaoRiQi", tvYouxiaoTime?.text)
                jsonObject?.put("baoFeiQiRi", tvBaofeiTime?.text)
                jsonObject?.put("leiXing", sb_id)//设备类型（传二级类型id）
                Log.d("xing", jsonObject.toString())

                return jsonObject.toString()
            }

            override fun parseResult(jdata: JSONObject?) {}

        })

    }

    /**
     * 查询芯片状态
     */
    fun chaXunXinPianStatus(str: String?) {

        act.showProgress()
        BackTask.post(object : CZBackTask(act) {
            override fun getURL(): String {
                return "xunJianSheBei/chaXunSheBeiLeiXing"
//                return "http://192.168.3.103:10000/psi/services/xunJianSheBei/chaXunSheBeiLeiXing"
            }

            override fun runFront2() {
            }

            override fun getInputParam(): String {
                val json = JSONObject()
                json.put("xinPianHao", str)
                return json.toString()
            }

            override fun parseResult(jdata: JSONObject?) {
                if (jdata?.getInt("code") == 200) {
                    isOK = true
                }
            }

        })

    }

    /**
     * 查询设备类型
     */
    fun seleteType() {

        BackTask.post(object : BackFrontTask {
            var json = JSONObject()
            override fun runFront() {

                if (json.getInt("code") == 200) {
                    val bean = Gson().fromJson(json.toString(), XunjianShebeiLeixingBean::class.java)

                    for (b in bean.result) {
                        o1Items?.add(b.mingcheng)
                        var options2Items = mutableListOf<XunjianShebeiLeixingBean.ResultDTO.ErJiListDTO>()
                        for (dto in b.erJiList) {
                            options2Items?.add(dto)
                        }
                        o2Items?.add(options2Items)
                    }
                }
            }

            override fun runBack() {
                json = CZNetUtils.postCZHttp("xunJianLeiXing/chaXunSheBeiLeiXingAll", null)
            }

        })
    }

    fun checkNull(): Boolean {
        if (etSBName?.text?.isEmpty()!!) {
            Utils.toastSHORT("请输入设备名称")
            return false
        } else if (etSBGuige?.text?.isEmpty()!!) {
            Utils.toastSHORT("请输入设备规格")
            return false
        } else if (etSBNum?.text?.isEmpty()!!) {
            Utils.toastSHORT("请输入设备编号")
            return false
        } else if (sb_id.equals("0")) {
            Utils.toastSHORT("请选择设备类型")
            return false
        } else if (etSBWeizhi?.text?.isEmpty()!!) {
            Utils.toastSHORT("请输入设备位置")
            return false
        } else if (etSBChangjia?.text?.isEmpty()!!) {
            Utils.toastSHORT("请输入设备厂家")
            return false
        } else if (tvYouxiaoTime?.text?.isEmpty()!!) {
            Utils.toastSHORT("请选择有效日期")
            return false
        } else if (tvBaofeiTime?.text?.isEmpty()!!) {
            Utils.toastSHORT("请选择报废日期")
            return false
        } else if (!DateUtils.shijian1(tvYouxiaoTime?.text.toString(), tvBaofeiTime?.text.toString())) {
//            Utils.toastSHORT("报废日期必须大于等于有效日期")
            Toast.makeText(act, "报废日期必须大于等于有效日期", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

}

