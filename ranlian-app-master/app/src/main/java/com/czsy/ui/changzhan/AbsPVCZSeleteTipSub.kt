package com.czsy.ui.changzhan

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import android.view.View
import android.widget.*
import com.czsy.CZBackTask
import com.czsy.CZNetUtils
import com.czsy.INFCHandler
import com.czsy.android.R
import com.czsy.bean.AnJianOrderBean
import com.czsy.ui.AbsPVBase
import com.czsy.ui.MainActivity
import mylib.app.BackFrontTask
import mylib.app.BackTask
import mylib.utils.CameraUtils
import mylib.utils.FileUtils
import mylib.utils.ImageUtils
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception

/**
 * 选择问题提交-基类
 */
abstract class AbsPVCZSeleteTipSub(a: MainActivity) : AbsPVBase(a), INFCHandler, View.OnClickListener {

    var tv_right: TextView? = null
    var tv_title: TextView? = null
    var et_input: EditText? = null
    var et_comment: EditText? = null
    var ivPic: ImageView? = null
    var main_container: LinearLayout? = null
    var line_top: LinearLayout? = null

    var picF: String? = null
    var picUrl: String? = null
    val anjian_list: MutableList<XunjianViewHolder> = mutableListOf()


    override fun createMainView(ctx: Context?) {

        CameraUtils.initPhotoError()
        mMainView = View.inflate(act, inflateMainView(), null)

        tv_right = mMainView.findViewById(R.id.tv_right)
        et_input = mMainView.findViewById(R.id.et_input)
        et_comment = mMainView.findViewById(R.id.et_comment)
        ivPic = mMainView.findViewById(R.id.iv_add)
        line_top = mMainView.findViewById(R.id.line_top)
        main_container = mMainView.findViewById(R.id.main_container)
        tv_title = mMainView.findViewById(R.id.tv_title)
        tv_title?.setText("巡检设备")

        tv_right?.visibility = View.VISIBLE
        tv_right?.setText("记录")
        tv_right?.setOnClickListener(this)
        ivPic?.setOnClickListener(this)
        mMainView.findViewById<Button>(R.id.btn_search).setOnClickListener(this)
        mMainView.findViewById<Button>(R.id.btn_sub).setOnClickListener(this)
        mMainView.findViewById<ImageView>(R.id.iv_back).setOnClickListener(this)
        initView()
    }

