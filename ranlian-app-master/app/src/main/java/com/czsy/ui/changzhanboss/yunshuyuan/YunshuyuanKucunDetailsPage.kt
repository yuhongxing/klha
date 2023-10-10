package com.czsy.ui.changzhanboss.yunshuyuan

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.czsy.CZNetUtils
import com.czsy.Constant
import com.czsy.android.R
import com.czsy.android.databinding.PvczbossChurukuDetailsBinding
import com.czsy.android.databinding.PvczbossChurukuDetailsItemBinding
import com.czsy.android.databinding.PvczbossYunshuyuankucunDetailsBinding
import com.czsy.bean.YajinGuanliBean
import com.czsy.bean.changzhanboss.ChuRuKuBeanItem
import com.czsy.bean.changzhanboss.ChuRuKuDetailsBeanItem
import com.czsy.bean.changzhanboss.ChuRuKuDetailsBeans
import com.czsy.bean.changzhanboss.YunshuyuanKucunBeanItem
import com.czsy.ui.AbsPVBase
import com.czsy.ui.MainActivity
import com.czsy.ui.sqg.PVYajinGuanli
import com.google.gson.reflect.TypeToken
import mylib.app.BackFrontTask
import mylib.app.BackTask
import mylib.ui.list.AbstractAdapter
import mylib.utils.Utils
import org.json.JSONObject

/**
 * 运输员库存 详情页
 */
class YunshuyuanKucunDetailsPage(a: MainActivity, val bean: YunshuyuanKucunBeanItem) : AbsPVBase(a), BackFrontTask {

    var beanList = ChuRuKuDetailsBeans<ChuRuKuDetailsBeanItem>()
    private lateinit var binding: PvczbossYunshuyuankucunDetailsBinding

    private var adapter: AbstractAdapter<ChuRuKuDetailsBeanItem?> = object : AbstractAdapter<ChuRuKuDetailsBeanItem?>() {
        override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {

            val binding = PvczbossChurukuDetailsItemBinding.inflate(LayoutInflater.from(viewGroup?.context), viewGroup, false)
            val bean = getItem(i)
            binding.tvGangpingNum.text = "气瓶${i + 1}：${bean?.gangPingHao}"
            binding.tvXinpian.text = "芯片号：${bean?.xinPianHao}"
            binding.tvType.text = "${bean?.yuQiStatusName} / ${bean?.guiGeName}"

            return binding.root
        }
    }


    override fun createMainView(ctx: Context?) {
        binding = PvczbossYunshuyuankucunDetailsBinding.inflate(LayoutInflater.from(act))
        mMainView = binding.root

        binding.include.tvTitle.text = "气瓶详情"
        binding.tvName.text = bean.yunShuYuan
        binding.tvChangzhan.text = "充装站：${bean.changZhan}"
        binding.tvNum.text = bean.sum.sum.toString()
//        binding.tvBeizhu.text = bean.beiZhu
        binding.listView.adapter = adapter

        binding.include.ivBack.setOnClickListener {
            act.main_pvc.pop()
        }

        BackTask.post(this)
        act.showProgress()

    }

    override fun runFront() {

        adapter.data = beanList as List<ChuRuKuDetailsBeanItem?>?
    }

    override fun runBack() {
        val jsonObject = JSONObject()
        jsonObject.put("yunShuYuanId", bean.yunShuYuanId)
        jsonObject.put("zuiHouWeiZhi", 2)
        jsonObject.put("noStatus", 4)
        val ret = CZNetUtils.postCZHttp("gangPing/chaXun/100/1", jsonObject.toString())
        act.hideProgress()
        var code = ret.getInt("code")

        if (code == 200) {
            val type = object : TypeToken<ChuRuKuDetailsBeans<ChuRuKuDetailsBeanItem>>() {}.type
            beanList = Constant.gson.fromJson(ret.getJSONArray("result").toString(), type)
        } else {
            Utils.toastSHORT(ret.getString("message"))
        }
    }

}