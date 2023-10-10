package com.czsy.ui.weixiuyuan

import android.content.Context
import android.view.LayoutInflater
import com.czsy.CZEvents
import com.czsy.android.databinding.PvMainWeixiuBinding
import com.czsy.ui.AbsPVBase
import com.czsy.ui.MainActivity
import com.czsy.ui.PVPushMsg
import com.czsy.ui.sqg.PVAnJianList
import com.czsy.ui.sqg.PVWeiXiuList
import mylib.app.BaseActivity.EventTypes
import mylib.app.EventHandler

class PVWeixiuMain(a: MainActivity) : AbsPVBase(a) {

    lateinit var binding: PvMainWeixiuBinding

    var pv_pushmsg: PVPushMsg? = null

    private val evt: CZEvents = object : CZEvents() {
        override fun onPushDataChanged() {
            pv_pushmsg?.reload()
        }
    }

    private fun getEvents(): EventTypes? {
        return EventTypes(arrayOf<Enum<*>>(
                CZEvents.Event.onPushDataChanged), evt)
    }

    override fun createMainView(ctx: Context?) {
        binding = PvMainWeixiuBinding.inflate(LayoutInflater.from(act))
        mMainView = binding.root

        pv_pushmsg = PVPushMsg(act, this, binding.tvNum)

        binding.btnAnjian.setOnClickListener {
            act.main_pvc.push(PVAnJianList(act, false))
        }

        binding.btnWeixiu.setOnClickListener {
            act.main_pvc.push(PVWeiXiuList(act, false))
        }

        binding.tvXiaoxi.setOnClickListener {
            act.main_pvc.push(pv_pushmsg)
        }

    }

    override fun onAttach(firstShow: Boolean) {
        super.onAttach(firstShow)
        if (firstShow) {
            pv_pushmsg!!.getView(act)
            pv_pushmsg!!.reload()

            val et = getEvents()
            EventHandler.addEventHandler(et!!.mEvts, et!!.mHandler)

        }
    }


}