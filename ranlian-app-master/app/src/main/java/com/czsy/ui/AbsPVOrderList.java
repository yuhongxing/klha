package com.czsy.ui;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.czsy.CZBackTask;
import com.czsy.CZNetUtils;
import com.czsy.android.R;
import com.czsy.ui.sqg.order.PVSQG_Order;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.List;

import mylib.app.BackTask;
import mylib.ui.list.AbstractAdapter;
import mylib.utils.Utils;

abstract public class AbsPVOrderList<T> extends AbsPVList {

    public AbsPVOrderList(MainActivity a, boolean has_date) {
        super(a, has_date);
    }

    protected View getView1(int i, View view, ViewGroup viewGroup) {
        PVSQG_Order.ViewHolder vh;
        if (view == null) {
//            view = View.inflate(viewGroup.getContext(), R.layout.common_order_item, null);
            view = View.inflate(viewGroup.getContext(), R.layout.dingqi_order_item1, null);
            vh = new PVSQG_Order.ViewHolder(view);
        } else {
            vh = (PVSQG_Order.ViewHolder) view.getTag();
        }
        T data = adapter.getItem(i);
        bindItem(i, data, vh);

        vh.btn_reject_order.setOnClickListener(this);
        vh.btn_reject_order.setTag(data);
        return vh.root;
    }


    /**
     * 目前返回的数据不是统一格式的实体类，所以不能在此直接赋值
     * 若格式统一，则可在此统一赋值，子类不用重写
     */
    abstract protected void bindItem(int pos, T data, PVSQG_Order.ViewHolder vh);

    protected AbstractAdapter<T> adapter = new AbstractAdapter<T>() {

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            return getView1(i, view, viewGroup);
        }
    };


    abstract protected String getTuiDanURL();

    protected String getTuiDanInputString(T data, String reason) throws Exception {
        JSONObject j = new JSONObject();
        Field f = data.getClass().getField("dingDanHao");
        j.put("sid", f.get(data).toString());
        j.put("yuanYin", reason);
        return j.toString();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.btn_reject_order == id) {
            final T data = (T) v.getTag();

            Context ctx = act;
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
                            return getTuiDanInputString(data, reason);
                        }

                        @Override
                        protected String getURL() {
                            return getTuiDanURL();
                        }

                        @Override
                        protected void runFront2() {
                            loadData();
                            Utils.toast(R.string.tip_op_ok);
                        }

                        @Override
                        public void runFront() {
                            super.runFront();
                            d.dismiss();
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
        } else {
            super.onClick(v);
        }
    }

    @Override
    protected void createMainView(Context ctx) {
        super.createMainView(ctx);
        list_view.setAdapter(adapter);
    }

    protected void runFrontOk() {
        adapter.setData(ret_data);
    }

    protected List<T> ret_data;

    static public class ViewHolder {
        public final View root;
        public final TextView tv_content;
        public final View btn_tuidan;
        public LinearLayout top_container;
        public TextView tv_ordernum, tv_order_time, tv_orderOk_time, zhifufangshi,
                tv_kehu_num, tv_name, tv_tel, tv_address, tv_gyz, tv_order_money, tv_laiyuan,tv_laidianTel, tv_beizhu,
                tv_shiTuiYaJin, tv_tuiQi, tv_gangpingNum,
                tv_good, tv_jiage, tv_num, tv_money;
        public GridLayout gridLayout_tuiping, gridLayout_goods;


        public ViewHolder(View r) {
            root = r;
            tv_content = r.findViewById(R.id.tv_content);
            top_container = r.findViewById(R.id.top_container);
            btn_tuidan = r.findViewById(R.id.btn_tuidan);
            tv_ordernum = r.findViewById(R.id.tv_ordernum);
            tv_order_time = r.findViewById(R.id.tv_order_time);
            tv_orderOk_time = r.findViewById(R.id.tv_orderOk_time);
            zhifufangshi = r.findViewById(R.id.zhifufangshi);
            tv_kehu_num = r.findViewById(R.id.tv_kehu_num);
            tv_name = r.findViewById(R.id.tv_name);
            tv_tel = r.findViewById(R.id.tv_tel);
            tv_address = r.findViewById(R.id.tv_address);
            tv_gyz = r.findViewById(R.id.tv_gyz);
            tv_order_money = r.findViewById(R.id.tv_order_money);
            tv_laiyuan = r.findViewById(R.id.tv_laiyuan);
            tv_laidianTel = r.findViewById(R.id.tv_laidianTel);
            tv_beizhu = r.findViewById(R.id.tv_beizhu);
            gridLayout_tuiping = r.findViewById(R.id.gridLayout_tuiping);
            gridLayout_goods = r.findViewById(R.id.gridLayout_goods);

            tv_shiTuiYaJin = r.findViewById(R.id.tv_shiTuiYaJin);
            tv_tuiQi = r.findViewById(R.id.tv_tuiQi);
            tv_gangpingNum = r.findViewById(R.id.tv_gangpingNum);

            tv_good = r.findViewById(R.id.tv_good);
            tv_jiage = r.findViewById(R.id.tv_jiage);
            tv_num = r.findViewById(R.id.tv_num);
            tv_money = r.findViewById(R.id.tv_money);

            root.setTag(this);
        }

    }
}
