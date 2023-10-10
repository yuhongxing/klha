package com.czsy.ui.sqg;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.czsy.CZNetUtils;
import com.czsy.android.R;
import com.czsy.bean.YajinGuanliBean;
import com.czsy.ui.AbsPVBase;
import com.czsy.ui.MainActivity;
import com.czsy.ui.PicFullImageView;

/**
 * 押金管理详情
 */
public class PVYajinGuanliDetails extends AbsPVBase {


    private TextView tv_ordernum,tv_name,tv_tel,tv_guige,tv_yajin,tv_title,
    tv_ss_yajin,tv_type,tv_gongyingzhan,tv_time,tv_shouqvren;
    private ImageView image;
    private YajinGuanliBean bean;

    public PVYajinGuanliDetails(MainActivity a, YajinGuanliBean bean) {
        super(a);
        this.bean = bean;
    }

    @Override
    protected void createMainView(Context ctx) {
        mMainView = View.inflate(ctx, R.layout.pv_sqg_yajinguanli_details, null);

        tv_title = mMainView.findViewById(R.id.tv_title);
        tv_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                act.getPVC().pop();
            }
        });

        tv_ordernum = mMainView.findViewById(R.id.tv_ordernum);
        tv_name = mMainView.findViewById(R.id.tv_name);
        tv_tel = mMainView.findViewById(R.id.tv_tel);
        tv_guige = mMainView.findViewById(R.id.tv_guige);
        tv_yajin = mMainView.findViewById(R.id.tv_yajin);
        tv_ss_yajin = mMainView.findViewById(R.id.tv_ss_yajin);
        tv_type = mMainView.findViewById(R.id.tv_type);
        tv_gongyingzhan = mMainView.findViewById(R.id.tv_gongyingzhan);
        tv_shouqvren = mMainView.findViewById(R.id.tv_shouqvren);
        tv_time = mMainView.findViewById(R.id.tv_time);

        image = mMainView.findViewById(R.id.image);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                act.getPVC().push(new PicFullImageView(act, CZNetUtils.svr_host + bean.getYaJinTiao()));
            }
        });
        Glide.with(act).load(CZNetUtils.svr_host + bean.getYaJinTiao()).into(image);
//        Glide.with(act).load("http://ranliankeji.com/api/v1/psi/services/resource/d/20210205/1612488234232.jpg").into(image);

//        Glide.with(act).load("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=3011097188,3806330712&fm=26&gp=0.jpg").into(image);
        tv_ordernum.setText(bean.getDingDanHao());
        tv_name.setText(bean.getKeHuMingCheng());
        tv_tel.setText(bean.getMobile());
        tv_guige.setText(bean.getGuiGeMingCheng());
        tv_yajin.setText(bean.getYaJinJinE()+"元");
        tv_ss_yajin.setText(bean.getShiShouYaJin()+"元");
        tv_type.setText(bean.getLeiXingMingCheng());
        tv_gongyingzhan.setText(bean.getGongYingZhan());
        tv_shouqvren.setText(bean.getShouQuRen());
        tv_time.setText(bean.getShouQuRiQi());

    }

}
