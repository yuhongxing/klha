package com.czsy.ui.gongyingzhan

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import com.czsy.android.databinding.PvZhanzhangMainBinding
import com.czsy.ui.AbsPVBase
import com.czsy.ui.MainActivity
import com.czsy.ui.gongyingzhan.yunying.dingdanlist.OrderFeipeiList
import com.czsy.ui.gongyingzhan.yunying.dingdanlist.YunShuDanList
import com.czsy.ui.gongyingzhan.yunying.manageSqg.GpHuikuList
import com.czsy.ui.gongyingzhan.yunying.manageSqg.KucunAct
import com.czsy.ui.gongyingzhan.yunying.manageSqg.LingqvShenpiList
import mylib.utils.Utils

/**
 * 供应站-站长主页
 */
class ZZMain(a: MainActivity) : AbsPVBase(a) {

    private lateinit var binding: PvZhanzhangMainBinding

    override fun createMainView(ctx: Context?) {

        binding = PvZhanzhangMainBinding.inflate(LayoutInflater.from(act))
        mMainView = binding.root
        binding.btnShenpi.setOnClickListener {
            act.main_pvc.push(LingqvShenpiList(act, false))
        }
        binding.btnHuiku.setOnClickListener {
            act.main_pvc.push(GpHuikuList(act,false))
        }

        binding.btnFenpei.setOnClickListener {
            act.main_pvc.push(OrderFeipeiList(act))
        }

        binding.btnKucun.setOnClickListener {
            act.main_pvc.push(KucunAct(act))
        }

        binding.btnYunshudan.setOnClickListener {
            act.main_pvc.push(YunShuDanList(act))
        }
    }

    override fun onAttach(firstShow: Boolean) {
        super.onAttach(firstShow)
        Log.d("xing","main");
    }

}