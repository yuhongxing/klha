package com.czsy.ui.sqg;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.czsy.CZNetUtils;
import com.czsy.Constant;
import com.czsy.TheApp;
import com.czsy.android.R;
import com.czsy.bean.AnJianOrderBean;
import com.czsy.bean.ClientBean;
import com.czsy.bean.CommonOrderBean;
import com.czsy.bean.PageInfoBean;
import com.czsy.bean.TuiPinDanBean;
import com.czsy.bean.WeiXiuBean;
import com.czsy.other.DatePickerDialog;
import com.czsy.ui.AbsPVOrderList;
import com.czsy.ui.AbsPVTabList;
import com.czsy.ui.MainActivity;
import com.czsy.ui.PVOrderDetail;
import com.czsy.ui.PVSimpleText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import mylib.app.MyLog;
import mylib.ui.list.AbstractAdapter;
import mylib.utils.Utils;

/**
 * 各种订单查询
 */
public class PVOrderSearch extends AbsPVTabList {
    public PVOrderSearch(MainActivity a) {
        super(a);
    }

    private PageInfoBean[] pageInfo = {new PageInfoBean(), new PageInfoBean(), new PageInfoBean(), new PageInfoBean(), new PageInfoBean(), new PageInfoBean()};
    private boolean[] isFirstPage = {false, true, true, true, true, true};
    private List[] listsBean = {new LinkedList<CommonOrderBean>(), new LinkedList<CommonOrderBean>(), new LinkedList<CommonOrderBean>(),
            new LinkedList<WeiXiuBean>(), new LinkedList<AnJianOrderBean>(), new LinkedList<TuiPinDanBean>()};

    protected int getNextPage() {
        return (pageInfo[loading_idx].curPage + 1);
    }

