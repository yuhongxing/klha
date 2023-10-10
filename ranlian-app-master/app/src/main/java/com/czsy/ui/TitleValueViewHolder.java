package com.czsy.ui;

import android.view.View;
import android.widget.TextView;

import com.czsy.android.R;

public class TitleValueViewHolder {
    public final View root;
    public final TextView tv_title, tv_value;

    public TitleValueViewHolder(View r) {
        root = r;
        tv_title = r.findViewById(R.id.tv_title);
        tv_value = r.findViewById(R.id.tv_value);
    }
}

