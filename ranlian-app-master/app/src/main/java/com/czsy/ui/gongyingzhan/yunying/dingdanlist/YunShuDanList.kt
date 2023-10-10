package com.czsy.ui.gongyingzhan.yunying.dingdanlist

import android.app.Dialog
import android.content.Context
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import com.czsy.CZBackTask
import com.czsy.CZNetUtils
import com.czsy.Constant
import com.czsy.android.R
import com.czsy.android.databinding.ItemGyzzzYunshudanBinding
import com.czsy.bean.gongyingzhanzz.ItemBean
import com.czsy.other.DatePickerDialog
import com.czsy.push.PushBean
import com.czsy.ui.AbsPVTabList
import com.czsy.ui.MainActivity
import com.czsy.ui.PVSimpleText
import mylib.app.BackTask
import mylib.app.MyLog
import mylib.ui.list.AbstractAdapter
import mylib.utils.Utils
import org.json.JSONObject
import java.lang.reflect.ParameterizedType
import java.util.*

/**
 * 运输单
 */
class YunShuDanList(a: MainActivity) : AbsPVTabList(a) {

    var id: String = ""
    var index: Int = 0

    constructor(a: MainActivity, id: String, type: Int) : this(a) {
        this.id = id
        if (type == PushBean.type_gyz_ck) {
            index = 0
        } else if (type == PushBean.type_gyz_rk) {
            index = 1
        }

    }

