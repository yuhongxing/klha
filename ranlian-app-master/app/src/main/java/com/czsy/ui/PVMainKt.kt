package com.czsy.ui

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.czsy.Constant
import com.czsy.android.R
import com.czsy.ui.changzhanboss.PVCZBossMain
import com.czsy.ui.sqg.PVWeiXiuList
import com.czsy.ui.weixiuyuan.PVWeixiuMain
import com.czsy.ui.xunjian.PVXunjianKt
import com.czsy.ui.yunshuyuan.PVYSYMain

class PVMainKt constructor(a: MainActivity, role: Int) : AbsPVBase(a), View.OnClickListener {

    private var tv_tabs: Array<TextView>? = null
    private var ic_tabs: Array<ImageView>? = null
    private val icNo: IntArray = intArrayOf(R.mipmap.ic_app_no, R.mipmap.ic_setting_no)
    private val icYes: IntArray = intArrayOf(R.mipmap.ic_app_yes, R.mipmap.ic_setting_yes)
    private var rl_tabs: Array<RelativeLayout>? = null

    private var pv_tabs: Array<AbsPVBase>? = null
    private var pv_container: ViewGroup? = null
    private var cur_tab: Int = -1

    private var role = role
    override fun createMainView(ctx: Context?) {
        Log.d("xing", "role==>" + role)
        mMainView = View.inflate(act, R.layout.pv_main_cz, null)

        if (role == Constant.role_cz_zhanzhang) {

            var tv : TextView= mMainView.findViewById(R.id.btn_app)
            tv.text = "管理"

            val pv = PVCZBossMain(act)
            pv_tabs = arrayOf(pv
                    , PVSetting(act, 0))

        } else if (role == Constant.role_yun_shu_yuan) {
            val pv: PVYSYMain = PVYSYMain(act)
            pv_tabs = arrayOf(pv
                    , PVSetting(act, 0))
        } else if (role == Constant.role_xun_jian) {
            val pv: PVXunjianKt = PVXunjianKt(act)
            pv_tabs = arrayOf(pv
                    , PVSetting(act, 0))
        } else if (role == Constant.role_wei_xiu) {
//            val pv = PVWeiXiuList(act, false)
            val pv = PVWeixiuMain(act)
            pv_tabs = arrayOf(pv
                    , PVSetting(act, -1))
        }

        pv_container = mMainView.findViewById(R.id.pv_container)

        tv_tabs = arrayOf(mMainView.findViewById(R.id.btn_app)
                , mMainView.findViewById(R.id.btn_setting))

        ic_tabs = arrayOf(
                mMainView.findViewById(R.id.image_app),
                mMainView.findViewById(R.id.image_setting))
        rl_tabs = arrayOf(
                mMainView.findViewById(R.id.rl_app),
                mMainView.findViewById(R.id.rl_setting))

        var tab: Int = act.intent.getIntExtra("tab", 0);
        updateTab(tab)


        for (v: View in rl_tabs!!) {
            v.setOnClickListener(this)
        }
    }

    override fun onClick(v: View?) {
        var id = v?.id
        if (R.id.rl_setting == id) {
            updateTab(1)
        } else if (R.id.rl_app == id) {
            updateTab(0)
        }
    }

    override fun onDetach(lastShow: Boolean) {
        super.onDetach(lastShow)
        if (cur_tab >= 0 && pv_tabs!![cur_tab] != null) {
            pv_tabs!![cur_tab].onDetach(lastShow)
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

}