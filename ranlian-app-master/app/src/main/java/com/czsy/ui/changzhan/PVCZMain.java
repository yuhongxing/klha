package com.czsy.ui.changzhan;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.czsy.android.R;
import com.czsy.ui.AbsPVBase;
import com.czsy.ui.MainActivity;
import com.czsy.ui.PVPushMsg;
import com.czsy.ui.PVSetting;
import com.czsy.ui.sqg.PVSQGApp;

import mylib.app.BaseActivity;
import mylib.app.EventHandler;

public class PVCZMain extends AbsPVBase implements View.OnClickListener {

    public PVCZMain(MainActivity a) {
        super(a);
    }

    TextView tv_tabs[];
    ImageView ic_tabs[];
    int icNo[] = {R.mipmap.ic_app_no, R.mipmap.ic_setting_no};
    int icYes[] = {R.mipmap.ic_app_yes, R.mipmap.ic_setting_yes};
    RelativeLayout rl_tabs[];

    AbsPVBase pv_tabs[];
    int cur_tab = -1;
    ViewGroup pv_container;

    void updateTab(int tab) {
        if (cur_tab == tab || tab < 0 || tab >= pv_tabs.length) {
            return;
        }
        if (cur_tab >= 0) {
            if (pv_tabs[cur_tab] != null) {
                pv_tabs[cur_tab].onDetach(true);
            }

            tv_tabs[cur_tab].setTextColor(Color.parseColor("#FFBFBFBF"));
            ic_tabs[cur_tab].setImageResource(icNo[cur_tab]);
        }
        pv_container.removeAllViews();
        if (pv_tabs[tab] != null) {
            pv_tabs[tab].onAttach(true);
            pv_container.addView(pv_tabs[tab].getView(act));

        }
        cur_tab = tab;
        tv_tabs[tab].setTextColor(Color.BLACK);
        ic_tabs[tab].setImageResource(icYes[tab]);
    }


    @Override
    protected void createMainView(Context ctx) {
        mMainView = View.inflate(act, R.layout.pv_main_cz, null);
        PVChangZhanMain pv = new PVChangZhanMain(act);
        pv_tabs = new AbsPVBase[]{
                pv,
                new PVSetting(act, 0),
        };

        pv_container = mMainView.findViewById(R.id.pv_container);
        tv_tabs = new TextView[]{
                mMainView.findViewById(R.id.btn_app),
                mMainView.findViewById(R.id.btn_setting),
        };
        ic_tabs = new ImageView[]{
                mMainView.findViewById(R.id.image_app),
                mMainView.findViewById(R.id.image_setting),
        };
        rl_tabs = new RelativeLayout[]{
                mMainView.findViewById(R.id.rl_app),
                mMainView.findViewById(R.id.rl_setting),
        };
        int tab = act.getIntent().getIntExtra("tab", 0);
        updateTab(tab);
        for (View v : rl_tabs) {
            v.setOnClickListener(this);
        }

    }


    @Override
    public void onDetach(boolean lastShow) {
        super.onDetach(lastShow);
        if (cur_tab >= 0 && pv_tabs[cur_tab] != null) {
            pv_tabs[cur_tab].onDetach(lastShow);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.rl_setting == id) {
            updateTab(1);
        } else if (R.id.rl_app == id) {
            updateTab(0);
        }
    }

}

