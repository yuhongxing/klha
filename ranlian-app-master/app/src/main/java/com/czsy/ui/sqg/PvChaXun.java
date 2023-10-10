package com.czsy.ui.sqg;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.czsy.CZBackTask;
import com.czsy.Constant;
import com.czsy.INFCHandler;
import com.czsy.ReturnRunable;
import com.czsy.android.R;
import com.czsy.bean.CommonOrderBean;
import com.czsy.bean.GangPingBean;
import com.czsy.bean.GangPingChaXunBean;
import com.czsy.ui.MainActivity;
import com.czsy.ui.PVSimpleText;

import org.json.JSONObject;

import mylib.app.BackTask;
import mylib.utils.Utils;

/**
 * 配送员查询钢瓶
 */
public class PvChaXun extends AbsPVGuestOrder implements INFCHandler, View.OnClickListener {

    protected RadioButton rb_nfc_id, rb_gp_id;
    protected EditText et_id;
    protected TextView tv_title, tv_content;
    protected View btn_ok;
    protected RadioGroup rg_id;
    private LinearLayout line1, line_kehu;
    private TextView tv_kehu, tv_gangping, tv_xinpian, tv_jiandang, tv_zuihou,
            tv_baofei, tv_state, tv_ping, tv_changzhan, tv_name,tv_num, tv_tel, tv_type,
            tv_address, tv_gongyingzhan, tv_kaihu,tv_gengxinriqi;
    private GangPingChaXunBean bean;

    public PvChaXun(MainActivity a, CommonOrderBean b) {
        super(a, b);
    }

    @Override
    protected void createMainView(Context ctx) {
        mMainView = View.inflate(ctx, R.layout.pv_guest_chaxun_empty, null);
        mMainView.findViewById(R.id.iv_back).setOnClickListener(this);
        et_id = mMainView.findViewById(R.id.et_id);
        tv_title = mMainView.findViewById(R.id.tv_title);
        tv_content = mMainView.findViewById(R.id.tv_content);
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
        line1 = mMainView.findViewById(R.id.line1);
        line_kehu = mMainView.findViewById(R.id.line_kehu);
        tv_kehu = mMainView.findViewById(R.id.tv_kehu);
        tv_gangping = mMainView.findViewById(R.id.tv_gangping);
        tv_xinpian = mMainView.findViewById(R.id.tv_xinpian);
        tv_jiandang = mMainView.findViewById(R.id.tv_jiandang);
        tv_baofei = mMainView.findViewById(R.id.tv_baofei);
        tv_zuihou = mMainView.findViewById(R.id.tv_zuihou);
        tv_state = mMainView.findViewById(R.id.tv_state);
        tv_ping = mMainView.findViewById(R.id.tv_ping);
        tv_changzhan = mMainView.findViewById(R.id.tv_changzhan);
        tv_name = mMainView.findViewById(R.id.tv_name);
        tv_gengxinriqi = mMainView.findViewById(R.id.tv_gengxinriqi);
        tv_num = mMainView.findViewById(R.id.tv_num);
        tv_tel = mMainView.findViewById(R.id.tv_tel);
        tv_type = mMainView.findViewById(R.id.tv_type);
        tv_address = mMainView.findViewById(R.id.tv_address);
        tv_gongyingzhan = mMainView.findViewById(R.id.tv_gongyingzhan);
        tv_kaihu = mMainView.findViewById(R.id.tv_kaihu);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.btn_ok == id) {
            line1.setVisibility(View.GONE);
            String id_string = et_id.getText().toString().trim().toUpperCase();
            netWork("gangPingHao", id_string);
        } else if (R.id.iv_back == id) {
            act.getPVC().pop();
        }

    }

    @Override
    public void onNFCIntent(NFCInfo data) {

        line1.setVisibility(View.GONE);
        GangPingBean b = new GangPingBean(data);

        rb_nfc_id.setChecked(true);
        btn_ok.setVisibility(View.GONE);
        netWork("xinPianHao", b.xinPianHao);

    }

    private void netWork(final String parameter, final String val) {
        act.showProgress();
        BackTask.post(new CZBackTask(act) {
            @Override
            protected void parseResult(JSONObject jdata) throws Exception {

                bean = Constant.gson.fromJson(jdata.toString(), GangPingChaXunBean.class);

            }

            @Override
            protected String getInputParam() throws Exception {
                JSONObject j = new JSONObject();
                j.put(parameter, val);
                return j.toString();
            }

            @Override
            protected String getURL() {
                return "gangPing/peiSongYuanChaXun";
            }

            @Override
            protected void runFront2() {
                act.hideProgress();
                //收起键盘
                InputMethodManager imm = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et_id.getWindowToken(), 0);

                Utils.toastSHORT("操作已成功完成！");
                tv_gangping.setText("气瓶号：" + bean.getResult().getGangPingHao() +
                        "  ;  规格：" + bean.getResult().getGuiGeName()+
                        "\n气体类型："+bean.getResult().getQiTiLeiXing()+
                        "\n空瓶重量："+bean.getResult().getJingZhong()+"kg");

                tv_xinpian.setText("芯片号：" + bean.getResult().getXinPianHao());
                tv_jiandang.setText("建档日期：" + bean.getResult().getJianDangRiQi());
                tv_baofei.setText("报废日期：" + bean.getResult().getBaoFeiRiQi());
                tv_gengxinriqi.setText("更新日期：" + bean.getResult().getLastUpdateWeiZhiRiQi());
//                String s = bean.getResult().getKeHuXingMing() != null ? "-->" + bean.getResult().getKeHuXingMing() : "";
//                tv_zuihou.setText("最后位置：" + bean.getResult().getZuiHouWeiZhiName() + s);
                tv_zuihou.setText("最后位置："+bean.getResult().getZuiHouWeiZhiName()+"/"+bean.getResult().getZuiHouWeiZhiHolderName());

                tv_state.setText(bean.getResult().getStatusName());
                tv_ping.setText(bean.getResult().getYuQiStatusName());
                tv_changzhan.setText(bean.getResult().getChangZhan());

                if (Integer.parseInt(bean.getResult().getZuiHouWeiZhi()) == 5) {
                    tv_name.setText("姓名：" + bean.getResult().getKeHuXingMing());
                    tv_num.setText("编号：" + bean.getResult().getKeHuBianHao());
                    tv_tel.setText("电话：" + bean.getResult().getMobile());
                    tv_type.setText("类型：" + bean.getResult().getKeHuLeiXing());
                    tv_address.setText("地址：" + bean.getResult().getAddress());
                    tv_gongyingzhan.setText("供应站：" + bean.getResult().getGongYingZhan());
                    tv_kaihu.setText("开户日期：" + bean.getResult().getKaiHuRiQi());
                    tv_kehu.setVisibility(View.VISIBLE);
                    line_kehu.setVisibility(View.VISIBLE);
                } else {
                    tv_kehu.setVisibility(View.GONE);
                    line_kehu.setVisibility(View.GONE);
                }
                line1.setVisibility(View.VISIBLE);

            }
        });
    }

    protected boolean isEmpty() {
        return true;
    }
}
