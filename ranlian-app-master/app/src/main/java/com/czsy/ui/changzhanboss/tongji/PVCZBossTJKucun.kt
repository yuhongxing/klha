package com.czsy.ui.changzhanboss.tongji

import android.graphics.Color
import android.graphics.DashPathEffect
import android.os.Bundle
import com.czsy.CZNetUtils
import com.czsy.Constant
import com.czsy.android.databinding.PvczbossTjKucunBinding
import com.czsy.bean.changzhanboss.TongJiGPKucun
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.utils.ViewPortHandler
import com.google.gson.reflect.TypeToken
import mylib.app.BackFrontTask
import mylib.app.BackTask


/**
 * 气瓶库存统计
 */
class PVCZBossTJKucun : DemoBase() {

    private lateinit var binding: PvczbossTjKucunBinding
    private var bean: TongJiGPKucun? = null
    val list = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PvczbossTjKucunBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.include.tvTitle.text = "库存气瓶统计"
        binding.include.ivBack.setOnClickListener {
            finish()
        }

        initBarChart()

    }


    override fun saveToGallery() {

    }

    private fun initBarChart() {

        binding.chart1.animateY(1500)
        binding.chart1.setTouchEnabled(false)
        binding.chart1.isDragEnabled = false
        binding.chart1.setScaleEnabled(false)
        binding.chart1.setPinchZoom(false)
        binding.chart1.isDoubleTapToZoomEnabled = false
        binding.chart1.getDescription().isEnabled = false // 不显示描述
        binding.chart1.setExtraOffsets(10f, 15f, 0f, 20f) // 设置饼图的偏移量，类似于内边距 ，设置视图窗口大小
//        setAxis() // 设置坐标轴
        setLegend() // 设置图例
//        setData() // 设置数据
    }

    /**
     * 因为此处的 barData.setBarWidth(0.3f);，也就是说柱子的宽度是0.3f
     * 所以第二个柱子的值要比第一个柱子的值多0.3f，这样才会并列显示两根柱子
     */
    private fun setData() {
        val sets: MutableList<IBarDataSet> = ArrayList()
        // 此处有两个DataSet，所以有两条柱子，BarEntry（）中的x和y分别表示显示的位置和高度
        // x是横坐标，表示位置，y是纵坐标，表示高度
        val barEntries1: MutableList<BarEntry> = ArrayList()
        bean?.`5kg`?.zhong?.toFloat()?.let { BarEntry(0.3F, it) }?.let { barEntries1.add(it) }
        bean?.`10kg`?.zhong?.toFloat()?.let { BarEntry(1F + 0.3f, it) }?.let { barEntries1.add(it) }
        bean?.`15kg`?.zhong?.toFloat()?.let { BarEntry(2F + 0.3F, it) }?.let { barEntries1.add(it) }
        bean?.`50kg`?.zhong?.toFloat()?.let { BarEntry(3F + 0.3F, it) }?.let { barEntries1.add(it) }
        bean?.`50kgII`?.zhong?.toFloat()?.let { BarEntry(4F + 0.3F, it) }?.let { barEntries1.add(it) }
//        barEntries1.add(BarEntry(1F + 0.3f, 90f))
//        barEntries1.add(BarEntry(2F + 0.3f, 90f))
//        barEntries1.add(BarEntry(3F + 0.3f, 150f))
//        barEntries1.add(BarEntry(4F + 0.3f, 90f))
        val barDataSet1 = BarDataSet(barEntries1, "重瓶")
//        barDataSet1.valueTextColor = Color.RED // 值的颜色
        barDataSet1.valueTextSize = 10f // 值的大小
        barDataSet1.color = Color.parseColor("#536FFF") // 柱子的颜色
//        barDataSet1.label = "蔬菜" // 设置标签之后，图例的内容默认会以设置的标签显示
        // 设置柱子上数据显示的格式
        barDataSet1.valueFormatter = object : IValueFormatter {

            override fun getFormattedValue(value: Float, entry: Entry?, dataSetIndex: Int, viewPortHandler: ViewPortHandler?): String {
                // 此处的value默认保存一位小数
                return value.toInt().toString()
            }
        }
        sets.add(barDataSet1)
        val barEntries2: MutableList<BarEntry> = ArrayList()
        bean?.`5kg`?.kong?.toFloat()?.let { BarEntry( 0.6f +0.1f, it) }?.let { barEntries2.add(it) }
        bean?.`10kg`?.kong?.toFloat()?.let { BarEntry( 1.3f + 0.3f +0.1f, it) }?.let { barEntries2.add(it) }
        bean?.`15kg`?.kong?.toFloat()?.let { BarEntry( 2.3f + 0.3f +0.1f, it) }?.let { barEntries2.add(it) }
        bean?.`50kg`?.kong?.toFloat()?.let { BarEntry( 3.3f + 0.3f + 0.1f, it) }?.let { barEntries2.add(it) }
        bean?.`50kgII`?.kong?.toFloat()?.let { BarEntry( 4.3f + 0.3f +0.1f, it) }?.let { barEntries2.add(it) }
//        barEntries2.add(BarEntry(0.6f + 0.1f, 20f))
//        barEntries2.add(BarEntry(1.3f + 0.3f + 0.1f, 20f))
//        barEntries2.add(BarEntry(2.3f + 0.3f + 0.1f, 40f))
//        barEntries2.add(BarEntry(3.3f + 0.3f + 0.1f, 40f))
//        barEntries2.add(BarEntry(4.3f + 0.3f + 0.1f, 80f))
        val barDataSet2 = BarDataSet(barEntries2, "空瓶")
        // 不显示第二根柱子上的值
//        barDataSet2.setDrawValues(false) // 不显示值
        barDataSet2.color = Color.parseColor("#04CD99")
        barDataSet2.valueFormatter = object : IValueFormatter {

            override fun getFormattedValue(value: Float, entry: Entry?, dataSetIndex: Int, viewPortHandler: ViewPortHandler?): String {
                // 此处的value默认保存一位小数
                return value.toInt().toString()
            }
        }
//        barDataSet2.label = "水果"
        sets.add(barDataSet2)
        val barData = BarData(sets)
        barData.barWidth = 0.35f // 设置柱子的宽度
        binding.chart1!!.data = barData
    }

    private fun setLegend() {
        val legend = binding.chart1!!.legend
        legend.formSize = 14f // 图例的图形大小
        legend.textSize = 15f // 图例的文字大小
        legend.setDrawInside(false) // 设置图例是否在图中
//        legend.orientation = Legend.LegendOrientation.VERTICAL // 图例的方向为水平|垂直
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT //显示位置，水平右对齐
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP // 显示位置，垂直上对齐
        // 设置水平与垂直方向的偏移量
//        legend.yOffset = 10f
//        legend.xOffset = 20f
    }

    private fun setAxis() {
        // 设置x轴
        val xAxis = binding.chart1!!.xAxis
        xAxis.position = XAxisPosition.BOTTOM // 设置x轴显示在下方，默认在上方
        xAxis.setDrawGridLines(true) // 将此设置为true，绘制该轴的网格线。
        xAxis.setGridDashedLine(DashPathEffect(floatArrayOf(5.5f, 5f), 1.5F))//网格虚线
        xAxis.setCenterAxisLabels(true)
        xAxis.labelCount = 5 // 设置x轴上的标签个数
        xAxis.textSize = 12f // x轴上标签的大小
        val labelName = arrayOf("5kg", "10kg", "15kg", "50kg", "50kgII")

        xAxis.setValueFormatter(IndexAxisValueFormatter(labelName))
        xAxis.yOffset = 5f // 设置标签对x轴的偏移量，垂直方向

        // 设置y轴，y轴有两条，分别为左和右
        val yAxis_right = binding.chart1!!.axisRight
        yAxis_right.axisMaximum = list[0]+10.toFloat()//200f // 设置y轴的最大值
        yAxis_right.axisMinimum = 0f // 设置y轴的最小值
        yAxis_right.isEnabled = false // 不显示右边的y轴
        val yAxis_left = binding.chart1!!.axisLeft
        yAxis_left.axisMaximum = list[0]+10.toFloat()//150f//最大值
        yAxis_left.axisMinimum = 0f
        yAxis_left.textSize = 12f // 设置y轴的标签大小
        yAxis_left.setDrawAxisLine(false)
        yAxis_left.setDrawGridLines(true)
        yAxis_left.setGridDashedLine(DashPathEffect(floatArrayOf(5.5f, 5f), 1.5F))//网格虚线
        yAxis_left.granularity = 25F

    }

    override fun onResume() {

        BackTask.post(object : BackFrontTask {
            override fun runFront() {
                setAxis() // 设置坐标轴
                setData()
            }

            override fun runBack() {
                val ret = CZNetUtils.postCZHttp("pda/qiPingKuCun", null)

                val type = object : TypeToken<TongJiGPKucun>() {}.type
                bean = Constant.gson.fromJson(ret.getJSONObject("result").toString(), type)

                bean?.`5kg`?.kong?.let { list.add(it) }
                bean?.`10kg`?.kong?.let { list.add(it) }
                bean?.`15kg`?.kong?.let { list.add(it) }
                bean?.`50kg`?.kong?.let { list.add(it) }
                bean?.`50kgII`?.kong?.let { list.add(it) }
                bean?.`5kg`?.zhong?.let { list.add(it) }
                bean?.`10kg`?.zhong?.let { list.add(it) }
                bean?.`15kg`?.zhong?.let { list.add(it) }
                bean?.`50kg`?.zhong?.let { list.add(it) }
                bean?.`50kgII`?.zhong?.let { list.add(it) }
                list.sortDescending()
            }

        })

        super.onResume()
    }


}