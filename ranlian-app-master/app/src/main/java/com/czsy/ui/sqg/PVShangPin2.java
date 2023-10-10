package com.czsy.ui.sqg;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.czsy.*;
import com.czsy.android.R;
import com.czsy.bean.*;
import com.czsy.ui.AbsPVBase;
import com.czsy.ui.CountWidget;
import com.czsy.ui.MainActivity;
import com.czsy.ui.sqg.order.PVClientOrder_Pay3;

import mylib.app.BackTask;
import mylib.ui.list.AbstractAdapter;
import mylib.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

// 商品
public class PVShangPin2 extends AbsPVBase implements View.OnClickListener {

    final ClientBean client_bean;
    View btn_ok;
    TextView tv_nfc;
    //ListView lv_search;
    ListView lv_product;

    //RadioButton rb_xin_jin, rb_wei_xin, rb_chong_zhi_ka, rb_qiankuan;
    //private String czk_id; // 充值卡id

    EditText et_comment;

    Spinner sp_cate1, sp_cate2;

    public PVShangPin2(MainActivity a, ClientBean b) {
        super(a);
        client_bean = b;
    }

    @Override
    public void onDetach(boolean lastShow) {
        super.onDetach(lastShow);
        //et_input.setText(null);
    }

    @Override
    protected void createMainView(Context ctx) {
        mMainView = View.inflate(act, R.layout.pv_shang_pin2, null);
        mMainView.findViewById(R.id.btn_ok).setOnClickListener(this);
        mMainView.findViewById(R.id.btn_cancel).setOnClickListener(this);
        mMainView.findViewById(R.id.iv_back).setOnClickListener(this);
        mMainView.findViewById(R.id.btn_add).setOnClickListener(this);

        et_comment = mMainView.findViewById(R.id.et_comment);

//        rb_xin_jin = mMainView.findViewById(R.id.rb_xin_jin);
//        rb_wei_xin = mMainView.findViewById(R.id.rb_wei_xin);
//        rb_chong_zhi_ka = mMainView.findViewById(R.id.rb_chong_zhi_ka);
//        rb_qiankuan = mMainView.findViewById(R.id.rb_qiankuan);
//        rb_qiankuan.setVisibility(View.GONE);


        sp_cate1 = mMainView.findViewById(R.id.sp_cate1);
        sp_cate2 = mMainView.findViewById(R.id.sp_cate2);
//        lv_search = mMainView.findViewById(R.id.lv_search);
//        lv_search.setVisibility(View.GONE);
//        et_input = mMainView.findViewById(R.id.et_input);
//        lv_search.setAdapter(search_dapter);

        lv_product = mMainView.findViewById(R.id.lv_product);

        lv_product.setAdapter(product_adapter);
        btn_ok = mMainView.findViewById(R.id.btn_ok);

        TextView tv_client_info = mMainView.findViewById(R.id.tv_client_info);
        tv_client_info.setText(String.format("客户：%s (%s)\n地址: %s",
                client_bean.userName, client_bean.telNum, client_bean.getDiZhi()));

        sp_cate1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sp_cate2.setAdapter(null);
                loadCate(false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        loadCate(true);

    }

    // 查询品类
    private void loadCate(final boolean cate1) {
        act.showProgress();
        final ShangPinCate1Bean b = cate1 ? null : (ShangPinCate1Bean) sp_cate1.getSelectedItem();

        BackTask.post(new CZBackTask(act) {
            List<ShangPinCate1Bean> list = null;
            List<ShangPinBean_Count> list_sp = null;

            @Override
            protected void parseResult(JSONObject jdata) throws Exception {
                //MyLog.LOGD(jdata.toString());
                JSONArray ja = jdata.optJSONArray("result");
                if (cate1) {
                    if (ja != null && ja.length() > 0) {
                        list = new LinkedList<>();
                        for (int i = 0; i < ja.length(); i++) {
                            list.add(Constant.gson.fromJson(ja.getJSONObject(i).toString(), ShangPinCate1Bean.class));
                        }
                    }
                } else {
                    if (ja != null && ja.length() > 0) {
                        list_sp = new LinkedList<>();
                        for (int i = 0; i < ja.length(); i++) {
                            list_sp.add(Constant.gson.fromJson(ja.getJSONObject(i).toString(),
                                    ShangPinBean_Count.class));
                        }
                    }
                }
            }

            @Override
            protected String getInputParam() throws Exception {
                return cate1 ? "{}" : String.format("{\"pinLeiId\": %d}", b.id);
            }

            @Override
            protected String getURL() {
                return cate1 ? "/shangPin/chaXunPinLei/100000/1" : "/shangPin/chaXunShangPinAll";
            }

            @Override
            protected void runFront2() {
                if (cate1) {
                    sp_cate2.setAdapter(null);
                    product_adapter.setData_shangpin(null);
                    if (list == null) {
                        Utils.toastLONG("无法获取商品分类！");
                    } else {
                        sp_cate1.setAdapter(new ArrayAdapter<ShangPinCate1Bean>(act, R.layout.gp_id_item,
                                list.toArray(new ShangPinCate1Bean[0])));
                        sp_cate1.setSelection(0);
                    }
                } else {
                    if (list_sp == null) {
                        Utils.toastLONG("无法获取商品列表！");
                    } else {
                        sp_cate2.setAdapter(new ArrayAdapter<ShangPinBean_Count>(act, R.layout.gp_id_item,
                                list_sp.toArray(new ShangPinBean_Count[0])));
                        sp_cate2.setSelection(0);
                    }
                }
            }
        });
    }

//    @Override
//    public void onNFCIntent(NFCInfo data) {
//        if (!rb_chong_zhi_ka.isChecked()) {
//            return;
//        }
//        czk_id = data.chip_sn;
//    }

    @Override
    public void onAttach(boolean firstShow) {
        super.onAttach(firstShow);
//        loadCate(true);
    }

    public static class ShangPinBean_Count extends ShangPinBean {
        transient public int count = 1;
    }

    private static class ViewHolder {
        final View root;
        final ImageView iv_add;
        final TextView tv_content;
        final CountWidget count_widget;
        ShangPinBean_Count bean;

        ViewHolder(View convertView) {
            root = convertView;
            tv_content = convertView.findViewById(R.id.tv_content);
            iv_add = convertView.findViewById(R.id.iv_add);
            count_widget = convertView.findViewById(R.id.count_widget);
            count_widget.setCallBack(new CountWidget.GetCont() {
                @Override
                public void cont(int cont) {
                    bean.count = cont;
                }
            });
        }


    }

    private class ShanPingAdapter extends AbstractAdapter<ShangPinBean_Count> {
        ShanPingAdapter() { //boolean b) {
            //is_search = b;
        }

        //final boolean is_search;

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            boolean create = null == convertView;
            if (create) {
                convertView = View.inflate(parent.getContext(), R.layout.item_shang_pin, null);
                convertView.setTag(new ViewHolder(convertView));

            }
            ShangPinBean_Count b = getItem(position);
            ViewHolder vh = (ViewHolder) convertView.getTag();
            //vh.count_widget.setVisibility(!is_search ? View.VISIBLE : View.GONE);
            //vh.iv_add.setImageResource(is_search ? android.R.drawable.ic_menu_add :
            //        android.R.drawable.ic_delete);
            vh.tv_content.setText(b.toString());
            if (create) {
                vh.iv_add.setOnClickListener(PVShangPin2.this);
                // vh.iv_add.setVisibility(View.GONE);
            }

            vh.bean = b;
            vh.iv_add.setTag(b);
            return convertView;
        }
    }