    @Override
    protected void createMainView(Context ctx) {
        super.createMainView(ctx);
        Log.d("xiaofa", "createMainView()");
        tv_title.setText(R.string.title_his_order_search);
        tab_layout.setupWithViewPager(view_pager);
        view_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (isFirstPage[position]) {

                    loadData();
                }
                isFirstPage[position] = false;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
//                if (state == ViewPager.SCROLL_STATE_IDLE) {
//                    swiperefresh.setEnabled(true);
//
//                } else {
//                    swiperefresh.setEnabled(false);
//                }

            }
        });
        view_pager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return tab_info.length;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                boolean create = tab_info[position].root == null;
                if (create) {
                    tab_info[position].root = View.inflate(act, R.layout.search_order_tab, null);
                    tab_info[position].list_view = tab_info[position].root.findViewById(R.id.list_view);
                    ListView lv = tab_info[position].list_view;
                    lv.setOnItemClickListener(tab_info[position]);
                    View head = tab_info[position].root.findViewById(R.id.top_container);
                    tab_info[position].tv_time = head.findViewById(R.id.tv_time);
                    tab_info[position].tv_time1 = head.findViewById(R.id.tv_time1);
                    tab_info[position].tv_time.setOnClickListener(tab_info[position]);
                    tab_info[position].tv_time1.setOnClickListener(tab_info[position]);

                    tab_info[position].updateTitle();
                    lv.setAdapter(tab_info[position]);
                    lv.setOnScrollListener(new AbsListView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(AbsListView view, int i) {
                            if (i == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                                int first = view.getFirstVisiblePosition();
                                View firstView = view.getChildAt(first);
                                if (first == 0 && (firstView == null || firstView.getTop() == 0)) {
                                    swiperefresh.setEnabled(true);
                                } else {
                                    swiperefresh.setEnabled(false);
                                }
                            }

                            if (_totalItemCount == _lastVisibleItem && i == SCROLL_STATE_IDLE) {
                                Log.d("xing", "滑到了底部");
                                if (!is_loading) {
                                    loadData();
                                }
                            }
                        }

                        @Override
                        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                            _lastVisibleItem = firstVisibleItem + visibleItemCount;
                            _totalItemCount = totalItemCount;
                        }

                    });
                }
                container.addView(tab_info[position].root);
                return tab_info[position].root;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }


            @Override
            public CharSequence getPageTitle(int position) {
                return (tab_info[position].title);
            }
        });
    }

    @Override
    protected void loadData() {
        if (is_loading) {
            return;
        }
        is_loading = true;
        super.loadData();
    }

    @Override
    public void onAttach(boolean firstShow) {
        super.onAttach(firstShow);
        loadData();
        Log.d("xiaofa", "onAttach--loadData()");
    }

    abstract class TabInfo<T> extends AbstractAdapter<T> implements
            View.OnClickListener, AdapterView.OnItemClickListener {
        final String title;
        final String url;
        long search_time = System.currentTimeMillis() - (1000 * 60 * 60 * 24 + 1000);
        long search_time2 = System.currentTimeMillis();

        ListView list_view;
        View root;
        TextView tv_time, tv_time1;

        protected void updateTitle() {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(search_time);
            int y = c.get(Calendar.YEAR);
            int m = c.get(Calendar.MONTH);
            int d = c.get(Calendar.DAY_OF_MONTH);
            tv_time.setText(String.format("开始日期: %4d-%02d-%02d", y, m + 1, d));

            c = Calendar.getInstance();
            c.setTimeInMillis(search_time2);
            y = c.get(Calendar.YEAR);
            m = c.get(Calendar.MONTH);
            d = c.get(Calendar.DAY_OF_MONTH);
            tv_time1.setText(String.format("结束日期: %4d-%02d-%02d", y, m + 1, d));
        }

        @Override
        public void onClick(final View v) {
            if (v != tv_time && v != tv_time1) {
                return;
            }
            DatePickerDialog d = new DatePickerDialog(act);
            d.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int i, int i1, int i2) {
                    Calendar c = Calendar.getInstance();
                    c.set(Calendar.YEAR, i);
                    c.set(Calendar.MONTH, i1);
                    c.set(Calendar.DAY_OF_MONTH, i2);
                    c.set(Calendar.HOUR_OF_DAY, 0);
                    c.set(Calendar.MINUTE, 0);
                    c.set(Calendar.SECOND, 0);
                    long t = c.getTimeInMillis();
                    if (v == tv_time) {
                        if (t > search_time2) {
                            Utils.toastLONG("起始时间必须在结束时间之前");
                            return;
                        }
                        search_time = t;
                    } else {
                        if (t < search_time) {
                            Utils.toastLONG("结束时间必须在起始时间之后");
                            return;
                        }
                        search_time2 = t;
                    }

                    if (listsBean[loading_idx] != null) {
                        listsBean[loading_idx].clear();
                    }

                    pageInfo[loading_idx].curPage = 0;
                    pageInfo[loading_idx].pageSize = 10;

                    updateTitle();
                    loadData();
                    list_view.setSelection(0);
                }
            });
            d.show();
        }

        @Override
        public void onItemClick(AdapterView<?> var1, View var2, int var3, long var4) {
            T data = getItem(var3);
            if (data instanceof AnJianOrderBean) {
                AnJianOrderBean ab = (AnJianOrderBean) data;
                ClientBean c = ab.toClientBean();
                PVAnJianDan pv = new PVAnJianDan(act, c, ab);
                act.getPVC().push(pv);
            } else if (data instanceof CommonOrderBean) {
                // 送气单需要查询瓶子详情
                CommonOrderBean cob = (CommonOrderBean) data;
                act.getPVC().push(new PVOrderDetail(act, cob, false));
            } else {
                PVSimpleText pv = new PVSimpleText(act, data.toString());
                act.getPVC().push(pv);
            }


        }

        TabInfo(String title, String url) {
            this.title = (title);
            this.url = url;
        }

        TabInfo(int title, String url) {
            this(TheApp.sInst.getString(title), url);
        }

        abstract void initSearchParam(JSONObject j) throws Exception;

        void runFront() {
            if (!TextUtils.isEmpty(err_msg)) {
                Utils.toastLONG(err_msg);
                return;
            }
            setData(listsBean[loading_idx]);
        }

        void runInBack() {
            try {
                JSONObject in = new JSONObject();
                initSearchParam(in);

                Log.d("NetWork", "getNextPage-->" + getNextPage());
                JSONObject j = CZNetUtils.postCZHttp(url + getNextPage(), in.toString());
                JSONArray jr = j.optJSONArray("result");
                pageInfo[loading_idx] = Constant.gson.fromJson(
                        j.getJSONObject("pageVO").toString(), PageInfoBean.class);

                ParameterizedType type = (ParameterizedType) this.getClass()
                        .getGenericSuperclass();
                Class clazz = (Class<T>) type.getActualTypeArguments()[0];
                for (int i = 0; i < (jr == null ? 0 : jr.length()); i++) {
                    T data = (T) Constant.gson.fromJson(jr.getJSONObject(i).toString(), clazz);

                    listsBean[loading_idx].add(data);

                }
                if (jr.length() == 0) {
                    Utils.toastSHORT("没有更多了");
                }
                return;

            } catch (Exception e) {
                MyLog.LOGE(e);
                if (TextUtils.isEmpty(err_msg)) {
                    err_msg = act.getString(R.string.tip_common_err);
                }
            }
        }

        abstract public View getView(int i, View view, ViewGroup viewGroup);

    }

    @Override
    public void runFront() {
        Log.d("xiaofa", "runFront()");
        is_loading = false;
        swiperefresh.setRefreshing(false);
        if (!TextUtils.isEmpty(err_msg)) {
            Utils.toastLONG(err_msg);
            return;
        }
        if (loading_idx < 0 || loading_idx >= tab_info.length) {
            return;
        }
        TabInfo ti = tab_info[loading_idx];
        ti.runFront();
    }

    @Override
    public void runBack() {
        Log.d("xiaofa", "runBack()---loading_idx:" + loading_idx);
        if (loading_idx < 0 || loading_idx >= tab_info.length) {
            return;
        }
        TabInfo ti = tab_info[loading_idx];
        ti.runInBack();
    }

    @Override
    public void onRefresh() {

        if (listsBean[loading_idx] != null) {
            listsBean[loading_idx].clear();
        }

        pageInfo[loading_idx].curPage = 0;
        pageInfo[loading_idx].pageSize = 10;

        super.onRefresh();
    }

    final private TabInfo[] tab_info = new TabInfo[]{
            new TabInfo<CommonOrderBean>("订气单", "dingDan/chaXunAnZhuo/10/") {

                @Override
                void initSearchParam(JSONObject in) throws Exception {
                    Pair<String, String> p = Constant.getTimeDuration(search_time, search_time2);
                    in.put("startDate", p.first);
                    in.put("endDate", p.second + " 23:59:59");
                    in.put("status", 6); //Constant.status_done);
                    in.put("dingDanLeiXing", Constant.dingdan_type_gouqi);
                }

                @Override
                public View getView(int i, View view, ViewGroup viewGroup) {
                    Log.d("xiaofa", "订气单 java");

                    CommonOrderBean o = getItem(i);

                    if (view == null) {
                        view = View.inflate(viewGroup.getContext(), R.layout.common_text_item1, null);
                        new AbsPVOrderList.ViewHolder(view);
                    }
                    AbsPVOrderList.ViewHolder vh = (AbsPVOrderList.ViewHolder) view.getTag();
                    bindItem(i, o, vh);
                    return vh.root;
                }
            },
            new TabInfo<WeiXiuBean>("维修单", "baoXiuTouSu/queryBaoXiu/10/") {

                @Override
                void initSearchParam(JSONObject in) throws Exception {
                    Pair<String, String> p = Constant.getTimeDuration(search_time, search_time2);
                    in.put("startDate", p.first);
                    in.put("endDate", p.second + " 23:59:59");
                    // 报修单完结状态4
                    in.put("status", 4);
                }

                @Override
                public View getView(int i, View view, ViewGroup viewGroup) {
                    WeiXiuBean b = getItem(i);
                    TextView tv = (TextView) view;
                    if (tv == null) {
                        tv = (TextView) View.inflate(viewGroup.getContext(), R.layout.common_text_item, null);
                    }
                    tv.setText(b.toString());
                    return tv;
                }


            },
            new TabInfo<AnJianOrderBean>("安检单", "anJian/queryAnZhuo/10/") {
                @Override
                void initSearchParam(JSONObject in) throws Exception {
                    Pair<String, String> p = Constant.getTimeDuration(search_time, search_time2);
                    in.put("startDate", p.first);
                    in.put("endDate", p.second + " 23:59:59");
                    in.put("status", 4);
                }

                @Override
                public View getView(int i, View view, ViewGroup viewGroup) {
                    AnJianOrderBean b = getItem(i);
                    TextView tv = (TextView) view;
                    if (tv == null) {
                        tv = (TextView) View.inflate(viewGroup.getContext(), R.layout.common_text_item, null);
                    }
                    tv.setText(b.toString());
                    return tv;
                }
            },
            new TabInfo<TuiPinDanBean>("退瓶单", "dingDan/chaXunAnZhuo/10/") {

                @Override
                void initSearchParam(JSONObject in) throws Exception {
                    Pair<String, String> p = Constant.getTimeDuration(search_time, search_time2);
                    in.put("startDate", p.first);
                    in.put("endDate", p.second + " 23:59:59");
                    in.put("status", 6);
                    in.put("dingDanLeiXing", Constant.dingdan_type_tuipin);
                }

                @Override
                public View getView(int i, View view, ViewGroup viewGroup) {
                    TuiPinDanBean b = getItem(i);

                    if (view == null) {
                        view = View.inflate(viewGroup.getContext(), R.layout.common_text_item1, null);
                        new AbsPVOrderList.ViewHolder(view);
                    }
                    AbsPVOrderList.ViewHolder vh = (AbsPVOrderList.ViewHolder) view.getTag();
                    bindItem(i, b, vh);
                    return vh.root;
                }
            },
            new TabInfo<CommonOrderBean>("商品单", "dingDan/chaXunAnZhuo/10/") {

                @Override
                void initSearchParam(JSONObject in) throws Exception {
                    Pair<String, String> p = Constant.getTimeDuration(search_time, search_time2);
                    in.put("startDate", p.first);
                    in.put("endDate", p.second + " 23:59:59");
                    in.put("status", 6);
                    in.put("dingDanLeiXing", Constant.dingdan_type_shangpin);
                }

                @Override
                public View getView(int i, View view, ViewGroup viewGroup) {
                    CommonOrderBean b = getItem(i);
                    if (view == null) {
                        view = View.inflate(viewGroup.getContext(), R.layout.common_text_item1, null);
                        new AbsPVOrderList.ViewHolder(view);
                    }
                    AbsPVOrderList.ViewHolder vh = (AbsPVOrderList.ViewHolder) view.getTag();
                    bindItem(i, b, vh);
                    return vh.root;
                }
            },
            new TabInfo<CommonOrderBean>("回收单", "dingDan/chaXunAnZhuo/10/") {

                @Override
                void initSearchParam(JSONObject in) throws Exception {
                    Pair<String, String> p = Constant.getTimeDuration(search_time, search_time2);
                    in.put("startDate", p.first);
                    in.put("endDate", p.second + " 23:59:59");
                    in.put("status", 6);
                    in.put("dingDanLeiXing", Constant.dingdan_type_zhejiu);
                }

                @Override
                public View getView(int i, View view, ViewGroup viewGroup) {
                    CommonOrderBean b = getItem(i);
                    if (view == null) {
                        view = View.inflate(viewGroup.getContext(), R.layout.common_text_item1, null);
                        new AbsPVOrderList.ViewHolder(view);
                    }
                    AbsPVOrderList.ViewHolder vh = (AbsPVOrderList.ViewHolder) view.getTag();
                    bindItem(i, b, vh);
                    return vh.root;
                }
            },
    };


    private void bindItem(int i, CommonOrderBean o, AbsPVOrderList.ViewHolder vh) {

        vh.tv_ordernum.setText(o.dingDanLeiXingName + "：" + o.dingDanHao);
        vh.tv_order_time.setText("下单时间：" + o.chuangJianRiQi);
        vh.tv_orderOk_time.setVisibility(o.status == 6 ? View.VISIBLE : View.GONE);
        vh.tv_orderOk_time.setText("完成时间：" + o.wanChengRiQi);
        vh.zhifufangshi.setVisibility(o.status == 6 ? View.VISIBLE : View.GONE);
        vh.zhifufangshi.setText("支付方式：" + o.zhiFuFangShiName);
        vh.tv_kehu_num.setText("客户编号：" + o.keHuBianHao);
        vh.tv_name.setText("客        户：" + o.keHuMing);
        vh.tv_tel.setText("电        话：" + o.keHuDianHua);
        String louceng = o.louCeng == null ? "" : "; 楼层：" + o.louCeng;
        vh.tv_address.setText("地        址：" + o.diZhi + louceng);
        vh.tv_gyz.setText("供  应  站：" + o.gongYingZhan);
        vh.tv_order_money.setText("订单金额：" + Math.abs(o.shiShouJinE));
        vh.tv_laiyuan.setText("来        源：" + o.laiYuanName);
        vh.tv_beizhu.setVisibility(o.beiZhu != null ? View.VISIBLE : View.GONE);
        vh.tv_beizhu.setText("备        注：" + o.beiZhu);

        vh.gridLayout_tuiping.setVisibility(o.dingDanLeiXing == Constant.dingdan_type_tuipin ? View.VISIBLE : View.GONE);
        vh.tv_shiTuiYaJin.setText(Math.abs(o.yaJinJinE) + "");
        vh.tv_tuiQi.setText(o.yuQi + "");
        vh.tv_gangpingNum.setText(o.zheJiuGangPing + "");

        String good = "", jiage = "", num = "", money = "";
        if (o.details != null && !o.details.isEmpty()) {
            vh.gridLayout_goods.setVisibility(o.status == Constant.status_done && o.dingDanLeiXing == 2 ? View.VISIBLE : View.GONE);
            for (CommonOrderBean.DetailInfo detailInfo : o.details) {
                good = good + detailInfo.shangPingMingCheng + "\n";
                jiage = jiage + detailInfo.price + "\n";
                num = num + detailInfo.count + "\n";
                money = money + detailInfo.zongJinE + "\n";
            }
        }
        vh.tv_good.setText(good);
        vh.tv_jiage.setText(jiage);
        vh.tv_num.setText(num);
        vh.tv_money.setText(money);


    }

}
