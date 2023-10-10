package com.czsy.ui.gongyingzhan.yunying.manageSqg

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import com.czsy.CZNetUtils
import com.czsy.Constant
import com.czsy.android.databinding.ZzShenpiItemBinding
import com.czsy.bean.PageInfoBean
import com.czsy.bean.gongyingzhanzz.KucunItemBean
import com.czsy.bean.gongyingzhanzz.ShenPiBean
import com.czsy.bean.gongyingzhanzz.ShenPiBeanItem
import com.czsy.ui.AbsPVPageList
import com.czsy.ui.MainActivity
import com.google.gson.reflect.TypeToken
import mylib.utils.Utils
import org.json.JSONObject

class KucunList(a: MainActivity, gongYingZhanId: String) : AbsPVPageList<KucunItemBean>(a, false) {
    var gongYingZhanId1: String? = null

    init {
        gongYingZhanId1 = gongYingZhanId
    }

    var kucunBeanList = mutableListOf<KucunItemBean>()

    override fun initView() {
        super.initView()
        tv_title.text = "库存列表"
    }

    override fun getView(i: Int, view: View?, viewGroup: ViewGroup?): View {
        val binding = ZzShenpiItemBinding.inflate(LayoutInflater.from(viewGroup?.context), viewGroup, false)

        val bean = adapter.getItem(i)
        binding.tvName.text = "气瓶号：" + bean.gangPingHao
        binding.tvStatus.text = "状态：" + bean.statusName
        binding.tvContent.text = "类型：" + bean.yuQiStatusName
        binding.tvShenpiren.text = "规格：" + bean.guiGeName
        binding.tvTime.text = "气体类型：" + bean.qiTiLeiXing
        binding.btnShenpi.visibility = View.GONE
        binding.btnChexiao.visibility = View.GONE

        return binding.root
    }

    override fun runBack() {
        var url = "gangPing/chaXun/10/" + nextPage
        var json = JSONObject()
        json.put("noStatus", 4)
        json.put("zuiHouWeiZhi", 3)
        json.put("gongYingZhanId", gongYingZhanId1)
        var ret = CZNetUtils.postCZHttp(url, json.toString())

        err_msg = ret.getString("message")
        var code = ret.getInt("code")
        if (code != 200) return
//            throw CZNetUtils.CZNetErr(code, ret)

        page_info = Constant.gson.fromJson(ret.getJSONObject("pageVO").toString(), PageInfoBean::class.java)
        val rets = ret.getJSONArray("result")

        for (i in 0..rets.length() - 1) {
            val bean = Constant.gson.fromJson(rets.get(i).toString(), KucunItemBean::class.java)

            kucunBeanList.add(bean)
        }

        err_msg = null

        if (kucunBeanList.size == 0) Utils.toastSHORT("没有更多了")

        return
    }

    override fun runFrontOk() {
        if (kucunBeanList != null && kucunBeanList.size != 0) {
            adapter.data = kucunBeanList
        } else {
            adapter.notifyDataSetChanged()
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
    }

    override fun onRefresh() {
        if (kucunBeanList != null && kucunBeanList.size != 0) {
            kucunBeanList.removeAll(kucunBeanList)
        }
        super.onRefresh()
    }
}