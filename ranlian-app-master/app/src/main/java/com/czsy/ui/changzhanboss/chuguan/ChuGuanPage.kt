package com.czsy.ui.changzhanboss.chuguan

import android.R
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.czsy.CZBackTask
import com.czsy.CZNetUtils
import com.czsy.Constant
import com.czsy.android.databinding.PvczbossChuguanItemBinding
import com.czsy.android.databinding.PvczbossChurukuItemBinding
import com.czsy.bean.PageInfoBean
import com.czsy.bean.changzhanboss.ChuGuanBeanItem
import com.czsy.bean.changzhanboss.ChuGuanBeans
import com.czsy.bean.changzhanboss.ChuRuKuBeanItem
import com.czsy.bean.changzhanboss.ChuRuKuBeans
import com.czsy.ui.AbsPVPageList
import com.czsy.ui.MainActivity
import com.czsy.ui.changzhanboss.churuku.ChuRukuDetailsPage
import com.google.gson.reflect.TypeToken
import mylib.app.BackFrontTask
import mylib.app.BackTask
import mylib.utils.Utils
import org.json.JSONObject

/**
 * 储罐列表
 */

class ChuGuanPage(a: MainActivity, has_date: Boolean) : AbsPVPageList<ChuGuanBeanItem>(a, has_date) {

    var beanList = ChuGuanBeans<ChuGuanBeanItem>()

    override fun initView() {
        super.initView()
        tv_title.text = "储罐"


    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun createMainView(ctx: Context?) {
        super.createMainView(ctx)
        /**
         * 重新list_view.setOnScrollListener，不做任何操作，避免上拉加载请求接口
         */
        list_view.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {

            }

            override fun onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
            }
        })
    }


    override fun getView(i: Int, view: View?, viewGroup: ViewGroup?): View {

        val binding = PvczbossChuguanItemBinding.inflate(LayoutInflater.from(viewGroup?.context), viewGroup, false)
        val bean = adapter.getItem(i)
        binding.tvBianhao.text = bean.bianHao
        binding.tvChangzhan.text = bean.changZhan
        binding.tvQiti.text = "气体类型：${bean.qiTiTypeName}"

        return binding.root
    }

    override fun runBack() {

        var url = "chuGuanLog/chuGuanAllQuery"
        var ret = CZNetUtils.postCZHttp(url, "")

        err_msg = ret.getString("message")
        var code = ret.getInt("code")
        if (code != 200) throw CZNetUtils.CZNetErr(code, ret)

        page_info = PageInfoBean()
        page_info.hasNextPage = false

        val type = object : TypeToken<ChuGuanBeans<ChuGuanBeanItem>>() {}.type
        beanList = Constant.gson.fromJson(ret.getJSONArray("result").toString(), type)

        err_msg = null

    }

    override fun runFrontOk() {
        adapter.data = beanList
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        act.pvc.push(ChuGuanDetailsPage(act, beanList[position]))

    }


}