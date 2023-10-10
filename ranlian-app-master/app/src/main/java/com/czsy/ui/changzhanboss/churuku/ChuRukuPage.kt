package com.czsy.ui.changzhanboss.churuku

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.czsy.CZNetUtils
import com.czsy.Constant
import com.czsy.android.databinding.PvczbossChurukuItemBinding
import com.czsy.bean.PageInfoBean
import com.czsy.bean.changzhanboss.ChuRuKuBeanItem
import com.czsy.bean.changzhanboss.ChuRuKuBeans
import com.czsy.ui.AbsPVPageList
import com.czsy.ui.MainActivity
import com.google.gson.reflect.TypeToken
import mylib.app.BackFrontTask
import mylib.app.BackTask
import mylib.utils.Utils
import org.json.JSONObject

/**
 * 气瓶出库
 */

class ChuRukuPage(a: MainActivity, val title: String, val type: Int, has_date: Boolean) : AbsPVPageList<ChuRuKuBeanItem>(a, has_date) {

//    constructor():this(a: MainActivity, title: String, type: Int, has_date: Boolean)

    var beanList = ChuRuKuBeans<ChuRuKuBeanItem>()

    override fun initView() {
        super.initView()
        tv_title.text = title


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

        val binding = PvczbossChurukuItemBinding.inflate(LayoutInflater.from(viewGroup?.context), viewGroup, false)
        val bean = adapter.getItem(i)
        binding.tvName.text = bean.yunShuYuanName
        binding.tvChepai.text = "车牌号：${bean.chePai}"
        binding.tvNum.text = bean.gangPingCount.toString()
        binding.tvType.text = bean.statusName
        binding.tvTime.text = bean.chuangJianRiQi

        if (bean.status == 2) {
            binding.layoutBottom.visibility = View.VISIBLE
            binding.tvType.setTextColor(Color.parseColor("#13CE9B"))
        } else if (bean.status == 1) binding.tvType.setTextColor(Color.parseColor("#238CFC"))
        else if (bean.status == 3) binding.tvType.setTextColor(Color.parseColor("#FF7723"))

        binding.btnOk.setOnClickListener {
            showDialog("通过", bean.id.toInt(), "gangPingLiuZhuan/processingYunShuYuanLiuZhuan")
        }
        binding.btnPass.setOnClickListener {
            showDialog("驳回", bean.id.toInt(), "gangPingLiuZhuan/cancelYunShuDan")
        }

        return binding.root
    }

    override fun runBack() {

        var url = "pda/yunShuDanChaXun"
        var json = JSONObject()
        json.put("type", type)//类型 (3:出库， 4:入库 )
        var ret = CZNetUtils.postCZHttp(url, json.toString())

        err_msg = ret.getString("message")
        var code = ret.getInt("code")
        if (code != 200) throw CZNetUtils.CZNetErr(code, ret)

        page_info = PageInfoBean()
        page_info.hasNextPage = false

        val type = object : TypeToken<ChuRuKuBeans<ChuRuKuBeanItem>>() {}.type
        beanList = Constant.gson.fromJson(ret.getJSONArray("result").toString(), type)

        err_msg = null

    }

    override fun runFrontOk() {
        adapter.data = beanList
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        act.pvc.push(ChuRukuDetailsPage(act, beanList[position]))

    }

    val d = Dialog(act, android.R.style.Theme_Translucent_NoTitleBar)

    /**
     * 显示提交 输入框  resources transparent_dialog
     */
    fun showDialog(title: String, id: Int, url: String) {

        d.setContentView(com.czsy.android.R.layout.dialog_input_czboss)
        d.findViewById<TextView>(com.czsy.android.R.id.tv_title).text = title + "申请"
        d.findViewById<TextView>(com.czsy.android.R.id.tv_title1).text = title + "备注："

        val et_input = d.findViewById<EditText>(com.czsy.android.R.id.et_input)
        d.findViewById<View>(com.czsy.android.R.id.btn_ok).setOnClickListener(View.OnClickListener {
            val reason = et_input.text.toString()
            if (TextUtils.isEmpty(reason)) {
                Utils.toastLONG("请填写备注信息")
                return@OnClickListener
            }
            act.showProgress()
            netWorkOK(id, et_input.text.toString(), url)

        })
        d.findViewById<View>(com.czsy.android.R.id.btn_cancel).setOnClickListener { d.dismiss() }
        d.show()
    }

    /**
     * 审批
     */
    fun netWorkOK(id: Int, daFu: String, url: String) {

        BackTask.post(object : BackFrontTask {
            var json: JSONObject? = null
            override fun runFront() {
                act.hideProgress()
                if (json?.getInt("code") == 200) {
                    d.dismiss()
                    Utils.toastSHORT("提交成功")
                    loadData()
                }

            }

            override fun runBack() {
                val jsonObject = JSONObject()
                jsonObject.put("id", id)
                jsonObject.put("daFu", daFu)
                json = CZNetUtils.postCZHttp(url, jsonObject.toString())

            }

        })

    }

    override fun onAttach(firstShow: Boolean) {
        loadData()
        super.onAttach(firstShow)
    }

}