package com.czsy.ui.gongyingzhan

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.czsy.CZEvents
import com.czsy.android.R
import com.czsy.ui.AbsPVBase
import com.czsy.ui.MainActivity
import com.czsy.ui.PVPushMsg
import com.czsy.ui.PVSetting
import mylib.app.BaseActivity.EventTypes
import mylib.app.EventHandler

class GYZZhanZhangMain(a: MainActivity) : AbsPVBase(a), View.OnClickListener {

    private var tv_tabs: Array<TextView>? = null
    private var ic_tabs: Array<ImageView>? = null
    private val icNo: IntArray = intArrayOf(R.mipmap.ic_msg_no, R.mipmap.ic_app_no, R.mipmap.ic_setting_no)
    private val icYes: IntArray = intArrayOf(R.mipmap.ic_msg_yes, R.mipmap.ic_app_yes, R.mipmap.ic_setting_yes)
    private var rl_tabs: Array<RelativeLayout>? = null

    private var pv_tabs: Array<AbsPVBase>? = null
    private var pv_container: ViewGroup? = null
    private var cur_tab: Int = -1

    lateinit var pv_pushmsg: PVPushMsg

    private val evt = object : CZEvents() {
        override fun onPushDataChanged() {
            pv_pushmsg.reload()
        }
    }

    private fun getEvents(): EventTypes? {
        return EventTypes(arrayOf<Enum<*>>(
                CZEvents.Event.onPushDataChanged), evt)
    }

    override fun createMainView(ctx: Context?) {
        mMainView = View.inflate(act, R.layout.pv_main_sqg, null)

        val pv = ZZMain(act)
        pv_pushmsg = PVPushMsg(act, this, mMainView.findViewById<TextView>(R.id.tv_msg_cnt))
        pv_tabs = arrayOf(pv_pushmsg,
                pv
                , PVSetting(act, -1))

        pv_container = mMainView.findViewById(R.id.pv_container)
        tv_tabs = arrayOf(mMainView.findViewById(R.id.btn_msg)
                , mMainView.findViewById(R.id.btn_app)
                , mMainView.findViewById(R.id.btn_setting))
        ic_tabs = arrayOf(
                mMainView.findViewById(R.id.image_msg),
                mMainView.findViewById(R.id.image_app),
                mMainView.findViewById(R.id.image_setting))
        rl_tabs = arrayOf(
                mMainView.findViewById(R.id.rl_msg),
                mMainView.findViewById(R.id.rl_app),
                mMainView.findViewById(R.id.rl_setting))


        var tab: Int = act.intent.getIntExtra("tab", 1);
        updateTab(tab)


        for (v: View in rl_tabs!!) {
            v.setOnClickListener(this)
        }
    }

    fun updateTab(tab: Int) {
        if (cur_tab == tab || tab < 0 || tab >= pv_tabs?.size!!) return

        if (cur_tab >= 0) {
            if (pv_tabs!![cur_tab] != null) {
                pv_tabs!![cur_tab].onDetach(true)
            }
            tv_tabs!![cur_tab].setTextColor(Color.parseColor("#FFBFBFBF"))
            ic_tabs!![cur_tab].setImageResource(icNo[cur_tab])
        }

        pv_container?.removeAllViews()
        if (pv_tabs!![tab] != null) {
            pv_container?.addView(pv_tabs!![tab].getView(act))
            pv_tabs!![tab].onAttach(true)

        }
        cur_tab = tab
        tv_tabs!![tab].setTextColor(Color.BLACK)
        ic_tabs!![tab].setImageResource(icYes[tab])
    }

    override fun onDetach(lastShow: Boolean) {
        super.onDetach(lastShow)
        if (cur_tab >= 0 && pv_tabs!![cur_tab] != null) {
            pv_tabs!![cur_tab].onDetach(lastShow)
        }
        if (lastShow) {
            val et = getEvents()
            EventHandler.removeEventHandler(et!!.mEvts, et.mHandler)
            for (pv in pv_tabs!!) {
                pv?.onDetach(true)
            }
        }
    }

    override fun onClick(v: View?) {
        var id = v?.id
        if (R.id.rl_msg == id) {
            updateTab(0)
        } else if (R.id.rl_setting == id) {
            updateTab(2)
        } else if (R.id.rl_app == id) {
            updateTab(1)
        }
    }

    override fun onAttach(firstShow: Boolean) {
        super.onAttach(firstShow)
        if (cur_tab >= 0 && pv_tabs!![cur_tab] != null) {
            pv_tabs!![cur_tab].onAttach(firstShow)
        }
        if (firstShow) {
            pv_pushmsg.getView(act)
            pv_pushmsg.reload()
            val et = getEvents()
            EventHandler.addEventHandler(et!!.mEvts, et!!.mHandler)
        }
    }

}