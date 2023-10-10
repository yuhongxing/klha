package com.czsy.ui.changzhan;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Selection;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.czsy.CZBackTask;
import com.czsy.CZNetUtils;
import com.czsy.Constant;
import com.czsy.INFCHandler;
import com.czsy.android.R;
import com.czsy.bean.GPChangJiaBean;
import com.czsy.bean.GPQitiTypeBean;
import com.czsy.bean.GangPingBean;
import com.czsy.bean.IDNameBean;
import com.czsy.other.DatePickerDialog;
import com.czsy.ui.AbsPVBase;
import com.czsy.ui.MainActivity;

import mylib.app.BackFrontTask;
import mylib.app.BackTask;
import mylib.app.MyLog;
import mylib.ui.list.AbstractAdapter;
import mylib.utils.DateUtils;
import mylib.utils.FileUtils;
import mylib.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

// 建档
public class PVCZJianDang extends AbsPVBase implements INFCHandler, View.OnClickListener {
    public PVCZJianDang(MainActivity a) {
        super(a);
    }

    public final static String yuzhi_file = "yuzhi_file";
    private AlertDialog.Builder builder;

    TextView tv_dangriJilu, tv_guige, tv_pizhong, tv_changjia, tv_all, tv_prefix;
    ListView list_view;

    private static PVCZJianDang.CZJianDangYuZhi yuzhi_info;

    static {
        yuzhi_info = (PVCZJianDang.CZJianDangYuZhi) FileUtils.getObject(
                PVCZJianDang.yuzhi_file, PVCZJianDang.CZJianDangYuZhi.class);
        if (yuzhi_info == null) {
            yuzhi_info = new PVCZJianDang.CZJianDangYuZhi();
        }
    }

    public static PVCZJianDang.CZJianDangYuZhi getYuZhiInfo() {
        return yuzhi_info;
    }

    // 建档预置信息
    public static class CZJianDangYuZhi implements Serializable {
        //public String leixing = "天然气";
        //public String guige = "15";
        public IDNameBean guige;
        public String chang_jia = "", prefix = "HD";
        public long chang_jia_id = 0;
        public double pi_zhong = 15.0;
        public String riqi = "";
        public String riqi_shangcijianyan = "";
        public int max_gy_len = 12;
        public int min_gy_len = 6;
        public String leixing = "";
        public int qiTiLeiXing = 0;
    }

    private void updateYuZhiInfo() {
        if (yuzhi_info == null) {
            return;
        }
        tv_guige.setText("规格\n" + yuzhi_info.guige);
        tv_pizhong.setText(act.getString(R.string.title_init_weight) +
                "\n" + yuzhi_info.pi_zhong);
        //tv_changjia.setText("类型\n" + yuzhi_info.leixing);
        tv_prefix.setText("编号前缀: " + yuzhi_info.prefix + "  生产日期: " + yuzhi_info.riqi);
    }

    @Override
    protected void createMainView(Context ctx) {
        mMainView = View.inflate(ctx, R.layout.cz_pv_jiandang, null);
        tv_dangriJilu = mMainView.findViewById(R.id.tv_dangriJilu);
        tv_guige = mMainView.findViewById(R.id.tv_guige);
        tv_pizhong = mMainView.findViewById(R.id.tv_pizhong);
        tv_changjia = mMainView.findViewById(R.id.tv_changjia);
        tv_all = mMainView.findViewById(R.id.tv_all);
        tv_prefix = mMainView.findViewById(R.id.tv_prefix);

        mMainView.findViewById(R.id.iv_back).setOnClickListener(this);
        mMainView.findViewById(R.id.tv_dangriJilu).setOnClickListener(this);
        mMainView.findViewById(R.id.btn_reset).setOnClickListener(this);
        mMainView.findViewById(R.id.btn_finish).setOnClickListener(this);

        mMainView.findViewById(R.id.btn_yuzhi).setOnClickListener(this);
        list_view = mMainView.findViewById(R.id.list_view);
        list_view.setAdapter(adapter);

        if (yuzhi_info == null) {
            loadYuZhiInfo();
        } else {
            updateYuZhiInfo();
        }
    }

