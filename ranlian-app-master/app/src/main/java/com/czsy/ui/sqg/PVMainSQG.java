package com.czsy.ui.sqg;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.czsy.CZEvents;
import com.czsy.android.R;
import com.czsy.ui.AbsPVBase;
import com.czsy.ui.MainActivity;
import com.czsy.ui.PVPushMsg;
import com.czsy.ui.PVSetting;

import mylib.app.BaseActivity;
import mylib.app.EventHandler;

public class PVMainSQG extends AbsPVBase implements View.OnClickListener {

    private CZEvents evt = new CZEvents() {
        @Override
        public void onPushDataChanged() {

            pv_pushmsg.reload();

        }
    };

    private BaseActivity.EventTypes getEvents() {
        return new BaseActivity.EventTypes(new Enum[]{

                CZEvents.Event.onPushDataChanged,
        }, evt);
    }

    public PVMainSQG(MainActivity a) {
        super(a);
    }

    PVPushMsg pv_pushmsg;
    TextView tv_tabs[];
    ImageView ic_tabs[];
    int icNo[] = {R.mipmap.ic_msg_no,R.mipmap.ic_app_no,R.mipmap.ic_setting_no};
    int icYes[] = {R.mipmap.ic_msg_yes,R.mipmap.ic_app_yes,R.mipmap.ic_setting_yes};
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
        mMainView = View.inflate(act, R.layout.pv_main_sqg, null);
//        PVSQGApp pv = new PVSQGApp(act);
        PVSQGApp1 pv = new PVSQGApp1(act);
        pv_pushmsg = new PVPushMsg(act, pv,
                (TextView) mMainView.findViewById(R.id.tv_msg_cnt));
        pv_tabs = new AbsPVBase[]{
                pv_pushmsg,
                pv,
                new PVSetting(act),
        };

        pv_container = mMainView.findViewById(R.id.pv_container);
        tv_tabs = new TextView[]{
                mMainView.findViewById(R.id.btn_msg),
                mMainView.findViewById(R.id.btn_app),
                mMainView.findViewById(R.id.btn_setting),
        };
        ic_tabs = new ImageView[]{
                mMainView.findViewById(R.id.image_msg),
                mMainView.findViewById(R.id.image_app),
                mMainView.findViewById(R.id.image_setting),
        };
        rl_tabs = new RelativeLayout[]{
                mMainView.findViewById(R.id.rl_msg),
                mMainView.findViewById(R.id.rl_app),
                mMainView.findViewById(R.id.rl_setting),
        };
        int tab = act.getIntent().getIntExtra("tab", 1);
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
        if (lastShow) {
            BaseActivity.EventTypes et = getEvents();
            EventHandler.removeEventHandler(et.mEvts, et.mHandler);

            for (AbsPVBase pv : pv_tabs) {
                if (pv != null) {
                    pv.onDetach(true);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.rl_app == id) {
            updateTab(1);
        } else if (R.id.rl_msg == id) {
            updateTab(0);
        } else if (R.id.rl_setting == id) {
            updateTab(2);
        }
    }

    @Override
    public void onAttach(boolean firstShow) {
        super.onAttach(firstShow);
        if (cur_tab >= 0 && pv_tabs[cur_tab] != null) {
            pv_tabs[cur_tab].onAttach(firstShow);
        }
        if (firstShow) {
            LocationManager locationManager
                    = (LocationManager) act.getSystemService(Context.LOCATION_SERVICE);
            boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (!gps) {
                Log.e("xing", "!gps：" + !gps);
                mMainView.post(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder ab = new AlertDialog.Builder(act);
                        ab.setTitle(R.string.app_name).setMessage(R.string.tip_use_gps);
                        ab.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                act.startActivity(intent);

                            }
                        });
                        ab.show();
                    }
                });

            }

            pv_pushmsg.getView(act);
            pv_pushmsg.reload();
            BaseActivity.EventTypes et = getEvents();
            EventHandler.addEventHandler(et.mEvts, et.mHandler);
        }
    }

}
