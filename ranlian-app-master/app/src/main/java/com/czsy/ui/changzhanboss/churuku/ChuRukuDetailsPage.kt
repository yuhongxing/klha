package com.czsy.ui.changzhanboss.churuku

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
import com.czsy.bean.YajinGuanliBean
import com.czsy.bean.changzhanboss.ChuRuKuBeanItem
import com.czsy.bean.changzhanboss.ChuRuKuDetailsBeanItem
import com.czsy.bean.changzhanboss.ChuRuKuDetailsBeans
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
 * 出入库详情页
 */
class ChuRukuDetailsPage(a: MainActivity, val bean: ChuRuKuBeanItem) : AbsPVBase(a), BackFrontTask {

    var beanList = ChuRuKuDetailsBeans<ChuRuKuDetailsBeanItem>()
    private lateinit var binding: PvczbossChurukuDetailsBinding

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
        binding = PvczbossChurukuDetailsBinding.inflate(LayoutInflater.from(act))
        mMainView = binding.root

        binding.include.tvTitle.text = "气瓶详情"
        binding.tvName.text = bean.yunShuYuanName
        binding.tvChepai.text = "车牌号：${bean.chePai}"
        binding.tvNum.text = bean.gangPingCount.toString()
        binding.tvType.text = bean.statusName
        binding.tvTime.text = bean.chuangJianRiQi
//        binding.tvBeizhu.text = bean.beiZhu
        binding.listView.adapter = adapter

        binding.include.ivBack.setOnClickListener {
            act.main_pvc.pop()
        }

        binding.btnOk.setOnClickListener {
            showDialog(1, "通过", bean.id.toInt(), "gangPingLiuZhuan/processingYunShuYuanLiuZhuan")
        }

        binding.btnPass.setOnClickListener {
            showDialog(3, "驳回", bean.id.toInt(), "gangPingLiuZhuan/cancelYunShuDan")
        }

        if (bean.status == 1 || bean.status == 3) {
            binding.layoutBottom.visibility = View.GONE
            binding.layoutBeizhu.visibility = View.VISIBLE
        }
        if (bean.status == 1) {
            binding.tvBeizhu.text = bean.beiZhu
            binding.tvType.setTextColor(Color.parseColor("#238CFC"))
        } else if (bean.status == 3) {
            binding.tvBeizhu.text = bean.yuanYin
            binding.tvType.setTextColor(Color.parseColor("#FF7723"))
        } else if (bean.status == 2) {
            binding.tvType.setTextColor(Color.parseColor("#13CE9B"))
        }


        BackTask.post(this)

    }

    override fun runFront() {

        adapter.data = beanList as List<ChuRuKuDetailsBeanItem?>?
    }

    override fun runBack() {
        val jsonObject = JSONObject()
        jsonObject.put("id", bean.id)
        val ret = CZNetUtils.postCZHttp("gangPing/chaXunYunShuDanGangPingDetails", jsonObject.toString())
        var code = ret.getInt("code")

        if (code == 200) {
            val type = object : TypeToken<ChuRuKuDetailsBeans<ChuRuKuDetailsBeanItem>>() {}.type
            beanList = Constant.gson.fromJson(ret.getJSONArray("result").toString(), type)
        } else {
            Utils.toastSHORT(ret.getString("message"))
        }
    }


    val d = Dialog(act, android.R.style.Theme_Translucent_NoTitleBar)

    /**
     * 显示提交 输入框  resources transparent_dialog
     */
    fun showDialog(type: Int, title: String, id: Int, url: String) {

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
            netWorkOK(id, et_input.text.toString(), url, type)

        })
        d.findViewById<View>(com.czsy.android.R.id.btn_cancel).setOnClickListener { d.dismiss() }
        d.show()
    }

    /**
     * 审批
     */
    fun netWorkOK(id: Int, daFu: String, url: String, type: Int) {

        BackTask.post(object : BackFrontTask {
            var json: JSONObject? = null
            override fun runFront() {
                act.hideProgress()
                if (json?.getInt("code") == 200) {
                    d.dismiss()
                    Utils.toastSHORT("提交成功")
                    BackTask.post(this)



                    if (type == 1) {
                        binding.tvType.text = "已审批"
                        binding.tvType.setTextColor(Color.parseColor("#238CFC"))
                    } else if (type == 3) {
                        binding.tvType.text = "已驳回"
                        binding.tvType.setTextColor(Color.parseColor("#FF7723"))
                    }

                    binding.layoutBottom.visibility = View.GONE
                    binding.tvBeizhu.text = daFu
                    binding.layoutBeizhu.visibility = View.VISIBLE

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

}