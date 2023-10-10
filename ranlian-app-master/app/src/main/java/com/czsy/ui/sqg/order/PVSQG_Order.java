package com.czsy.ui.sqg.order;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.czsy.CZBackTask;
import com.czsy.CZNetUtils;
import com.czsy.Constant;
import com.czsy.android.R;
import com.czsy.bean.CommonOrderBean;
import com.czsy.bean.OrderDetailBean;
import com.czsy.bean.PageInfoBean;
import com.czsy.push.PushBean;
import com.czsy.ui.AbsPVBase;
import com.czsy.ui.MainActivity;

import com.czsy.ui.PVOrderDetail;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.zip.Inflater;

import mylib.app.BackFrontTask;
import mylib.app.BackTask;
import mylib.app.MyLog;
import mylib.ui.list.AbstractAdapter;
import mylib.utils.Utils;

/**
 * 接单退单
 */
public class PVSQG_Order extends AbsPVBase
        implements View.OnClickListener, AdapterView.OnItemClickListener {
    Set<String> fav_orders;

    public PVSQG_Order(MainActivity a) {
        super(a);
        fav_orders = FavOrderManager.getFavOrderIds();
        if (fav_orders == null) {
            fav_orders = new HashSet<>();
        }
    }

    int position;//page索引,从0开始
    TextView tv_tab1, tv_tab2, tv_tab3;
    ViewPager view_pager;
    TextView tv_num, tv_content;
    List<CommonOrderBean> to_do_order = new LinkedList<>();
    List<CommonOrderBean> done_order = new LinkedList<>();
    List<CommonOrderBean> fav_order = new LinkedList<>();
    boolean isShowTuidan = true;


    ListView listView;
    private TextView load_more_view;
    int _totalItemCount;// 总数量；
    int _lastVisibleItem;// 最后一个可见的item；
    int numNo, numKo;
    boolean first;//第一次进入，滑动到“已完成”自动请求

    protected PageInfoBean page_info_no, page_info_ok;

    protected int getNextPage() {
        if (position == 0) {
            return page_info_no == null ? 1 : page_info_no.curPage + 1;

        } else if (position == 1) {
            return page_info_ok == null ? 1 : page_info_ok.curPage + 1;
        }
        return 1;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void updateTab(int tab) {
        if (tab == 0) {
            Log.d("xing", "tab:" + tab + "; " + "to_do_order.size():" + to_do_order.size());
            tv_num.setText(numNo + "");
            tv_content.setText("未完成共计");
            tab_adapter[0].notifyDataSetChanged();

            tv_tab1.setTextColor(act.getResources().getColor(R.color.bg_blue));
            tv_tab2.setTextColor(Color.WHITE);
            tv_tab3.setTextColor(Color.WHITE);

            tv_tab1.setBackground(act.getDrawable(R.drawable.bg_yuanjiao_top));
            tv_tab2.setBackground(null);
            tv_tab3.setBackground(null);

        } else if (tab == 1) {
            Log.d("xing", "tab:" + tab + "; " + "done_order.size():" + done_order.size());
            tv_num.setText(numKo + "");
            tv_content.setText("已完成共计");
            tab_adapter[1].notifyDataSetChanged();

            tv_tab2.setTextColor(act.getResources().getColor(R.color.bg_blue));

            tv_tab1.setTextColor(Color.WHITE);
            tv_tab3.setTextColor(Color.WHITE);

            tv_tab1.setBackground(null);
            tv_tab2.setBackground(act.getDrawable(R.drawable.bg_yuanjiao_top));
            tv_tab3.setBackground(null);

        } else if (tab == 2) {

            tv_num.setText(fav_order.size() + "");
            tv_content.setText("收藏共计");

            tv_tab3.setTextColor(act.getResources().getColor(R.color.bg_blue));

            tv_tab1.setTextColor(Color.WHITE);
            tv_tab2.setTextColor(Color.WHITE);

            tv_tab1.setBackground(null);
            tv_tab2.setBackground(null);
            tv_tab3.setBackground(act.getDrawable(R.drawable.bg_yuanjiao_top));
        }
    }

    @Override
    protected void createMainView(Context ctx) {
        mMainView = View.inflate(act, R.layout.pv_sqg_order1, null);

        tv_num = mMainView.findViewById(R.id.tv_num);
        tv_content = mMainView.findViewById(R.id.tv_content);
        view_pager = mMainView.findViewById(R.id.view_pager);
        tv_tab1 = mMainView.findViewById(R.id.tv_tab1);
        tv_tab2 = mMainView.findViewById(R.id.tv_tab2);
        tv_tab3 = mMainView.findViewById(R.id.tv_tab3);
        tv_tab1.setOnClickListener(this);
        tv_tab2.setOnClickListener(this);
        tv_tab3.setOnClickListener(this);

        mMainView.findViewById(R.id.iv_back).setOnClickListener(this);

        view_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onPageSelected(int p) {
                Log.d("xing", "position : " + p);
                position = p;
                updateTab(position);

                if (first && position == 1) {//第一次进入，滑动到“已完成”自动请求
                    if (page_info_ok == null) {
                        page_info_ok = new PageInfoBean();
                        page_info_ok.curPage = 0;
                        page_info_ok.pageSize = 10;
                    }
                    page_info_ok.curPage = 0;
                    loadData(6, null);
                    first = false;
                }


                Log.d("data", "to_do_order.size() = " + to_do_order.size() + " ;  done_order.size() = " + done_order.size());

            }

            @Override
            public void onPageScrollStateChanged(int state) {
//                SwipeRefreshLayout swiperefresh = view_pager.getChildAt(position).findViewById(R.id.swiperefresh1);
//
//                if (state == ViewPager.SCROLL_STATE_IDLE) {
//                    swiperefresh.setEnabled(true);
//                    Log.d("swiperefresh", "swiperefresh.setEnabled(true) ；state = " + state);
//                } else {
//                    swiperefresh.setEnabled(false);
//                    Log.d("swiperefresh", "swiperefresh.setEnabled(false) ；state = " + state);
//                }
            }
        });
        view_pager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public Object instantiateItem(ViewGroup container, final int position) {

                View view = View.inflate(act, R.layout.activity_test, null);

                final SwipeRefreshLayout swiperefresh = view.findViewById(R.id.swiperefresh1);
                swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        if (position == 2) {
                            swiperefresh.setEnabled(false);
                        } else {
                            swiperefresh.setEnabled(true);
                        }
                        if (position != 2) {

                            swiperefresh.setRefreshing(true);
                            Log.d("swiperefresh", "onRefresh() ；isRefreshing = " + swiperefresh.isRefreshing());

                            if (position == 0) {
                                if (to_do_order != null) {
                                    to_do_order.removeAll(to_do_order);
                                }


                                if (page_info_no == null) {
                                    page_info_no = new PageInfoBean();
                                    page_info_no.curPage = 0;
                                    page_info_no.pageSize = 10;
                                }
                                page_info_no.curPage = 0;

                            } else if (position == 1) {
                                if (done_order != null) {
                                    done_order.removeAll(done_order);
                                }
                                if (page_info_ok == null) {
                                    page_info_ok = new PageInfoBean();
                                    page_info_ok.curPage = 0;
                                    page_info_ok.pageSize = 10;
                                }
                                page_info_ok.curPage = 0;
                            }


                            int type = 2;//  status参数类型 2：已分配；6：已完成
                            if (position == 0) {
                                type = 2;
                            } else if (position == 1) {
                                type = 6;
                            }
                            loadData(type, swiperefresh);
                        }

                    }
                });

                listView = view.findViewById(R.id.list_view);
                listView.setOnScrollListener(new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {

                        if (_totalItemCount == _lastVisibleItem && scrollState == SCROLL_STATE_IDLE) {
                            Log.d("xing", "滑到了底部");

                            if (position != 2) {

                                if (!is_loading1) {
                                    listView.removeFooterView(load_more_view);

                                    int type = 2;//  status参数类型 2：已分配；6：已完成
                                    if (position == 0) {
                                        type = 2;
                                    } else if (position == 1) {
                                        type = 6;
                                    }
                                    loadData(type, null);
                                }
                            }
                        }
                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                        _lastVisibleItem = firstVisibleItem + visibleItemCount;
                        _totalItemCount = totalItemCount;

                    }
                });
                listView.setOnItemClickListener(PVSQG_Order.this);
                listView.setAdapter(tab_adapter[position]);

                container.addView(view);

                return view;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
        });
    }

    @Override
    public void onAttach(boolean firstShow) {
        super.onAttach(firstShow);
        if (firstShow) {
            loadData(2, null);
            first = true;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Utils.toastSHORT("??" + i);
        int idx = view_pager.getCurrentItem();
        final CommonOrderBean b = tab_adapter[idx].getItem(i);
        if (b.status != null) {

            if (Constant.status_done == (b.status)) {//(b.status != CommonOrderBean.STATUS_TODO) {
                act.getPVC().push(new PVOrderDetail(act, b, false));
            } else {
                act.getPVC().push(new PVOrderDetail(act, b, false));
            }
        }

    }

    public void onTuiDan(CommonOrderBean b) {
        tab_adapter[0].getData().remove(b);
        tab_adapter[0].notifyDataSetChanged();

        tab_adapter[2].getData().remove(b);
        tab_adapter[2].notifyDataSetChanged();

        to_do_order.remove(b);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            updateTab(position);
        }
    }

    private class OrderAdpater extends AbstractAdapter<CommonOrderBean> {
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (view == null) {
                view = View.inflate(viewGroup.getContext(), R.layout.dingqi_order_item1, null);
                viewHolder = new ViewHolder(view);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            CommonOrderBean bean = getItem(i);
            bindItem(i, bean, viewHolder);
            return viewHolder.root;

        }
    }

    void favoClick(CommonOrderBean order, TextView favo) {
        if (!fav_orders.contains(order.dingDanHao)) {
            FavOrderManager.saveFavOrder(order.dingDanHao);
            addFavo(order, true);
            favo.setText("取消收藏");
            Utils.toastSHORT("收藏成功");
        } else {
            FavOrderManager.removeFavOrder(order.dingDanHao);
            addFavo(order, false);
            favo.setText("收藏");
            Utils.toastSHORT("取消收藏");
        }
    }

    private void addFavo(CommonOrderBean b, boolean add) {
        List<CommonOrderBean> list = tab_adapter[2].getData();
        if (add) {
            fav_orders.add(b.dingDanHao);
            list.add(b);
            tab_adapter[2].notifyDataSetChanged();
        } else {
            fav_orders.remove(b.dingDanHao);
            list.remove(b);
        }
        tab_adapter[2].notifyDataSetChanged();
    }

    private AbstractAdapter<CommonOrderBean> tab_adapter[] = new AbstractAdapter[]{
            new OrderAdpater(), new OrderAdpater(), new OrderAdpater()
    };

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.iv_back == id) {
            act.getPVC().pop();
        } else if (R.id.tv_tab1 == id) {
            if (view_pager.getCurrentItem() != 0) {
                view_pager.setCurrentItem(0);
            }
        } else if (R.id.tv_tab2 == id) {
            if (view_pager.getCurrentItem() != 1) {
                view_pager.setCurrentItem(1);
            }
        } else if (R.id.tv_tab3 == id) {
            if (view_pager.getCurrentItem() != 2) {
                view_pager.setCurrentItem(2);
            }
        }
    }

    private boolean is_loading1 = false;

    /**
     * @param type         status参数类型 2：已分配；6：已完成
     * @param swiperefresh 刷新控件
     */
    private void loadData(final int type, final SwipeRefreshLayout swiperefresh) {
        act.showProgress();
        if (is_loading1) {
            return;
        }
        is_loading1 = true;

        BackTask.post(new BackFrontTask() {
            @Override
            public void runFront() {
                act.hideProgress();
                is_loading1 = false;

                if (swiperefresh != null) {
                    Log.d("swiperefresh", "onRefresh() ；isRefreshing = " + swiperefresh.isRefreshing());

                    swiperefresh.setRefreshing(false);
                    Log.d("swiperefresh", "onRefresh() ；isRefreshing = " + swiperefresh.isRefreshing());

                }

                if (load_more_view == null) {
                    load_more_view = new TextView(act);
                    load_more_view.setPadding(10, 10, 10, 45);
                    load_more_view.setText("加载更多");
                    load_more_view.setGravity(Gravity.CENTER);
                    load_more_view.setTextSize(18);
                }

                tab_adapter[0].setData(to_do_order);
                tab_adapter[1].setData(done_order);
                tab_adapter[2].setData(fav_order);

                if (position == 0) {

                    if (page_info_no.hasNextPage) {
                        listView.addFooterView(load_more_view, null, false);
                    } else {
                        listView.removeFooterView(load_more_view);
                    }

                    tv_num.setText(numNo + "");
                    tv_content.setText("未完成共计");
                } else if (position == 1) {

                    if (page_info_ok.hasNextPage) {
                        listView.addFooterView(load_more_view, null, false);
                    } else {
                        listView.removeFooterView(load_more_view);
                    }

                    tv_num.setText(numKo + "");
                    tv_content.setText("已完成共计");
                }
            }

            @Override
            public void runBack() {

                /**
                 * 保修单，安检单都是这2种
                 * YI_FEN_PEI(new Byte("2"), "已分配"),
                 *     FINISH(new Byte("4"), "完成"),
                 *
                 * 购瓶单，商品单，退瓶单，折旧单是这三种
                 * YI_FEN_PEI(new Byte("2"), "已分配"),
                 *     DAI_FU_KUAN(new Byte("4"), "待付款"),
                 *     FINISH(new Byte("6"), "完成"),
                 */
                try {

                    JSONObject j = new JSONObject();
                    j.put("status", type);
                    j.put("dingDanLeiXing", 1);
                    if (type == 6) {
                        j.put("wanChengRiQiStart", getDate());
                    }

                    JSONObject ret = CZNetUtils.postCZHttp(
                            "dingDan/chaXun/10/" + getNextPage(),
                            j.toString());


                    if (position == 0) {
                        page_info_no = Constant.gson.fromJson(
                                ret.getJSONObject("pageVO").toString(), PageInfoBean.class);

                        numNo = page_info_no.totalRows;
                    } else if (position == 1) {
                        page_info_ok = Constant.gson.fromJson(
                                ret.getJSONObject("pageVO").toString(), PageInfoBean.class);
                        numKo = page_info_ok.totalRows;
                    }


                    JSONArray result = ret.getJSONArray("result");
                    for (int k = 0; k < result.length(); k++) {
                        CommonOrderBean b = Constant.gson.fromJson(result.getJSONObject(k).toString()
                                , CommonOrderBean.class);

                        if (type == 2) {
                            to_do_order.add(b);
                        } else if (type == 6) {
                            done_order.add(b);
                        }


                    }
                    if (type == 2) {
                        fav_order.clear();
                        for (CommonOrderBean b : to_do_order) {
                            if (fav_orders.contains(b.dingDanHao)) {
                                fav_order.add(b);
                            }
                        }
                    }
                    if (result.length() == 0) {
                        Utils.toastSHORT("没有更多了");
                    }
                    return;

                } catch (Exception e) {
                    MyLog.LOGE(e);
                }
            }
        });
    }


    private String getDate() {
        long time = System.currentTimeMillis();//long now = android.os.SystemClock.uptimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date d1 = new Date(time);
        String t1 = format.format(d1);
        return t1 + " 00:00:00";
    }

    private void bindItem(int i, final CommonOrderBean bean, ViewHolder viewHolder) {
        viewHolder.tv_name.setText(bean.keHuMing + "  (编号" + bean.keHuBianHao + ")");
//        viewHolder.tv_phone.setText("联系电话：" + bean.keHuDianHua);
        viewHolder.tv_phone.setText("");
        Utils.setTextSpan("联系电话：", bean.keHuDianHua, "", viewHolder.tv_phone, new Utils.TextClickListener() {
            @Override
            public void onClick() {
                Utils.toCall(bean.keHuDianHua);
            }
        });
        if (bean.status == 6 || bean.status == 4) {
            viewHolder.btn_reject_order.setVisibility(View.GONE);
        } else {

            viewHolder.btn_reject_order.setVisibility(View.VISIBLE);
        }
        viewHolder.tv_address.setText("地址：" + bean.diZhi);
        if (bean.beiZhu == null) {
            viewHolder.tv_beizhu.setText("备注：无");
        } else {
            viewHolder.tv_beizhu.setText("备注：" + bean.beiZhu);
        }
        viewHolder.tv_time.setText(bean.chuangJianRiQi);
        viewHolder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bean.status != null) {

                    if (Constant.status_done == (bean.status)) {//(b.status != CommonOrderBean.STATUS_TODO) {
                        act.getPVC().push(new PVOrderDetail(act, bean, false));
                    } else {
                        act.getPVC().push(new PVOrderDetail(act, bean, false));
                    }
                }
            }
        });
        viewHolder.btn_reject_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context ctx = view.getContext();
                final Dialog d = new Dialog(ctx, android.R.style.Theme_Translucent_NoTitleBar);
                d.setContentView(R.layout.dialog_input);
                final EditText et_input = d.findViewById(R.id.et_input);
                d.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String reason = et_input.getText().toString();
                        if (TextUtils.isEmpty(reason)) {
                            Utils.toastLONG("请输入退单原因");
                            return;
                        }
