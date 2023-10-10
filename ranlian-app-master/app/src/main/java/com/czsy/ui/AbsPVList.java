package com.czsy.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.czsy.android.R;
import com.czsy.bean.AnJianOrderBean;
import com.czsy.other.DatePickerDialog;

import java.util.Calendar;
import java.util.List;
import java.util.Random;

import mylib.app.BackFrontTask;
import mylib.app.BackTask;
import mylib.utils.Utils;

/**
 * 可加载 & refresh 的内容
 */
abstract public class AbsPVList extends AbsPVBase
        implements View.OnClickListener,
        AdapterView.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener, BackFrontTask {//, AbsListView.OnScrollListener
    public AbsPVList(MainActivity a, boolean has_date) {
        super(a);
        this.has_date = has_date;
    }

    protected long cur_date = System.currentTimeMillis();
    protected final boolean has_date; // has date select
    protected TextView tv_title, tv_right,tv_left; // tv_tip,
    protected SwipeRefreshLayout swiperefresh;
    protected ListView list_view;
    protected TextView tv_empty,tv_msg_cnt;
    protected ImageView iv_back;
    protected boolean is_loading = false;

    protected int getMainViewRes() {
        return R.layout.abs_pv_list;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void createMainView(Context ctx) {
        mMainView = View.inflate(act, getMainViewRes(), null);

        tv_right = mMainView.findViewById(R.id.tv_right);
        tv_right.setOnClickListener(this);
        tv_left = mMainView.findViewById(R.id.tv_left);
        tv_title = mMainView.findViewById(R.id.tv_title);
        tv_empty = mMainView.findViewById(R.id.tv_empty);
        tv_msg_cnt = mMainView.findViewById(R.id.tv_msg_cnt);
        list_view = mMainView.findViewById(R.id.list_view);
        iv_back = mMainView.findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        swiperefresh = mMainView.findViewById(R.id.swiperefresh);
        swiperefresh.setOnRefreshListener(this);
        if (has_date) {
            tv_title.setOnClickListener(this);
            cur_date = System.currentTimeMillis();
            updateTitle();
        }
        list_view.setEmptyView(tv_empty);
        list_view.setOnItemClickListener(this);
        initView();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.iv_back == id) {
            act.getPVC().pop();
        } else if (R.id.tv_title == id) {
            if (!has_date) {
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
                    cur_date = c.getTimeInMillis();
                    updateTitle();
                    loadData();
                }
            });
            d.show();
        }
    }

    protected void updateTitle() {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(cur_date);
        Random r = new Random();
        int y = c.get(Calendar.YEAR);
        int m = c.get(Calendar.MONTH);
        int d = c.get(Calendar.DAY_OF_MONTH);
        tv_title.setText(String.format("%4d-%02d-%02d", y, m + 1, d));
    }

    protected void loadData() {
        if (is_loading) {
            return;
        }
        err_msg = null;
        is_loading = true;
        swiperefresh.setRefreshing(true);
        BackTask.post(this);
    }

    protected String err_msg;

    abstract protected void  runFrontOk();

    protected void  initView(){

    }

    @Override
    public void runFront() {
        is_loading = false;
        swiperefresh.setRefreshing(false);
        if (!TextUtils.isEmpty(err_msg)) {
            Utils.toastLONG(err_msg);
            return;
        }
        runFrontOk();
    }

    @Override
    public void onAttach(boolean firstShow) {
        super.onAttach(firstShow);
        Log.d("xing","AbsPVList-->onAttach");
        loadData();
    }

    @Override
    public void onRefresh() {
        loadData();
    }

}
