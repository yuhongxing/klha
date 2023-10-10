package com.czsy.ui;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.czsy.android.R;

public class PVImage extends AbsPVBase implements View.OnClickListener {
    ImageView iv_content;
    final String url;

    public PVImage(MainActivity a, String url) {
        super(a);
        this.url = url;
    }


    @Override
    protected void createMainView(Context ctx) {
        mMainView = View.inflate(ctx, R.layout.pv_image, null);
        iv_content = mMainView.findViewById(R.id.iv_content);
        Glide.with(act).load(url).into(iv_content);
        mMainView.findViewById(R.id.iv_back).setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        act.getPVC().pop();
    }
}
