package com.czsy.ui.sqg;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.czsy.CZBackTask;
import com.czsy.Constant;
import com.czsy.ReturnRunable;
import com.czsy.TheApp;
import com.czsy.android.R;
import com.czsy.bean.ClientBean;
import com.czsy.bean.CommonOrderBean;
import com.czsy.bean.GangPingBean;
import com.czsy.bean.LoginUser;
import com.czsy.bean.WeiXiuBean;
import com.czsy.bean.YaJinBean;
import com.czsy.ui.AbsPVBase;
import com.czsy.ui.AbsPVScanEmpty;
import com.czsy.ui.MainActivity;
import com.czsy.ui.PVMyKunCun2;
import com.czsy.ui.sqg.huishou.PVZheJiuDan2;
import com.czsy.ui.sqg.order.PVClientOrder_Client;
import com.czsy.ui.sqg.order.PVClientOrder_Empty;
import com.czsy.ui.sqg.order.PVSQG_Order;
import com.czsy.ui.xunjianka.XunjiankaBind;
import com.czsy.ui.yunshuyuan.CompletedKt;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

import mylib.app.BackTask;
import mylib.app.LocationService;
import mylib.utils.Utils;

/**
 * 配送员--应用主页
 */
public class PVSQGApp1 extends AbsPVBase implements View.OnClickListener {

    public PVSQGApp1(MainActivity a) {
        super(a);
    }

    public static WeakReference<PVSQGApp1> thiz;
    View[] view_dots;
    ViewPager view_pager;
    private TextView tv_num_jieDanTuiDan, tv_num_anjian, tv_num_shangPin, tv_num_qita;
    private String huishou, tuiping, baoxiu;
    private JSONObject stat_json;

    private class ViewHolder {
        ViewHolder(View r) {
            root = (ViewGroup) r;
            root.setTag(this);
            tv_titles = new TextView[3];
            tv_values = new TextView[3];
            tv_titles[0] = root.getChildAt(0).findViewById(R.id.tv_title1);
            tv_values[0] = root.getChildAt(0).findViewById(R.id.tv_value1);

            tv_titles[1] = root.getChildAt(1).findViewById(R.id.tv_title2);
            tv_values[1] = root.getChildAt(1).findViewById(R.id.tv_value2);

            tv_titles[2] = root.getChildAt(2).findViewById(R.id.tv_title3);
            tv_values[2] = root.getChildAt(2).findViewById(R.id.tv_value3);

        }

        final ViewGroup root;
        final TextView tv_titles[], tv_values[];

        void bind(int pos) {
            /**
             * {"peiSongGangPing":0,"huiShouGangPing":0,"dingDanShouRuJinE":2120.0,"dingDanShouRuJinEBefore14":2120.0,
             * "dingDanShouRuJinEAfter14":0.0,"yaJinJinE":45.0,"yaJinJinEBefore14":45.0,"yaJinJinEAfter14":0.0,"qianKuanJinE":0.0}
             */
            if (0 == pos) {
                tv_values[0].setText("订单收入");
                tv_values[1].setText("押金收入");
                tv_values[2].setText("总欠款");

                if (stat_json == null) {
                    tv_titles[0].setText(null);
                    tv_titles[1].setText(null);
                    tv_titles[2].setText(null);
                } else {
                    double d1 = stat_json.optDouble("dingDanShouRuBefore14", 0);
                    double d2 = stat_json.optDouble("dingDanShouRuAfter14", 0);
                    tv_titles[0].setText(String.format("%.2f", d1 + d2));

                    d1 = stat_json.optDouble("shouYaJinBefore14", 0);
                    d2 = stat_json.optDouble("shouYaJinAfter14", 0);
                    tv_titles[1].setText(String.format("%.2f", d1 + d2));

                    d1 = stat_json.optDouble("qianKuanBefore14", 0);
                    d2 = stat_json.optDouble("qianKuanAfter14", 0);
                    tv_titles[2].setText(String.format("%.2f", d1 + d2));
                }

            } else {
                tv_values[0].setText("气瓶总数");
                tv_values[1].setText("配送重瓶");
                tv_values[2].setText("回收空瓶");

                int zp_cnt = stat_json == null ? 0 : stat_json.optInt("peiSongGangPing", 0);
                int kp_cnt = stat_json == null ? 0 : stat_json.optInt("huiShouGangPing", 0);
                tv_titles[0].setText(stat_json == null ? "" : String.valueOf(zp_cnt + kp_cnt));
                tv_titles[1].setText(stat_json == null ? "" : String.valueOf(zp_cnt));
                tv_titles[2].setText(stat_json == null ? "" : String.valueOf(kp_cnt));
            }
        }
    }

