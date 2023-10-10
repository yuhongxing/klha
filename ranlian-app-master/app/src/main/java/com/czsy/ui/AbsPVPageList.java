package com.czsy.ui;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.czsy.android.R;
import com.czsy.bean.PageInfoBean;
import com.czsy.other.DatePickerDialog;

import java.util.Calendar;
import java.util.Random;

import mylib.app.BackFrontTask;
import mylib.app.BackTask;
import mylib.ui.list.AbstractAdapter;
import mylib.utils.Utils;

/**
 * 可翻页（业务示例-->运输员-我的运输单）
 */
abstract public class AbsPVPageList<T> extends AbsPVList {
    public AbsPVPageList(MainActivity a, boolean has_date) {
        super(a, has_date);
    }

    protected PageInfoBean page_info;
    private TextView load_more_view;

    int _totalItemCount;// 总数量；
    int _lastVisibleItem;// 最后一个可见的item；

    protected int getNextPage() {
        return page_info == null ? 1 : page_info.curPage + 1;
    }

    final protected AbstractAdapter<T> adapter = new AbstractAdapter<T>() {
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            return AbsPVPageList.this.getView(i, view, viewGroup);
        }

        @Override
        public int getCount() {
            return super.getCount();
        }
    };

    protected abstract View getView(int i, View view, ViewGroup viewGroup);

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void createMainView(Context ctx) {
        super.createMainView(ctx);
        list_view.setAdapter(adapter);
        list_view.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
//      totalItemCount == lastVisibleItem相等时说明滑到了底部
                if (_totalItemCount == _lastVisibleItem && scrollState == SCROLL_STATE_IDLE) {
                    Log.d("xing", "滑到了底部");
                    if (!is_loading) {
                        list_view.removeFooterView(load_more_view);
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
        loadData();
    }


    protected String err_msg;

    abstract protected void runFrontOk();

    @Override
    public void runFront() {
        is_loading = false;
        swiperefresh.setRefreshing(false);
        if (!TextUtils.isEmpty(err_msg)) {
            Utils.toastLONG(err_msg);
            return;
        }
        if (load_more_view == null) {
            load_more_view = new TextView(act);
            load_more_view.setPadding(10, 10, 10, 45);
            load_more_view.setText("加载更多");
            load_more_view.setGravity(Gravity.CENTER);
            load_more_view.setTextSize(18);
        }

        if (page_info.hasNextPage) {
            list_view.addFooterView(load_more_view, null, false);
        } else {
            list_view.removeFooterView(load_more_view);
        }
        runFrontOk();
    }

    @Override
    public void onAttach(boolean firstShow) {
//重写父类onAttach，什么也不做，为了防止不必要的数据请求
    }

    @Override
    public void onRefresh() {
        list_view.removeFooterView(load_more_view);
        if (page_info == null){
            page_info = new PageInfoBean();
            page_info.curPage = 0;
            page_info.pageSize = 10;
        }
        page_info.curPage = 0;
        loadData();
    }


}
