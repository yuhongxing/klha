package com.czsy.ui.gongyingzhan.yunying.dingdanlist


import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import com.czsy.CZNetUtils
import com.czsy.Constant
import com.czsy.android.R
import com.czsy.android.databinding.ItemGyzzzOrderFenpeiBinding
import com.czsy.android.databinding.ItemGyzzzOrderWeixiuBinding
import com.czsy.bean.gongyingzhanzz.AnjianBean
import com.czsy.bean.gongyingzhanzz.OrderItmeBean
import com.czsy.bean.gongyingzhanzz.PeisongyuanBean
import com.czsy.bean.gongyingzhanzz.WeixiuBean
import com.czsy.other.DatePickerDialog
import com.czsy.push.PushBean
import com.czsy.ui.AbsPVTabList
import com.czsy.ui.MainActivity
import mylib.app.BackFrontTask
import mylib.app.BackTask
import mylib.app.MyLog
import mylib.ui.list.AbstractAdapter
import mylib.utils.Utils
import org.json.JSONArray
import org.json.JSONObject
import java.lang.reflect.ParameterizedType
import java.util.*
import kotlin.collections.ArrayList

/**
 * 订单分配列表
 */
@Suppress("INACCESSIBLE_TYPE")
class OrderFeipeiList(a: MainActivity) : AbsPVTabList(a) {

    var sid: String = ""
    var index: Int = 0
    var isFormMsg = false
    var isFirst = true

    constructor(a: MainActivity, sid: String, type: Int, isFormMsg: Boolean) : this(a) {//
        this.sid = sid
        this.isFormMsg = isFormMsg
        if (type == PushBean.type_gouqi) {
            index = 0
        } else if (type == PushBean.type_shangpin) {
            index = 1
        } else if (type == PushBean.type_tuiping) {
            index = 2
        } else if (type == PushBean.type_zhejiu) {
            index = 3
        } else if (type == PushBean.type_weixiu) {
            index = 4
        } else if (type == PushBean.type_anjian) {
            index = 5
        }
    }

    val psyBeanList = mutableListOf<PeisongyuanBean>()
    val psyNameList = arrayListOf<String>()

    val wxyBeanList = mutableListOf<PeisongyuanBean>()
    val wxyNameList = arrayListOf<String>()

    override fun createMainView(ctx: Context?) {
        super.createMainView(ctx)
        tv_title.text = "订单分配"
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
                    tab_info[position].spinner = head?.findViewById(R.id.sp_type)
                    tab_info[position].tv_time?.setOnClickListener(tab_info[position])
                    tab_info[position].tv_time1?.setOnClickListener(tab_info[position])
                    tab_info[position].spinner?.visibility = View.VISIBLE
                    tab_info[position].updateTitle()
                    tab_info[position].seleteType()
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

//        seletePeisongyuan()
    }

    override fun onAttach(firstShow: Boolean) {
        super.onAttach(firstShow)
        Log.d("xing", "订单分配列表-->onAttach")
        if (index == 0) {
            loadData()
        }
        seletePeisongyuan()
    }