    override fun onNFCIntent(i: INFCHandler.NFCInfo?) {
        et_input?.setText(i?.chip_sn)
        seleteType(et_input?.text.toString())
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_back -> act.pvc.pop()
            R.id.tv_right ->
                rightBtnOnClick()
            R.id.btn_search -> seleteType(et_input?.text.toString())
            R.id.btn_sub -> nextBtnOnClick()
            R.id.iv_add -> paizhao()
        }
    }

    abstract fun rightBtnOnClick()
    abstract fun nextBtnOnClick()
    abstract fun getTipUrl(): String
    open fun inflateMainView(): Int {
        return R.layout.pv_xj_tip
    }

    open fun initView() {}

    var status: Int = 0//充装前后检查的字段，1：前，2：后

    override fun onAttach(firstShow: Boolean) {
        super.onAttach(firstShow)
        if (status != 0) seleteType("")
    }

    /**
     * 查询设备的对应类型问题
     * @param str 设备编号or芯片号
     */
    fun seleteType(str: String) {
        act.showProgress()
        BackTask.post(object : CZBackTask(act) {
            var item_list: MutableList<AnJianOrderBean.QA> = mutableListOf()

            override fun getURL(): String {
                return getTipUrl()
            }

            override fun runFront2() {
//                Log.d("NetWork","in runFront2")
                item_list.let { updateQues(it) }
            }

            override fun getInputParam(): String {

                if ("queryChongZhuangQuestion" in getTipUrl()) {
                    return "{\"status\":\"$status\"}"
                } else if ("queryXunJianNeiRongGuanLiByXinPianHao" in getTipUrl()) {
                    return "{\"xinPianHao\":\"$str\"}"
                }
                return ""
            }

            override fun parseResult(jsonObject: JSONObject?) {
                val err_msg = jsonObject?.optString("message")
                val code: Int = jsonObject?.optInt("code", 200)!!
//                if (code != 200) {
//                    throw CZNetUtils.CZNetErr(code, jsonObject)
//                }
                if (code == 200) {
                    val result: JSONArray = jsonObject?.getJSONArray("result")
                    for (i in 0 until result.length()) {
                        val j_obj = result.getJSONObject(i)
                        val id = j_obj.optInt("id")
                        val title = j_obj.getString("wenti")
                        val optionList = j_obj.getJSONArray("opthions")
                        var item = AnJianOrderBean.QA()
                        item.id = id
                        item.wenTi = title
                        for (j in 0 until optionList.length()) {
                            val daan = optionList.getString(j)
                            item.daAn.add(daan)
                        }
                        if (item.daAn.size > 0) {
                            item_list.add(item)
                        }
                    }
                }
            }

            override fun runFront() {
                super.runFront()
//                Log.d("NetWork","in runFront")
                item_list.let { updateQues(it) }
            }

        })

    }

    fun updateQues(item_list: List<AnJianOrderBean.QA>) {
        anjian_list.removeAll(anjian_list)
        main_container?.removeAllViews()
//        Log.d("NetWork","in updateQues")
        if (item_list.isEmpty()) {
            return
        }
        for (item in item_list) {
            val vh = XunjianViewHolder(act, item)
            main_container!!.addView(vh.root)
            anjian_list?.add(vh)
        }
    }

    fun paizhao() {
        picF = FileUtils.getDir(FileUtils.DirType.pic) + System.currentTimeMillis() + ".jpg"
        CameraUtils.doCamera(act, picF)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {

        var file = CameraUtils.getPicutre(data, requestCode, resultCode, act, picF)
        if (ImageUtils.isImage(file)) {
            BackTask.post(object : BackFrontTask {
                var bmp: Bitmap? = null
                override fun runFront() {
                    ivPic?.setImageBitmap(bmp)
                }

                override fun runBack() {
                    act.showProgress()
                    bmp = ImageUtils.resizeBitmap(ImageUtils.getFileBmp(file), 100)
                    picUrl = CZNetUtils.upload(bmp);
                    act.hideProgress()
                }

            })
        }


        return super.onActivityResult(requestCode, resultCode, data)
    }

    class XunjianViewHolder {
        var item: AnJianOrderBean.QA? = null
        var cbs: Array<CheckBox>? = null
        var tv_title: TextView? = null
        var root: View? = null

        constructor(ctx: Context?, i: AnJianOrderBean.QA) {
            val r = View.inflate(ctx, R.layout.anjian_ques_item, null)
            root = r
            root?.setTag(this)
            item = i
            cbs = arrayOf(
                    r.findViewById(R.id.cb1),
                    r.findViewById(R.id.cb2),
                    r.findViewById(R.id.cb3),
                    r.findViewById(R.id.cb4),
                    r.findViewById(R.id.cb5),
                    r.findViewById(R.id.cb6),
                    r.findViewById(R.id.cb7),
                    r.findViewById(R.id.cb8),
                    r.findViewById(R.id.cb9),
                    r.findViewById(R.id.cb10))
            for (cb in cbs!!) {
                cb.tag = this
            }

            tv_title = r.findViewById(R.id.tv_title)
            tv_title?.setText(item?.wenTi)
            for (idx in cbs!!.indices) {
                if (idx >= item!!.daAn.size) {
                    cbs!![idx].visibility = View.GONE
                } else {
                    cbs!![idx].visibility = View.VISIBLE
                    cbs!![idx].text = item!!.daAn[idx]
                }
            }
        }

    }

}