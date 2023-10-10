package com.czsy.ui;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.czsy.CZNetUtils;
import com.czsy.Constant;
import com.czsy.TheApp;
import com.czsy.android.R;
import com.czsy.bean.KuCunBean;
import mylib.app.BackFrontTask;
import mylib.app.BackTask;
import mylib.app.MyLog;
import mylib.ui.list.AbstractAdapter;
import mylib.utils.Utils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

// 我的库存
public class PVMyKunCun2 extends AbsPVBase implements BackFrontTask, AdapterView.OnItemClickListener {
    private String err_msg;
    private ListView list_view;
    private View list_container;
    private ViewGroup stat_container;
    private TextView tv_title, tv_empty;

    final private Map<Integer, StatData> stat_data = new TreeMap<>();
    final private List<KuCunBean> tmp_list = new LinkedList<>();
    final static int stat_heavy = -1;
    final static int stat_empty = -2;
    final StatData st_heavy = new StatData(stat_heavy, "重瓶");
    final StatData st_empty = new StatData(stat_empty, "空瓶");

    static class StatData {
        final public int stat_type;
        public final String stat_name;
        final List<KuCunBean> list_data = new LinkedList<>();
        public boolean selected = false;

        StatData(int stat_type, String name) {
            this.stat_type = stat_type;
            stat_name = name;
        }
    }

