package com.czsy.ui.changzhanboss.chongzhuanglog

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import com.czsy.CZNetUtils
import com.czsy.Constant
import com.czsy.android.databinding.PvczbossChuguanItemBinding
import com.czsy.bean.changzhanboss.*
import com.czsy.ui.AbsPVList
import com.czsy.ui.MainActivity
import com.google.gson.reflect.TypeToken
import mylib.ui.list.AbstractAdapter
import mylib.utils.Utils

/**
 * 充装日志 -- 储罐列表
 */

class ChongzhuangLogPage(a: MainActivity, has_date: Boolean) : AbsPVList(a, has_date) {

    var beanList = ChuGuanBeans<ChuGuanBeanItem>()

    lateinit var imgAdd: ImageView

    override fun initView() {
        super.initView()
        tv_title.text = "储罐充装日志"

    }

    private var adapter: AbstractAdapter<ChuGuanBeanItem?> = object : AbstractAdapter<ChuGuanBeanItem?>() {
        override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {

            val binding = PvczbossChuguanItemBinding.inflate(LayoutInflater.from(viewGroup?.context), viewGroup, false)
            val bean = getItem(i)
            binding.tvBianhao.text = bean?.bianHao
            binding.tvChangzhan.text = bean?.changZhan
            binding.tvQiti.text = "气体类型：${bean?.qiTiTypeName}"
            return binding.root
        }
    }

    override fun getMainViewRes(): Int {
        return com.czsy.android.R.layout.pvczboss_chongzhuang_log
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun createMainView(ctx: Context?) {
        super.createMainView(ctx)

        list_view.adapter = adapter

        imgAdd = mMainView.findViewById(com.czsy.android.R.id.img_add)
        imgAdd.setOnClickListener {
            act.main_pvc.push(ChongzhuangLogCreateJiLuPage(act, beanList))
        }
    }


    override fun runBack() {

        var url = "chuGuanLog/chuGuanAllQuery"
        var ret = CZNetUtils.postCZHttp(url, "")

        err_msg = ret.getString("message")
        var code = ret.getInt("code")
        if (code != 200) throw CZNetUtils.CZNetErr(code, ret)

        val type = object : TypeToken<ChuGuanBeans<ChuGuanBeanItem>>() {}.type
        beanList = Constant.gson.fromJson(ret.getJSONArray("result").toString(), type)

        err_msg = null

    }

    override fun runFrontOk() {
        adapter.data = beanList as List<ChuGuanBeanItem?>?
    }


    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        act.pvc.push(ChongZhuangLogDetailsPage(act, beanList[position]))

    }

}