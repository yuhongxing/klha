package com.czsy.ui.gongyingzhan.yunying.manageSqg

import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.TextView
import com.czsy.CZNetUtils
import com.czsy.Constant
import com.czsy.android.R
import com.czsy.bean.PageInfoBean
import com.czsy.bean.gongyingzhanzz.ShenPiBean
import com.czsy.bean.gongyingzhanzz.ShenPiBeanItem
import com.czsy.ui.AbsPVPageList
import com.czsy.ui.MainActivity
import com.google.gson.reflect.TypeToken
import mylib.app.BackFrontTask
import mylib.app.BackTask
import mylib.utils.Utils
import org.json.JSONObject

class LingqvShenpiList(a: MainActivity, has_date: Boolean) : AbsPVPageList<ShenPiBeanItem>(a, has_date) {

    var shenPiBeanList = ShenPiBean<ShenPiBeanItem>()

    override fun initView() {
        super.initView()
        tv_title.text = "审批列表"
    }

    override fun getView(i: Int, view: View?, viewGroup: ViewGroup?): View? {
        var view1 = view
        if (view1 == null) {
            view1 = View.inflate(act, R.layout.zz_shenpi_item, null)
        }
        var tv_content: TextView? = view1?.findViewById(R.id.tv_content)
        var tv_name: TextView? = view1?.findViewById(R.id.tv_name)
        var tv_status: TextView? = view1?.findViewById(R.id.tv_status)
        var tv_shenpiren: TextView? = view1?.findViewById(R.id.tv_shenpiren)
        var tv_time: TextView? = view1?.findViewById(R.id.tv_time)
        var btn_chexiao: Button? = view1?.findViewById(R.id.btn_chexiao)
        var btn_shenpi: Button? = view1?.findViewById(R.id.btn_shenpi)

        val bean = adapter.getItem(i)
        tv_name?.text = "配送员：" + bean.userName
        val content = fun(): String {
            val str: StringBuffer = StringBuffer()
            if (bean.zhongPing.gangPing5KGCount != 0) {
                str.append("5kg：" + bean.zhongPing.gangPing5KGCount.toString() + "；")
            }
            if (bean.zhongPing.gangPing10KGCount != 0) {
                str.append("10kg：" + bean.zhongPing.gangPing10KGCount.toString() + "；")
            }
            if (bean.zhongPing.gangPing15KGCount != 0) {
                str.append("15kg：" + bean.zhongPing.gangPing15KGCount.toString() + "；")
            }
            if (bean.zhongPing.gangPing50KGCount != 0) {
                str.append("50kg：" + bean.zhongPing.gangPing50KGCount.toString() + "；")
            }
            if (bean.zhongPing.gangPing50KGIICount != 0) {
                str.append("50kg(II)：" + bean.zhongPing.gangPing50KGIICount.toString() + "；")
            }
            if (bean.zhongPing.gangPing50KGYeCount != 0) {
                str.append("50kg(液)：" + bean.zhongPing.gangPing50KGYeCount.toString() + "；")
            }
            return str.toString()
        }
        tv_content?.text = "领取气瓶：" + content()


        if (bean.shenPiStatusName == null || bean.shenPiStatus == 2) {
            //未审批或撤销的
            tv_shenpiren?.visibility = View.GONE
            btn_chexiao?.visibility = View.VISIBLE
            btn_shenpi?.visibility = View.VISIBLE
        } else {
            //已审批或撤销的
            tv_shenpiren?.visibility = View.VISIBLE
            btn_chexiao?.visibility = View.GONE
            btn_shenpi?.visibility = View.GONE
        }

        tv_status?.text = "状态：" + bean.shenPiStatusName

        tv_shenpiren?.text = "审批人：" + bean.shenPiRen
        tv_time?.text = "时间：" + bean.operationDate

        btn_shenpi?.setOnClickListener {
            shenpi(bean.id)
        }
        btn_chexiao?.setOnClickListener {
            chexiao(bean.id)
        }

        return view1
    }

    override fun runBack() {

        var url = "gangPing/chaXunPeiSongYuanLingQvGangPing/10/" + nextPage
        var json = JSONObject()
        json.put("type", 2)//类型 (1:钢瓶回库， 2:钢瓶领取)
        var ret = CZNetUtils.postCZHttp(url, json.toString())

        err_msg = ret.getString("message")
        var code = ret.getInt("code")
        if (code != 200) throw CZNetUtils.CZNetErr(code, ret)

        page_info = Constant.gson.fromJson(ret.getJSONObject("pageVO").toString(), PageInfoBean::class.java)

        val type = object : TypeToken<ShenPiBean<ShenPiBeanItem>>() {}.type
        shenPiBeanList.addAll(Constant.gson.fromJson(ret.getJSONArray("result").toString(), type))

        err_msg = null

        if (shenPiBeanList.size == 0) Utils.toastSHORT("没有更多了")

        return

    }

    override fun runFrontOk() {
        if (shenPiBeanList != null && shenPiBeanList.size != 0) {
            adapter.data = shenPiBeanList
        } else {
            adapter.notifyDataSetChanged()
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

    }

    override fun onRefresh() {
        if (shenPiBeanList != null && shenPiBeanList.size != 0) {
            shenPiBeanList.removeAll(shenPiBeanList)
        }
        super.onRefresh()
    }


    fun shenpi(id: String) {

        BackTask.post(object : BackFrontTask {
            override fun runFront() {

                onRefresh()

            }

            override fun runBack() {

                var ret = CZNetUtils.postCZHttp("peiSongYuan/shenPiPeiSongYuanYunShuDan", "{\"id\":${id}}")
                if (ret.getInt("code") == 200) {
                    Utils.toastSHORT("已审批")
                } else {
                    Utils.toastSHORT(ret.getString("message"))
                }
            }


        })
    }

    fun chexiao(id: String) {
        BackTask.post(object : BackFrontTask {
            override fun runFront() {
                onRefresh()
            }

            override fun runBack() {

                var ret = CZNetUtils.postCZHttp("peiSongYuan/cancel", "{\"id\":${id}}")
                if (ret.getInt("code") == 200) {
                    Utils.toastSHORT("已撤销")
                } else {
                    Utils.toastSHORT(ret.getString("message"))
                }
            }

        })
    }

}