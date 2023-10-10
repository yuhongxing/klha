package com.czsy.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;

import com.bumptech.glide.Glide;
import com.czsy.*;
import com.czsy.android.R;
import com.czsy.bean.CommonOrderBean;
import com.czsy.bean.GangPingBean;
import com.czsy.bean.XiaDanShangPinBean;
import com.czsy.ui.sqg.AbsPVGuestOrder;

import mylib.app.BackFrontTask;
import mylib.app.BackTask;
import mylib.app.MyLog;
import mylib.ui.list.AbstractAdapter;
import mylib.utils.CameraUtils;
import mylib.utils.EditTextUtils;
import mylib.utils.FileUtils;
import mylib.utils.ImageUtils;
import mylib.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

//  扫空瓶
abstract public class AbsPVScanEmpty extends
        AbsPVGuestOrder implements INFCHandler, View.OnClickListener, Constant.GPChecker {
    private final boolean has_yuqi_yintuikuang; // (是否退瓶)是否需要填入余期和退款
    private boolean isHuishou; // 是否是回收单
    private boolean isGouqi = false;//是否为购气单
    //    private boolean is //如果是购气订单，则扫芯片后就拍摄押金图片（每张都拍）
    String pic_file;//如果是购气订单,押金单号地址
    String url = "";//dialog中图片的地址
    //    Bitmap bmp;
    ImageView img_dialog;

    private class ViewHolder implements View.OnClickListener {
        CommonOrderBean.GangPingInfo info;
        final View root, btn_modify;
        final TextView tv_content, tv_title;
        final ImageView image_yajin;

        private ViewHolder(View r) {
            this.tv_content = r.findViewById(R.id.tv_content);
            this.tv_title = r.findViewById(R.id.tv_title);
            btn_modify = r.findViewById(R.id.btn_modify);
            image_yajin = r.findViewById(R.id.image_yajin);
            image_yajin.setOnClickListener(this);
            root = r;
            r.setTag(this);
            if (has_yuqi_yintuikuang || isHuishou) {
                btn_modify.setVisibility(View.VISIBLE);
                btn_modify.setOnClickListener(this);
                image_yajin.setVisibility(View.GONE);
            } else {
                btn_modify.setVisibility(View.GONE);
            }
            if (isGouqi) {
                image_yajin.setVisibility(View.VISIBLE);
            } else {
                image_yajin.setVisibility(View.GONE);
            }
        }

        void bind(int pos, CommonOrderBean.GangPingInfo b, boolean isEmpty, boolean is_nfc) {
            info = b;
            TextView tv = (TextView) tv_title;
            if (TextUtils.isEmpty(info.gp_bean.gangPingHao)) { // is_nfc
                tv.setText("" + (pos) + ": " +
                        TheApp.sInst.getString(R.string.title_nfc) + ' ' + info.gp_bean.xinPianHao
                        + "   规格: " + info.gp_bean.guiGeName
                        + " \n气体类型：" + info.gp_bean.qiTiLeiXing
                        + " \n空瓶重量：" + info.jingZhong + "kg");
            } else {
                tv.setText("" + (pos) + ": " +
                        TheApp.sInst.getString(R.string.title_gp_id) + ' ' + info.gp_bean.gangPingHao
                        + "   规格: " + info.gp_bean.guiGeName
                        + " \n气体类型：" + info.gp_bean.qiTiLeiXing
                        + " \n空瓶重量：" + info.jingZhong + "kg");
            }
            if (!has_yuqi_yintuikuang) {//不是退瓶单

                btn_modify.setVisibility(View.GONE);

                if (isGouqi) {
                    if (info.yajin_file != null && !info.yajin_file.equals("")) {
                        Glide.with(act).load(CZNetUtils.svr_host + info.yajin_file).into(image_yajin);
                    } else {
                        image_yajin.setImageResource(android.R.drawable.ic_input_add);
                    }
                    if (info.yajin_number != null && !info.yajin_number.equals("")) {
                        tv_content.setText("纸质单号：" + info.yajin_number);
                    } else {
                        tv_content.setText("");
                    }

                    //判断是否需要显示拍押金
                    if (info.isShowImage) {
                        image_yajin.setVisibility(View.VISIBLE);
                    } else {
                        image_yajin.setVisibility(View.GONE);
                    }
                }

            } else {

                btn_modify.setVisibility(View.VISIBLE);
                if (isHuishou) {
                    tv_content.setText("余气：" + info.yu_qi + "  余气金额: " + info.yin_tui_kuan + "  气瓶金额：" + info.gangping_jine);

                } else {
                    tv_content.setText("退气：" + info.yu_qi + "  应退款: " + info.yin_tui_kuan);

                }
            }

        }

        @Override
        public void onClick(View view) {
            if (info == null) {
                return;
            }

            final Dialog d = new Dialog(view.getContext());
            d.requestWindowFeature(Window.FEATURE_NO_TITLE);
            Window window = d.getWindow();
            window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(layoutParams);

            window.setBackgroundDrawableResource(android.R.color.transparent);
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

            if (view == btn_modify) {
                d.setContentView(R.layout.dialog_empty_left);
                final EditText et_left_gas = d.findViewById(R.id.et_left_gas);
                final EditText et_left_money = d.findViewById(R.id.et_left_money);
                final TextView tv_left_weight = d.findViewById(R.id.tv_left_weight);
                final TextView tv_left_money = d.findViewById(R.id.tv_left_money);
                final EditText et_gangping_money = d.findViewById(R.id.et_gangping_money);

                EditTextUtils.afterDotTwo(et_left_gas);
                EditTextUtils.afterDotTwo(et_left_money);
                //设置字符限制
//                et_left_gas.setFilters(new InputFilter[]{new InputFilter() {
//                    @Override
//                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
//                        if (source.equals(".") && dest.toString().length() == 0) {
//                            return "0.";
//                        }
//                        if (dest.toString().contains(".")) {
//                            int index = dest.toString().indexOf(".");
//                            int length = dest.toString().substring(index).length();
//                            if (length == 3) {
//                                return "";
//                            }
//                        }
//                        return null;
//                    }
//                }});

//                et_left_money.setFilters(new InputFilter[]{new InputFilter() {
//                    @Override
//                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
//                        if (source.equals(".") && dest.toString().length() == 0) {
//                            return "0.";
//                        }
//                        if (dest.toString().contains(".")) {
//                            int index = dest.toString().indexOf(".");
//                            int length = dest.toString().substring(index).length();
//                            if (length == 3) {
//                                return "";
//                            }
//                        }
//                        return null;
//                    }
//                }});

                if (isHuishou) {
                    LinearLayout line_gangping_money = d.findViewById(R.id.line_gangping_money);
                    line_gangping_money.setVisibility(View.VISIBLE);
                    tv_left_weight.setText("余气");
                    tv_left_money.setText("余气金额");

                }
                et_left_gas.setText(String.format("%.2f", info.yu_qi));
                et_left_money.setText(String.format("%.2f", info.yin_tui_kuan));
                et_gangping_money.setText(String.format("%.2f", info.gangping_jine));
                d.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            info.yu_qi = Double.valueOf(et_left_gas.getText().toString());
                            info.yin_tui_kuan = Double.valueOf(et_left_money.getText().toString());
                            info.gangping_jine = Double.valueOf(et_gangping_money.getText().toString());

                            if (isHuishou) {
                                tv_content.setText("余气：" + info.yu_qi + "  余气金额: " + info.yin_tui_kuan + "  气瓶金额：" + info.gangping_jine);
                            } else {
                                tv_content.setText("退气：" + info.yu_qi + "  应退款: " + info.yin_tui_kuan);

                            }

                        } catch (Exception e) {
                        }
                        d.dismiss();
                    }
                });
            } else if (view == image_yajin) {
                url = "";
                d.setContentView(R.layout.dialog_heavy_addyajin);
                img_dialog = d.findViewById(R.id.image_yajin);
                final EditText etDanhao = d.findViewById(R.id.et_danhao);

                etDanhao.setText(info.yajin_number);
                if (info.yajin_file != null && !info.yajin_file.equals("")) {
                    Glide.with(act).load(CZNetUtils.svr_host + info.yajin_file).into(img_dialog);

                }

                img_dialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pic_file = FileUtils.getDir(FileUtils.DirType.pic) + System.currentTimeMillis() + ".jpg";
                        CameraUtils.doCamera(act, pic_file);
                    }
                });
                d.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (url.isEmpty() && info.yajin_file.isEmpty()) {
                            Utils.toastSHORT("请先拍照");
                            return;
                        }
                        if (etDanhao.getText().toString().isEmpty()) {
                            Utils.toastSHORT("请填写纸质单号");
                            return;
                        }

                        BackTask.post(new CZBackTask(act) {
                            @Override
                            protected void parseResult(JSONObject jdata) throws Exception {

                            }

                            @Override
                            protected String getInputParam() throws Exception {
                                return "{\"zhiZhiDanHao\":\"" + etDanhao.getText().toString() + "\"}";
                            }

                            @Override
                            protected String getURL() {
                                return "caiWu/checkYaJinDanHao";
                            }

                            @Override
                            protected void runFront2() {

                                tv_content.setText("纸质单号：" + etDanhao.getText().toString());
                                if (!url.isEmpty()) {
                                    info.yajin_file = url;
                                }
                                info.yajin_number = "";
                                List<String> gpInfoList = new LinkedList<>();
                                for (CommonOrderBean.GangPingInfo gangPingInfo : nfc_gp_list) {
                                    gpInfoList.add(gangPingInfo.yajin_number);
                                }

                                boolean isHas = gpInfoList.contains(etDanhao.getText().toString());

                                if (!isHas) {
                                    info.yajin_number = etDanhao.getText().toString();
                                    d.dismiss();
                                } else {
                                    Utils.toastSHORT("押金单号重复,请重新输入");
                                    return;
                                }


                            }
                        });


                    }
                });

            }


            d.show();


        }
    }

    // nfc 扫的瓶
    final protected List<CommonOrderBean.GangPingInfo> nfc_gp_list = new LinkedList<>();
    // 输入的瓶号
    final protected List<CommonOrderBean.GangPingInfo> gp_id_list = new LinkedList<>();

    protected RadioButton rb_nfc_id, rb_gp_id;
    protected EditText et_id;
    protected ListView list_view;
    protected TextView tv_title, btn_next, btn_no_empty, btn_clear;
    protected View btn_ok, bottom_container;
    protected RadioGroup rg_id;
    //protected final boolean can_input_gangping; // 是否允许输入钢瓶号
    protected final boolean is_tuipin;

    protected AbstractAdapter<CommonOrderBean.GangPingInfo> gp_adapter =
            new AbstractAdapter<CommonOrderBean.GangPingInfo>() {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    CommonOrderBean.GangPingInfo b = null;
                    boolean is_nfc = true;
                    int nfc_size = nfc_gp_list.size();
                    if (position >= nfc_size) {
                        is_nfc = false;
                        b = gp_id_list.get(position - nfc_size);
                    } else {
                        is_nfc = true;
                        b = nfc_gp_list.get(position);
                    }
                    if (null == convertView) {
                        convertView = View.inflate(parent.getContext(), R.layout.gp_scan_item, null);
                        new ViewHolder(convertView);
                    }
                    ViewHolder vh = (ViewHolder) convertView.getTag();
                    vh.bind(getCount() - position, b, AbsPVScanEmpty.this.isEmpty(), is_nfc);
                    return convertView;
                }

                @Override
                public int getCount() {
                    return nfc_gp_list.size() + gp_id_list.size();
                }
            };

    public AbsPVScanEmpty(MainActivity a, CommonOrderBean b, boolean has_yuqi) {
        super(a, b);
        this.has_yuqi_yintuikuang = has_yuqi;
        is_tuipin = b == null ? false : b.isTuiPingDan();
    }

    public AbsPVScanEmpty(MainActivity a, CommonOrderBean b, boolean has_yuqi, boolean isHuishou) {
        super(a, b);
        this.has_yuqi_yintuikuang = has_yuqi;
        this.isHuishou = isHuishou;
        is_tuipin = b == null ? false : b.isTuiPingDan();
    }

    public AbsPVScanEmpty(MainActivity a, CommonOrderBean b, boolean has_yuqi, boolean isHuishou, boolean isGouqi) {
        super(a, b);
        this.has_yuqi_yintuikuang = has_yuqi;
        this.isHuishou = isHuishou;
        this.isGouqi = isGouqi;
        is_tuipin = b == null ? false : b.isTuiPingDan();
    }

    @Override
    protected void createMainView(Context ctx) {
        CameraUtils.initPhotoError();
        mMainView = View.inflate(ctx, R.layout.pv_guest_order_empty, null);
        mMainView.findViewById(R.id.iv_back).setOnClickListener(this);
        bottom_container = mMainView.findViewById(R.id.bottom_container);
        btn_clear = mMainView.findViewById(R.id.btn_clear);
        et_id = mMainView.findViewById(R.id.et_id);
        tv_title = mMainView.findViewById(R.id.tv_title);
        btn_next = mMainView.findViewById(R.id.btn_next);
        btn_no_empty = mMainView.findViewById(R.id.btn_no_empty);
        btn_ok = mMainView.findViewById(R.id.btn_ok);
        rb_nfc_id = mMainView.findViewById(R.id.rb_nfc_id);
        rb_gp_id = mMainView.findViewById(R.id.rb_gp_id);
        rg_id = mMainView.findViewById(R.id.rg_id);
        rg_id.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (rb_gp_id.isChecked()) {
                    et_id.setHint(R.string.tip_input_gp);
                    et_id.setEnabled(true);
                    btn_ok.setVisibility(View.VISIBLE);
                } else {
                    et_id.setHint(R.string.tip_input_80);
                    et_id.setEnabled(false);
                    btn_ok.setVisibility(View.GONE);
                }
                et_id.setText(null);
            }
        });
        btn_ok.setOnClickListener(this);
        list_view = mMainView.findViewById(R.id.list_view);
        list_view.setAdapter(gp_adapter);
        list_view.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                AlertDialog.Builder ab = new AlertDialog.Builder(act);
                ab.setTitle(R.string.app_name).setMessage(R.string.tip_sur_delete).setNegativeButton(
                        android.R.string.cancel, null
                ).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int ii) {
                        dialogInterface.dismiss();
                        if (i >= nfc_gp_list.size()) {
                            gp_id_list.remove(i - nfc_gp_list.size());
                        } else {
                            nfc_gp_list.remove(i);
                        }
                        gp_adapter.notifyDataSetChanged();
                    }
                });
                ab.show();
                return true;
            }
        });

        mMainView.findViewById(R.id.btn_clear).setOnClickListener(this);
        mMainView.findViewById(R.id.btn_next).setOnClickListener(this);
        mMainView.findViewById(R.id.btn_no_empty).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.btn_ok == id) {
            String id_string = et_id.getText().toString().trim().toUpperCase();
            if (TextUtils.isEmpty(id_string)) {
                return;
            }
            boolean is_nfc = rb_nfc_id.isChecked();
            if (is_nfc) {
                return;
            }
            if (existGangPing(id_string)) {
                return;
            }
            GangPingBean b = new GangPingBean();
            if (is_nfc) {
                b.xinPianHao = id_string;
            } else {
                b.gangPingHao = id_string;
            }

            final CommonOrderBean.GangPingInfo info = new CommonOrderBean.GangPingInfo(isEmpty());
            info.gp_bean = b;
            info.yu_qi = 0.0;
            info.yin_tui_kuan = 0.0;
            info.yajin_number = "";
            info.yajin_file = "";
            // check gp each time
            Constant.getIdFromServer(act, info, this,
                    new ReturnRunable<Boolean>() {
                        @Override
                        public void run() {

                            //购气订单-->给钢瓶info bean 加上属性  判断扫重瓶时是否显示押金图片
                            if (isGouqi) {
                                for (CommonOrderBean.GangPingInfo bean : client_order_bean.empty_list) {

                                    Log.d("xianshi", "empty_list--bean.isShowImage-->" + bean.isShowImage);

                                    if (bean.gp_bean.guiGe == info.gp_bean.guiGe && bean.isShowImage) {

                                        info.isShowImage = false;
                                        bean.isShowImage = false;
                                        Log.d("xianshi", "bean.isShowImage-->" + bean.isShowImage);
                                        break;
                                    }


                                }
                            }
                            Log.d("xing", "ReturnRunable run()");
                            gp_id_list.add(0, info);
                            gp_adapter.notifyDataSetChanged();
                        }
                    });

            et_id.setText(null);
        } else if (R.id.btn_clear == id) {
            /**
             * 此解决：
             * 处理订单，有空瓶的情况下，重大于空时，
             * 扫完重瓶返回上级页面，再进入扫重瓶页面（或清空重扫），
             * 扫瓶还是显示全部都有押金条上传按钮。
             */
            for (CommonOrderBean.GangPingInfo bean : client_order_bean.empty_list) {
                bean.isShowImage = true;
            }
            nfc_gp_list.clear();
            gp_id_list.clear();
            gp_adapter.notifyDataSetChanged();
        } else if (R.id.btn_next == id) { // 下一步扫重瓶
            if (nfc_gp_list.isEmpty() && gp_id_list.isEmpty()) {
                Utils.toastSHORT("请先扫瓶");//请先扫"空"瓶
                return;
            }
            onClickScanHeavy();
        } else if (R.id.btn_no_empty == id) { // 无空瓶
            onClickNoEmpty();

        } else if (R.id.iv_back == id) {
            act.getPVC().pop();
        }
    }

    protected boolean isEmpty() {
        return true;
    }

    protected abstract void onClickScanHeavy();

    protected abstract void onClickNoEmpty();

    protected boolean existGangPing(String id) {

        Log.d("existGangPing", "AbsPVScanEmpty--" + id);
        for (CommonOrderBean.GangPingInfo gp : nfc_gp_list) {
            Log.d("existGangPing", "AbsPVScanEmpty--nfc_gp_list--芯片号-->" + gp.gp_bean.xinPianHao);
            Log.d("existGangPing", "AbsPVScanEmpty--nfc_gp_list--钢瓶号-->" + gp.gp_bean.gangPingHao);
            if (gp.gp_bean.xinPianHao.equals(id) || gp.gp_bean.gangPingHao.equals(id)) {
                Log.d("existGangPing", "nfc-List--return true");
                Utils.toast(R.string.tip_exist_gp);
                return true;
            }
        }
        Log.d("existGangPing", "AbsPVScanEmpty------------------------");
        for (CommonOrderBean.GangPingInfo gp : gp_id_list) {
            Log.d("existGangPing", "AbsPVScanEmpty--gp_id_list--芯片号-->" + gp.gp_bean.xinPianHao);
            Log.d("existGangPing", "AbsPVScanEmpty--gp_id_list--钢瓶号-->" + gp.gp_bean.gangPingHao);
            if (id.equals(gp.gp_bean.gangPingHao) || id.equals(gp.gp_bean.xinPianHao)) {
                Log.d("existGangPing", "gp-List--return true");
                Utils.toast(R.string.tip_exist_gp);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onNFCIntent(final NFCInfo data) {
        if (existGangPing(data.chip_sn)) {
            return;
        }
        final GangPingBean b = new GangPingBean(data);
        final CommonOrderBean.GangPingInfo info = new CommonOrderBean.GangPingInfo(isEmpty());
        info.gp_bean = b;
        info.yu_qi = 0.0;
        info.yin_tui_kuan = 0.0;
        info.yajin_number = "";
        info.yajin_file = "";
        // check gp each time
        Constant.getIdFromServer(act, info, this,
                new ReturnRunable<Boolean>() {
                    @Override
                    public void run() {

                        //购气订单-->给钢瓶info bean 加上属性  判断扫重瓶时是否显示押金图片
                        if (isGouqi) {
                            for (CommonOrderBean.GangPingInfo bean : client_order_bean.empty_list) {

                                Log.d("xianshi", "empty_list--bean.isShowImage-->" + bean.isShowImage);

                                if (bean.gp_bean.guiGe == info.gp_bean.guiGe && bean.isShowImage) {

                                    info.isShowImage = false;
                                    bean.isShowImage = false;
                                    Log.d("xianshi", "bean.isShowImage-->" + bean.isShowImage);
                                    break;
                                }


                            }
                        }
                        Log.d("xianshi", "isShowImage-->" + info.isShowImage);
                        if (existGangPing(info.gp_bean.xinPianHao)) {
                            return;
                        }
                        nfc_gp_list.add(0, info);
                        gp_adapter.notifyDataSetChanged();
                    }
                });
    }

    protected void getIdFromServer(final ReturnRunable<Boolean> call_back) {
        act.showProgress();
        // xin pian hao -> gp info
        final Map<String, CommonOrderBean.GangPingInfo> gp_map = new HashMap<>();
        final Map<String, CommonOrderBean.GangPingInfo> nfc_map = new HashMap<>();
        for (CommonOrderBean.GangPingInfo info : nfc_gp_list) {
            nfc_map.put(info.gp_bean.xinPianHao, info);
        }
        for (CommonOrderBean.GangPingInfo info : gp_id_list) {
            gp_map.put(info.gp_bean.gangPingHao, info);
        }
        call_back.ret = false;
        BackTask.post(new CZBackTask(act) {
            @Override
            protected void parseResult(JSONObject ret) throws Exception {

                Log.d("xing", "getIdFromServer");
                // JSONArray
                JSONArray result = ret.getJSONArray("result");
                if (result.length() == 0) {
                    call_back.ret = false;
                    call_back.msg = TheApp.sInst.getString(R.string.tip_gp_not_found);
                    return;
                }
                for (int i = 0; i < result.length(); i++) {
                    JSONObject jo = result.getJSONObject(i);
                    GangPingBean gpb = Constant.gson.fromJson(jo.toString(), GangPingBean.class);
                    if (gpb.xinPianHao != null && nfc_map.containsKey(gpb.xinPianHao)) {
                        CommonOrderBean.GangPingInfo info = nfc_map.get(gpb.xinPianHao);
                        info.gp_bean = gpb;
                    } else if (gpb.gangPingHao != null && gp_map.containsKey(gpb.gangPingHao)) {
                        CommonOrderBean.GangPingInfo info = gp_map.get(gpb.gangPingHao);
                        info.gp_bean = gpb;
                    }
                }

                // 商品set，去重， FIXME： 这里新算法有误！xiaDanShangPinList 没有返回了 ？
                Set<String> dd_sp_ge = null;
                // 查看下单商品
                if (client_order_bean != null && client_order_bean.xiaDanShangPinList != null) {
                    dd_sp_ge = new HashSet<>();
                    for (XiaDanShangPinBean sp : client_order_bean.xiaDanShangPinList) {
                        dd_sp_ge.add(sp.shangPin + sp.guiGe);
                    }
                }
                /// check if is ok
                if (dd_sp_ge != null) {
                    for (List<CommonOrderBean.GangPingInfo> list : new List[]{
                            gp_id_list, nfc_gp_list
                    }) {
                        for (CommonOrderBean.GangPingInfo info : list) {
                            if (info.gp_bean == null || info.gp_bean.id <= 0) { //|| info.gp_bean.guiGe == null) {
                                call_back.ret = false;
                                call_back.msg = list == gp_id_list ? "不正确的id气瓶!" : "不正确的NFC芯片";
                                return;
                            }
                            if (!dd_sp_ge.contains(/*info.gp_bean.leiXing +*/ info.gp_bean.guiGeName)) {
                                call_back.ret = false;
                                call_back.msg = /*info.gp_bean.leiXing +*/ info.gp_bean.guiGeName + "不在订单中！";
                                return;
                            }
                        }
                    }
                }
                call_back.ret = true;
            }

            @Override
            protected String getInputParam() throws Exception {
                JSONArray ja = new JSONArray();
                boolean is_empty = isEmpty();
                for (CommonOrderBean.GangPingInfo info : nfc_map.values()) {
                    JSONObject jo = new JSONObject();
                    jo.put("xinPianHao", info.gp_bean.xinPianHao);
                    ja.put(jo);
                }
                for (CommonOrderBean.GangPingInfo info : gp_map.values()) {
                    JSONObject jo = new JSONObject();
                    jo.put("gangPingHao", info.gp_bean.gangPingHao);
                    ja.put(jo);
                }
                return ja.toString();
            }

            @Override
            protected String getURL() {
                return "gangPing/piLiangChaXun";
            }

            @Override
            protected void runFront2() {
                if (call_back != null) {
                    call_back.run();
                }
            }
        });

    }

    protected void createOrderIfNeeded(final ReturnRunable<Boolean> task) {
        if (!client_order_bean.isNewOrder()) {
            MyLog.LOGD("不需要预创建订单");
            task.ret = true;
            task.run();
            return;
        }
        // 如果是代客下单，这里创建一个新订单
        act.showProgress();
        BackTask.post(new CZBackTask(act) {
            @Override
            protected void parseResult(JSONObject jdata) throws Exception {
                String result = jdata.getString("result");
                client_order_bean.dingDanHao = result;
            }

            @Override
            protected void onError(Exception e) {
                err_msg = CZNetUtils.CZNetErr.msg;
            }

            @Override
            protected String getInputParam() throws Exception {
                // 配送员
                JSONObject j = new JSONObject();
                j.put("diZhi", client_order_bean.diZhi);
                j.put("keHuId", client_order_bean.keHuId);
                j.put("louCeng", client_order_bean.louCeng);
                j.put("dingDanType",
                        is_tuipin ? Constant.dingdan_type_tuipin :
                                Constant.dingdan_type_gouqi);
                JSONArray gp_arr = new JSONArray();
                j.put("gangPingList", gp_arr);
                List<CommonOrderBean.GangPingInfo> list = is_tuipin ? client_order_bean.empty_list :
                        client_order_bean.heavy_list;
                for (CommonOrderBean.GangPingInfo info : list) {
                    JSONObject jj = new JSONObject();
                    jj.put("count", 1);
                    jj.put("guiGe", info.gp_bean.guiGe);
                    jj.put("qiTiLeiXingId", info.gp_bean.qiTiLeiXingId);
                    gp_arr.put(jj);
                }

                return j.toString();
            }

            @Override
            protected String getURL() {
                return "dingDan/chuangJianByPeiSongYuan";
            }

            @Override
            protected void runFront2() {
                task.ret = true;
                task.run();
            }
        });
    }

    @Override
    public String checkGanPing(GangPingBean gp) {
        return null;
    }


    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        final String file = CameraUtils.getPicutre(data, requestCode,
                resultCode, act, pic_file);
        if (ImageUtils.isImage(file)) {

            act.showProgress(false);
            BackTask.post(new BackFrontTask() {

                Bitmap bmp;

                @Override
                public void runFront() {
                    if (bmp == null) {
                        return;
                    }
                    img_dialog.setImageBitmap(bmp);

                    Log.d("xing", "url--》" + url);

//                    Glide.with(act).load(CZNetUtils.svr_host + url).into(img_dialog);
                    act.hideProgress();

                }

                @Override
                public void runBack() {
                    bmp = ImageUtils.resizeBitmap(ImageUtils.getFileBmp(file), 100);
                    try {

                        url = CZNetUtils.upload(pic_file);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            return true;
        } else {
            pic_file = null;
            return super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
