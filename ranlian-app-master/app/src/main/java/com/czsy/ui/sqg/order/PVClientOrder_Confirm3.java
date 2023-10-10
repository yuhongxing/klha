package com.czsy.ui.sqg.order;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.czsy.CZNetUtils;
import com.czsy.Constant;
import com.czsy.android.R;
import com.czsy.bean.CommonOrderBean;
import com.czsy.bean.YaJinBean;
import com.czsy.bean.YaJinDanBean;
import com.czsy.ui.MainActivity;
import com.czsy.ui.sqg.AbsPVGuestOrder;

import mylib.app.BackFrontTask;
import mylib.app.BackTask;
import mylib.app.MyLog;
import mylib.utils.CameraUtils;
import mylib.utils.FileUtils;
import mylib.utils.ImageUtils;
import mylib.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

// 订单下单 - 预处理
public class PVClientOrder_Confirm3 extends AbsPVGuestOrder {

    TextView tv_order_no, tv_order_type, tv_client_info;
    private LinearLayout yajin_container;

    EditText et_comment, et_real_ya_jin;
    final boolean is_tuiping_dan;
    double jineH = 0;//重瓶押金总额
    double jineE = 0;//空瓶押金总额

    public PVClientOrder_Confirm3(MainActivity a, CommonOrderBean b) {
        super(a, b);
        is_tuiping_dan = b.isTuiPingDan();
    }

    private ViewGroup heavy_dayu_empty_container;

