package com.czsy.ui.changzhan;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.czsy.CZBackTask;
import com.czsy.Constant;
import com.czsy.INFCHandler;
import com.czsy.android.R;
import com.czsy.bean.GangPingBean;
import com.czsy.bean.LoginUser;
import com.czsy.ui.AbsPVBase;
import com.czsy.ui.MainActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mylib.app.BackTask;
import mylib.ui.list.AbstractAdapter;
import mylib.utils.CZUtlis;
import mylib.utils.Utils;

/**
 * 批量操作
 */
abstract public class AbsPVCZPiLiang extends AbsPVBase implements INFCHandler, View.OnClickListener {

    private String TAG = "AbsPVCZPiLiang";
    protected LoginUser lu;
    private boolean isNext = true;//是否判断场站id

    /**
     * @param a
     * @param isNext 判断场站id，true:判断；false：不判断（调入不用判断）
     */
    public AbsPVCZPiLiang(MainActivity a, boolean isNext) {
        super(a);
        this.isNext = isNext;
        if (lu == null) {
            lu = LoginUser.get();
        }
    }

    protected TextView tv_xinpian;
    protected ListView listView;
    protected EditText et_input;
    protected TextView tv_title;
    protected Button btn_sub;
    protected String xinPianHao;
    protected List<GangPingBean> lists = new ArrayList<>();
    protected List<Long> ids = new ArrayList<>();


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (R.id.iv_back == id) {
            act.getPVC().pop();
        } else if (id == R.id.btn_search) {
            final String no = et_input.getText().toString().trim();
            if (TextUtils.isEmpty(no)) {
                et_input.requestFocus();
                return;
            }
            doSearch(no, true);
        } else if (id == R.id.btn_sub) {

            subData(ids);
        }
    }

    @Override
    public void onNFCIntent(NFCInfo i) {
        xinPianHao = i.chip_sn;
        tv_xinpian.setText("芯片号: " + i.chip_sn);
        doSearch(i.chip_sn, false);
    }

    protected abstract int getMainViewRes();

    protected abstract void subData(List<Long> lists);

    @Override
    protected void createMainView(Context ctx) {
        mMainView = View.inflate(ctx, getMainViewRes(), null);
        tv_xinpian = mMainView.findViewById(R.id.tv_xinpian);
        et_input = mMainView.findViewById(R.id.et_input);
        tv_title = mMainView.findViewById(R.id.tv_title);
        listView = mMainView.findViewById(R.id.list_view);
        btn_sub = mMainView.findViewById(R.id.btn_sub);
        mMainView.findViewById(R.id.btn_sub).setOnClickListener(this);
        mMainView.findViewById(R.id.iv_back).setOnClickListener(this);
        mMainView.findViewById(R.id.btn_search).setOnClickListener(this);

        listView.setAdapter(adapter);
        adapter.setData(lists);
    }

    protected boolean is_searching = false;

    protected void doSearch(final String no, final boolean is_gp_no) {
        if (is_searching) {
            return;
        }
        is_searching = true;
        act.showProgress();
        BackTask.post(new CZBackTask(act) {
            GangPingBean ret_gp;

            @Override
            protected void parseResult(JSONObject jdata) throws Exception {

                JSONArray ja = jdata.getJSONArray("result");
                if (ja.length() == 1) {
                    ret_gp = Constant.gson.fromJson(ja.getJSONObject(0).toString(), GangPingBean.class);
                    return;
                }
                ret_gp = null;
                err_msg = "没有找到气瓶!";
                throw new IOException();
            }

            @Override
            protected String getInputParam() throws Exception {
                JSONObject j = new JSONObject();
                if (is_gp_no) {
                    j.put("gangPingHao", no);
                } else {
                    j.put("xinPianHao", no);
                }
                return j.toString();
            }

            @Override
            protected String getURL() {
                return "gangPing/chaXun/1000/1";
            }

            @Override
            public void runFront() {
                is_searching = false;
                if (ret_gp == null) {
                    if (tv_xinpian != null) {
                        tv_xinpian.setText(null);
                    }
                }
                super.runFront();
            }

            @Override
            protected void runFront2() {
                // update
                if (ret_gp != null) {

                    if (CZUtlis.isok(ret_gp, lu, isNext)) {
//                        Log.d(TAG,"是否包含-->" + ids.contains(ret_gp.id));
                        if (!ids.contains(ret_gp.id)) {
                            lists.add(ret_gp);
                            ids.add(ret_gp.id);
                            adapter.notifyDataSetChanged();
                        } else {
                            Utils.toastSHORT("已在列表中");
                        }

                    } else {

                        AlertDialog.Builder ab = new AlertDialog.Builder(act);
                        ab.setTitle("提示：");
                        ab.setMessage(CZUtlis.toMsg(ret_gp, lu, isNext));
                        ab.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        ab.show();
                    }

                }
            }
        });

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
            return view;
        }
    };

    private class ViewHolder {
        private TextView tv_gangpingNum, tv_guige, tv_xinpian, tv_qiti;

        private ViewHolder(View view) {
            tv_gangpingNum = view.findViewById(R.id.tv_gangpingNum);
            tv_guige = view.findViewById(R.id.tv_guige);
            tv_xinpian = view.findViewById(R.id.tv_xinpian);
            tv_qiti = view.findViewById(R.id.tv_qiti);
        }

    }


}
