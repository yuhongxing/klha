package com.czsy.ui.changzhanboss.chongzhuanglog

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.czsy.CZBackTask
import com.czsy.android.R
import com.czsy.android.databinding.PvczbossChongzhuangLogCreateJiluBinding
import com.czsy.android.databinding.PvczbossChuguanCreateJiluBinding
import com.czsy.bean.ClientBean
import com.czsy.bean.changzhanboss.ChuGuanBeanItem
import com.czsy.bean.changzhanboss.ChuGuanBeans
import com.czsy.ui.AbsPVBase
import com.czsy.ui.MainActivity
import mylib.app.BackTask
import mylib.utils.DateUtils
import mylib.utils.EditTextUtils
import mylib.utils.Utils
import org.json.JSONObject

/**
 * 创建充装日志
 */
class ChongzhuangLogCreateJiLuPage(a: MainActivity, val beans: ChuGuanBeans<ChuGuanBeanItem>) : AbsPVBase(a) {

    private lateinit var binding: PvczbossChongzhuangLogCreateJiluBinding

    var id_chuguan = -1
    var qiti_type = -1

    override fun createMainView(ctx: Context?) {
        binding = PvczbossChongzhuangLogCreateJiluBinding.inflate(LayoutInflater.from(act))
        mMainView = binding.root

        binding.include.tvTitle.text = "创建充装日志"
        binding.tvLogNum.text = DateUtils.nowTime()
        EditTextUtils.afterDotTwo(binding.etZhongliang)

        binding.spinnerBianhao.setAdapter(ArrayAdapter(act, R.layout.gp_id_item, beans))
        binding.spinnerBianhao.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                id_chuguan = beans.get(position).id.toInt()
                qiti_type = beans.get(position).qiTiType

                binding.tvQiti.text = beans.get(position).qiTiTypeName
                binding.tvYvliang.text = "${beans.get(position).dangQianRongLiang} kg"

            }

        }

        binding.include.ivBack.setOnClickListener {
            act.main_pvc.pop()
        }

        binding.btnReset.setOnClickListener {

            binding.spinnerBianhao.setSelection(0)
            binding.etZhongliang.setText("")
            binding.etBeizhu.setText("")

        }
        binding.btnOk.setOnClickListener {
            if (binding.etZhongliang.text.toString().equals("")) {
                Utils.toastSHORT("请输入充装重量")
                return@setOnClickListener
            }

            addJilu()
        }

    }


    /**
     * 创建日志
     */
    fun addJilu() {
        BackTask.post(object : CZBackTask(act) {


            override fun getURL(): String {

                return "chuGuanLog/fillingLogSave"
            }

            override fun runFront2() {
                Utils.toastSHORT("创建成功")
                act.main_pvc.pop()
            }

            override fun getInputParam(): String {

                val json = JSONObject()
                json.put("chengHao", binding.tvLogNum.text.toString())//日志编号 （自动生成)
                json.put("chuGuanId", id_chuguan)//   储罐id
                json.put("bottleGasType", qiti_type)//气瓶气体类型
                json.put("chuGuanGasType", qiti_type)//储罐气体类型
                json.put("endStartWeight", binding.etZhongliang.text.toString())//充装重量
                json.put("remark", binding.etBeizhu.text.toString())//备注

                return json.toString()
            }

            override fun parseResult(jdata: JSONObject?) {

            }

        })
    }


}