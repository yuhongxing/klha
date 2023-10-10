package com.czsy.ui;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.czsy.android.R;
import com.czsy.bean.PageInfoBean;

import mylib.app.BackFrontTask;
import mylib.app.BackTask;
import mylib.utils.Utils;

/**
 * 标题栏，tab，可加载的内容
 * 黄色的总计tip条
 */
abstract public class AbsPVTabList extends AbsPVBase
        implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, BackFrontTask {
    public AbsPVTabList(MainActivity a) {
        super(a);
    }

    protected TextView tv_title, tv_right; // tv_tip,
    protected ViewPager view_pager;
    protected SwipeRefreshLayout swiperefresh;
    protected TabLayout tab_layout;
//
//    protected PageInfoBean page_info;
//    protected TextView load_more_view;
    protected int _totalItemCount;// 总数量；
    protected int _lastVisibleItem;// 最后一个可见的item；
    protected boolean is_loading = false;

    protected int getMainViewRes() {
        return R.layout.abs_pv_tab_list;
    }


//    protected int getNextPage() {
//        return page_info == null ? 1 : page_info.curPage + 1;
//    }


    @Override
    protected void createMainView(Context ctx) {
        mMainView = View.inflate(act, getMainViewRes(), null);
        tv_right = mMainView.findViewById(R.id.tv_right);
        view_pager = mMainView.findViewById(R.id.view_pager);
        tab_layout = mMainView.findViewById(R.id.tab_layout);
        tv_title = mMainView.findViewById(R.id.tv_title);
        //tv_tip = mMainView.findViewById(R.id.tv_tip);
        mMainView.findViewById(R.id.iv_back).setOnClickListener(this);
        swiperefresh = mMainView.findViewById(R.id.swiperefresh);
        swiperefresh.setOnRefreshListener(this);

        view_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    swiperefresh.setEnabled(true);
                } else {
                    swiperefresh.setEnabled(false);
                }
            }
        });

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.iv_back == id) {
            act.getPVC().pop();
        }
    }

    protected String err_msg;
    protected int loading_idx = -1;

    protected void loadData() {
        loading_idx = view_pager.getCurrentItem();
        swiperefresh.setRefreshing(true);
        err_msg = null;
        BackTask.post(this);
    }

    @Override
    public void runFront() {
//        is_loading = false;
        swiperefresh.setRefreshing(false);
//        if (!TextUtils.isEmpty(err_msg)) {
//            Utils.toastLONG(err_msg);
//            return;
//        }
//        if (load_more_view == null) {
//            load_more_view = new TextView(act);
//            load_more_view.setPadding(10, 10, 10, 45);
//            load_more_view.setText("加载更多");
//            load_more_view.setGravity(Gravity.CENTER);
//            load_more_view.setTextSize(18);
//        }


    }

    @Override
    public void onRefresh() {
        loadData();
    }
}
