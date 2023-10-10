package com.czsy.ui.yunshuyuan;

import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.czsy.CZBackTask;
import com.czsy.CZNetUtils;
import com.czsy.Constant;
import com.czsy.TheApp;
import com.czsy.android.R;
import com.czsy.bean.PageInfoBean;
import com.czsy.bean.YunShuDanBean;
import com.czsy.ui.AbsPVPageList;
import com.czsy.ui.MainActivity;
import com.czsy.ui.PVSimpleText;

import mylib.app.BackTask;
import mylib.app.MyLog;
import mylib.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SimpleTimeZone;

public class PVMyYunShuDan extends AbsPVPageList<YunShuDanBean> {

    private List<YunShuDanBean> list_data = new LinkedList<>();
    private boolean isALL = false;

    public PVMyYunShuDan(MainActivity a) {
        super(a, false);
    }

    public PVMyYunShuDan(MainActivity a, boolean isALL) {
        super(a, false);
        this.isALL = isALL;
    }

    @Override
    protected View getView(int i, View view, ViewGroup viewGroup) {
        TextView tv = (TextView) view;
        if (tv == null) {
            tv = new TextView(viewGroup.getContext());
            tv.setPadding(25, 20, 20, 20);
            // tv.setGravity(Gravity.CENTER);
            tv.setTextSize(18);
            tv.setBackgroundColor(Color.WHITE);
        }
        YunShuDanBean b = adapter.getItem(i);
        String typeStr = "";
        if (b.type == 1) {
            // 供应站-》运
            typeStr = String.format("供应站: %s\n运输员: %s", b.gongYingZhan, b.yunShuYuanName);
        } else if (b.type == 2) {
            // 运输员-》供应站
            typeStr = String.format("运输员: %s\n供应站: %s", b.yunShuYuanName, b.gongYingZhan);
        } else if (b.type == 3) {
            // 充装站-》运
            typeStr = String.format("充装站: %s\n运输员: %s", b.changZhan, b.yunShuYuanName);
        } else if (b.type == 4) {
            // 运输员-》充
            typeStr = String.format("运输员: %s\n充装站: %s", b.yunShuYuanName, b.changZhan);
        }
//        else {
//            typeStr = b.typeName + '\n' + String.format("充装站: %s\n供应站: %s\n运输员: %s",
//                    b.changZhan, b.gongYingZhan, b.yunShuYuanName);
//        }
        tv.setText(String.format("日期: %s\n%s\n个数: %d\n车牌: %s\n状态：%s\n方向：%s",
                b.chuangJianRiQi, typeStr,
                b.gangPingCount == null ? 0 : b.gangPingCount,
                (b.chePai == null ? "" : b.chePai),
                b.statusName,
                b.typeName
        ));
        return tv;
    }

    @Override
    protected void runFrontOk() {

        if (list_data != null) {
            adapter.setData(list_data);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void initView() {
        super.initView();
        if (!isALL) {
            tv_title.setText("今日运输单");
            tv_right.setText("全部");
            tv_right.setVisibility(View.VISIBLE);
            tv_right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    act.getPVC().push(new PVMyYunShuDan(act, true));
                }
            });
        } else {
            tv_title.setText("全部运输单");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        act.showProgress();
        final YunShuDanBean b = adapter.getItem(i);
        BackTask.post(new CZBackTask(act) {
            @Override
            protected void parseResult(JSONObject jdata) throws Exception {
                JSONArray ja = jdata.getJSONArray("result");
                StringBuffer sb = new StringBuffer();
                Map<String, Integer> map = new HashMap<>();

                for (int i = 0; i < ja.length(); i++) {
                    JSONObject jo = ja.getJSONObject(i);
                    String guige = jo.optString("guiGeName", "");
                    if (!TextUtils.isEmpty(guige)) {
                        if (!map.containsKey(guige)) {
                            map.put(guige, 1);
                        } else {
                            int ii = map.get(guige);
                            map.put(guige, ii + 1);
                        }
                    }

                    sb.append(
                            String.format(
                                    "----------------------------------------------------\n" +
                                            "气瓶号: %s\n气瓶类型：%s\n" +
                                            "气体类型：%s\n"+
                                            //"净重: %dkg\n重量: %d\n" +
                                            "规格：%s\n" +
                                            //"场站: %s\n供应站: %s\n" +
                                            "生产厂家: %s\n",
                                    jo.optString("gangPingHao", null),
                                    jo.optString("yuQiStatusName", ""),
                                    jo.optString("qiTiLeiXing",""),
                                    //jo.optInt("jingZhong", 0),
                                    //jo.optInt("zhongLiang", 0),
                                    guige,
                                    //jo.optString("changZhan", ""),
                                    //jo.optString("gongYingZhan", ""),
                                    jo.optString("shengChanChangJia", "")
                            )
                    );
                }
                StringBuffer sb2 = new StringBuffer();
                sb2.append("-------------------- 汇总统计\n");
                for (String key : map.keySet()) {
                    sb2.append(key + ": " + map.get(key));
                    sb2.append('\n');
                }
                sb2.append(sb.toString());
                ret_str = sb2.toString();

            }

            String ret_str = null;

            @Override
            protected String getInputParam() throws Exception {
                return String.format("{\"id\":\"%d\"}", b.id);
            }

            @Override
            protected String getURL() {
                return "gangPing/chaXunYunShuDanGangPingDetails";
            }

            @Override
            protected void runFront2() {
                if (ret_str != null) {
                    act.getPVC().push(new PVSimpleText(act, ret_str));
                }
            }
        });
    }


    @Override
    public void runBack() {
        try {
            String url = "gangPingLiuZhuan/yunShuYuanChaXun/10/" + getNextPage();
            if (url == null) {
                return;
            }
            JSONObject object = new JSONObject();

            if (!isALL) {
                SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
                String date = sd.format(new Date());
                object.put("startDate", date + " 00:00:00");
            }

            JSONObject ret = CZNetUtils.postCZHttp(url, isALL ? "{}" : object.toString());
            err_msg = ret.optString("message");
            int code = ret.optInt("code", 200);
            if (code != 200) {
                throw new CZNetUtils.CZNetErr(code, ret);
            }
            page_info = Constant.gson.fromJson(
                    ret.getJSONObject("pageVO").toString(), PageInfoBean.class);
//            page_info.hasNextPage = false; 分页闸
            JSONArray ja = ret.getJSONArray("result");
            for (int i = 0; i < ja.length(); i++) {
                list_data.add(Constant.gson.fromJson(
                        ja.getJSONObject(i).toString(), YunShuDanBean.class));
            }

            err_msg = null; // ok
            if (ja.length() == 0) {
                Utils.toastSHORT("没有更多了");
            }
            return;
        } catch (Exception e) {
            MyLog.LOGE(e);
            if (err_msg == null && (e instanceof IOException)) {
                err_msg = TheApp.sInst.getString(R.string.tip_network_error);
            }
            if (TextUtils.isEmpty(err_msg)) {
                err_msg = TheApp.sInst.getString(R.string.tip_common_err);
            }
        }
        list_data = null;
    }

    @Override
    public void onRefresh() {
        if (list_data != null) {
            list_data.removeAll(list_data);
        }
        super.onRefresh();
    }

}
