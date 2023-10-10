package com.czsy.ui.yunshuyuan

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import com.czsy.android.R
import com.czsy.bean.GangPingBean
import com.czsy.ui.AbsPVBase
import com.czsy.ui.MainActivity
import mylib.ui.list.AbstractAdapter

/**
 * 操作完成统计页面
 */
class CompletedKt(a: MainActivity?, val title: String, val data: List<String>) : AbsPVBase(a) {

    private var listview: ListView? = null

    override fun createMainView(ctx: Context?) {
        mMainView = View.inflate(ctx, R.layout.completed, null)

        mMainView.findViewById<TextView>(R.id.tv_title).setText(title)
        mMainView.findViewById<ImageView>(R.id.iv_back).setOnClickListener { act.main_pvc.pop() }
        mMainView.findViewById<Button>(R.id.btn_goMain).setOnClickListener { act.main_pvc.popTo1() }

        listview = mMainView.findViewById(R.id.list_view)
        listview?.adapter = adapter
        adapter.data = data

    }


    val adapter = object : AbstractAdapter<String>(){
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var tv = convertView as  TextView?
            if (tv == null){
                tv = View.inflate(parent?.context,R.layout.common_text_item ,null) as TextView
            }
            var data = data.get(position)
            tv.setText("  "+data)
            return tv;
        }

    }


}