package com.czsy.ui.xunjian

import android.content.Context
import android.view.View
import com.czsy.android.R
import com.czsy.ui.AbsPVBase
import com.czsy.ui.MainActivity

class PVXunjianKt(a: MainActivity?) : AbsPVBase(a), View.OnClickListener {

    override fun createMainView(ctx: Context?) {
        mMainView = View.inflate(act, R.layout.pv_xunjian_main, null)

        for (id in intArrayOf(
                R.id.btn_xunjian_bangding,
                R.id.btn_xunjian_selete
        )) {
            mMainView.findViewById<View>(id).setOnClickListener(this)
        }
    }

    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.btn_xunjian_bangding -> act.main_pvc.push(PVXunjianBangdingKt(act))
            R.id.btn_xunjian_selete -> act.main_pvc.push(PVXunjianJianceKt(act))
        }
    }
}