package com.czsy.ui.changzhan

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import com.czsy.CZBackTask
import com.czsy.Constant
import com.czsy.INFCHandler
import com.czsy.android.R
import com.czsy.bean.GangPingBean
import com.czsy.bean.LoginUser
import com.czsy.ui.AbsPVBase
import com.czsy.ui.MainActivity
import mylib.app.BackTask
import mylib.ui.list.AbstractAdapter
import mylib.utils.CZUtlis
import mylib.utils.Utils
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

/**
 * 批量操作，kt代码
 */
abstract class AbsPVCZPiLiangKt constructor(a: MainActivity?, _isNext: Boolean) : AbsPVBase(a), INFCHandler, View.OnClickListener {

    private val TAG = "AbsPVCZPiLiangK"
    protected var lu: LoginUser? = null
    private val isNext: Boolean

    /**
     * isNext 判断场站id，true:判断；false：不判断（调入不用判断）
     */
    init {
        isNext = _isNext
        if (lu == null) {
            lu = LoginUser.get()
        }
    }

    protected abstract fun getMainViewRes(): Int
    protected abstract fun subData(list: List<Long>)

    protected var tv_xinpian: TextView? = null
    protected var tv_fields1: TextView? = null
    protected var listView: ListView? = null
    protected var et_input: EditText? = null
    protected var tv_title: TextView? = null
    protected var btn_sub: Button? = null

    protected var alertDialog: AlertDialog? = null

    protected var xinPianHao = ""
    protected var lists = arrayListOf<GangPingBean>()
    protected var ids = arrayListOf<Long>()

    override fun createMainView(ctx: Context?) {
        mMainView = View.inflate(ctx, getMainViewRes(), null)
        tv_fields1 = mMainView.findViewById(R.id.tv_fields1)
        tv_xinpian = mMainView.findViewById(R.id.tv_xinpian)
        et_input = mMainView.findViewById(R.id.et_input)
        listView = mMainView.findViewById(R.id.list_view)
        tv_title = mMainView.findViewById(R.id.tv_title)
        btn_sub = mMainView.findViewById(R.id.btn_sub)

        btn_sub?.setOnClickListener(this)
        mMainView.findViewById<View>(R.id.iv_back).setOnClickListener(this)
        mMainView.findViewById<View>(R.id.btn_search).setOnClickListener(this)

        listView?.adapter = adapter
    }

    override fun onNFCIntent(i: INFCHandler.NFCInfo?) {

        if (alertDialog!=null)
            alertDialog?.dismiss()

        xinPianHao = i?.chip_sn.toString()
        tv_xinpian?.text = "芯片号: " + i?.chip_sn
        doSearch(i?.chip_sn.toString(), false)

    }

    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.iv_back ->
                act.pvc.pop()
            R.id.btn_search ->
                doSearch(et_input?.text.toString(), true)
            R.id.btn_sub ->
                subData(ids)
        }

    }


    protected var is_searching: Boolean = false

    protected fun doSearch(no: String, is_gp_no: Boolean) {

        if (is_searching) return
        is_searching = true
        act.showProgress()

        BackTask.post(object : CZBackTask(act) {
            var ret_gp: GangPingBean? = null

            override fun getURL(): String {
                return "gangPing/piLiangChaXun"
            }

            override fun runFront() {
                is_searching = false
                if (ret_gp == null) {
                    if (tv_xinpian != null) tv_xinpian?.setText(null)
                }
                super.runFront()
            }

            override fun runFront2() {
                if (ret_gp != null) {
                    if (CZUtlis.isok(ret_gp, lu, isNext)) {
                        if (!ids.contains(ret_gp?.id)) {
                            lists.add(ret_gp!!)
                            ids.add(ret_gp!!.id)
//                            lists.add(ret_gp!!)
//                            ids.add(ret_gp?.id!!)
                            adapter.data = lists
                            adapter.notifyDataSetChanged()
                        } else {
                            Utils.toastSHORT("已在列表中")
                        }
                    } else {
                        val ab: AlertDialog.Builder = AlertDialog.Builder(act)
                        ab.setPositiveButton("ok", DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
                        alertDialog = ab.create()
                        alertDialog?.setTitle("提示：")
                        alertDialog?.setMessage(CZUtlis.toMsg(ret_gp, lu, isNext))
                        alertDialog?.show()
                    }
                }
            }

            override fun getInputParam(): String {
                var j = JSONObject()
                var ja = JSONArray()
                if (is_gp_no) j.put("gangPingHao", no) else j.put("xinPianHao", no)
                ja.put(j)
                return ja.toString()
            }

            override fun parseResult(jdata: JSONObject?) {

                val ja = jdata?.getJSONArray("result")
                if (ja?.length() == 1) {
                    ret_gp = Constant.gson.fromJson(ja.getJSONObject(0).toString(), GangPingBean::class.java)
                    return
                }
                ret_gp = null
                err_msg = "没有找到气瓶!"
                throw IOException()

            }

        })
    }

    private val adapter: AbstractAdapter<GangPingBean> = object : AbstractAdapter<GangPingBean>() {
        override fun getView(i: Int, view: View?, viewGroup: ViewGroup?): View {
            var view = view
            var vh: ViewHolder? = null
            if (view == null) {
                view = View.inflate(viewGroup?.context, R.layout.jiandangjilu_item, null)
                vh = ViewHolder(view)
                view.tag = vh
            } else {
                vh = view.tag as ViewHolder
            }
            val data: GangPingBean = getItem(i)
            vh.tv_gangpingNum?.text = data.gangPingHao
            vh.tv_xinpian?.text = data.xinPianHao
            vh.tv_guige?.text = data.guiGeName
            vh.tv_qiti?.text = data.qiTiLeiXing
            return view!!
        }
    }


    private class ViewHolder {
        var tv_gangpingNum: TextView? = null
        var tv_guige: TextView? = null
        var tv_xinpian: TextView? = null
        var tv_qiti: TextView? = null

        constructor(view: View) {
            tv_gangpingNum = view.findViewById(R.id.tv_gangpingNum)
            tv_guige = view.findViewById(R.id.tv_guige)
            tv_xinpian = view.findViewById(R.id.tv_xinpian)
            tv_qiti = view.findViewById(R.id.tv_qiti)
        }

    }


}
