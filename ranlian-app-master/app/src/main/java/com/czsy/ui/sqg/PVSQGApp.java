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
import com.czsy.bean.*;
import com.czsy.ui.*;
import com.czsy.ui.changzhan.PVCZUpdateGangping;
import com.czsy.ui.sqg.huishou.PVZheJiuDan2;
import com.czsy.ui.sqg.order.PVClientOrder_Client;
import com.czsy.ui.sqg.order.PVClientOrder_Empty;
import com.czsy.ui.sqg.order.PVSQG_Order;
import com.czsy.ui.xunjianka.XunjiankaBind;
import com.czsy.ui.yunshuyuan.CompletedKt;

import mylib.app.BackTask;
import mylib.app.LocationService;
import mylib.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

/**
 * 配送员--应用主页
 */
public class PVSQGApp extends AbsPVBase implements View.OnClickListener {

    public PVSQGApp(MainActivity a) {
        super(a);
    }

    public static WeakReference<PVSQGApp> thiz;
    private TextView tv_num_jieDanTuiDan, tv_num_huiShouDan, tv_num_tuiPing, tv_num_keHuChongZhi, tv_num_shangPin;
    View[] view_dots;
    ViewPager view_pager;
    private JSONObject stat_json;

    private class ViewHolder {
        ViewHolder(View r) {
            root = (ViewGroup) r;
            root.setTag(this);
            tv_titles = new TextView[3];
            tv_values = new TextView[3];
//            for (int i = 0; i < 3; i++) {
//                View v = root.getChildAt(i);
//                tv_titles[i] = v.findViewById(R.id.tv_title);
//                tv_values[i] = v.findViewById(R.id.tv_value);
//            }
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
            r.setOnClickListener(PVSQGApp.this);
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
        mMainView = View.inflate(act, R.layout.pv_sqg_app, null);
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
        mMainView.findViewById(R.id.zhejiu_container).setOnClickListener(this);
        //mMainView.findViewById(R.id.peisong_huizong_container).setOnClickListener(this);
        mMainView.findViewById(R.id.tuiping_container).setOnClickListener(this);
        mMainView.findViewById(R.id.weixiu_container).setOnClickListener(this);
        mMainView.findViewById(R.id.anjian_container).setOnClickListener(this);
        mMainView.findViewById(R.id.his_order_search_container).setOnClickListener(this);
        mMainView.findViewById(R.id.ruku_container).setOnClickListener(this);
        mMainView.findViewById(R.id.chuku_container).setOnClickListener(this);
        mMainView.findViewById(R.id.shangpin_container).setOnClickListener(this);
        mMainView.findViewById(R.id.client_container).setOnClickListener(this);
        mMainView.findViewById(R.id.mykucun_container).setOnClickListener(this);
        mMainView.findViewById(R.id.chaxun_container).setOnClickListener(this);
        mMainView.findViewById(R.id.yajinGuanli_container).setOnClickListener(this);
        mMainView.findViewById(R.id.xunjianka_container).setOnClickListener(this);

        // mMainView.findViewById(R.id.upload_task_container).setOnClickListener(this);

        tv_num_jieDanTuiDan = mMainView.findViewById(R.id.tv_num_jieDanTuiDan);
        tv_num_huiShouDan = mMainView.findViewById(R.id.tv_num_huiShouDan);
        tv_num_tuiPing = mMainView.findViewById(R.id.tv_num_tuiPing);
        tv_num_keHuChongZhi = mMainView.findViewById(R.id.tv_num_keHuChongZhi);
        tv_num_shangPin = mMainView.findViewById(R.id.tv_num_shangPin);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        onClick(id);
    }