    //c查询配送员
    fun seletePeisongyuan() {
        BackTask.post(object : BackFrontTask {
            var ja: JSONArray? = null
            var wxyja: JSONArray? = null
            override fun runFront() {
                for (i in 0 until ja!!.length()) {
                    val gp = Constant.gson.fromJson(ja?.getJSONObject(i).toString(), PeisongyuanBean::class.java)
                    psyBeanList.add(gp)
                    psyNameList.add(gp.userName)
                }

                for (i in 0 until wxyja!!.length()) {
                    val gp = Constant.gson.fromJson(wxyja?.getJSONObject(i).toString(), PeisongyuanBean::class.java)
                    wxyBeanList.add(gp)
                    wxyNameList.add(gp.userName)
                }
            }

            override fun runBack() {
                psyBeanList.removeAll(psyBeanList)
                psyNameList.removeAll(psyNameList)
                val ret = CZNetUtils.postCZHttp("user/queryAllPeiSongYuan", null)
                ja = ret.getJSONArray("result")


                wxyBeanList.removeAll(wxyBeanList)
                wxyNameList.removeAll(wxyNameList)
                val wxyRet = CZNetUtils.postCZHttp("user/queryAllPeiSongYuanAndWeiXiuYuan", null)
                wxyja = wxyRet.getJSONArray("result")

            }

        })

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
            object : TabInfo<OrderItmeBean?>("订气单", "dingDan/chaXun/200/1") {

                override fun initSearchParam(j: JSONObject?) {

                    if (!isFirst && isFormMsg) {
                        val p = Constant.getTimeDuration(search_time, search_time2)
                        j?.put("startDate", p.first)
                        j?.put("endDate", p.second + " 23:59:59")
                    }

                    if (!isFormMsg) {
                        val p = Constant.getTimeDuration(search_time, search_time2)
                        j?.put("startDate", p.first)
                        j?.put("endDate", p.second + " 23:59:59")
                    }


                    j?.put("dingDanLeiXing", Constant.dingdan_type_gouqi)
                    j?.put("status", type)
                    if (!sid.equals("")) j?.put("sid", sid)
                }

                override fun getView(i: Int, convertView: View?, parent: ViewGroup?): View {
                    var binding: ItemGyzzzOrderFenpeiBinding = ItemGyzzzOrderFenpeiBinding.inflate(LayoutInflater.from(act))

                    val o = getItem(i)
                    setUiToDingqiAndShangpin(o!!, binding, 1)

                    return binding.root
                }
            },

            object : TabInfo<OrderItmeBean?>("商品单", "dingDan/chaXun/200/1") {

                override fun initSearchParam(j: JSONObject?) {

                    if (!isFirst && isFormMsg) {
                        val p = Constant.getTimeDuration(search_time, search_time2)
                        j?.put("startDate", p.first)
                        j?.put("endDate", p.second + " 23:59:59")
                    }

                    if (!isFormMsg) {
                        val p = Constant.getTimeDuration(search_time, search_time2)
                        j?.put("startDate", p.first)
                        j?.put("endDate", p.second + " 23:59:59")
                    }

                    j?.put("dingDanLeiXing", Constant.dingdan_type_shangpin)
                    j?.put("status", type)
                    if (!sid.equals("")) j?.put("sid", sid)
                }

                override fun getView(i: Int, convertView: View?, parent: ViewGroup?): View {
                    var binding: ItemGyzzzOrderFenpeiBinding = ItemGyzzzOrderFenpeiBinding.inflate(LayoutInflater.from(act))

                    val o = getItem(i)
                    setUiToDingqiAndShangpin(o!!, binding, 2)

                    return binding.root
                }
            },
            object : TabInfo<OrderItmeBean?>("退瓶单", "dingDan/chaXun/200/1") {

                override fun initSearchParam(j: JSONObject?) {
                    if (!isFirst && isFormMsg) {
                        val p = Constant.getTimeDuration(search_time, search_time2)
                        j?.put("startDate", p.first)
                        j?.put("endDate", p.second + " 23:59:59")
                    }

                    if (!isFormMsg) {
                        val p = Constant.getTimeDuration(search_time, search_time2)
                        j?.put("startDate", p.first)
                        j?.put("endDate", p.second + " 23:59:59")
                    }

                    j?.put("dingDanLeiXing", Constant.dingdan_type_tuipin)
                    j?.put("status", type)
                    if (!sid.equals("")) j?.put("sid", sid)
                }

                override fun getView(i: Int, convertView: View?, parent: ViewGroup?): View {
                    var binding: ItemGyzzzOrderFenpeiBinding = ItemGyzzzOrderFenpeiBinding.inflate(LayoutInflater.from(act))

                    val o = getItem(i)
                    setUiToDingqiAndShangpin(o!!, binding, 3)

                    return binding.root
                }
            },
            object : TabInfo<OrderItmeBean?>("回收单", "dingDan/chaXun/200/1") {

                override fun initSearchParam(j: JSONObject?) {
                    if (!isFirst && isFormMsg) {
                        val p = Constant.getTimeDuration(search_time, search_time2)
                        j?.put("startDate", p.first)
                        j?.put("endDate", p.second + " 23:59:59")
                    }

                    if (!isFormMsg) {
                        val p = Constant.getTimeDuration(search_time, search_time2)
                        j?.put("startDate", p.first)
                        j?.put("endDate", p.second + " 23:59:59")
                    }

                    j?.put("dingDanLeiXing", Constant.dingdan_type_zhejiu)
                    j?.put("status", type)
                    if (!sid.equals("")) j?.put("sid", sid)
                }

                override fun getView(i: Int, convertView: View?, parent: ViewGroup?): View {
                    var binding: ItemGyzzzOrderFenpeiBinding = ItemGyzzzOrderFenpeiBinding.inflate(LayoutInflater.from(act))

                    val o = getItem(i)
                    setUiToDingqiAndShangpin(o!!, binding, 4)

                    return binding.root
                }
            },

            object : TabInfo<WeixiuBean?>("维修单", "baoXiuTouSu/queryBaoXiu/100/1") {

                override fun initSearchParam(j: JSONObject?) {
                    if (!isFirst && isFormMsg) {
                        val p = Constant.getTimeDuration(search_time, search_time2)
                        j?.put("startDate", p.first)
                        j?.put("endDate", p.second + " 23:59:59")
                    }

                    if (!isFormMsg) {
                        val p = Constant.getTimeDuration(search_time, search_time2)
                        j?.put("startDate", p.first)
                        j?.put("endDate", p.second + " 23:59:59")
                    }

                    j?.put("status", type)
                    if (!sid.equals("")) j?.put("sid", sid)
                }


                override fun getView(i: Int, convertView: View?, parent: ViewGroup?): View {
                    var binding: ItemGyzzzOrderWeixiuBinding = ItemGyzzzOrderWeixiuBinding.inflate(LayoutInflater.from(act))
                    val b = getItem(i)
                    setUiToWeixiu(b!!, binding)
                    return binding.root
                }
            },
            object : TabInfo<AnjianBean?>("安检单", "anJian/query/100000/1") {

                override fun initSearchParam(j: JSONObject?) {
                    if (!isFirst && isFormMsg) {
                        val p = Constant.getTimeDuration(search_time, search_time2)
                        j?.put("startDate", p.first)
                        j?.put("endDate", p.second + " 23:59:59")
                    }

                    if (!isFormMsg) {
                        val p = Constant.getTimeDuration(search_time, search_time2)
                        j?.put("startDate", p.first)
                        j?.put("endDate", p.second + " 23:59:59")
                    }

                    j?.put("status", type)
                    if (!sid.equals("")) j?.put("sid", sid)
                }

                override fun getView(i: Int, convertView: View?, parent: ViewGroup?): View {
                    var binding: ItemGyzzzOrderWeixiuBinding = ItemGyzzzOrderWeixiuBinding.inflate(LayoutInflater.from(act))
                    val b = getItem(i)
                    setUiToAnjian(b!!, binding)
                    return binding.root
                }
            }
    )