    @Override
    protected void createMainView(Context ctx) {
        LayoutInflater li = LayoutInflater.from(ctx);
        mMainView = li.inflate(R.layout.pv_kucun2, null);
        tv_title = mMainView.findViewById(R.id.tv_title);
        tv_title.setText("我的库存");
        list_view = mMainView.findViewById(R.id.list_view);
        list_view.setAdapter(adapter);
        list_view.setOnItemClickListener(this);
        list_container = mMainView.findViewById(R.id.list_container);
        stat_container = mMainView.findViewById(R.id.stat_container);
        tv_empty = mMainView.findViewById(R.id.tv_empty);
        mMainView.findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                act.getPVC().pop();
            }
        });
    }

    public PVMyKunCun2(MainActivity a) {
        super(a);
    }

    @Override
    public void runFront() {
        act.hideProgress();
        if (!TextUtils.isEmpty(err_msg)) {
            Utils.toastLONG(err_msg);
            err_msg = null;
            return;
        }
        // update stat container
        stat_container.removeAllViews();
        CompoundButton.OnCheckedChangeListener l = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton cb, boolean b) {
                StatData sd = (StatData) cb.getTag();
                sd.selected = b;
                updateAdapter();
            }
        };
        ViewGroup.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT);
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(lp);
        lp2.weight = 1;
        List<StatData> list = new LinkedList<>();
        list.addAll(stat_data.values());
        list.add(0, st_empty);
        list.add(1, st_heavy);
        Iterator<StatData> iter = list.iterator();
        while (iter.hasNext()) {
            StatData sd1 = iter.next();
            StatData sd2 = iter.hasNext() ? iter.next() : null;

            LinearLayout ll = new LinearLayout(act);
            ll.setOrientation(LinearLayout.HORIZONTAL);
            stat_container.addView(ll, lp);

            for (StatData sd : new StatData[]{sd1, sd2}) {
                if (sd == null) {
                    continue;
                }
                CheckBox cb = new CheckBox(act);
                cb.setText(sd.stat_name + "：" + sd.list_data.size());
                cb.setChecked(sd.selected);
                cb.setOnCheckedChangeListener(l);
                cb.setTag(sd);
                ll.addView(cb, lp2);
            }
        }

        updateAdapter();
        if (adapter.isEmpty()) {
            tv_empty.setVisibility(View.VISIBLE);
            list_container.setVisibility(View.GONE);
        } else {
            tv_empty.setVisibility(View.GONE);
            list_container.setVisibility(View.VISIBLE);
        }
    }

    private AbstractAdapter<KuCunBean> adapter = new AbstractAdapter<KuCunBean>() {
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            TextView tv = (TextView) view;
            if (tv == null) {
                tv = new TextView(viewGroup.getContext());
                tv.setPadding(10, 10, 10, 10);
            }
            KuCunBean b = getItem(i);
            tv.setText(
                    String.format("气  瓶  号：%s\n气体类型：%s\n规        格：%s，%s\n状        态：%s",
                            b.gangPingHao, b.qiTiLeiXing,
                            b.yuQiStatusName, b.guiGeName,
                            b.statusName));
            return tv;
        }
    };


    private void updateAdapter() {

        boolean empty_checked = st_empty.selected;
        boolean heave_checked = st_heavy.selected;
        boolean filter = (empty_checked && !heave_checked) ||
                (!empty_checked && heave_checked);


        Set<KuCunBean> added = new HashSet<>();
        List<KuCunBean> list = new LinkedList<>();
        // type selected
        List<StatData> sd_list = new LinkedList<>();
        for (StatData val : stat_data.values()) {
            if (val.selected) {
                sd_list.add(val);
            }
        }
        if (sd_list.isEmpty()) {
            sd_list.addAll(stat_data.values());
        }

        for (StatData sd : sd_list) {
            for (KuCunBean bb : sd.list_data) {
                if (!added.contains(bb)) {
                    list.add(bb);
                    added.add(bb);
                }
            }
        }

        if (filter) {
            // remove some
            Iterator<KuCunBean> iter = list.iterator();
            while (iter.hasNext()) {
                KuCunBean b = iter.next();
                if ((empty_checked && !b.isEmpty()) ||
                        (heave_checked && b.isEmpty())) {
                    iter.remove();
                }
            }
        }
        adapter.setData(list);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    protected void parseResult(JSONObject jdata) throws Exception {
        JSONArray ja = jdata.optJSONArray("result");
        tmp_list.clear();
        stat_data.clear();
        if (ja == null || ja.length() == 0) {
            err_msg = "你没有气瓶库存";
            return;
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < ja.length(); i++) {
            JSONObject jo = ja.getJSONObject(i);
            KuCunBean b = Constant.gson.fromJson(jo.toString(), KuCunBean.class);
            tmp_list.add(b);
        }

        // stat data
        Map<Integer, StatData> map = new TreeMap<>();
        for (KuCunBean b : tmp_list) {
            int guige = b.guiGe;
            StatData sd = map.get(guige);
            if (sd == null) {
                sd = new StatData(guige, b.guiGeName);
                map.put(b.guiGe, sd);
            }
            sd.list_data.add(b);
            if (b.isEmpty()) {
                st_empty.list_data.add(b);
            } else {
                st_heavy.list_data.add(b);
            }
        }
//        if (st_empty.list_data.size() > 0) {
//            stat_data.put(st_empty.stat_type, st_empty);
//        }
//        if (st_heavy.list_data.size() > 0) {
//            stat_data.put(st_heavy.stat_type, st_heavy);
//        }
        stat_data.putAll(map);
    }

    @Override
    public void runBack() {
        try {
//            String url = "kuCun/chaKanPeiSongYuanKuCun/90000/1";
            String url = "gangPing/kuCunForAnZhuo/90000/1";
            String s = "{}";
            JSONObject ret = CZNetUtils.postCZHttp(url, s);
            err_msg = ret.optString("message");
            int code = ret.optInt("code", 200);
            if (code != 200) {
                throw new CZNetUtils.CZNetErr(code, ret);
            }
            err_msg = null;
            parseResult(ret);
        } catch (Exception e) {
            MyLog.LOGE(e);
            if (err_msg == null && (e instanceof IOException)) {
                err_msg = TheApp.sInst.getString(R.string.tip_network_error);
            }
            err_msg = "服务器错误： " + e.toString();

        }
    }

    @Override
    public void onAttach(boolean firstShow) {
        super.onAttach(firstShow);

        if (firstShow) {
            act.showProgress();
            BackTask.post(this);
        }
    }
}