    private void selectLouCeng(final CommonOrderBean client_order_bean, final Runnable task) {
//        AlertDialog.Builder ab = new AlertDialog.Builder(act);
//        ab.setTitle("请选择楼层").setItems(new String[]{
//                "1楼", "2楼", "3楼", "4楼", "5楼", "6楼", // "7楼", "8楼", "9楼", "10楼",
//        }, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                client_order_bean.louCeng = String.valueOf(i + 1);
//                //checkGPIds();
//                task.run();
//            }
//        }).show();
        client_order_bean.louCeng = "1";
        task.run();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (R.id.btn_ok == id) {
            // 下单
            if (product_adapter.isEmpty()) {
                Utils.toastLONG("请加入商品!");
                return;
            }
            final String beizhu = et_comment.getText().toString();
            final ShangPingOrderBean client_order_bean = new ShangPingOrderBean();
            client_order_bean.fromClientBean(client_bean);
            // create the product order...
            selectLouCeng(client_order_bean, new Runnable() {
                @Override
                public void run() {
                    act.showProgress();
                    BackTask.post(new CZBackTask(act) {
                        @Override
                        protected void parseResult(JSONObject jdata) throws Exception {
                            String result = jdata.getString("result");
                            client_order_bean.dingDanHao = result;
                        }

                        @Override
                        protected void onError(Exception e) {
//                            err_msg = "无法创建订单";
                            err_msg = CZNetUtils.CZNetErr.msg;
                        }

                        @Override
                        protected String getInputParam() throws Exception {
                            JSONObject j = new JSONObject();
                            j.put("diZhi", client_order_bean.diZhi);
                            j.put("keHuId", client_order_bean.keHuId);
                            j.put("louCeng", client_order_bean.louCeng);
                            j.put("dingDanType", Constant.dingdan_type_shangpin);
                            // 商品内容
                            JSONArray ja = new JSONArray();
                            j.put("shangPingList", ja);
                            for (ShangPinBean_Count sp : product_adapter.getData()) {
                                JSONObject j_sp = new JSONObject();
                                j_sp.put("count", sp.count);
                                j_sp.put("shangPingId", sp.id);
                                ja.put(j_sp);
                            }
                            j.put("beiZhu", beizhu);
                            return j.toString();
                        }

                        @Override
                        protected String getURL() {
                            return "dingDan/chuangJianByPeiSongYuan";
                        }

                        @Override
                        protected void runFront2() {
                            //act.getPVC().push(new PVShangPinJieSuan(act, client_order_bean, f_pb));

                            act.getPVC().push(new PVClientOrder_Pay3(act, client_order_bean, 0));
                        }
                    });
                }
            });

//            BackTask.post(new CZBackTask(act) {
//                @Override
//                protected void parseResult(JSONObject jdata) throws Exception {
//                    j_result = Constant.gson.fromJson(jdata.getJSONObject("result").toString(),
//                            ShanPingOrderBean.class);
//                }
//

//                ShanPingOrderBean j_result;
//
//                @Override
//                protected String getInputParam() throws Exception {
//                    JSONObject j = new JSONObject();
//                    j.put("dingDanLeiXing", "物品订单");
//                    j.put("keHuMing", client_bean.userName);
//                    j.put("keHuId", client_bean.id);
//                    j.put("diZhi", client_bean.diZhi == null ? "" : client_bean.getDiZhi());
//                    j.put("beiZhu", "");
//                    return j.toString();
//                }
//
//                @Override
//                protected String getURL() {
//                    return "dingDan/dingDanYuJieSuan";
//                }
//
//                @Override
//                protected void runFront2() {
//                    act.getPVC().push(new PVShangPinJieSuan(act, j_result, f_pb));
//                }
//            });
        } else if (R.id.iv_back == id || R.id.btn_cancel == id) {
            act.getPVC().pop();
            return;
        } else if (R.id.btn_add == id || R.id.iv_add == id) {
            final boolean is_add = R.id.btn_add == id; //lv_search.getVisibility() == View.VISIBLE;
            final ShangPinBean_Count b = is_add ? (ShangPinBean_Count)
                    sp_cate2.getSelectedItem() : (ShangPinBean_Count) view.getTag();
            if (b == null) {
                Utils.toastLONG("没有选择商品！");
                return;
            }
            List<ShangPinBean_Count> data = product_adapter.getData();
            if (is_add) {
                if (data != null) {
                    for (ShangPinBean_Count sp : data) {
                        if (sp.id == b.id) {
                            Utils.toastLONG("已添加");
                            return;
                        }
                    }
                }
                // add new product
                if (data == null) {
                    data = new LinkedList<>();
                }
                data.add(b);
                product_adapter.setData_shangpin(data);

                //et_input.setText(null);
            } else {
                AlertDialog.Builder ab = new AlertDialog.Builder(act);
                ab.setMessage(R.string.tip_sur_delete);
                ab.setTitle(R.string.app_name);
                ab.setNegativeButton(android.R.string.cancel, null);
                ab.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        List<ShangPinBean_Count> data = product_adapter.getData();
                        if (data == null) {
                            return;
                        }
                        Iterator<ShangPinBean_Count> iter = data.iterator();
                        while (iter.hasNext()) {
                            ShangPinBean_Count b = iter.next();
                            if (b.id == b.id) {
                                iter.remove();
                                break;
                            }
                        } // while
                        product_adapter.setData_shangpin(data);
                        dialogInterface.dismiss();
                    }
                });
                ab.show();
            }
        }
    }


    //private ShanPingAdapter search_dapter = new ShanPingAdapter(true);

    private ShanPingAdapter product_adapter = new ShanPingAdapter();

}
