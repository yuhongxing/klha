package com.czsy.ui.sqg;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.czsy.CZNetUtils;
import com.czsy.Constant;
import com.czsy.android.R;
import com.czsy.bean.YajinGuanliBean;
import com.czsy.ui.AbsPVList;
import com.czsy.ui.AbsPVOrderList;
import com.czsy.ui.MainActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import mylib.app.BackFrontTask;
import mylib.app.BackTask;
import mylib.ui.list.AbstractAdapter;
import mylib.utils.Utils;

/**
 * 押金管理查询列表
 */
public class PVYajinGuanli extends AbsPVList implements BackFrontTask {

    private EditText et_id;
    private Button btn_search;
    private List<YajinGuanliBean> ret_data;

    public PVYajinGuanli(MainActivity a, boolean has_date) {
        super(a, has_date);
    }

    private AbstractAdapter<YajinGuanliBean> adapter = new AbstractAdapter<YajinGuanliBean>() {

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder vh = null;
            if (view == null) {
                view = View.inflate(viewGroup.getContext(), R.layout.yajinguanli_item, null);
                vh = new ViewHolder(view);
                view.setTag(vh);
            } else {
                vh = (ViewHolder) view.getTag();
            }
            YajinGuanliBean data = adapter.getItem(i);
            vh.tv_name.setText(data.getKeHuMingCheng());
            vh.tv_guige.setText(data.getGuiGeMingCheng());
            vh.tv_time.setText(data.getShouQuRiQi());
            return view;
        }
    };

    @Override
    protected int getMainViewRes() {
        return R.layout.pv_sqg_yajinguanli;
    }

    @Override
    protected void createMainView(Context ctx) {
        super.createMainView(ctx);
        tv_title.setText("押金管理");
        list_view.setAdapter(adapter);
        et_id = mMainView.findViewById(R.id.et_id);
        et_id.setHint("手机号、客户编号或纸质单号");

        String digists = "0123456789abcdefghigklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        et_id.setKeyListener(DigitsKeyListener.getInstance(digists));

        btn_search = mMainView.findViewById(R.id.btn_search);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.hideSoftInputFromWindow(act, et_id);
                showMultiBtnDialog();
            }
        });
    }

    @Override
    protected void runFrontOk() {
        adapter.setData(ret_data);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        YajinGuanliBean bean = adapter.getItem(i);

        act.getPVC().push(new PVYajinGuanliDetails(act, bean));

    }

    private String _key="";
    @Override
    public void runBack() {

        try {
            JSONObject in = new JSONObject();

            if (!et_id.getText().toString().isEmpty()) {
                in.put(_key, et_id.getText().toString().trim());
            }

            JSONObject j = CZNetUtils.postCZHttp("caiWu/yaJinList/100/1", in.toString());
            int code = j.getInt("code");
            if (code == 200) {
                JSONArray jr = j.getJSONArray("result");
                ret_data = new LinkedList<>();
                for (int i = 0; i < jr.length(); i++) {
                    YajinGuanliBean bean = Constant.gson.fromJson(
                            jr.getJSONObject(i).toString(), YajinGuanliBean.class
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

    /* @setNeutralButton 设置中间的按钮
     * 若只需一个按钮，仅设置 setPositiveButton 即可
     */
    private void showMultiBtnDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(act);
        AlertDialog dialog = builder
                .setMessage("请选择检索条件")
                .setPositiveButton("手机号", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        _key = "mobile";
                        loadData();

                    }
                }).setNeutralButton("客户编号", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        _key = "keHuBianHao";
                        loadData();
                    }
                }).setNegativeButton("纸质单号", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        _key = "zhiZhiDanHao";
                        loadData();
                    }
                }).create();
        dialog.show();
        //一定要在dialog.show()之后才能执行下面的代码
        Button mNegativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        Button mPositiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button mNeutralButton = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);

        LinearLayout.LayoutParams positiveButtonLL = (LinearLayout.LayoutParams) mPositiveButton.getLayoutParams();
        positiveButtonLL.weight = 1;
        mPositiveButton.setLayoutParams(positiveButtonLL);

        LinearLayout.LayoutParams mNegativeButtonLL = (LinearLayout.LayoutParams) mNegativeButton.getLayoutParams();
        mNegativeButtonLL.weight = 1;
        mNegativeButton.setLayoutParams(mNegativeButtonLL);

        LinearLayout.LayoutParams mNeutralButtonLL = (LinearLayout.LayoutParams) mNeutralButton.getLayoutParams();
        mNeutralButtonLL.weight = 1;
        mNeutralButton.setLayoutParams(mNeutralButtonLL);
    }


    private class ViewHolder {
        private TextView tv_name, tv_guige, tv_time;

        private ViewHolder(View view) {
            tv_name = view.findViewById(R.id.tv_name);
            tv_guige = view.findViewById(R.id.tv_guige);
            tv_time = view.findViewById(R.id.tv_time);
        }

    }
}