//                        final MainActivity act = (MainActivity) pv_sqg.act;
                        act.showProgress();
                        BackTask.post(new CZBackTask(act) {
                            @Override
                            protected void parseResult(JSONObject jdata) throws Exception {
                                int code = jdata.optInt("code", 0);
                                if (code != 200) {
                                    throw new CZNetUtils.CZNetErr(code, jdata);
                                }
                            }

                            @Override
                            protected String getInputParam() throws Exception {
                                JSONObject j = new JSONObject();
                                j.put("sid", bean.dingDanHao);
                                j.put("yuanYin", reason);
                                return j.toString();
                            }

                            @Override
                            protected String getURL() {
                                return "dingDan/tuiDan";
                            }

                            @Override
                            protected void runFront2() {
                                d.dismiss();
                                Utils.toastLONG("退单成功！");
                                onTuiDan(bean);
                            }
                        });
                    }
                });
                d.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        d.dismiss();
                    }
                });
                d.show();
            }
        });
    }

    public static class ViewHolder {
        public View root;
        public TextView tv_name, tv_time, tv_phone, tv_address, tv_beizhu;
        public Button btn_reject_order;
        public ConstraintLayout container;

        public ViewHolder(View root) {
            this.root = root;
            container = root.findViewById(R.id.container);
            tv_name = root.findViewById(R.id.tv_name);
            tv_time = root.findViewById(R.id.tv_time);
            tv_phone = root.findViewById(R.id.tv_phone);
            tv_address = root.findViewById(R.id.tv_address);
            tv_beizhu = root.findViewById(R.id.tv_beizhu);
            btn_reject_order = root.findViewById(R.id.btn_reject_order);
            root.setTag(this);
        }

    }
}
