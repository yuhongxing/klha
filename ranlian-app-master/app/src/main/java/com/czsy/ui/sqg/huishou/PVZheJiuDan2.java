package com.czsy.ui.sqg.huishou;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import android.widget.Spinner;

import com.czsy.CZBackTask;
import com.czsy.Constant;
import com.czsy.android.R;
import com.czsy.bean.CommonOrderBean;
import com.czsy.bean.IDNameBean;
import com.czsy.bean.PayMethodBean;
import com.czsy.ui.AbsPVBase;
import com.czsy.ui.MainActivity;

import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import mylib.app.BackFrontTask;
import mylib.app.BackTask;
import mylib.app.MyLog;
import mylib.utils.Utils;

public class PVZheJiuDan2 extends AbsPVBase implements View.OnClickListener { //, INFCHandler {
    public final CommonOrderBean zj_bean;

    public PVZheJiuDan2(MainActivity a, CommonOrderBean b) {
        super(a);
        zj_bean = b;
//        if (b != null && b.dingDanLeiXing != Constant.dingdan_type_zhejiu) {
//            throw new IllegalArgumentException("只能处理折旧单");
//        }
    }

    Spinner sp_louceng, sp_guige;
    EditText et_beizhu, et_gpsl, et_tuiQiJinE, et_shangPinJinE, et_yuqi;//et_fyzj
    List<IDNameBean> ge = new LinkedList<>();
//    @Override
//    public void onNFCIntent(INFCHandler.NFCInfo data) {
//        if (pay_method != null) {
//            pay_method.onNFCIntent(data);
//        }
//    }

    @Override
    protected void createMainView(Context ctx) {
        mMainView = View.inflate(ctx, R.layout.pv_zhejiu_dan2, null);
        et_beizhu = mMainView.findViewById(R.id.et_beizhu);
        et_gpsl = mMainView.findViewById(R.id.et_gpsl);
        et_yuqi = mMainView.findViewById(R.id.et_yuqi);
//        et_fyzj = mMainView.findViewById(R.id.et_fyzj);
        et_tuiQiJinE = mMainView.findViewById(R.id.et_tuiQiJinE);
        et_shangPinJinE = mMainView.findViewById(R.id.et_shangPinJinE);
        sp_guige = mMainView.findViewById(R.id.sp_guige);
        sp_louceng = mMainView.findViewById(R.id.sp_louceng);
        mMainView.findViewById(R.id.btn_ok).setOnClickListener(this);
        mMainView.findViewById(R.id.iv_back).setOnClickListener(this);
        //pay_method = new PayMethodObject(mMainView.findViewById(R.id.pay_type_container));


        BackTask.post(new BackFrontTask() {
            @Override
            public void runFront() {
                sp_guige.setAdapter(new ArrayAdapter<IDNameBean>(act, R.layout.gp_id_item, ge.toArray(new IDNameBean[0])));

            }

            @Override
            public void runBack() {
                try {
                    Constant.getLX_GE(ge);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        sp_louceng.setAdapter(new ArrayAdapter<String>(ctx, R.layout.gp_id_item,
                Constant.getLouCeng()));
    }

    private List<CommonOrderBean> ret_data;

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.btn_ok == id) {


            if (TextUtils.isEmpty(et_tuiQiJinE.getText().toString())
                    || TextUtils.isEmpty(et_shangPinJinE.getText().toString())
                    || TextUtils.isEmpty(et_gpsl.getText().toString())
                    || TextUtils.isEmpty(et_yuqi.getText().toString())
            ) {
                Utils.toastLONG("请填写完整");
                return;
            }

            try {
                final int louCeng = sp_louceng.getSelectedItemPosition() + 1;
                final int guiGe_Position = sp_guige.getSelectedItemPosition();
                final Long guiGe = ge.get(guiGe_Position).id;
                Log.d("xing", String.valueOf(guiGe));

                final String beizhu = et_beizhu.getText().toString().trim();
                final double gpsl = Double.valueOf(et_gpsl.getText().toString().trim());
//                final double fyzj = Double.valueOf(et_fyzj.getText().toString().trim());
                final double shangPinJinE = Double.valueOf(et_shangPinJinE.getText().toString().trim());
                final double tuiQiJinE = Double.valueOf(et_tuiQiJinE.getText().toString().trim());
                final double yuqi = Double.valueOf(et_yuqi.getText().toString().trim());
                act.showProgress();
                BackTask.post(new CZBackTask(act) {
                    @Override
                    protected void parseResult(JSONObject jdata) throws Exception {
                        int code = jdata.optInt("code", 0);
                        if (code != 200) {
                            throw new Exception();
                        }
                    }

                    @Override
                    protected String getInputParam() throws Exception {
                        JSONObject jdata = new JSONObject();
                        if (zj_bean != null &&
                                !zj_bean.isNewOrder()) {
                            /*
                            beiZhu (string, optional): 订单备注 ,
feiYongZongJi (number): 费用总计 ,
gangPingShuLiang (integer): 折旧钢瓶数量 ,
sid (integer): 订单号 ,
yuQi (number, optional): 余气，2位精度
}
                             */

                            jdata.put("sid", zj_bean.dingDanHao);
//                            jdata.put("feiYongZongJi", fyzj);
                            jdata.put("tuiQiJinE", tuiQiJinE);
                            jdata.put("shangPinJinE", shangPinJinE);
                            jdata.put("gangPingShuLiang", gpsl);

                        } else {
                            jdata.put("keHuId", zj_bean.keHuId);
                            jdata.put("dingDanType", Constant.dingdan_type_zhejiu);
//                            jdata.put("zheJiuDanFeiYongZongJi", fyzj);
                            jdata.put("tuiQiJinE", tuiQiJinE);
                            jdata.put("shangPinJinE", shangPinJinE);
                            jdata.put("zheJiuDanHuiShouGangPingCount", gpsl);
                            jdata.put("diZhi", zj_bean.diZhi);
                            jdata.put("zhiFuFangShi", PayMethodBean.xian_jin.id);
                        }
                        jdata.put("beiZhu", beizhu);
                        jdata.put("yuQi", yuqi);
                        jdata.put("louCeng", louCeng);
                        jdata.put("guiGe", guiGe);
                        return jdata.toString();
                    }

                    @Override
                    protected String getURL() {
                        return zj_bean == null ||
                                zj_bean.isNewOrder() ? "dingDan/chuangJianByPeiSongYuan" : "dingDan/jieSuanZheJiuDan";
                    }

                    @Override
                    protected void runFront2() {
                        Utils.toast(R.string.tip_op_ok);
                        act.getPVC().popMe(PVZheJiuDan2.this);
                    }
                });
            } catch (Exception e) {

                MyLog.LOGE(e);
                Utils.toastLONG("输入错误，请重新输入");
            }
        } else if (id == R.id.iv_back) {

            act.getPVC().pop();
        }
    }

}
