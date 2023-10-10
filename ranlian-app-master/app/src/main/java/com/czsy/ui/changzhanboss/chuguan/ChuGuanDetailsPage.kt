package com.czsy.ui.changzhanboss.chuguan

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import android.widget.ListView
import com.czsy.CZNetUtils
import com.czsy.Constant
import com.czsy.android.databinding.PvczbossChuguanDetailsBinding
import com.czsy.android.databinding.PvczbossChuguanItemBinding
import com.czsy.android.databinding.PvczbossChuguanJiluItemBinding
import com.czsy.android.databinding.PvczbossChurukuDetailsItemBinding
import com.czsy.bean.changzhanboss.*
import com.czsy.ui.AbsPVBase
import com.czsy.ui.MainActivity
import com.google.gson.reflect.TypeToken
import mylib.app.BackFrontTask
import mylib.app.BackTask
import mylib.ui.list.AbstractAdapter
import mylib.utils.Utils
import org.json.JSONObject

/**
 * 储罐气体出入库详情
 */
class ChuGuanDetailsPage(a: MainActivity, val bean: ChuGuanBeanItem) : AbsPVBase(a), BackFrontTask {

    var beanList = ChuguanQitiChurukuBeans<ChuguanQitiChurukuBeanItem>()
    private lateinit var binding: PvczbossChuguanDetailsBinding

    private var adapter: AbstractAdapter<ChuguanQitiChurukuBeanItem?> = object : AbstractAdapter<ChuguanQitiChurukuBeanItem?>() {
        override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {

            val binding = PvczbossChuguanJiluItemBinding.inflate(LayoutInflater.from(viewGroup?.context), viewGroup, false)
            val bean = getItem(i)
            binding.tvChepai.text = bean?.chePai
            binding.tvTime.text = bean?.chuangJianRiQi
            binding.tvName.text = "操  作  人 : ${bean?.caoZuoRen}"
            binding.tvJiage.text = "实际价格 : ${bean?.shiJiJiaGe.toString()} 元"
            binding.tvZhongliang.text = "实际重量 : ${bean?.shiJiZhongLiang.toString()} kg"
            binding.tvType.text = bean?.leiXingName

            when (bean?.leiXing) {
                0 -> binding.tvType.setTextColor(Color.parseColor("#238CFC"))
                1 -> binding.tvType.setTextColor(Color.parseColor("#13CE9B"))
            }

            return binding.root
        }
    }


    override fun createMainView(ctx: Context?) {
        binding = PvczbossChuguanDetailsBinding.inflate(LayoutInflater.from(act))
        mMainView = binding.root

        binding.include.tvTitle.text = "储罐详情"
        binding.tvBianhao.text = bean.bianHao
        binding.tvChangzhan.text = bean.changZhan
        binding.tvQiti.text = "气体类型：${bean.qiTiTypeName}"
        binding.tvRongji.text = "${bean.chuGuanChangDu} m³"
        binding.tvRongliang.text = "${bean.zuiDaRongLiang} kg"
        binding.tvYvliang.text = "${bean.dangQianRongLiang} kg"
        binding.tvWendu.text = "${bean.dangQianWenDu} ℃"
        binding.tvYali.text = "${bean.dangQianYaLi} 千帕"
        binding.tvYewei.text = "${bean.dangQianYeWei}"
        binding.tvType.text = bean.statusName


        binding.listView.adapter = adapter
        binding.include.ivBack.setOnClickListener {
            act.main_pvc.pop()
        }
        binding.btnAdd.setOnClickListener {
            act.main_pvc.push(ChuGuanCreateJiLuPage(act, bean.id.toInt(), bean.changZhanId.toInt(), bean.bianHao))
        }

//        BackTask.post(this)

    }

    override fun runFront() {

        adapter.data = beanList as List<ChuguanQitiChurukuBeanItem?>?
    }

    override fun runBack() {
        val jsonObject = JSONObject()
        jsonObject.put("id", bean.id)
        val ret = CZNetUtils.postCZHttp("pda/chuGuanChuRuKuJiLu", jsonObject.toString())
        var code = ret.getInt("code")

        if (code == 200) {
            val type = object : TypeToken<ChuguanQitiChurukuBeans<ChuguanQitiChurukuBeanItem>>() {}.type
            beanList = Constant.gson.fromJson(ret.getJSONArray("result").toString(), type)
        } else {
            Utils.toastSHORT(ret.getString("message"))
        }
    }

    override fun onAttach(firstShow: Boolean) {
        super.onAttach(firstShow)
        BackTask.post(this)
    }

}