    override fun createMainView(ctx: Context?) {
        super.createMainView(ctx)
        tv_title.text = "运输单"
        tab_layout.setupWithViewPager(view_pager)
        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                swiperefresh.isEnabled = state == ViewPager.SCROLL_STATE_IDLE

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                loadData()
            }

        })

        view_pager.adapter = object : PagerAdapter() {

            override fun instantiateItem(container: ViewGroup, position: Int): Any {
                val create = tab_info[position].root == null
                if (create) {
                    tab_info[position].root = View.inflate(act, R.layout.search_order_tab, null)
                    tab_info[position].list_view = tab_info[position].root?.findViewById(R.id.list_view)
                    val lv = tab_info[position].list_view
                    if (lv != null) {
                        Log.d("xiaofa", "lv != null")
                        lv.onItemClickListener = tab_info[position]
                    } else {
                        Log.d("xiaofa", "lv == null")
                    }
                    val head = tab_info[position].root?.findViewById<View>(R.id.top_container)
                    tab_info[position].tv_time = head?.findViewById(R.id.tv_time)
                    tab_info[position].tv_time1 = head?.findViewById(R.id.tv_time1)
                    tab_info[position].tv_time?.setOnClickListener(tab_info[position])
                    tab_info[position].tv_time1?.setOnClickListener(tab_info[position])
                    tab_info[position].updateTitle()
                    if (lv != null) {
                        lv.adapter = tab_info[position]
                        lv.setOnScrollListener(object : AbsListView.OnScrollListener {
                            override fun onScrollStateChanged(view: AbsListView, i: Int) {
                                if (i == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                                    val first = view.firstVisiblePosition
                                    val firstView = view.getChildAt(first)
                                    swiperefresh.isEnabled = first == 0 && (firstView == null || firstView.top == 0)
                                }
                            }

                            override fun onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {}
                        })
                    }
                }
                container.addView(tab_info[position].root)
                return tab_info[position].root as View
            }

            override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
                container.removeView(obj as View)
            }

            override fun isViewFromObject(view: View, `object`: Any): Boolean {

                return view == `object`
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return tab_info[position].title
            }

            override fun getCount(): Int {
                return tab_info.size
            }

        }
        tab_layout.getTabAt(index)?.select()
    }

    override fun onAttach(firstShow: Boolean) {
        super.onAttach(firstShow)

        if (index == 0) loadData()
    }

    override fun runFront() {
        swiperefresh.isRefreshing = false
        if (loading_idx < 0 || loading_idx >= tab_info.size) {
            return
        }
        val ti: TabInfo<*> = tab_info[loading_idx]
        ti.runFront()

    }

    override fun runBack() {
        if (loading_idx < 0 || loading_idx >= tab_info.size) {
            return
        }
        val ti: TabInfo<*> = tab_info[loading_idx]
        ti.runInBack()
    }

    private val tab_info = arrayOf<TabInfo<*>>(
            object : TabInfo<ItemBean?>("供应站出库", "gangPingLiuZhuan/yunShuYuanChaXun/200/1") {

                override fun initSearchParam(j: JSONObject?) {
                    val p = Constant.getTimeDuration(search_time, search_time2)
                    j?.put("startDate", p.first)
                    j?.put("endDate", p.second + " 23:59:59")
                    j?.put("type", 1)
                    if (!id.equals("")) j?.put("id",id)
                }

                override fun getView(i: Int, convertView: View?, parent: ViewGroup?): View {
                    var binding: ItemGyzzzYunshudanBinding = ItemGyzzzYunshudanBinding.inflate(LayoutInflater.from(act))

                    val o = getItem(i)
                    setUi(o!!, binding, 1)

                    return binding.root
                }
            },

            object : TabInfo<ItemBean?>("供应站入库", "gangPingLiuZhuan/yunShuYuanChaXun/200/1") {

                override fun initSearchParam(j: JSONObject?) {
                    val p = Constant.getTimeDuration(search_time, search_time2)
                    j?.put("startDate", p.first)
                    j?.put("endDate", p.second + " 23:59:59")
                    j?.put("type", 2)
                    if (!id.equals("")) j?.put("id",id)
                }

                override fun getView(i: Int, convertView: View?, parent: ViewGroup?): View {
                    var binding: ItemGyzzzYunshudanBinding = ItemGyzzzYunshudanBinding.inflate(LayoutInflater.from(act))

                    val o = getItem(i)
                    setUi(o!!, binding, 2)

                    return binding.root
                }
            })


    abstract inner class TabInfo<T>(title: String, url: String?) : AbstractAdapter<T>(), View.OnClickListener, AdapterView.OnItemClickListener {
        val title: String
        val url: String?

        var search_time = System.currentTimeMillis() - (1000 * 60 * 60 * 24 + 1000)
        var search_time2 = System.currentTimeMillis()
        val list_data: MutableList<T> = LinkedList()
        var list_view: ListView? = null
        var root: View? = null
        var tv_time: TextView? = null
        var tv_time1: TextView? = null
        fun updateTitle() {
            var c = Calendar.getInstance()
            c.timeInMillis = search_time
            var y = c[Calendar.YEAR]
            var m = c[Calendar.MONTH]
            var d = c[Calendar.DAY_OF_MONTH]
            tv_time!!.text = String.format("开始日期: %4d-%02d-%02d", y, m + 1, d)
            c = Calendar.getInstance()
            c.timeInMillis = search_time2
            y = c[Calendar.YEAR]
            m = c[Calendar.MONTH]
            d = c[Calendar.DAY_OF_MONTH]
            tv_time1!!.text = String.format("结束日期: %4d-%02d-%02d", y, m + 1, d)
        }

        override fun onClick(v: View) {
            if (v !== tv_time && v !== tv_time1) {
                return
            }
            val d = DatePickerDialog(act)
            d.setOnDateSetListener(DatePickerDialog.OnDateSetListener { view, i, i1, i2 ->
                val c = Calendar.getInstance()
                c[Calendar.YEAR] = i
                c[Calendar.MONTH] = i1
                c[Calendar.DAY_OF_MONTH] = i2
                c[Calendar.HOUR_OF_DAY] = 0
                c[Calendar.MINUTE] = 0
                c[Calendar.SECOND] = 0
                val t = c.timeInMillis
                if (v === tv_time) {
                    if (t > search_time2) {
                        Utils.toastLONG("起始时间必须在结束时间之前")
                        return@OnDateSetListener
                    }
                    search_time = t
                } else {
                    if (t < search_time) {
                        Utils.toastLONG("结束时间必须在起始时间之后")
                        return@OnDateSetListener
                    }
                    search_time2 = t
                }
                updateTitle()
                loadData()
            })
            d.show()
        }

        override fun onItemClick(var1: AdapterView<*>?, var2: View, var3: Int, var4: Long) {
            //item的 点击事件-bug->没有按钮显示的时候可以触发，有按钮显示的时候不触发
            Log.d("xing", "onticlik-->" + var3)
//            val data = getItem(var3)
//            var pv : PVSimpleText? = null
//            if (data is OrderItmeBean){
//
//            }else if (data is AnjianBean){
//                pv = PVSimpleText(act, anjianToString(data as AnjianBean))
//            }
//            act.getPVC().push(pv)
        }

        abstract fun initSearchParam(j: JSONObject?)
        fun runFront() {
            if (!TextUtils.isEmpty(err_msg)) {
                Utils.toastLONG(err_msg)
                return
            }
            data = list_data
            id = ""
        }


        fun runInBack() {
            try {
                val `in` = JSONObject()
                initSearchParam(`in`)
                val j = CZNetUtils.postCZHttp(url, `in`.toString())
                val jr = j.optJSONArray("result")
                list_data.clear()
                val type = this.javaClass
                        .genericSuperclass as ParameterizedType
                val clazz: Class<*> = type.actualTypeArguments[0] as Class<T>
                for (i in 0 until (jr?.length() ?: 0)) {
                    list_data.add(Constant.gson.fromJson<Any>(

                            jr!!.getJSONObject(i).toString(), clazz) as T)
                }
            } catch (e: Exception) {
                MyLog.LOGE(e)
                if (TextUtils.isEmpty(err_msg)) {

                    err_msg = act.getString(R.string.tip_common_err)

                }
            }
        }


        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            return convertView!!
        }

        init {
            this.title = title
            this.url = url
        }
    }


    private fun setUi(bean: ItemBean, binding: ItemGyzzzYunshudanBinding, type: Int) {

        binding.tvChongzhuangzhan.text = bean.changZhan
        binding.tvYunshuyuan.text = bean.yunShuYuanName
        binding.tvChepai.text = bean.chePai
        binding.tvNum.text = bean.gangPingCount.toString()
        binding.tvStatus.text = bean.statusName
        binding.tvFangxiang.text = bean.typeName
        binding.tvRiqi.text = bean.chuangJianRiQi

        when (bean.status) {//1:审批，2：未审批，3：驳回
            1,3 -> {
                binding.btnShenpi.visibility = GONE
                binding.btnChexiao.visibility = GONE
            }
            2 -> binding.btnShenpi.visibility = VISIBLE
        }

        binding.btnShenpi.setOnClickListener {
            netShenOrChe("审批备注", bean.id, 1)
        }

        binding.btnChexiao.setOnClickListener {
            netShenOrChe("撤销备注", bean.id, 2)
        }

        binding.btnDetails.setOnClickListener {

            act.showProgress()
            BackTask.post(object : CZBackTask(act) {
                @Throws(java.lang.Exception::class)
                override fun parseResult(jdata: JSONObject) {
                    val ja = jdata.getJSONArray("result")
                    val sb = StringBuffer()
                    val map: MutableMap<String, Int?> = HashMap()
                    for (i in 0 until ja.length()) {
                        val jo = ja.getJSONObject(i)
                        val guige = jo.optString("guiGeName", "")
                        if (!TextUtils.isEmpty(guige)) {
                            if (!map.containsKey(guige)) {
                                map[guige] = 1
                            } else {
                                val ii = map[guige]!!
                                map[guige] = ii + 1
                            }
                        }
                        sb.append(String.format(
                                """
                                    ----------------------------------------------------
气瓶号: %s
气瓶类型：%s
气体类型：%s
规格：%s
生产厂家: %s
                                    """,
                                jo.optString("gangPingHao", null),
                                jo.optString("yuQiStatusName", ""),
                                jo.optString("qiTiLeiXing", ""),
                                guige,
                                jo.optString("shengChanChangJia", "")
                        ))
                    }
                    val sb2 = StringBuffer()
                    sb2.append("-------------------- 汇总统计\n")
                    for (key in map.keys) {
                        sb2.append(key + ": " + map[key])
                        sb2.append('\n')
                    }
                    sb2.append(sb.toString())
                    ret_str = sb2.toString()
                }

                var ret_str: String? = null

                @Throws(java.lang.Exception::class)
                override fun getInputParam(): String {
                    return "{\"id\":${bean.id}}"
                }

                override fun getURL(): String {
                    return "gangPing/chaXunYunShuDanGangPingDetails"
                }

                override fun runFront2() {
                    if (ret_str != null) {
//                        act.pvc.push(PVSimpleText(act, ret_str))

                        act.pvc.push(object : PVSimpleText(act, ret_str) {
                            override fun initView() {
//                                btn_chexiao.visibility = VISIBLE
//                                btn_shenpi.visibility = VISIBLE
                            }

                            override fun shenPi() {
                                Utils.toastSHORT("shenpi")
                            }

                            override fun cheXiao() {
                                Utils.toastSHORT("chexiao")
                            }
                        })
                    }
                }
            })

        }


    }

    /**
     * 审批、撤销 请求
     */
    fun netShenOrChe(title: String, id: String, type: Int) {

        val d = Dialog(act, android.R.style.Theme_Translucent_NoTitleBar)
        d.setContentView(R.layout.dialog_input)
        d.findViewById<TextView>(R.id.tv_title).text = title
        val et_input = d.findViewById<EditText>(R.id.et_input)
        d.findViewById<View>(R.id.btn_ok).setOnClickListener(View.OnClickListener {
            val reason = et_input.text.toString()
            if (TextUtils.isEmpty(reason)) {
                Utils.toastLONG("请填写备注信息")
                return@OnClickListener
            }
            act.showProgress()
            BackTask.post(object : CZBackTask(act) {

                @Throws(java.lang.Exception::class)
                override fun parseResult(jdata: JSONObject) {

                }

                @Throws(java.lang.Exception::class)
                override fun getInputParam(): String {
                    val json = JSONObject()
                    json.put("id", id)
                    json.put("daFu", et_input.text.toString())
                    return json.toString()
                }

                override fun getURL(): String {
                    return when (type) {
                        1 -> "gangPingLiuZhuan/processingYunShuYuanLiuZhuan"
                        2 -> "gangPingLiuZhuan/cancelYunShuDan"
                        else -> ""
                    }
                }

                override fun runFront2() {
                    loadData()
                    Utils.toast(R.string.tip_op_ok)
                }

                override fun runFront() {
                    d.dismiss()
                    super.runFront()

                }
            })
        })
        d.findViewById<View>(R.id.btn_cancel).setOnClickListener { d.dismiss() }
        d.show()
    }

}