    private AbstractAdapter<GangPingBean> adapter = new AbstractAdapter<GangPingBean>() {
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            TextView tv = (TextView) view;
            if (tv == null) {
                tv = new TextView(act);
                tv.setTextSize(18);
                tv.setTextColor(Color.BLACK);
                tv.setPadding(10, 10, 10, 10);
            }
            GangPingBean gp = getItem(i);
            tv.setText("芯片号: " + gp.xinPianHao + "\n气瓶号：" + gp.gangPingHao + "\n规格：" + gp.guiGeName);
//            tv.setText("==: "+gp.detailString() );
            return tv;
        }
    };
    final List<GangPingBean> list = new LinkedList<>();

    private boolean existGangPing(String id) {
        for (GangPingBean gp : list) {
            if (gp.xinPianHao.equals(id)) {
                return true;
            }
        }
        return false;
    }

    private boolean is_checking = false;
    private Dialog input_dialog;

    private void doBind(final NFCInfo data, final String gyh, final double zhong_liang) {
        act.showProgress();
        BackTask.post(new CZBackTask(act) {
            @Override
            protected void parseResult(JSONObject jdata) throws Exception {

            }

            GangPingBean input_gangpin = null;

            @Override
            protected String getInputParam() throws Exception {
                JSONObject j = new JSONObject();
                j.put("gangPingHao", gyh);
                j.put("xinPianHao", data.chip_sn);
                j.put("guiGe", yuzhi_info.guige.id);
                j.put("guiGeName", yuzhi_info.guige.name);
                //j.put("leiXing", yuzhi_info.leixing);
                j.put("jingZhong", yuzhi_info.pi_zhong);
                j.put("zhongLiang", yuzhi_info.pi_zhong);
                //j.put("zhongLiang", zhong_liang);
                j.put("shengChanChangJiaId", yuzhi_info.chang_jia_id);
                j.put("shengChanRiQi", yuzhi_info.riqi);
                j.put("shangCiJianXiuRiQi", yuzhi_info.riqi_shangcijianyan);
                j.put("qiTiLeiXing", yuzhi_info.qiTiLeiXing);
                String ret = j.toString();
                input_gangpin = Constant.gson.fromJson(ret
                        , GangPingBean.class);
                return ret;
            }

            @Override
            protected String getURL() {
                return "gangPing/chuangJian";
            }

            @Override
            public void runFront() {
                super.runFront();
                is_checking = false;
            }

            @Override
            protected void runFront2() {
                final GangPingBean b = input_gangpin; //new GangPingBean(data);
                list.add(b);
                adapter.setData(list);
                tv_all.setText("总计: " + list.size());
                input_dialog.dismiss();
                Utils.toastLONG("创建气瓶成功！");
            }
        });

    }

    @Override
    public void onNFCIntent(final NFCInfo data) {
        if (existGangPing(data.chip_sn) || is_checking || yuzhi_info == null) {
            return;
        }
        if (TextUtils.isEmpty(yuzhi_info.riqi)) {
            Utils.toastLONG("请填写预置生产日期");
            return;
        }
        is_checking = true;
        if (input_dialog == null) {
            input_dialog = new Dialog(act, android.R.style.Theme_Translucent_NoTitleBar);
            input_dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            input_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            input_dialog.setContentView(R.layout.cz_dialog_input);
            input_dialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    input_dialog.dismiss();
                }

            });
        }
        final TextView tv_content = input_dialog.findViewById(R.id.tv_content);
        final EditText et_input = input_dialog.findViewById(R.id.et_input);
        et_input.requestFocus();
        final EditText et_input1 = input_dialog.findViewById(R.id.et_input1);
        final EditText et_zhongliang = input_dialog.findViewById(R.id.et_zhongliang);
        et_zhongliang.setText(String.valueOf(yuzhi_info.pi_zhong));
        et_input.setText(null);
        et_input1.setText(null);
        tv_content.setText("芯片号: " + data.chip_sn);
        input_dialog.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String txt = et_input.getText().toString().trim();
                final String txt1 = et_input1.getText().toString().trim();
                final String zl = et_zhongliang.getText().toString().trim();

                if (TextUtils.isEmpty(txt) || TextUtils.isEmpty(txt1) || TextUtils.isEmpty(zl)) {
                    Utils.toastLONG("请填写完整");
                    et_input.requestFocus();
                    return;
                }

                if (!txt.equals(txt1)) {
                    Utils.toastLONG("输入的气瓶号不一致");
                    et_input.requestFocus();
                    return;
                }

                if (txt.length() > yuzhi_info.max_gy_len || txt.length() < yuzhi_info.min_gy_len) {
                    Utils.toastLONG(String.format("气瓶号必须在%d-%d位",
                            yuzhi_info.min_gy_len, yuzhi_info.max_gy_len));
                    et_input.requestFocus();
                    return;
                }
                String prefix = yuzhi_info.prefix;
                if (prefix == null) {
                    prefix = "";
                }

                try {
                    doBind(data, prefix + txt, Double.valueOf(zl));
                } catch (Exception e) {
                    MyLog.LOGE(e);
                    Utils.toastLONG("请填写完整");
                }
            }
        });
        input_dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                is_checking = false;
            }
        });
        input_dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                is_checking = false;
            }
        });

        input_dialog.show();
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (R.id.iv_back == id) {
            act.getPVC().pop();
        } else if (R.id.tv_dangriJilu == id) {//当日建档信息

            act.getPVC().push(new PVCZJianDangJiLu(act, false));
        } else if (R.id.btn_yuzhi == id) { // 预置信息
            if (/*lx_list.isEmpty() ||*/ guige_list.isEmpty() || cj_list.isEmpty()) {
                loadYuZhiInfo();
                Utils.toastLONG("加载预置信息...");
                return;
            }
            final Dialog d = new Dialog(act,
                    android.R.style.Theme_DeviceDefault_Light_NoActionBar);
            d.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            //d.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            // set color transpartent
            d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            d.setContentView(R.layout.cz_yuzhi_dialog);
            //生产日期
            final TextView tv_time = d.findViewById(R.id.tv_time);
            tv_time.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    onYearMonthDayTime(tv_time);
                    return;
                }
            });
            //s上次检验时间
            final TextView tv_shangCiJianYan_time = d.findViewById(R.id.tv_shangCiJianYan_time);
            yuzhi_info.riqi_shangcijianyan = "";
            tv_shangCiJianYan_time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final DatePickerDialog d = new DatePickerDialog(act);
                    d.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int i, int i1, int i2) {

                            boolean isok1 = DateUtils.shijianPanduan(String.format("%4d-%02d-%02d", i, i1 + 1, i2), "yyyy-MM-dd");
                            if (isok1) {

                                boolean isok = DateUtils.shijian(yuzhi_info.riqi, String.format("%4d-%02d", i, i1 + 1));
                                if (isok) {
                                    yuzhi_info.riqi_shangcijianyan = String.format("%4d-%02d-%02d", i, i1 + 1, i2);
                                    tv_shangCiJianYan_time.setText(yuzhi_info.riqi_shangcijianyan);
                                    d.dismiss();
                                } else {
                                    yuzhi_info.riqi_shangcijianyan = "";
                                    tv_shangCiJianYan_time.setText(yuzhi_info.riqi_shangcijianyan);
                                }
                            } else {
                                yuzhi_info.riqi_shangcijianyan = "";
                                tv_shangCiJianYan_time.setText(yuzhi_info.riqi_shangcijianyan);
                            }

                        }
                    });
                    d.show();
                }
            });
            final EditText et_pizhong = d.findViewById(R.id.et_pizhong);
            final EditText et_prefix = d.findViewById(R.id.et_prefix);
            final EditText tv_gy_max = d.findViewById(R.id.tv_gy_max);
            final EditText tv_gy_min = d.findViewById(R.id.tv_gy_min);
            final Spinner et_qiti = d.findViewById(R.id.et_qiti);
            final Spinner et_changjia = d.findViewById(R.id.et_changjia);
            final Spinner sp_guige = d.findViewById(R.id.sp_guige);

            ArrayAdapter type_adapter = null; //new ArrayAdapter<IDNameBean>(act,
            type_adapter = new ArrayAdapter<IDNameBean>(act,
                    R.layout.gp_id_item, guige_list.toArray(new IDNameBean[0]));
            sp_guige.setAdapter(type_adapter);
            sp_guige.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    IDNameBean b = (IDNameBean) adapterView.getSelectedItem();
                    yuzhi_info.guige = b;//.name;
                    String str1 = b.name.substring(0, b.name.indexOf("kg"));
                    et_pizhong.setText(str1);
                    Selection.setSelection(et_pizhong.getText(), et_pizhong.getText().toString().length());
                    FileUtils.saveObject(yuzhi_file, yuzhi_info);
                    updateYuZhiInfo();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });


            type_adapter = new ArrayAdapter<IDNameBean>(act,
                    R.layout.gp_id_item, qiti_list.toArray(new IDNameBean[0]));
            et_qiti.setAdapter(type_adapter);

            type_adapter = new ArrayAdapter<GPChangJiaBean>(act,
                    R.layout.gp_id_item, cj_list.toArray(new GPChangJiaBean[0]));
            et_changjia.setAdapter(type_adapter);


            if (yuzhi_info != null) {
                et_prefix.setText(yuzhi_info.prefix);
                tv_time.setText(yuzhi_info.riqi);
                tv_gy_min.setText(String.valueOf(yuzhi_info.min_gy_len));
                tv_gy_max.setText(String.valueOf(yuzhi_info.max_gy_len));
                et_pizhong.setText(String.valueOf(yuzhi_info.pi_zhong));

                boolean sel = false;
                for (int i = 0; i < cj_list.size(); i++) {
                    GPChangJiaBean cj = cj_list.get(i);
                    if (cj.name != null && cj.name.equals(yuzhi_info.chang_jia)) {
                        et_changjia.setSelection(i);
                        sel = true;
                        break;
                    }
                }
                if (!sel) {
                    et_changjia.setSelection(0);
                }
                sel = false;
                for (int i = 0; i < guige_list.size(); i++) {
                    IDNameBean cj = guige_list.get(i);
                    if (cj.id != 0 && yuzhi_info.guige != null &&
                            cj.id == yuzhi_info.guige.id) {
                        sp_guige.setSelection(i);
                        sel = true;
                        break;
                    }
                }
                if (!sel) {
                    sp_guige.setSelection(0);
                }

                sel = false;
                for (int i = 0; i < qiti_list.size(); i++) {
                    IDNameBean qiti = qiti_list.get(i);
                    if (qiti.name != null && qiti.name.equals(yuzhi_info.leixing)) {
                        et_qiti.setSelection(i);
                        sel = true;
                        break;
                    }
                }
                if (!sel) {
                    et_qiti.setSelection(0);
                }
            }

            d.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    d.dismiss();
                }
            });
            d.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        int max_gy_len = Integer.valueOf(tv_gy_max.getText().toString().trim());
                        int min_gy_len = Integer.valueOf(tv_gy_min.getText().toString().trim());
                        if (min_gy_len > max_gy_len) {
                            throw new Exception();
                        }
                        yuzhi_info.max_gy_len = max_gy_len;
                        yuzhi_info.min_gy_len = min_gy_len;
                    } catch (Exception e) {
                        Utils.toastLONG("气瓶长度输入错误！");
                        return;
                    }
                    try {
                    } catch (Exception e) {
                    }

                    d.dismiss();
                    IDNameBean ge = (IDNameBean) sp_guige.getSelectedItem();
                    // IDNameBean lx = (IDNameBean) sp_leixing.getSelectedItem();
                    double d = 15.5;
                    GPChangJiaBean cj_bean = (GPChangJiaBean) et_changjia.getSelectedItem();
                    IDNameBean qiti_bean = (IDNameBean) et_qiti.getSelectedItem();
                    try {
                        d = Double.valueOf(et_pizhong.getText().toString().trim());
                    } catch (Exception e) {
                    }
                    yuzhi_info.chang_jia = cj_bean.name;
                    yuzhi_info.chang_jia_id = cj_bean.id;
                    yuzhi_info.leixing = qiti_bean.name;
                    yuzhi_info.qiTiLeiXing = (int) qiti_bean.id;
                    //yuzhi_info.leixing = lx.name;
                    yuzhi_info.guige = ge;//.name;
                    yuzhi_info.pi_zhong = d;
                    yuzhi_info.prefix = et_prefix.getText().toString().trim().toUpperCase();

                    FileUtils.saveObject(yuzhi_file, yuzhi_info);
                    updateYuZhiInfo();
                }
            });
            d.show();
        } else if (R.id.btn_reset == id) { //清零
            list.removeAll(list);
            adapter.setData(list);
            tv_all.setText("总计: " + list.size());
        } else if (R.id.btn_finish == id) { // 结束本次建档
            jieShu();
        }
    }

    //final List<IDNameBean> lx_list = new LinkedList<>();
    final List<IDNameBean> guige_list = new LinkedList<>();
    final List<GPChangJiaBean> cj_list = new LinkedList<>();
    final List<IDNameBean> qiti_list = new LinkedList<>();


    private void loadYuZhiInfo() {
        act.showProgress();
        // get data
        BackTask.post(new BackFrontTask() {
            @Override
            public void runFront() {
                act.hideProgress();
                if (/*lx.isEmpty() ||*/ ge.isEmpty()) {
                    Utils.toastLONG("无法获取气瓶数据！");
                    act.getPVC().popMe(PVCZJianDang.this);
                    return;
                }
                // lx_list.clear();
                guige_list.clear();
                cj_list.clear();
                qiti_list.clear();
                //lx_list.addAll(lx);
                guige_list.addAll(ge);
                cj_list.addAll(cj);
                addQitiData();

                if (yuzhi_info == null) {
                    yuzhi_info = new CZJianDangYuZhi();
                    yuzhi_info.guige = guige_list.get(0); //.name;
                    //yuzhi_info.leixing = lx_list.get(0).name;
                    updateYuZhiInfo();
                }

            }

            //List<IDNameBean> lx = new LinkedList<>();
            List<IDNameBean> ge = new LinkedList<>();
            List<GPChangJiaBean> cj = new LinkedList<>();

            @Override
            public void runBack() {
                try {
                    Constant.getLX_GE(ge);

                    JSONObject j_lx = CZNetUtils.getCZHttp("changJia/chaXun");
                    JSONArray ja = j_lx.getJSONArray("result");
                    for (int i = 0; i < ja.length(); i++) {
                        GPChangJiaBean gp = Constant.gson.fromJson(ja.getJSONObject(i).toString(), GPChangJiaBean.class);
                        cj.add(gp);
                    }

                } catch (Exception e) {
                    if (TextUtils.isEmpty(err_msg)) {
                        err_msg = act.getString(R.string.tip_common_err);
                    }
                }
            }

            String err_msg;
        });

    }

    @Override
    public void onAttach(boolean firstShow) {
        super.onAttach(firstShow);
        if (firstShow) {
            loadYuZhiInfo();
        }
        if (yuzhi_info != null && TextUtils.isEmpty(yuzhi_info.riqi)) {
            Utils.toastLONG("请填写预置日期");
            //onClick(mMainView.findViewById(R.id.btn_yuzhi));
        }
    }

    private void addQitiData() {

        IDNameBean qitiTypeBean1 = new IDNameBean();
        qitiTypeBean1.id = 1;
        qitiTypeBean1.name = "液化石油气";

        IDNameBean qitiTypeBean2 = new IDNameBean();
        qitiTypeBean2.id = 2;
        qitiTypeBean2.name = "丙烷";

        IDNameBean qitiTypeBean3 = new IDNameBean();
        qitiTypeBean3.id = 3;
        qitiTypeBean3.name = "丁烷";

        IDNameBean qitiTypeBean4 = new IDNameBean();
        qitiTypeBean4.id = 4;
        qitiTypeBean4.name = "天然气";

        qiti_list.add(qitiTypeBean1);
        qiti_list.add(qitiTypeBean2);
        qiti_list.add(qitiTypeBean3);
        qiti_list.add(qitiTypeBean4);

    }


    //返回键监听
    @Override
    public boolean doBackPressed() {
        jieShu();
        return true;
    }

    //确认结束本次建档对话框
    private void jieShu() {
        if (builder == null) {
            builder = new AlertDialog.Builder(act);
        }
        builder.setTitle("确定要结束本次建档吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        act.getPVC().pop();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

            }
        });

        builder.create().show();
    }

    public void onYearMonthDayTime(final TextView textView) {

        TimePickerView pvTime = new TimePickerBuilder(act, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
                String dateString = formatter.format(date);

                boolean isok = DateUtils.shijianPanduan(dateString, "yyyy-MM");
                if (isok) {
                    yuzhi_info.riqi = dateString;
                    textView.setText(yuzhi_info.riqi);
                    tv_prefix.setText("编号前缀: " + yuzhi_info.prefix + "  生产日期: " + yuzhi_info.riqi);
                } else {
                    yuzhi_info.riqi = "";
                    textView.setText(yuzhi_info.riqi);
                    tv_prefix.setText("编号前缀: " + yuzhi_info.prefix + "  生产日期: " + yuzhi_info.riqi);
                }
            }
        })
                .setType(new boolean[]{true, true, false, false, false, false})// 默认全部显示
                .setCancelText("取消")//取消按钮文字
                .setSubmitText("确定")//确认按钮文字
//                .setTitleSize(20)//标题文字大小
                .setTitleText("选择年月")//标题文字
                .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(true)//是否循环滚动
//                .setTitleColor(Color.BLACK)//标题文字颜色
//                .setSubmitColor(Color.BLUE)//确定按钮文字颜色
//                .setCancelColor(Color.BLUE)//取消按钮文字颜色
//                .setTitleBgColor(0xFF666666)//标题背景颜色 Night mode
//                .setBgColor(0xFF333333)//滚轮背景颜色 Night mode
//                .setLabel("年","月")//默认设置为年月日时分秒
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .isDialog(true)//是否显示为对话框样式
                .build();
        pvTime.show();
    }
}
