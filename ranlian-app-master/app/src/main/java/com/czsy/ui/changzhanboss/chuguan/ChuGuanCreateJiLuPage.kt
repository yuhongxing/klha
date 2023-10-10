package com.czsy.ui.changzhanboss.chuguan

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import com.czsy.CZBackTask
import com.czsy.android.databinding.PvczbossChuguanCreateJiluBinding
import com.czsy.ui.AbsPVBase
import com.czsy.ui.MainActivity
import mylib.app.BackTask
import mylib.utils.EditTextUtils
import mylib.utils.Utils
import org.json.JSONObject

/**
 * 创建储罐出入库记录
 */
class ChuGuanCreateJiLuPage(a: MainActivity, val id_chuguan: Int, val id_changzhan: Int, val bianhao: String) : AbsPVBase(a) {

    private lateinit var binding: PvczbossChuguanCreateJiluBinding
    var type_caozuo = -1
    var type_qiti = -1
    var type_qiti_str = ""

    override fun createMainView(ctx: Context?) {
        binding = PvczbossChuguanCreateJiluBinding.inflate(LayoutInflater.from(act))
        mMainView = binding.root

        binding.include.tvTitle.text = "创建出入库记录"

        binding.tvBianhao.text = bianhao

        EditTextUtils.afterDotTwo(binding.etDanjia)
        EditTextUtils.afterDotTwo(binding.etBangzhong1)
        EditTextUtils.afterDotTwo(binding.etBangzhong2)

        binding.radioGroupTypeCaozuo.setOnCheckedChangeListener { group, checkedId ->

            when (checkedId) {
                binding.rbRuku.id -> type_caozuo = 1
                binding.rbChuku.id -> type_caozuo = 0
            }

        }
        binding.radioGroupTypeQiti.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                binding.rbYhsyq.id -> {
                    type_qiti = 1
                    type_qiti_str = "液化石油气"
                }
                binding.rbBw.id -> {
                    type_qiti = 2
                    type_qiti_str = "丙烷"
                }
            }
        }
        binding.include.ivBack.setOnClickListener {
            act.main_pvc.pop()
        }
        binding.btnReset.setOnClickListener {

            binding.edChepai.setText("")
            binding.etBangzhong1.setText("")
            binding.etBangzhong2.setText("")
            binding.etDanjia.setText("")
            binding.radioGroupTypeCaozuo.clearCheck()
            binding.radioGroupTypeQiti.clearCheck()
            type_caozuo = -1
            type_qiti = -1
            type_qiti_str = ""
        }
        binding.btnOk.setOnClickListener {

            when ("") {
                binding.edChepai.text.toString() -> {
                    Utils.toastSHORT("请输入车牌号")
                    return@setOnClickListener
                }
                type_qiti_str -> {
                    Utils.toastSHORT("请选择气体类型")
                    return@setOnClickListener
                }
                binding.etBangzhong1.text.toString() -> {
                    Utils.toastSHORT("请输入首次磅重")
                    return@setOnClickListener
                }
                binding.etBangzhong2.text.toString() -> {
                    Utils.toastSHORT("请输入二次磅重")
                    return@setOnClickListener
                }
                binding.etDanjia.text.toString() -> {
                    Utils.toastSHORT("请输入单价")
                    return@setOnClickListener
                }
            }
            if (type_caozuo == -1) {
                Utils.toastSHORT("请选择操作类型")
                return@setOnClickListener
            }

            addJilu()
        }

    }


    /**
     * 创建记录
     */
    fun addJilu() {
        BackTask.post(object : CZBackTask(act) {


            override fun getURL(): String {

                return "pda/createCaoCheChuRuKu"
            }

            override fun runFront2() {
                Utils.toastSHORT("创建成功")
                act.main_pvc.pop()
            }

            override fun getInputParam(): String {

                val json = JSONObject()
                json.put("leiXing", type_caozuo)// 0:出库； 1:入库
                json.put("chePai", binding.edChepai.text.toString())//车牌
                json.put("danWeiId", id_changzhan)//充装站id
                json.put("chuGuanId", id_chuguan)//储罐id
                json.put("qiTiLeiXing", type_qiti)//1：液化石油气 / 2：丙烷
                json.put("qiTiMingCheng", type_qiti_str)//液化石油气 / 丙烷
                json.put("firstBangZhong", binding.etBangzhong1.text.toString())//首次磅重
                json.put("secondBangZhong", binding.etBangzhong2.text.toString())//二次磅重
                json.put("danJia", binding.etDanjia.text.toString())//单价

                return json.toString()
            }

            override fun parseResult(jdata: JSONObject?) {

            }

        })
    }


}