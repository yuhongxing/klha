package com.github.mikephil.charting.myadd;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.highlight.PieRadarHighlighter;
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet;

public class MyPieHighlighter extends PieRadarHighlighter<MyPieChart> {

    public MyPieHighlighter(MyPieChart chart) {
        super(chart);
    }

    @Override
    protected Highlight getClosestHighlight(int index, float x, float y) {

        IPieDataSet set = mChart.getData().getDataSet();

        final Entry entry = set.getEntryForIndex(index);

        return new Highlight(index, entry.getY(), x, y, 0, set.getAxisDependency());
    }
}
