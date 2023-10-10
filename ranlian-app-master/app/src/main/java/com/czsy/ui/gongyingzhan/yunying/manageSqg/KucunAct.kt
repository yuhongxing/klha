package com.czsy.ui.gongyingzhan.yunying.manageSqg

import android.content.Context
import android.view.LayoutInflater
import com.czsy.CZBackTask
import com.czsy.CZNetUtils
import com.czsy.Constant
import com.czsy.android.databinding.GggzzKucunBinding
import com.czsy.bean.gongyingzhanzz.KucunBean
import com.czsy.ui.AbsPVBase
import com.czsy.ui.MainActivity
import mylib.app.BackFrontTask
import mylib.app.BackTask
import org.json.JSONObject

class KucunAct(a: MainActivity) : AbsPVBase(a) {

    private lateinit var binding: GggzzKucunBinding

    var gongyingzhanId: String? = null

    override fun createMainView(ctx: Context?) {
        binding = GggzzKucunBinding.inflate(LayoutInflater.from(act))
        mMainView = binding.root

        binding.include.ivBack.setOnClickListener {
            act.main_pvc.pop()
        }
        binding.include.tvTitle.text = "库存总览"

//        binding.lineKong.setOnClickListener {
//            if (gongyingzhanId != null) {
//                act.main_pvc.push(KucunList(act, gongyingzhanId!!))
//
//            }
//        }

//        binding.lineZhong.setOnClickListener {
//            if (gongyingzhanId != null) {
//                act.main_pvc.push(KucunList(act, gongyingzhanId!!))
//            }
//        }

        getNetWork()
    }

    fun getNetWork() {

        act.showProgress()
        BackTask.post(object : BackFrontTask {

            var zBean: KucunBean? = null
            var kBean: KucunBean? = null

            override fun runFront() {

                binding.tv5kgZ.text = "5kg：" + zBean?.gangPing5KGCount.toString()
                binding.tv10kgZ.text = "10kg：" + zBean?.gangPing10KGCount.toString()
                binding.tv15kgZ.text = "15kg：" + zBean?.gangPing15KGCount.toString()
                binding.tv50kgZ.text = "50kg：" + zBean?.gangPing50KGCount.toString()
                binding.tv50kgIIZ.text = "50kgII：" + zBean?.gangPing50KGIICount.toString()

                binding.tv5kgK.text = "5kg：" + kBean?.gangPing5KGCount.toString()
                binding.tv10kgK.text = "10kg：" + kBean?.gangPing10KGCount.toString()
                binding.tv15kgK.text = "15kg：" + kBean?.gangPing15KGCount.toString()
                binding.tv50kgK.text = "50kg：" + kBean?.gangPing50KGCount.toString()
                binding.tv50kgIIK.text = "50kgII：" + kBean?.gangPing50KGIICount.toString()
                act.hideProgress()
            }

            override fun runBack() {
                val ret = CZNetUtils.postCZHttp("kuCun/chaKanGongYingZhanKuCun/1/1", "{\"zuiHouWeiZhi\":\"3\"}")
//                val ret = CZNetUtils.postCZHttp("chaKanGongYingZhanKuCun", "{\"zuiHouWeiZhi\":\"3\"}")
                if (ret.optJSONArray("result").length() >= 1) {
                    val json: JSONObject = ret.optJSONArray("result").get(0) as JSONObject


                    val jsonZ = json.getJSONObject("zhongPing")
                    zBean = Constant.gson.fromJson(jsonZ.toString(), KucunBean::class.java)

                    val jsonK = json.getJSONObject("kongPing")
                    kBean = Constant.gson.fromJson(jsonK.toString(), KucunBean::class.java)

                    gongyingzhanId = json.getString("gongYingZhanId")
                }

            }


        })
    }

}