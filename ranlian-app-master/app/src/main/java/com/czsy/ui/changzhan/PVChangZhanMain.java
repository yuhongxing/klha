package com.czsy.ui.changzhan;

import android.content.Context;
import android.view.View;

import com.czsy.android.R;
import com.czsy.ui.AbsPVBase;
import com.czsy.ui.MainActivity;
import com.czsy.ui.changzhan.chongzhuangcheck.PVCZCheck;
import com.czsy.ui.xunjian.PVXunjianBangdingKt;
import com.czsy.ui.xunjian.PVXunjianJianceKt;

// 场站main
public class PVChangZhanMain extends AbsPVBase implements View.OnClickListener {
    public PVChangZhanMain(MainActivity a) {
        super(a);
    }

    @Override
    protected void createMainView(Context ctx) {
        mMainView = View.inflate(ctx, R.layout.pv_changzhan_main, null);

        for (int id : new int[]{
                R.id.btn_chadang,
                R.id.btn_jiandang,
                R.id.btn_shandang,
                R.id.btn_diaoru,
                R.id.btn_diaochu,
                R.id.btn_songjian,
                R.id.btn_baofei,
                R.id.btn_genghuan,
                R.id.btn_chongzhuangqian,
                R.id.btn_xiugai


        }) {
            mMainView.findViewById(id).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.btn_jiandang == id) {
            act.getPVC().push(new PVCZJianDang(act));
        } else if (R.id.btn_chadang == id) {
//            act.getPVC().push(new PVCZChaDang(act));
            act.getPVC().push(new PVCZJianYan(act));
        } else if (R.id.btn_shandang == id) {
            act.getPVC().push(new PVCZShanDang(act));
        } else if (R.id.btn_songjian == id) {
            act.getPVC().push(new PVCZSongJian(act, true));
        } else if (R.id.btn_baofei == id) {
            act.getPVC().push(new PVCZBaoFei(act));
        } else if (R.id.btn_diaochu == id) {
            act.getPVC().push(new PVCZDiaoChu(act, true));
        } else if (R.id.btn_diaoru == id) {
            act.getPVC().push(new PVCZDiaoRu(act, false));
        } else if (R.id.btn_genghuan == id) {
            act.getPVC().push(new PVCZGengHuan(act));
        } else if (R.id.btn_chongzhuangqian == id) {
            act.getPVC().push(new PVCZCheck(act, true));
        } else if (R.id.btn_xiugai == id) {
            act.getPVC().push(new PVCZUpdateGangping(act));
        }
    }
}
