package com.czsy.ui.changzhanboss

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import com.czsy.android.databinding.PvczbossmainBinding
import com.czsy.bean.LoginUser
import com.czsy.ui.AbsPVBase
import com.czsy.ui.MainActivity
import com.czsy.ui.changzhanboss.chongzhuanglog.ChongzhuangLogPage
import com.czsy.ui.changzhanboss.chuguan.ChuGuanPage
import com.czsy.ui.changzhanboss.churuku.ChuRukuPage
import com.czsy.ui.changzhanboss.tongji.PVCZBossTJKucun
import com.czsy.ui.changzhanboss.tongji.PVCZBossTJQiti
import com.czsy.ui.changzhanboss.yunshuyuan.YunshuyuanPage

/**
 * 场站--站长/经理
 */
class PVCZBossMain(a: MainActivity) : AbsPVBase(a) {

    private lateinit var binding: PvczbossmainBinding

    override fun createMainView(ctx: Context?) {
        binding = PvczbossmainBinding.inflate(LayoutInflater.from(act))
        mMainView = binding.root

        binding.tvZhandian.text = LoginUser.get().userName

        binding.imgTjKucun.setOnClickListener {
            val ii = Intent(act, PVCZBossTJKucun::class.java)
            act.startActivity(ii)
        }

        binding.imgTjQiti.setOnClickListener {
            act.startActivity(Intent(act, PVCZBossTJQiti::class.java))
        }

        binding.chukuContainer.setOnClickListener {
            act.main_pvc.push(ChuRukuPage(act, "气瓶出库", 3, false))
        }

        binding.rukuContainer.setOnClickListener {

            act.main_pvc.push(ChuRukuPage(act, "气瓶入库", 4, false))
        }

        binding.chuguanContainer.setOnClickListener {
            act.main_pvc.push(ChuGuanPage(act, false))
        }

        binding.rizhiContainer.setOnClickListener {
            act.main_pvc.push(ChongzhuangLogPage(act, false))
        }

        binding.kucunContainer.setOnClickListener {
            act.main_pvc.push(YunshuyuanPage(act, false))
        }

    }
}