package com.czsy.ui.xunjianka

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.czsy.CZNetUtils
import com.czsy.Constant
import com.czsy.INFCHandler
import com.czsy.android.R
import com.czsy.bean.Address2Bean
import com.czsy.bean.ClientBean
import com.czsy.ui.AbsPVBase
import com.czsy.ui.AddressDialog
import com.czsy.ui.AddressDialog.OnAddressOK
import com.czsy.ui.MainActivity
import mylib.app.BackFrontTask
import mylib.app.BackTask
import mylib.utils.Utils
import org.json.JSONObject

class XunjiankaBind(a: MainActivity) : AbsPVBase(a), INFCHandler, View.OnClickListener {

    var tv_title: TextView? = null
    var tv_address: TextView? = null
    var iv_back: ImageView? = null
    var tv_content: TextView? = null
    var tv_xinpian: TextView? = null
    var btn_sub: Button? = null
    var btn_search: Button? = null
    var et_id: EditText? = null
    var xinpianhao: String? = ""
    var id: String? = ""
    var addr: Address2Bean? = null
    var type: Int? = null

    override fun createMainView(ctx: Context?) {
        mMainView = View.inflate(ctx, R.layout.xunjianka_bind, null)
        tv_title = mMainView.findViewById(R.id.tv_title)
        tv_address = mMainView.findViewById(R.id.tv_address)
        iv_back = mMainView.findViewById(R.id.iv_back)
        tv_content = mMainView.findViewById(R.id.tv_content)
        tv_xinpian = mMainView.findViewById(R.id.tv_xinpian)
        btn_sub = mMainView.findViewById(R.id.btn_sub)
        btn_search = mMainView.findViewById(R.id.btn_search)
        et_id = mMainView.findViewById(R.id.et_id)

        btn_sub?.setOnClickListener(this)
        btn_search?.setOnClickListener(this)
        iv_back?.setOnClickListener(this)

        tv_title?.text = "巡检卡绑定"
    }

    override fun onClick(v: View?) {

        if (v == btn_search) {

            searchKehu(et_id?.text.toString())
        } else if (v == btn_sub) {
            if (id.equals("")) {
                Utils.toastSHORT("请查询客户")

            } else if (type != 2) {
                Utils.toastSHORT("只有商业用户可以绑定巡检卡")
            } else if (xinpianhao.equals("")) {
                Utils.toastSHORT("请扫描巡检卡")
            } else if (addr == null) {
                Utils.toastSHORT("请选择地址")
            } else {
                bind(xinpianhao, id)

            }
        } else if (v == iv_back) {
            act.pvc.pop()
        }

    }

    override fun onNFCIntent(i: INFCHandler.NFCInfo?) {

        tv_xinpian?.text = "巡检卡号：" + i?.chip_sn
        xinpianhao = i?.chip_sn

    }

    fun searchKehu(tel: String) {
        act.showProgress()
        tv_content?.text = ""
        tv_address?.text = ""
        BackTask.post(object : BackFrontTask {
            var bean: ClientBean? = null
            override fun runFront() {
                act.hideProgress()
                if (bean != null) {

                    id = bean?.id.toString()
                    type = bean?.type?.toInt()
                    var content = "姓名：" + bean?.userName + "\n" +
                            "客户编号：" + bean?.keHuBianHao + "\n" +
                            "手机号：" + bean?.telNum + "\n" +
                            "用户类型：" + bean?.typeName
                    tv_content?.text = content
                    Utils.hideSoftInputFromWindow(act, et_id)

                    if (type == 2) {
                        seleteAddress()
                    } else {
                        Utils.toastSHORT("只有商业用户可以绑定巡检卡")
                    }


                }
            }

            override fun runBack() {
                var json = JSONObject()
                json?.put("data", tel)
                json?.put("type", 1)

                var jsonObject = JSONObject()
                jsonObject = CZNetUtils.postCZHttp("keHu/chaXunKeHuAnZhuo", json.toString())

                var code = jsonObject.getInt("code")
                if (code == 200) {
                    bean = Constant.gson.fromJson(jsonObject.getJSONObject("result").toString(), ClientBean::class.java)

                } else {
                    Utils.toastSHORT(jsonObject.getString("message"))
                }

            }

        })
    }

    fun bind(cardNumber: String?, objectId: String?) {
        act.showProgress()
        BackTask.post(object : BackFrontTask {
            var code = 0
            override fun runFront() {

                act.hideProgress()
                if (code == 200) {
                    Utils.toastSHORT("绑定成功")
                    act.main_pvc.pop()
                }
            }

            override fun runBack() {
                var json = JSONObject()
                json?.put("cardNumber", cardNumber)
                json?.put("objectId", objectId)

                json?.put("shengFen", addr?.sheng)
                json?.put("diShi", addr?.shi)
                json?.put("quXian", addr?.qu)
                json?.put("xiangZhen", addr?.xiang)
                json?.put("cun", addr?.cun)
                json?.put("diZhi", addr?.detailAddress)

                var ret = JSONObject()
                ret = CZNetUtils.postCZHttp("xinPian/bind", json.toString())
                code = ret.getInt("code")
                if (code != 200) {
                    Utils.toastSHORT(ret.getString("message"))
                }
            }

        })
    }

    fun seleteAddress() {

        val ad = AddressDialog(act, null, OnAddressOK { abc ->
            addr = abc.addr
//            Utils.toastSHORT(abc.addr.toString())
            tv_address?.text = abc.addr.toString()
        })
        ad.show()

    }


}