    PagerAdapter adapter = new PagerAdapter() {
        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ViewGroup r = (ViewGroup) View.inflate(container.getContext(), R.layout.sqg_app_page, null);
            container.addView(r);
            ViewHolder vh = new ViewHolder(r);
            vh.bind(position);
            r.setOnClickListener(PVSQGApp1.this);
            return vh;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ViewHolder vh = (ViewHolder) object;
            container.removeView(vh.root);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            ViewHolder vh = (ViewHolder) object;
            return view == vh.root;
        }
    };

    @Override
    protected void createMainView(Context ctx) {
        mMainView = View.inflate(act, R.layout.pv_sqg_app1, null);
        TextView tv_title = mMainView.findViewById(R.id.tv_title);
        LoginUser lu = LoginUser.get();
        tv_title.setText(lu.userName);
        view_pager = mMainView.findViewById(R.id.view_pager);
        view_pager.setAdapter(adapter);

        view_dots = new View[]{
                mMainView.findViewById(R.id.view_dot1),
                mMainView.findViewById(R.id.view_dot2),
        };
        view_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < view_dots.length; i++) {
                    view_dots[i].setBackgroundResource(
                            i == position ? R.drawable.dot_selected : R.drawable.dot_unselected);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mMainView.findViewById(R.id.tv_get_order).setOnClickListener(this);
        mMainView.findViewById(R.id.tv_client_order).setOnClickListener(this);

        mMainView.findViewById(R.id.anjian_container).setOnClickListener(this);
        mMainView.findViewById(R.id.shangpin_container).setOnClickListener(this);
        mMainView.findViewById(R.id.xunjianka_container).setOnClickListener(this);
        mMainView.findViewById(R.id.chaxun_container).setOnClickListener(this);
        mMainView.findViewById(R.id.yajinGuanli_container).setOnClickListener(this);
        mMainView.findViewById(R.id.qita_container).setOnClickListener(this);
        tv_num_jieDanTuiDan = mMainView.findViewById(R.id.tv_num_jieDanTuiDan);
        tv_num_anjian = mMainView.findViewById(R.id.tv_num_anjian);
        tv_num_shangPin = mMainView.findViewById(R.id.tv_num_shangPin);
        tv_num_qita = mMainView.findViewById(R.id.tv_num_qita);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        onClick(id);
    }

    public void onClick(int id) {
        if (R.id.tv_client_order == id) { // 代客下单
            act.getPVC().push(new PVClientOrder_Client(act, new CommonOrderBean()));
        } else if (R.id.tv_get_order == id) { // 结单派单
            act.getPVC().push(new PVSQG_Order(act));
        } else if (R.id.root_view == id) { // 每日总结
            if (stat_json != null) {
                HuiZongAct pv = new HuiZongAct(act);
                act.getPVC().push(pv);

            }
        } else if (R.id.anjian_container == id) { // 安检

            if (true) {
                act.getPVC().push(new PVAnJianList(act, false));
            } else {
                // 直接新建
                act.getPVC().push(new PVClientOrder_Client(act, null) {
                    @Override
                    public void onClickOk() {
                        if (!client_pv.validClient()) {
                            return;
                        }
                        ClientBean cb = client_pv.client_bean;
                        act.getPVC().replace(new PVAnJianDan(act, cb, null) {
                            protected void onAnJianOk() {
                                act.getPVC().pop();
                                Utils.toast(R.string.tip_op_ok);
                            }

                            protected void onAnJianSkip() {
                            }
                        });
                    }
                });
            }
        } else if (R.id.shangpin_container == id) { // 商品单下单
            act.getPVC().push(new PVCommonOrderList(act, false,
                    Constant.dingdan_type_shangpin)); //new PVTuiPingList(act, false));
        } else if (R.id.chaxun_container == id) {//查询客户信息

            act.getPVC().push(new PvChaXun(act, null));
        } else if (R.id.yajinGuanli_container == id) {//押金管理
            act.getPVC().push(new PVYajinGuanli(act, false));
        } else if (R.id.xunjianka_container == id) {//巡检卡

            act.getPVC().push(new XunjiankaBind(act));
        } else if (R.id.qita_container == id) {//其他

            act.getPVC().push(new PvQita(act, tuiping, baoxiu, huishou));
        }
    }

    private boolean is_loading_huizong = false;

    public void reloadHuiZong() {
        if (is_loading_huizong) {
            return;
        }
        is_loading_huizong = true;
        BackTask.post(
                new CZBackTask(null) {
                    @Override
                    protected void parseResult(final JSONObject jdata) throws Exception {
                        final JSONObject j = jdata.getJSONObject("result");

                        YaJinBean.yajin_map.clear();
                        JSONArray ja = j.getJSONArray("yaJinList");
                        for (int i = 0; i < ja.length(); i++) {
                            YaJinBean b = Constant.gson.fromJson(ja.getJSONObject(i).toString(), YaJinBean.class);
                            YaJinBean.yajin_map.put(b.guiGe, b);
                        }
                        j.remove("yaJinList");
                        TheApp.sHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                stat_json = j;
                                adapter.notifyDataSetChanged();
                            }
                        });

                    }

                    @Override
                    protected String getInputParam() throws Exception {
                        return "{}";
                    }

                    @Override
                    protected String getURL() {
                        return "dingDan/peiSongYuanHuiZong";
                    }

                    @Override
                    protected void runFront2() {
                    }

                    @Override
                    public void runFront() {
                        super.runFront();
                        is_loading_huizong = false;

                        try {
                            if (stat_json != null) {
                                JSONObject data = stat_json.optJSONObject("ordersCounter");
                                //判断数量是不是“0”，如果是就前后加个空格，如果是个位数就前后加空格。把页面撑起来，纯粹为了美观。
                                String GANG_PING_DING_DAN = data.getString("GANG_PING_DING_DAN");
                                String ZHE_JIU_DING_DAN = data.getString("ZHE_JIU_DING_DAN");
                                String TUI_PING_DING_DAN = data.getString("TUI_PING_DING_DAN");
                                String CHU_ZHI_KA_CHONG_ZHI_DING_DAN = data.getString("CHU_ZHI_KA_CHONG_ZHI_DING_DAN");
                                String SHANG_PIN_DING_DAN = data.getString("SHANG_PIN_DING_DAN");
                                String AN_JIAN_DING_DAN = data.getString("AN_JIAN_DING_DAN");
                                String BAO_XIU_DING_DAN = data.getString("BAO_XIU_DING_DAN");
                                tv_num_jieDanTuiDan.setText(GANG_PING_DING_DAN.equals("0") ? " 0 " : GANG_PING_DING_DAN.length() == 1 ? " " + GANG_PING_DING_DAN + " " : GANG_PING_DING_DAN);
                                tv_num_anjian.setText(AN_JIAN_DING_DAN.equals("0") ? " 0 " : AN_JIAN_DING_DAN.length() == 1 ? " " + AN_JIAN_DING_DAN + " " : AN_JIAN_DING_DAN);
                                tv_num_shangPin.setText(SHANG_PIN_DING_DAN.equals("0") ? " 0 " : SHANG_PIN_DING_DAN.length() == 1 ? " " + SHANG_PIN_DING_DAN + " " : SHANG_PIN_DING_DAN);
                                //tv_num_keHuChongZhi.setText(CHU_ZHI_KA_CHONG_ZHI_DING_DAN.equals("0") ? " 0 " : CHU_ZHI_KA_CHONG_ZHI_DING_DAN.length() == 1 ? " " + CHU_ZHI_KA_CHONG_ZHI_DING_DAN + " " : CHU_ZHI_KA_CHONG_ZHI_DING_DAN);
                                huishou = ZHE_JIU_DING_DAN.equals("0") ? " 0 " : ZHE_JIU_DING_DAN.length() == 1 ? " " + ZHE_JIU_DING_DAN + " " : ZHE_JIU_DING_DAN;
                                tuiping = TUI_PING_DING_DAN.equals("0") ? " 0 " : TUI_PING_DING_DAN.length() == 1 ? " " + TUI_PING_DING_DAN + " " : TUI_PING_DING_DAN;
                                baoxiu = BAO_XIU_DING_DAN.equals("0") ? " 0 " : BAO_XIU_DING_DAN.length() == 1 ? " " + BAO_XIU_DING_DAN + " " : BAO_XIU_DING_DAN;
                                String qitanum = Integer.parseInt(huishou.trim())+Integer.parseInt(tuiping.trim())+Integer.parseInt(baoxiu.trim())+"";
                                tv_num_qita.setText(qitanum.equals("0") ? " 0 " : qitanum.length() == 1 ? " " + qitanum + " " : qitanum);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    }

    @Override
    public void onAttach(boolean firstShow) {
        super.onAttach(firstShow);
        reloadHuiZong();
        thiz = new WeakReference<>(this);
    }

    @Override
    public void onDetach(boolean lastShow) {
        super.onDetach(lastShow);
        if (lastShow) {
            thiz = null;
        }
    }

}
