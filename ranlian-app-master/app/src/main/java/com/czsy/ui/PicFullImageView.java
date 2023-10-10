package com.czsy.ui;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.czsy.android.R;

public class PicFullImageView extends AbsPVBase {

    private String picUrl;
    public PicFullImageView(MainActivity a, String picUrl) {
        super(a);
        this.picUrl = picUrl;
    }

    @Override
    protected void createMainView(Context ctx) {

        mMainView =  View.inflate(ctx, R.layout.full_image, null);
        ImageView image = mMainView.findViewById(R.id.image);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                act.getPVC().pop();
            }
        });

        Glide.with(act).load(picUrl).into(image);
    }
}
