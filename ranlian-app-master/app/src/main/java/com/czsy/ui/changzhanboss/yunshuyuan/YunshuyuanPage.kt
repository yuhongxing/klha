package com.czsy.ui.changzhanboss.yunshuyuan

import android.content.Context
import android.os.Build
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.AdapterView
import androidx.annotation.RequiresApi
import com.czsy.CZNetUtils
import com.czsy.Constant
import com.czsy.TheApp
import com.czsy.android.R
import com.czsy.android.databinding.PvczbossYunshuyuanItemBinding
import com.czsy.bean.PageInfoBean
import com.czsy.bean.changzhanboss.YunshuyuanKucunBeanItem
import com.czsy.bean.changzhanboss.YunshuyuanKucunBeans
import com.czsy.ui.AbsPVPageList
import com.czsy.ui.MainActivity
import com.google.gson.reflect.TypeToken
import java.io.IOException

/**
 * 运输员--列表
 */

class YunshuyuanPage(a: MainActivity, has_date: Boolean) : AbsPVPageList<YunshuyuanKucunBeanItem>(a, has_date) {

    var beanList = YunshuyuanKucunBeans<YunshuyuanKucunBeanItem>()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun createMainView(ctx: Context?) {
        super.createMainView(ctx)

        tv_title.text = "运输员库存"

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

        val binding = PvczbossYunshuyuanItemBinding.inflate(LayoutInflater.from(viewGroup?.context), viewGroup, false)
        val bean = adapter.getItem(i)
        binding.tvName.text = bean.yunShuYuan
        binding.tvNum.text = bean.sum.sum.toString()
        binding.tvChangzhan.text = "充装站：${bean.changZhan}"

        return binding.root
    }

    override fun runBack() {

        try {
            var url = "kuCun/chaKanYunShuYuanKuCun/100/1"
            var ret = CZNetUtils.postCZHttp(url, "{}")

            err_msg = ret.getString("message")
            var code = ret.getInt("code")

            if (code != 200) throw CZNetUtils.CZNetErr(code, ret)



            page_info = PageInfoBean()
            page_info.hasNextPage = false

            val type = object : TypeToken<YunshuyuanKucunBeans<YunshuyuanKucunBeanItem>>() {}.type
            beanList = Constant.gson.fromJson(ret.getJSONArray("result").toString(), type)

            err_msg = null
        } catch (e: Exception) {
            if (err_msg == null && e is IOException) {
                err_msg = TheApp.sInst.getString(R.string.tip_network_error)
            }
            if (TextUtils.isEmpty(err_msg)) {
                err_msg = TheApp.sInst.getString(R.string.tip_common_err)
            }
        }
    }

    override fun runFrontOk() {
        adapter.data = beanList
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        act.pvc.push(YunshuyuanKucunDetailsPage(act, beanList[position]))

    }

}