    @Override
    protected void createMainView(Context ctx) {
        mMainView = View.inflate(act, R.layout.pv_guest_order_confirm3, null);
        yajin_container = mMainView.findViewById(R.id.yajin_container);
        tv_order_no = mMainView.findViewById(R.id.tv_order_no);
        tv_order_type = mMainView.findViewById(R.id.tv_order_type);
        et_comment = mMainView.findViewById(R.id.et_comment);
        et_real_ya_jin = mMainView.findViewById(R.id.et_real_ya_jin);
        tv_client_info = mMainView.findViewById(R.id.tv_client_info);

        et_comment.setText(client_order_bean.beiZhu);

        String str = client_order_bean.dingDanLeiXingName;

        if (str == null) {
            tv_order_type.setVisibility(View.GONE);
            Log.d("xing", "1111");
        } else {
            Log.d("xing", "2222");
        }

        if (client_order_bean.laiYuan == 3 && client_order_bean.zhiFuFangShi == 5) { //微信小程序&积分兑换
            str = str + "--" + client_order_bean.zhiFuFangShiName;
        } else {
            str = str + "--" + client_order_bean.laiYuanName;
        }

        tv_order_no.setText("订单号: " + (client_order_bean.isNewOrder() ? "新订单" : client_order_bean.dingDanHao));

        tv_order_type.setText("订单类型：" + str);

        mMainView.findViewById(R.id.iv_back).setOnClickListener(this);
        mMainView.findViewById(R.id.btn_ok).setOnClickListener(this);
        String s_addr = client_order_bean.diZhi;

        tv_client_info.setText(String.format("%s: %s\n%s: %s\n%s: %s",
                act.getString(R.string.title_client_name), client_order_bean.keHuMing
                , act.getString(R.string.title_phone), client_order_bean.client_bean != null ?
                        client_order_bean.client_bean.telNum : client_order_bean.keHuDianHua
                , act.getString(R.string.title_address), s_addr
        ));
        TextView tv_gp_stat = mMainView.findViewById(R.id.tv_gp_stat);
        heavy_dayu_empty_container = mMainView.findViewById(R.id.heavy_dayu_empty_container);

        //如果是小程序下单并且已支付押金，就隐藏输入押金功能
        if (client_order_bean.laiYuan == 3 && client_order_bean.zhiFuFangShi == 6) {
            yajin_container.setVisibility(View.GONE);
        }

        int empty_size = client_order_bean.empty_list.size();
        if (is_tuiping_dan) {
            StringBuffer sb = new StringBuffer(String.format("收回 <font color='red'>%d</font> 个空瓶"
                    , empty_size));
            sb.append("<br>");
            for (int i = 0; i < empty_size; i++) {
                CommonOrderBean.GangPingInfo gp = client_order_bean.empty_list.get(i);
                sb.append(String.format("%s: 应退款:<font color='red'>%.2f</font>, 退气:<font color='red'>%.2f</font>",
                        gp.gp_bean.guiGeName, gp.yin_tui_kuan, gp.yu_qi));
                if (i != empty_size - 1) {
                    sb.append("<br>");
                }
            }
            tv_gp_stat.setText(Html.fromHtml(sb.toString()));
            tv_gp_stat.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            heavy_dayu_empty_container.setVisibility(View.GONE);
        } else {
            int heavy_size = client_order_bean.heavy_list.size();

            // 重瓶肯定大于空瓶！
            if (empty_size > heavy_size) {
                Utils.toastLONG("空瓶大于重瓶！");
                act.getPVC().pop();
                return;
            } else if (heavy_size > empty_size) {
                heavy_dayu_empty_container.setVisibility(View.VISIBLE);
            } else {
                heavy_dayu_empty_container.setVisibility(View.GONE);
            }
            StringBuffer sb = new StringBuffer(String.format("收回%d个空瓶，配送%d个重瓶。\n"
                    , empty_size, heavy_size));
            if (heavy_size > empty_size) {
                sb.append("押金收取为:\n");
                sb.append(huiZong(client_order_bean.heavy_list, true));
                if (client_order_bean.laiYuan == 3 && client_order_bean.zhiFuFangShi == 6) {
//                    sb.append(" (小程序已收取)");
                }
                sb.append("\n");
                /**
                 * 当有空瓶的时候，需要把相应规格的钢瓶押金数量抵消，展示出来即可，在下一个逻辑层面做抵消
                 */
                if (empty_size > 0) {
                    sb.append("抵换押金为:\n");
                    sb.append(huiZong(client_order_bean.empty_list, false));
                }
            }
            tv_gp_stat.setText(sb.toString());

            et_real_ya_jin.setFilters(new InputFilter[]{new InputFilter() {
                @Override
                public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                    if (source.equals(".") && dest.toString().length() == 0) {
                        return "0.";
                    }
                    if (dest.toString().contains(".")) {
                        int index = dest.toString().indexOf(".");
                        int length = dest.toString().substring(index).length();
                        if (length == 3) {
                            return "";
                        }
                    }
                    return null;
                }
            }});
            et_real_ya_jin.setText((jineH - jineE) + "");
        }
    }

    /**
     * 钢瓶押金汇总（空瓶或者重瓶）
     *
     * @param listInfo
     * @param isHeavy  是否为重瓶：true重瓶，false空瓶。
     * @return 汇总后的字符串说明
     */
    private String huiZong(List<CommonOrderBean.GangPingInfo> listInfo, boolean isHeavy) {

        String huizong = "";
        //Map<规格id，数量>
        Map<Integer, Integer> gui_map = new HashMap<>();
        for (CommonOrderBean.GangPingInfo i : listInfo) {
            int ge = i.gp_bean.guiGe;
            if (gui_map.containsKey(ge)) {
                gui_map.put(ge, gui_map.get(ge) + 1);
            } else {
                gui_map.put(ge, 1);
            }
        }
        for (Integer guiGe : gui_map.keySet()) {
            YaJinBean b = YaJinBean.yajin_map.get(guiGe);
            int cnt = gui_map.get(guiGe);
            if (b != null) {
                huizong += String.format("        %s(%d): %.2f元", b.guiGeName, cnt, b.price * cnt);
                if (isHeavy) {
                    jineH = jineH + b.price * cnt;
                } else {
                    jineE = jineE + b.price * cnt;
                }
            }
        }


        return huizong;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.btn_ok == id) { // 预结算
            if (is_tuiping_dan) {
                // 选中押金记录
                client_order_bean.yajindan_list.clear();

                final Set<Integer> gp_guige = new HashSet<>();
                for (CommonOrderBean.GangPingInfo gp : client_order_bean.empty_list) {
                    gp_guige.add(gp.gp_bean.guiGe);
                    Log.d("xing", "gp_guige: " + gp.gp_bean.guiGe);
                }
                final List<YaJinDanBean> yaJinList2 = new ArrayList<>();
                List<String> s = new LinkedList<>();
                Log.d("xing", "yajin_list: " + yajin_list.toString());
                for (YaJinDanBean yj : yajin_list) {
                    if (gp_guige.contains(yj.guiGe)) {
                        s.add(yj.toString());
                        yaJinList2.add(yj);
                    }
                }
                Log.d("xing", "yaJinList2==>" + yaJinList2.toString());
                if (s.isEmpty()) {
                    AlertDialog.Builder ab = new AlertDialog.Builder(act);
                    ab.setTitle(R.string.app_name).setMessage("没有找到对应气瓶规格的押金记录!")
                            .setPositiveButton(android.R.string.ok, null).show();
                    return;
                }

                AlertDialog.Builder ab = new AlertDialog.Builder(act);
                ab.setTitle("请选择押金记录");
                ab.setMultiChoiceItems(s.toArray(new String[0]), null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        if (b) {
                            client_order_bean.yajindan_list.add(yaJinList2.get(i));
                            Log.d("xing", "选中的Id==》" + yaJinList2.get(i).id);


                        } else {
                            client_order_bean.yajindan_list.remove(yaJinList2.get(i));
                        }
                    }
                });

                ab.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (client_order_bean.yajindan_list.isEmpty()) {
                            Utils.toastLONG("请选择押金记录");
                            return;
                        }


                        Log.d("xing", "押金条存入的数据：" + client_order_bean.yajindan_list.toString());
                        client_order_bean.beiZhu = et_comment.getText().toString();
                        act.getPVC().push(new PVClientOrder_Pay3(act,
                                client_order_bean, 0));
                    }
                });
                ab.show();
                return;
            }

            if (et_real_ya_jin.getText().toString().isEmpty()) {
                Utils.toastSHORT("请填写实收押金");
                return;
            }

            client_order_bean.beiZhu = et_comment.getText().toString();
            act.getPVC().push(new PVClientOrder_Pay3(act,
                    client_order_bean, Double.parseDouble(et_real_ya_jin.getText().toString())));
        } else {
            super.onClick(v);
        }
    }


    private List<YaJinDanBean> yajin_list = new LinkedList<>();

    @Override
    public void onAttach(boolean firstShow) {
        super.onAttach(firstShow);
        if (tv_order_no != null) {
            tv_order_no.setText("订单号: " +
                    (TextUtils.isEmpty(client_order_bean.dingDanHao) ? "新订单" : client_order_bean.dingDanHao));
        }
        if (is_tuiping_dan) {
            act.showProgress();
            BackTask.post(new BackFrontTask() {
                @Override
                public void runFront() {
                    act.hideProgress();
                    if (act.isFinishing()) {
                        return;
                    }
                    if (!TextUtils.isEmpty(err_msg)) {
                        Utils.toastLONG(err_msg);
                    }
                    if (yajin_list.isEmpty()) {
                        AlertDialog.Builder ab = new AlertDialog.Builder(act);
                        ab.setMessage("该客户没有押金记录，不能退瓶");
                        ab.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                act.getPVC().pop();
                            }
                        });
                        ab.show();
                    }
                }

                String err_msg;

                @Override
                public void runBack() {
                    try {
                        // 查询客户押金列表
                        JSONObject json = new JSONObject();
                        json.put("keHuId", client_order_bean.keHuId);
                        json.put("id", client_order_bean.keHuId);
                        JSONObject ret = CZNetUtils.postCZHttp(
                                "keHu/chaXunKeHuYaJinTiaoAnZhuo",
                                json.toString());
                        err_msg = ret.optString("message");
                        JSONArray result = ret.isNull("result") ? null : ret.optJSONArray("result");

                        yajin_list.clear();
                        client_order_bean.yajindan_list.clear();
                        Log.d("xing", "押金数据==》" + result.toString());
                        for (int i = 0; i < result.length(); i++) {
                            yajin_list.add(
                                    Constant.gson.fromJson(result.getJSONObject(i).toString(),
                                            YaJinDanBean.class));
                        }
                        //
                        err_msg = yajin_list.isEmpty() ? "该客户没有押金记录" : null;
                    } catch (Exception e) {
                        MyLog.LOGE(e);
                        if (null == err_msg) {
                            err_msg = act.getString(R.string.tip_common_err);
                        }
                    }
                }
            });
        }

    }


}
