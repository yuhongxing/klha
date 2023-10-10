package com.czsy.ui.yunshuyuan;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.czsy.CZBackTask;
import com.czsy.Constant;
import com.czsy.android.R;
import com.czsy.bean.MenDianBean;
import com.czsy.ui.AbsPVBase;
import com.czsy.ui.MainActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import mylib.app.BackTask;

abstract public class AbsPVSeleItem extends AbsPVBase implements View.OnClickListener {


    private String title, url;
    private List<MenDianBean> list = new LinkedList<>();

//    public AbsPVSeleItem(MainActivity a) {
//        super(a);
//    }

    public AbsPVSeleItem(MainActivity a, String title, String url) {
        super(a);
        this.title = title;
        this.url = url;

    }

    Spinner sp_item;

    @Override
    protected void createMainView(Context ctx) {
        mMainView = View.inflate(ctx, R.layout.pv_ysy_sel_item, null);
        mMainView.findViewById(R.id.iv_back).setOnClickListener(this);
        mMainView.findViewById(R.id.btn_ok).setOnClickListener(this);
        sp_item = (Spinner) mMainView.findViewById(R.id.sp_item);

        TextView tv_title = mMainView.findViewById(R.id.tv_title);
        tv_title.setText("请选择" + title);
        TextView tv_content = mMainView.findViewById(R.id.tv_content);
        tv_content.setText("请选择" + title);
    }

    @Override
    public void onAttach(boolean firstShow) {
        super.onAttach(firstShow);
        if (firstShow) {
            act.showProgress();
            BackTask.post(new CZBackTask(act) {
                @Override
                protected void parseResult(JSONObject jdata) throws Exception {
                    JSONArray ja = jdata.getJSONArray("result");
                    list.clear();
                    for (int i = 0; i < ja.length(); i++) {
                        list.add(Constant.gson.fromJson(
                                ja.getJSONObject(i).toString(), MenDianBean.class));
                    }
                }


                @Override
                protected String getInputParam() throws Exception {
                    return "";
                }

                @Override
                protected String getURL() {
                    return url;
                }

                @Override
                protected void runFront2() {
                    ArrayAdapter adapter = new ArrayAdapter<MenDianBean>(act,
                            R.layout.gp_id_item, list.toArray(new MenDianBean[0]));
                    sp_item.setAdapter(adapter);
                    if (list.size() > 0) {
                        sp_item.setSelection(0);
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.iv_back == id) {
            act.getPVC().pop();
        } else if (R.id.btn_ok == id) {
            final MenDianBean mb = (MenDianBean) sp_item.getSelectedItem();

            AlertDialog.Builder adBd = new AlertDialog.Builder(act);
            adBd.setTitle("请确认站点");
            adBd.setMessage("您选择的是：" + mb.mingCheng);
            adBd.setPositiveButton("下一步", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    onOk(mb, list);

                }
            });
            adBd.setNegativeButton("重选", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            adBd.create();
            adBd.show();

        }
    }

//    protected abstract void onOk(MenDianBean mb);

    protected abstract void onOk(MenDianBean mb, List<MenDianBean> list);

}