    abstract inner class TabInfo<T>(title: String, url: String?) : AbstractAdapter<T>(), View.OnClickListener, OnItemClickListener {
        val title: String
        val url: String?

        var search_time = System.currentTimeMillis() - (1000 * 60 * 60 * 24 + 1000)
        var search_time2 = System.currentTimeMillis()
        val list_data: MutableList<T> = LinkedList()
        var list_view: ListView? = null
        var root: View? = null
        var tv_time: TextView? = null
        var tv_time1: TextView? = null
        var spinner: Spinner? = null
        var type = 1
        var isLoad = false
        var load = 1
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

        fun seleteType() {

            spinner?.adapter = ArrayAdapter(act, R.layout.gp_id_item, arrayListOf("未分配", "已分配", "已完成", "拒绝分配", "全部"))
            spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    when (position) {
                        0 -> type = 1
                        1 -> type = 2
                        2 -> {

                            val stp = tab_layout.selectedTabPosition
                            when (stp) {
                                4, 5 -> type = 4
                                else -> type = 6
                            }

                        }
                        3 -> type = 3
                        4 -> type = 0
                    }

                    load++
                    if (isLoad || load == 1) {
                        loadData()
                    } else {
                        load = 1
                        isLoad = true
                    }
                }

            }
        }

        override fun onClick(v: View) {
            if (v == tv_time || v == tv_time1) {
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

        }

        override fun onItemClick(var1: AdapterView<*>?, var2: View, var3: Int, var4: Long) {
            //item的 点击事件-bug->没有按钮显示的时候可以触发，有按钮显示的时候不触发
            //ListView中的item布局中如果有button，则ListView的OnItemClickListener不会触发。如果设置button的focusable为false可解决

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
            isFirst = false
            data = list_data
            sid = ""
        }


        fun runInBack() {
            try {
                val `in` = JSONObject()
                initSearchParam(`in`)
                val j = CZNetUtils.postCZHttp(url, `in`.toString())
                val code = j.getInt("code")
                if (code != 200) {
                    Utils.toastSHORT("${j.getString("message")}")
                }

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

    /**
     * setUi--订气（1）、商品（2）、退瓶（3）、回收（4）
     * 根据type判断显示商品的内容
     */
    private fun setUiToDingqiAndShangpin(o: OrderItmeBean, binding: ItemGyzzzOrderFenpeiBinding, type: Int) {

        binding.tvOrdernum.text = o.dingDanLeiXingName + "：" + o.dingDanHao
        binding.tvOrderTime.text = "下单时间：" + o.chuangJianRiQi
        binding.tvPeisongyuan.visibility = if (o.peiSongYuan != null) View.VISIBLE else View.GONE
        binding.tvPeisongyuan.text = "配  送  员：" + o.peiSongYuan
        binding.tvState.text = "状        态：" + o.statusName

        binding.zhifufangshi.visibility = if (o.zhiFuFangShi != null && o.zhiFuFangShi != 0) View.VISIBLE else View.GONE
        binding.zhifufangshi.text = "支付方式：" + o.zhiFuFangShiName
        binding.tvTuidan.visibility = if (o.tuiDanYuanYin != null) View.VISIBLE else View.GONE
        binding.tvTuidan.text = "退单原因：" + o.tuiDanYuanYin
        binding.tvKehuNum.text = "客户编号：" + o.keHuBianHao
        binding.tvName.text = "客        户：" + o.keHuMing
        binding.tvTel.text = ""
        Utils.setTextSpan("客户电话：",o.keHuDianHua,"",binding.tvTel, Utils.TextClickListener { Utils.toCall(o.keHuDianHua) })
        binding.tvKehuleixing.text = "客户类型：" + o.keHuLeiXingName

        val louceng = if (o.louCeng == null) "" else "; 楼层：" + o.louCeng
        binding.tvAddress.text = "地        址：" + o.diZhi + louceng
        binding.tvGyz.text = "供  应  站：" + o.gongYingZhan
        binding.tvLaiyuan.text = "来        源：" + o.laiYuanName
        binding.tvBeizhu.visibility = if (o.beiZhu != null) View.VISIBLE else View.GONE
        binding.tvBeizhu.text = "备        注：" + o.beiZhu


        var good = ""
        var qitiLeixing = ""
        var num = ""
        var priceZ = ""


        for ((count, _, _, shangPingMingCheng, qiTiLeiXingName, type1, zongJinE) in o.details) {

            if (type1 != "退瓶") {
                good = "$good${shangPingMingCheng}\n"

                qitiLeixing = "$qitiLeixing${qiTiLeiXingName}\n"

                num = "$num${count}\n"

                priceZ = "$priceZ${zongJinE}\n"
            }

        }


        when (type) {
            1 -> {
                binding.tvTitle.text = "气体类型"
                binding.gridLayoutGoods.visibility = View.VISIBLE
                binding.tvGood.text = good
                binding.tvQiti.text = qitiLeixing
                binding.tvNum.text = num
            }
            2 -> {
                binding.tvTitle.text = "总价"
                binding.gridLayoutGoods.visibility = View.VISIBLE
                binding.tvGood.text = good
                binding.tvQiti.text = priceZ
                binding.tvNum.text = num
            }
        }


        if (o.status == 1 || o.status == 3 || o.status == 13) {
            binding.container.visibility = View.VISIBLE
        } else {
            binding.container.visibility = View.GONE
        }

        binding.btnChexiao.setOnClickListener {

            when(type){
                1,2->{
                    chexiao("cancel",o.dingDanHao)
                }
                3,4->{
                    chexiao("quXiao",o.dingDanHao)
                }
            }

        }

        binding.btnFenpei.setOnClickListener {

            var peiSongYuanId = ""
            val dialog = Dialog(act, R.style.Theme_Design_Light_NoActionBar)
            dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setContentView(R.layout.dialog_ggzzz_fenpei)

            val spinner = dialog.findViewById<Spinner>(R.id.sp_peisongyuan)

            spinner.adapter = ArrayAdapter(act, R.layout.gp_id_item, psyNameList)
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    peiSongYuanId = psyBeanList.get(position).id
                }

            }
            dialog.findViewById<Button>(R.id.btn_ok).setOnClickListener {
                dialog.dismiss()
                fenpeiRenyuan(o.dingDanHao, peiSongYuanId, "dingDan/fenPei")
            }
            dialog.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()

        }
    }

    //setUi--维修
    private fun setUiToWeixiu(o: WeixiuBean, binding: ItemGyzzzOrderWeixiuBinding) {

        binding.tvOrdernum.text = "维  修  单：" + o.dingDanHao
        binding.tvOrderTime.text = "下单时间：" + o.chuangJianRiQi
        binding.tvState.text = "状        态：" + o.statusName

        binding.tvKehuNum.text = "客户编号：" + o.keHuBianHao
        binding.tvName.text = "客        户：" + o.keHuMing
        binding.tvTel.text = ""
        Utils.setTextSpan("客户电话：",o.keHuDianHua,"",binding.tvTel, Utils.TextClickListener { Utils.toCall(o.keHuDianHua) })
        binding.tvKehuleixing.text = "客户类型：" + o.keHuLeiXingName

        binding.tvAddress.text = "地        址：" + o.diZhi
        binding.tvGyz.text = "供  应  站：" + o.gongYingZhan
        binding.tvLaiyuan.text = "来        源：" + o.laiYuanName
        binding.tvBeizhu.visibility = if (o.beiZhu != null) View.VISIBLE else View.GONE
        binding.tvBeizhu.text = "备        注：" + o.beiZhu

        if (o.status == 1 || o.status == 3) {
            binding.container.visibility = View.VISIBLE
        } else {
            binding.container.visibility = View.GONE
        }

        binding.btnFenpei.setOnClickListener {

            var wxyYuanId = ""
            val dialog = Dialog(act, R.style.Theme_Design_Light_NoActionBar)
            dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setContentView(R.layout.dialog_ggzzz_fenpei)

            val spinner = dialog.findViewById<Spinner>(R.id.sp_peisongyuan)

            spinner.adapter = ArrayAdapter(act, R.layout.gp_id_item, wxyNameList)
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    wxyYuanId = wxyBeanList.get(position).id
                }

            }
            dialog.findViewById<Button>(R.id.btn_ok).setOnClickListener {
                dialog.dismiss()
                fenpeiRenyuan(o.dingDanHao, wxyYuanId, "baoXiuTouSu/fenPei")
            }
            dialog.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()

        }
    }

    //setUi--安检
    private fun setUiToAnjian(o: AnjianBean, binding: ItemGyzzzOrderWeixiuBinding) {

        binding.tvOrdernum.text = "安  检  单：" + o.dingDanHao
        binding.tvOrderTime.text = "下单时间：" + o.chuangJianRiQi
        binding.tvState.text = "状        态：" + o.statusName

        binding.tvKehuNum.text = "客户编号：" + o.keHuId
        binding.tvName.text = "客        户：" + o.keHuMing
        binding.tvTel.text = ""
        Utils.setTextSpan("客户电话：",o.keHuDianHua,"",binding.tvTel, Utils.TextClickListener { Utils.toCall(o.keHuDianHua) })
        binding.tvKehuleixing.text = "客户类型：" + o.keHuLeiXingName

        binding.tvAddress.text = "地        址：" + o.diZhi
        binding.tvGyz.text = "供  应  站：" + o.gongYingZhan
        binding.tvLaiyuan.text = "来        源：" + o.laiYuanName
        binding.tvBeizhu.visibility = if (o.beiZhu != null && !o.beiZhu.equals("")) View.VISIBLE else View.GONE
        binding.tvBeizhu.text = "备        注：" + o.beiZhu

        if (o.status == 1 || o.status == 3) {
            binding.container.visibility = View.VISIBLE
        } else {
            binding.container.visibility = View.GONE
        }

        binding.btnFenpei.setOnClickListener {

            var peiSongYuanId = ""
            val dialog = Dialog(act, R.style.Theme_Design_Light_NoActionBar)
            dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setContentView(R.layout.dialog_ggzzz_fenpei)

            val spinner = dialog.findViewById<Spinner>(R.id.sp_peisongyuan)

            spinner.adapter = ArrayAdapter(act, R.layout.gp_id_item, psyNameList)
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    peiSongYuanId = psyBeanList.get(position).id
                }

            }
            dialog.findViewById<Button>(R.id.btn_ok).setOnClickListener {
                dialog.dismiss()
                fenpeiRenyuan(o.dingDanHao, peiSongYuanId, "anJian/fenPei")
            }
            dialog.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()

        }
    }

    //分配人员
    fun fenpeiRenyuan(sid: String, peiSongYuanId: String, url: String) {
        BackTask.post(object : BackFrontTask {
            var code: Int = 0
            var msg: String = "操作失败"
            override fun runFront() {
                if (code == 200) {
                    Utils.toastSHORT("分配成功")
//                    notSendMsgUpdateFenPei(sid)
                    loadData()
                } else {
                    Utils.toastSHORT(msg)
                }
            }

            override fun runBack() {
                val json = JSONObject()
                json.put("sid", sid)
                json.put("peiSongYuanId", peiSongYuanId)
                val ret = CZNetUtils.postCZHttp(url, json.toString())
                code = ret.getInt("code")
                msg = ret.getString("message")
            }

        })
    }

    //撤销
    fun chexiao(url: String,sid: String) {
        BackTask.post(object : BackFrontTask {
            var code: Int = 0
            var msg: String = "操作失败"
            override fun runFront() {
                if (code == 200) {
                    Utils.toastSHORT("已撤销")
                    loadData()
                } else {
                    Utils.toastSHORT(msg)
                }
            }

            override fun runBack() {
                val json = JSONObject()
                json.put("sid", sid)
                json.put("yuanYin", "来自PDA操作")
                val ret = CZNetUtils.postCZHttp("dingDan/${url}", json.toString())
                code = ret.getInt("code")
                msg = ret.getString("message")
            }

        })
    }

    /**
     * 订单分配后改变消息是否分配的状态
     */
    fun notSendMsgUpdateFenPei(dingdanhao: String) {
        BackTask.post(object : BackFrontTask {
            override fun runFront() {

            }

            override fun runBack() {
                CZNetUtils.postCZHttp("notSendMsg/updateFenPei", "{dingDanHao:\"${dingdanhao}\"}")
            }

        })
    }

}