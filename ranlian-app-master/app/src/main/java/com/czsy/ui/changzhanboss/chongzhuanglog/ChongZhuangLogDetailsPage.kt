package com.czsy.ui.changzhanboss.chongzhuanglog

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import android.widget.ListView
import com.czsy.CZNetUtils
import com.czsy.Constant
import com.czsy.android.databinding.*
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
 * 充装记录详情
 */
class ChongZhuangLogDetailsPage(a: MainActivity, val bean: ChuGuanBeanItem) : AbsPVBase(a), BackFrontTask {

    var beanList = ChongzhuangLogBeans<ChongzhuangLogBeanItem>()
    private lateinit var binding: PvczbossChongzhuangLogDetailsBinding

    private var adapter: AbstractAdapter<ChongzhuangLogBeanItem?> = object : AbstractAdapter<ChongzhuangLogBeanItem?>() {
        override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {

            val binding = PvczbossChongzhuangLogJiluItemBinding.inflate(LayoutInflater.from(viewGroup?.context), viewGroup, false)
            val bean = getItem(i)
            binding.tvTime.text = bean?.chuangJianRiQi
            binding.tvName.text = "充  装  人 : ${bean?.user}"
            binding.tvZhongliang.text = "充装重量 : ${bean?.endStartWeight} kg"
            binding.tvKucun.text = "当前库存 : ${bean?.useWeight} kg"

            return binding.root
        }
    }


    override fun createMainView(ctx: Context?) {
        binding = PvczbossChongzhuangLogDetailsBinding.inflate(LayoutInflater.from(act))
        mMainView = binding.root

        binding.include.tvTitle.text = "充装记录"
        binding.tvBianhao.text = bean.bianHao
        binding.tvChangzhan.text = bean.changZhan
        binding.tvQiti.text = "气体类型：${bean.qiTiTypeName}"


        binding.listView.adapter = adapter
        binding.include.ivBack.setOnClickListener {
            act.main_pvc.pop()
        }

    }

    override fun runFront() {

        adapter.data = beanList as List<ChongzhuangLogBeanItem?>?
    }

    override fun runBack() {
        val jsonObject = JSONObject()
        jsonObject.put("chuGuanId", bean.id)
        val ret = CZNetUtils.postCZHttp("pda/fillingLogQuery", jsonObject.toString())
        var code = ret.getInt("code")

        if (code == 200) {
            val type = object : TypeToken<ChongzhuangLogBeans<ChongzhuangLogBeanItem>>() {}.type
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