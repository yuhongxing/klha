package com.czsy.ui.gongyingzhan.yunying.manageSqg

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import com.czsy.CZNetUtils
import com.czsy.Constant
import com.czsy.android.databinding.ItemShangPinBinding
import com.czsy.android.databinding.ZzShenpiItemBinding
import com.czsy.bean.PageInfoBean
import com.czsy.bean.gongyingzhanzz.HuikuDetilsBean
import com.czsy.bean.gongyingzhanzz.ShenPiBean
import com.czsy.bean.gongyingzhanzz.ShenPiBeanItem
import com.czsy.ui.AbsPVBase
import com.czsy.ui.AbsPVPageList
import com.czsy.ui.MainActivity
import com.czsy.ui.PVSimpleText
import com.google.gson.reflect.TypeToken
import mylib.app.BackFrontTask
import mylib.app.BackTask
import mylib.utils.Utils
import org.json.JSONObject
import java.lang.StringBuilder

/**
 * 钢瓶回库列表查看
 */
class GpHuikuList(a: MainActivity, has_date: Boolean) : AbsPVPageList<ShenPiBeanItem>(a, has_date) {

    var shenPiBeanList = ShenPiBean<ShenPiBeanItem>()

    override fun initView() {
        super.initView()
        tv_title.text = "回库列表"
    }

    override fun getView(i: Int, view: View?, viewGroup: ViewGroup?): View {
        val binding = ZzShenpiItemBinding.inflate(LayoutInflater.from(viewGroup?.context), viewGroup, false)

        val bean = adapter.getItem(i)
        binding.tvName.text = "配送员：" + bean.userName
        binding.tvTime.text = "时间：" + bean.operationDate
        binding.tvStatus.visibility = View.GONE
//        binding.tvContent.visibility = View.GONE
        binding.tvShenpiren.visibility = View.GONE
        binding.btnChexiao.visibility = View.GONE
        binding.btnShenpi.visibility = View.GONE

        val contentZ = fun(): String {
            val str: StringBuffer = StringBuffer()
            str.append("重瓶：")
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
                str.append("50kg(液)：" + bean.zhongPing.gangPing50KGYeCount.toString() + "；\n")
            }
            if (bean.zhongPing.gangPing5KGCount == 0 &&
                    bean.zhongPing.gangPing10KGCount == 0 &&
                    bean.zhongPing.gangPing15KGCount == 0 &&
                    bean.zhongPing.gangPing50KGCount == 0 &&
                    bean.zhongPing.gangPing50KGIICount == 0 &&
                    bean.zhongPing.gangPing50KGYeCount == 0
            ) {
                str.append("无\n")
            }
            return str.toString()
        }

        val contentK = fun(): String {
            val str: StringBuffer = StringBuffer()
            str.append("空瓶：")
            if (bean.kongPing.gangPing5KGCount != 0) {
                str.append("5kg：" + bean.kongPing.gangPing5KGCount.toString() + "；")
            }
            if (bean.kongPing.gangPing10KGCount != 0) {
                str.append("10kg：" + bean.kongPing.gangPing10KGCount.toString() + "；")
            }
            if (bean.kongPing.gangPing15KGCount != 0) {
                str.append("15kg：" + bean.kongPing.gangPing15KGCount.toString() + "；")
            }
            if (bean.kongPing.gangPing50KGCount != 0) {
                str.append("50kg：" + bean.kongPing.gangPing50KGCount.toString() + "；")
            }
            if (bean.kongPing.gangPing50KGIICount != 0) {
                str.append("50kg(II)：" + bean.kongPing.gangPing50KGIICount.toString() + "；")
            }
            if (bean.kongPing.gangPing50KGYeCount != 0) {
                str.append("50kg(液)：" + bean.kongPing.gangPing50KGYeCount.toString())
            }
            if (bean.kongPing.gangPing5KGCount == 0 &&
                    bean.kongPing.gangPing10KGCount == 0 &&
                    bean.kongPing.gangPing15KGCount == 0 &&
                    bean.kongPing.gangPing50KGCount == 0 &&
                    bean.kongPing.gangPing50KGIICount == 0 &&
                    bean.kongPing.gangPing50KGYeCount == 0
            ) {
                str.append("无\n")
            }
            return str.toString()
        }



        binding.tvContent.text = "回库气瓶：" + contentZ() + contentK()

        return binding.root
    }

    override fun runBack() {

        var url = "gangPing/chaXunPeiSongYuanLingQvGangPing/10/" + nextPage
        var json = JSONObject()
        json.put("type", 1)//类型 (1:钢瓶回库， 2:钢瓶领取)
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

        getDetils(shenPiBeanList.get(position).id)

    }

    override fun onRefresh() {
        if (shenPiBeanList != null && shenPiBeanList.size != 0) {
            shenPiBeanList.removeAll(shenPiBeanList)
        }
        super.onRefresh()
    }

    fun getDetils(id: String) {

        act.showProgress()
        BackTask.post(object : BackFrontTask {
            val sb = StringBuilder()
            var code = 0
            var msg = ""
            override fun runFront() {
                act.hideProgress()
                if (code == 200) {
                    act.main_pvc.push(PVSimpleText(act, sb.toString()))

                } else {
                    Utils.toastSHORT(msg)
                }
            }

            override fun runBack() {
                val ret = CZNetUtils.postCZHttp("gangPing/chaXunPeiSongYuanChuRuKuGangPingXiangQing", "{\"id\":\"$id\"}")

                code = ret.getInt("code")
                msg = ret.getString("message")

                val datas = ret.optJSONArray("result")
                if (datas != null) {

                    for (i in 0..datas.length() - 1) {
                        val data = datas.getJSONObject(i)
                        val bean = Constant.gson.fromJson(data.toString(), HuikuDetilsBean::class.java)
                        sb.append("气瓶号：" + bean.gangPingHao + "\n")
                        sb.append("芯片号：" + bean.xinPianHao + "\n")
                        sb.append("气体类型：" + bean.qiTiLeiXing + "\n")
                        sb.append("规格：" + bean.guiGeName + "\n")
                        sb.append("状态：" + bean.statusName + "\n")
                        sb.append("供应站：" + bean.gongYingZhan + "\n")
                        sb.append("检修日期：" + bean.xiaCiJianXiuRiQi + "\n")
                        sb.append("充气次数：" + bean.chongQiCiShu + "\n")
                        sb.append("---------------------------\n")
                    }

                }
            }

        })
    }

}