    public void onClick(int id) {
        if (R.id.ruku_container == id || R.id.chuku_container == id) { // 钢瓶领取 || 钢瓶回库
            final boolean is_huiku = R.id.ruku_container != id;
            act.getPVC().push(new AbsPVScanEmpty(act, null, false) {
                @Override
                protected void createMainView(Context ctx) {
                    super.createMainView(ctx);
                    btn_next.setVisibility(View.GONE);
                    btn_clear.setVisibility(View.GONE);
                    tv_title.setText(is_huiku ? R.string.tip_chuku : R.string.tip_ruku);
                    btn_no_empty.setText(is_huiku ? R.string.tip_chuku : R.string.tip_ruku);
                    btn_no_empty.setTextColor(Color.WHITE);
                    btn_no_empty.setBackgroundResource(R.drawable.sel_btn_blue);
                }

                @Override
                public String checkGanPing(GangPingBean gp) {
                    LoginUser lu = LoginUser.get();
                    if (is_huiku) {
                        /**
                         * (1)钢瓶不在供应站不可入库（提示：钢瓶不在库存）
                         * (2)钢瓶超期、报废不可入库（提示：此瓶超期未检、此瓶已报废）
                         * (3)钢瓶为空瓶不可入库（提示：此钢瓶是空瓶）
                         */
                        if (gp.peiSongYuanId != lu.id
                                || gp.zuiHouWeiZhi != Constant.zhihouweizhi_psy) {
                            return "气瓶不在库存";
                        } else if (gp.status != GangPingBean.gp_status_using) {
                            return "气瓶状态: " + gp.getStatusName();
                        } else if (!gp.isEmpty()) {
                            Utils.toastLONG("此气瓶是重瓶");
                        }
                    } else {
                        // (1)钢瓶不在配送员不可回库（提示：钢瓶不在库存）
                        if (gp.gongYingZhanId != lu.orgId
                                || gp.zuiHouWeiZhi != Constant.zhihouweizhi_md) {
                            return "气瓶不在库存";
                        } else if (gp.status != GangPingBean.gp_status_using) {
                            return "气瓶状态: " + gp.getStatusName();
                        } else if (gp.isEmpty()) {
                            return "此气瓶是空瓶";
                        }
                    }
                    return super.checkGanPing(gp);
                }

                @Override
                protected void onClickScanHeavy() {
                    // throw new RuntimeException();
                }

                private void doIt() {
                    act.showProgress();
                    BackTask.post(new CZBackTask(act) {
                        @Override
                        protected void parseResult(JSONObject jdata) throws Exception {
                            // TODO:
                        }

                        @Override
                        protected String getInputParam() throws Exception {
                            LoginUser lu = LoginUser.get();
                            JSONObject j = new JSONObject();
                            double[] pos = LocationService.myGP;
                            if (pos != null) {
                                j.put("lat", pos[1]);
                                j.put("lng", pos[0]);
                            }
                            j.put("changZhanId", lu.orgId);
                            JSONArray ja = new JSONArray();
                            for (CommonOrderBean.GangPingInfo info : gp_id_list) {
                                ja.put(info.gp_bean.id);
                            }
                            for (CommonOrderBean.GangPingInfo info : nfc_gp_list) {
                                ja.put(info.gp_bean.id);
                            }
                            {
                                j.put("idList", ja);
                            }
                            return j.toString();
                        }

                        @Override
                        protected String getURL() {
                            return is_huiku ? "gangPing/peiSongGangPingHuiKu"
                                    : "gangPing/peiSongGangPingChuKu";
                        }

                        @Override
                        protected void runFront2() {
                            Utils.toast(R.string.tip_op_ok);
                            List<CommonOrderBean.GangPingInfo> list = new LinkedList<>();
                            list.addAll(gp_id_list);
                            list.addAll(nfc_gp_list);
                            List<String> s = Constant.gpHuiZongInfoNew(list, false);
                            act.getPVC().replace(new CompletedKt(act, is_huiku ? "气瓶回库" : "气瓶领取", s));
                        }
                    });
                }

                @Override
                protected void onClickNoEmpty() {
                    final int size = gp_id_list.size() + nfc_gp_list.size();
                    if (size == 0) {
                        Utils.toastLONG("没有输入气瓶");
                        return;
                    }
                    // 出库或入库
                    getIdFromServer(new ReturnRunable<Boolean>() {
                        @Override
                        public void run() {
                            if (!ret) {
                                return;
                            }

                            AlertDialog.Builder ab = new AlertDialog.Builder(act);
                            ab.setTitle(R.string.app_name);
                            ab.setMessage("输入了" + size + "个气瓶，确定提交？");
                            ab.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    doIt();
                                }
                            });
                            ab.setNegativeButton(android.R.string.cancel, null);
                            ab.show();
                        }
                    });
                }
            });
        } else if (R.id.tv_client_order == id) { // 代客下单
            act.getPVC().push(new PVClientOrder_Client(act, new CommonOrderBean()));
        } else if (R.id.tv_get_order == id) { // 结单派单
            act.getPVC().push(new PVSQG_Order(act));
        } else if (R.id.root_view == id) { // 每日总结
            if (stat_json != null) {
                HuiZongAct pv = new HuiZongAct(act);
                act.getPVC().push(pv);

            }
        } /*else if (R.id.peisong_huizong_container == id) { // 汇总
            act.getPVC().push(new PVPeiSongHuiZong(act));
        } */ else if (R.id.zhejiu_container == id) { // 折旧
            if (true) {
                act.getPVC().push(new PVCommonOrderList(act, false, Constant.dingdan_type_zhejiu));
            } else { // 直接新建
                act.getPVC().push(new PVClientOrder_Client(act, null) {
                    @Override
                    public void onClickOk() {
                        if (!client_pv.validClient()) {
                            return;
                        }
                        ClientBean cb = client_pv.client_bean;
                        CommonOrderBean b = new CommonOrderBean();
                        b.fromClientBean(cb);
                        b.dingDanLeiXing = Constant.dingdan_type_zhejiu;
                        act.getPVC().replace(new PVZheJiuDan2(act, b));
                    }
                });
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
        } else if (R.id.weixiu_container == id) { // 维修
            if (true) {
                act.getPVC().push(new PVWeiXiuList(act, false));
            } else {
                act.getPVC().push(new PVClientOrder_Client(act, null) {
                    @Override
                    public void onClickOk() {
                        if (!client_pv.validClient()) {
                            return;
                        }
                        act.getPVC().pop();
                        ClientBean cb = client_pv.client_bean;
                        WeiXiuBean b = new WeiXiuBean();
                        b.keHuId = cb.id;
                        b.diZhi = cb.diZhi;
                        b.keHuMing = cb.userName;
                        PVWeiXiuList.doWeiXiuDan(act, b, true,null);
                    }
                });
            }
        } else if (R.id.shangpin_container == id) { // 商品单下单
            act.getPVC().push(new PVCommonOrderList(act, false,
                    Constant.dingdan_type_shangpin)); //new PVTuiPingList(act, false));
        } else if (R.id.tuiping_container == id) { // 退瓶单
            if (true) {
                act.getPVC().push(new PVCommonOrderList(act, false, Constant.dingdan_type_tuipin)); //new PVTuiPingList(act, false));
            } else {
                // 直接新建
                act.getPVC().push(new PVClientOrder_Client(act, null) {
                    @Override
                    public void onClickOk() {
                        if (!client_pv.validClient()) {
                            return;
                        }
                        ClientBean cb = client_pv.client_bean;
                        CommonOrderBean b = new CommonOrderBean();
                        b.fromClientBean(cb);
                        b.dingDanLeiXing = Constant.dingdan_type_tuipin;
                        act.getPVC().replace(new PVClientOrder_Empty(act, b));
                    }
                });
            }
        } else if (R.id.his_order_search_container == id) { // 历史单查询
            act.getPVC().push(new PVOrderSearch(act));
        } else if (R.id.client_container == id) { // 充值
            final PVClientOrder_Client client = new PVClientOrder_Client(act, null) {
                @Override
                public void onClickOk() {
                    if (!client_pv.validClient()) {
                        return;
                    }
                    final ClientBean cb = client_pv.client_bean;
                    act.getPVC().replace(new PVCharge(act, cb));

                }
            };
            act.getPVC().push(client);
        } else if (R.id.mykucun_container == id) { // 我的库存
            act.getPVC().push(new PVMyKunCun2(act));

        } else if (R.id.chaxun_container == id) {//查询客户信息

            act.getPVC().push(new PvChaXun(act, null));
        } else if (R.id.yajinGuanli_container == id) {//押金管理
            act.getPVC().push(new PVYajinGuanli(act, false));
        } else if (R.id.xunjianka_container == id) {//巡检卡

            act.getPVC().push(new XunjiankaBind(act));
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
                        last_stat_time = System.currentTimeMillis();
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
                                tv_num_jieDanTuiDan.setText(GANG_PING_DING_DAN.equals("0") ? " 0 " : GANG_PING_DING_DAN.length() == 1 ? " " + GANG_PING_DING_DAN + " " : GANG_PING_DING_DAN);
                                tv_num_huiShouDan.setText(ZHE_JIU_DING_DAN.equals("0") ? " 0 " : ZHE_JIU_DING_DAN.length() == 1 ? " " + ZHE_JIU_DING_DAN + " " : ZHE_JIU_DING_DAN);
                                tv_num_tuiPing.setText(TUI_PING_DING_DAN.equals("0") ? " 0 " : TUI_PING_DING_DAN.length() == 1 ? " " + TUI_PING_DING_DAN + " " : TUI_PING_DING_DAN);
                                tv_num_keHuChongZhi.setText(CHU_ZHI_KA_CHONG_ZHI_DING_DAN.equals("0") ? " 0 " : CHU_ZHI_KA_CHONG_ZHI_DING_DAN.length() == 1 ? " " + CHU_ZHI_KA_CHONG_ZHI_DING_DAN + " " : CHU_ZHI_KA_CHONG_ZHI_DING_DAN);
                                tv_num_shangPin.setText(SHANG_PIN_DING_DAN.equals("0") ? " 0 " : SHANG_PIN_DING_DAN.length() == 1 ? " " + SHANG_PIN_DING_DAN + " " : SHANG_PIN_DING_DAN);
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
        long now = System.currentTimeMillis();
//        if (firstShow || (now < last_stat_time) || (now - last_stat_time) > Global._1k * 5) {
        reloadHuiZong();
//        }
        thiz = new WeakReference<>(this);
    }

    @Override
    public void onDetach(boolean lastShow) {
        super.onDetach(lastShow);
        if (lastShow) {
            thiz = null;
        }
    }

    private long last_stat_time = 0l;

}
