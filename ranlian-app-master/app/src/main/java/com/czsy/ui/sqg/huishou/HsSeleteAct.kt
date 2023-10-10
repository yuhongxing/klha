package com.czsy.ui.sqg.huishou

import android.content.Context
import android.view.LayoutInflater
import com.czsy.android.databinding.ActHuishouSeleteBinding
import com.czsy.bean.CommonOrderBean
import com.czsy.ui.AbsPVBase
import com.czsy.ui.MainActivity
import mylib.utils.Utils

/**
 * 选择有无芯片
 */
class HsSeleteAct(a: MainActivity, b: CommonOrderBean) : AbsPVBase(a) {

    var b: CommonOrderBean = b

    lateinit var binding: ActHuishouSeleteBinding

    override fun createMainView(ctx: Context?) {
        binding = ActHuishouSeleteBinding.inflate(LayoutInflater.from(act))
        mMainView = binding.root

        binding.include.tvTitle.text = "选择回收气瓶类型"
        binding.include.ivBack.setOnClickListener { act.main_pvc.pop() }
        binding.btnWu.setOnClickListener {
            act.main_pvc.push(PVZheJiuDan2(act, b))
        }
        binding.btnYou.setOnClickListener {
            act.main_pvc.push(HsYouXinpian(act, b, true))
        }
    }

}