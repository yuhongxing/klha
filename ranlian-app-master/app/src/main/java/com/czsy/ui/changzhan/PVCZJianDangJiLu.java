package com.czsy.ui.changzhan;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.czsy.CZNetUtils;
import com.czsy.Constant;
import com.czsy.android.R;
import com.czsy.bean.GangPingBean;
import com.czsy.bean.YajinGuanliBean;
import com.czsy.ui.AbsPVList;
import com.czsy.ui.MainActivity;
import com.czsy.ui.sqg.PVYajinGuanli;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import mylib.ui.list.AbstractAdapter;
import mylib.utils.Utils;

/**
 * 当日建档记录
 */
public class PVCZJianDangJiLu extends AbsPVList {

    private List<GangPingBean> ret_data;

    public PVCZJianDangJiLu(MainActivity a, boolean has_date) {
        super(a, has_date);
    }

    private AbstractAdapter<GangPingBean> adapter = new AbstractAdapter<GangPingBean>() {

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder vh = null;
            if (view == null) {
                view = View.inflate(viewGroup.getContext(), R.layout.jiandangjilu_item, null);
                vh = new ViewHolder(view);
                view.setTag(vh);
            } else {
                vh = (ViewHolder) view.getTag();
            }
            GangPingBean data = adapter.getItem(i);
            vh.tv_gangpingNum.setText(data.gangPingHao);
            vh.tv_xinpian.setText(data.xinPianHao);
            vh.tv_guige.setText(data.guiGeName);
            vh.tv_qiti.setText(data.qiTiLeiXing);
            if (data.getStatusName().equals("删档")){
                vh.tv_xinpian.setText("暂无");
                vh.tv_gangpingNum.setText(data.gangPingHao+"（"+data.statusName+"）");
            }
            return view;
        }
    };

    @Override
    protected void createMainView(Context ctx) {
        super.createMainView(ctx);
        tv_title.setText("当日建档信息");
        list_view.setAdapter(adapter);
    }

    @Override
    protected void runFrontOk() {
        adapter.setData(ret_data);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void runBack() {
        try {

            JSONObject j = CZNetUtils.postCZHttp("gangPing/jianDangJiLu/1000/1", null);
            int code = j.getInt("code");
            if (code == 200) {
                JSONArray jr = j.getJSONArray("result");
                ret_data = new LinkedList<>();
                for (int i = 0; i < jr.length(); i++) {
                    GangPingBean bean = Constant.gson.fromJson(
                            jr.getJSONObject(i).toString(), GangPingBean.class
                    );
                    ret_data.add(bean);
                }
            } else {
                String msg = j.getString("message");
                Utils.toastLONG(msg);
            }
        } catch (Exception e) {
            err_msg = "";
            e.printStackTrace();
        }
    }

    private class ViewHolder {
        private TextView tv_gangpingNum, tv_guige, tv_xinpian,tv_qiti;

        private ViewHolder(View view) {
            tv_gangpingNum = view.findViewById(R.id.tv_gangpingNum);
            tv_guige = view.findViewById(R.id.tv_guige);
            tv_xinpian = view.findViewById(R.id.tv_xinpian);
            tv_qiti = view.findViewById(R.id.tv_qiti);
        }

    }
}
