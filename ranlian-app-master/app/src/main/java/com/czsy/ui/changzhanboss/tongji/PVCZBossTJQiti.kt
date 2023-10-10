package com.czsy.ui.changzhanboss.tongji

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import com.czsy.CZNetUtils
import com.czsy.Constant
import com.czsy.android.databinding.PvczbossTjQitiBinding
import com.czsy.bean.changzhanboss.TongJiQitiBean
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler
import com.google.gson.reflect.TypeToken
import mylib.app.BackFrontTask
import mylib.app.BackTask
import java.util.*

/**
 * 气体数量统计
 */
class PVCZBossTJQiti : DemoBase() {

    private lateinit var binding: PvczbossTjQitiBinding

    private var bean: TongJiQitiBean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PvczbossTjQitiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.include.tvTitle.text = "气体数量统计"
        binding.include.ivBack.setOnClickListener {
            finish()
        }

//        initPieChart()

//        setData(3,100f)

    }

    fun initPieChart(){

        binding.chart1.setUsePercentValues(false)//显示数据百分比的值
        binding.chart1.getDescription().setEnabled(false)
        binding.chart1.setExtraOffsets(30f, 0f, 30f, 0f)
//        binding.chart1.setDrawEntryLabels(true)//设置pieChart是否只显示饼图上百分比不显示文字

        binding.chart1.setDragDecelerationFrictionCoef(0.95f)

        binding.chart1.setCenterText(generateCenterSpannableText(bean?.sum.toString()))


        binding.chart1.setDrawHoleEnabled(true)//设置为true，使饼中心为空
        binding.chart1.setHoleColor(Color.WHITE)

        binding.chart1.setTransparentCircleColor(Color.WHITE)
        binding.chart1.setTransparentCircleAlpha(110)//设置透明圆的透明度

        binding.chart1.setHoleRadius(64f)//中心孔的半径
        binding.chart1.setTransparentCircleRadius(68f)

        binding.chart1.setDrawCenterText(true)//中间文本显示

        binding.chart1.setRotationAngle(0f)
        binding.chart1.setRotationEnabled(true)//旋转
        binding.chart1.setHighlightPerTapEnabled(false)//每块区域是否可以点击
        binding.chart1.animateY(1400, Easing.EaseInOutQuad)

        val l: Legend = binding.chart1.getLegend()
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.VERTICAL
        l.setDrawInside(false)//
        l.isEnabled = false

    }

    private fun generateCenterSpannableText(string: String): SpannableString {
        val s = SpannableString("共计(kg)\n$string")
        s.setSpan(AbsoluteSizeSpan(50), 6,  s.length,0)
        s.setSpan(ForegroundColorSpan(Color.GRAY), 0, 6, 0)//
        s.setSpan(StyleSpan(Typeface.BOLD), 6, s.length , 0)
        s.setSpan(ForegroundColorSpan(Color.BLACK), 6, s.length , 0)//


        return s
    }


    private fun setData() {
        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(bean?.zhongPingAll!!.toFloat(), "重瓶余气"))
        entries.add(PieEntry(bean?.yuqiAll!!.toFloat(), "空瓶余气"))
        entries.add(PieEntry(bean?.chuGuanAll!!.toFloat(), "储罐余气"))

        val dataSet = PieDataSet(entries, "")
        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 5f

        val colors = ArrayList<Int>()
        colors.add(Color.parseColor("#1bcd9c"));
        colors.add(Color.parseColor("#fbda57"));
        colors.add(Color.parseColor("#4384f9"));

        dataSet.colors = colors
//        dataSet.setUseValueColorForLine(true)//线条颜色跟随
        dataSet.valueLinePart1OffsetPercentage = 80f//线条的某个设置，长短比例？
        dataSet.valueLinePart1Length = 0.3f//线条指向部分长度
        dataSet.valueLinePart2Length = 0.4f//线条横向部分长度

        dataSet.xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
        dataSet.yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
//        dataSet.setValueLineVariableLength(true)

        val data = PieData(dataSet)
        data.setValueFormatter(object :IValueFormatter{
            override fun getFormattedValue(value: Float, entry: Entry?, dataSetIndex: Int, viewPortHandler: ViewPortHandler?): String {

                return "${entry?.y}KG"
            }

        })
        data.setValueTextSize(15f)
        data.setValueTextColor(0xff000000.toInt())
//        data.setValueTextColors(colors)

        binding.chart1.setData(data)


        binding.chart1.highlightValues(null)
        binding.chart1.invalidate()
    }

    override fun onResume() {

        BackTask.post(object : BackFrontTask {
            override fun runFront() {
                initPieChart()
                setData()
            }

            override fun runBack() {
                val ret = CZNetUtils.postCZHttp("pda/gasCount", null)

                val type = object : TypeToken<TongJiQitiBean>() {}.type
                bean = Constant.gson.fromJson(ret.getJSONObject("result").toString(), type)

            }

        })

        super.onResume()
    }

    override fun saveToGallery() {

    }

}