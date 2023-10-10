package com.czsy.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.czsy.CZBackTask;
import com.czsy.CZNetUtils;
import com.czsy.Constant;
import com.czsy.INFCHandler;
import com.czsy.TheApp;
import com.czsy.android.R;
import com.czsy.bean.Address2Bean;
import com.czsy.bean.ClientBean;
import com.czsy.bean.CommonOrderBean;
import com.czsy.bean.IDNameBean;
import com.czsy.ui.sqg.order.NewClient;

import mylib.app.BackFrontTask;
import mylib.app.BackTask;
import mylib.app.MyLog;
import mylib.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * 显示查找客户页面
 */
public class ClientPVObject implements View.OnClickListener {


    public static interface ClientCallback {
        public void onClickOk();

        public void onClickCancel();
    }

    public boolean validClient() {
        return client_bean != null && client_bean.id > 0;
    }

    // public AddressBean address;
    private final ClientCallback callback;
    public ClientBean client_bean;
    public CommonOrderBean client_order_bean;
    private final boolean can_edit = false;

    public ClientPVObject(ClientCallback callback, ClientBean b) {
        this.callback = callback;
        client_bean = b;

    }

    private void updateClientInfo() {
        ClientBean cb = client_bean; //client_order_bean.client_bean;
        if (null == cb) {

            et_phone.setText(null);
            tv_client_no.setText(null);
            et_name.setText(null);
            et_address.setText(null);
            tv_client_info.setText(null);

        } else {
            sp_type.setSelection((int) (cb.type - 1));
            et_phone.setText(cb.telNum);
            tv_client_no.setText(cb.keHuBianHao);
            et_name.setText(cb.userName);
            et_address.setText(cb.diZhi);
        }
    }

    protected TextView et_phone, tv_client_no, et_name, et_address;
    // protected TextView et_floor, tv_last_order, tv_last_security, tv_insurance_info;
    protected Spinner sp_type;
    protected View mMainView, btn_search;
    MainActivity act;
    private boolean add_user = false;
    private Address2Bean new_client_address;
    private View qianfei_container;
    private CheckBox cb_qianfei;
    TextView tv_client_info;
    View tv_right;

    public View createMainView(MainActivity ctx) {
        act = ctx;
        mMainView = View.inflate(ctx, R.layout.pv_guest_order_client, null);
        tv_right = mMainView.findViewById(R.id.tv_right);
        tv_right.setOnClickListener(this);

        tv_client_info = mMainView.findViewById(R.id.tv_client_info);
        mMainView.findViewById(R.id.iv_back).setOnClickListener(this);
        btn_search = mMainView.findViewById(R.id.btn_search);
        btn_search.setOnClickListener(this);
        mMainView.findViewById(R.id.btn_reset).setOnClickListener(this);
        mMainView.findViewById(R.id.btn_ok).setOnClickListener(this);
        qianfei_container = mMainView.findViewById(R.id.qianfei_container);
        cb_qianfei = qianfei_container.findViewById(R.id.cb_qianfei);

        et_phone = mMainView.findViewById(R.id.et_phone);
        tv_client_no = mMainView.findViewById(R.id.tv_client_no);
        et_name = mMainView.findViewById(R.id.et_name);
        //et_name.setEnabled(can_edit);
        et_address = mMainView.findViewById(R.id.et_address);
        // et_address.setEnabled(can_edit);
//        et_address.setOnClickListener(this); 查询客户时，地址不可点击

        sp_type = mMainView.findViewById(R.id.sp_type);
        sp_type.setAdapter(new ArrayAdapter<IDNameBean>(ctx, R.layout.gp_id_item,
                ClientBean.clientTypes.toArray(new IDNameBean[0])));
        sp_type.setEnabled(can_edit);

        updateClientInfo();
        return mMainView;
    }

    private void onCreateUser() {

        tv_client_info.setText(null);
        et_phone.setText(null);
        et_address.setText(null);
        et_name.setText(null);
        tv_client_no.setText(null);

        et_address.setEnabled(true);
        et_name.setEnabled(true);
        sp_type.setEnabled(true);
        btn_search.setVisibility(View.GONE);
        add_user = true;
//        qianfei_container.setVisibility(View.VISIBLE);//暂时隐藏 -->“是否允许欠费”
        tv_right.setVisibility(View.GONE);
    }

    /**
     * 查询客户
     *
     * @param data 检索字段
     * @param type 1--》电话，2--》编号，3--》姓名, 4-->巡检卡
     */
    public void searchKehu(final String data, final int type) {
        tv_client_info.setText(null);
        act.showProgress();
        // 搜索用户
        BackTask.post(new BackFrontTask() {
            String err_msg;
            final List<ClientBean> clients = new LinkedList<>();

            @Override
            public void runFront() {
                act.hideProgress();
                if (act.isFinishing()) {
                    return;
                }
                if (not_found) {

                    if (type == 4) {
                        Utils.toastSHORT("该巡检卡没有信息");
                        return;
                    }

                    AlertDialog.Builder ab = new AlertDialog.Builder(act);
                    ab.setTitle(R.string.app_name);
                    ab.setMessage("没有找到该用户。是否添加？");
                    ab.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface d, int i) {
                            d.dismiss();
                            //onCreateUser();原
//                            act.getPVC().replace(new NewClient(act));
                            String phone = et_phone.getText().toString().trim();
                            String name = et_name.getText().toString().trim();
                            act.getPVC().replace(new NewClient(act, phone, name));
                        }
                    });
                    ab.setNegativeButton(android.R.string.cancel, null);
                    ab.show();
                    return;
                }
                if (!TextUtils.isEmpty(err_msg)) {
                    Utils.toastLONG(err_msg);
                    return;
                }

                if (type != 3) {//电话 或 编号 或 巡检卡 检索

                    if (tmp_addr != null) {
                        if (client_order_bean != null) {
                            client_order_bean.diZhi = tmp_addr;
                        }
                        if (client_bean != null) {
                            client_bean.diZhi = tmp_addr;
                        }
                    } else if (address_list.size() > 1) {
                        String add = address_list.get(0);
                        if (client_order_bean != null) {
                            client_order_bean.diZhi = add;
                        }
                        if (client_bean != null) {
                            client_bean.diZhi = add;
                        }
                        AlertDialog.Builder ab = new AlertDialog.Builder(act);
                        ab.setTitle(R.string.title_address);
                        List<String> s = new LinkedList<>();
                        for (String abb : address_list) {
                            s.add(abb); //.getAddress());
                        }
                        ab.setSingleChoiceItems(s.toArray(new String[0]), 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String add = address_list.get(i);
                                if (client_order_bean != null) {
                                    client_order_bean.diZhi = add;
                                }
                                if (client_bean != null) {
                                    client_bean.diZhi = add;
                                }
                                et_address.setText(client_bean.diZhi);
                            }
                        });
                        ab.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        ab.setNegativeButton(android.R.string.cancel, null);
                        ab.show();
                    } else {
                        Utils.toastLONG("未找到用户地址");
                    }
                } else {//姓名模糊检索
                    if (err_msg != null || clients.isEmpty()) {
                        Utils.toastLONG("找不到该用户!");
                        return;
                    }
                    if (clients.size() == 1) {
                        searchKehu(clients.get(0).telNum, 1);
                        return;
                    }
                    AlertDialog.Builder ab = new AlertDialog.Builder(act);
                    List<String> list = new LinkedList<>();
                    for (ClientBean cb : clients) {
                        list.add(cb.userName + "  电话：" + cb.telNum);
                    }
                    ab.setItems(list.toArray(new String[0]), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            searchKehu(clients.get(which).telNum, 1);
                        }
                    }).show();
                }

                updateClientInfo();
                tv_client_info.setText(client_info_tip);
            }

            //List<AddressBean> address_list = new LinkedList<>();
            //AddressBean tmp_addr;
            List<String> address_list = new LinkedList<>();
            String tmp_addr;

            boolean not_found = false;
            String client_info_tip;

            boolean multiAddr() {
                return address_list.size() > 1;
            }

            @Override
            public void runBack() {
                try {
                    JSONObject json = new JSONObject();
                    JSONObject ret = null, result = null;
                    JSONArray jsonName = null;
                    json.put("data", data);
                    json.put("type", type);
                    ret = CZNetUtils.postCZHttp(
                            "keHu/chaXunKeHuAnZhuo",
                            json.toString());

                    if (type == 1 || type == 2 || type == 4) {//电话或编号检索或巡检卡
                        result = ret.optJSONObject("result");
                        if (result == null) { // no client found
                            err_msg = null;
                            client_bean = null;
                            not_found = true;
                            return;
                        }
                        client_bean = Constant.gson.fromJson(result.toString(), ClientBean.class);
                        //client_bean.telNum = phone;

                        err_msg = ret.optString("message", null);

                        JSONArray addr_list = result.optJSONArray("diZhiList");
                        if (addr_list != null && addr_list.length() == 1) {
                            tmp_addr = addr_list.getString(0);
                        } else if (addr_list != null) {
                            // do select
                            for (int i = 0; i < addr_list.length(); i++) {
                                address_list.add(addr_list.getString(i));
                            }
                        } //  client_bean.telNum = phone;

                        // get tip
                        StringBuffer sb = new StringBuffer();
                        if (result.has("gangPingList")) {
                            sb.append("气瓶（规格 / 气瓶号）：\n");
                            JSONArray ja = result.getJSONArray("gangPingList");
                            for (int i = 0; i < ja.length(); i++) {
                                JSONObject jo = ja.getJSONObject(i);
                                sb.append(jo.optString("guiGeName", "") + " / " + jo.optString("gangPingHao", ""));
                                sb.append('\n');
                            }
                        }
                        if (result.has("yaJinList")) {
                            double yajin = 0.00f;
                            JSONArray ja = result.getJSONArray("yaJinList");
                            for (int i = 0; i < ja.length(); i++) {
                                yajin += ja.getJSONObject(i).getDouble("shiShouYaJin");
                            }
                            sb.append(String.format("\n押金：%.2f", yajin));
                        }

                        if (result.has("qianKuanList")) {
                            double yajin = 0.00f;
                            JSONArray ja = result.getJSONArray("qianKuanList");
                            for (int i = 0; i < ja.length(); i++) {
                                yajin += ja.getJSONObject(i).getDouble("shangPingJinE");
                            }
                            sb.append(String.format("\n欠款：%.2f", yajin));
                        }

                        if (result.has("anJianRenYuan") && result.has("shangCiAnJianRiQi")) {

                            sb.append("\n\n上次安检日期：" + result.getString("shangCiAnJianRiQi"));
                            sb.append("\n安检人员：" + result.getString("anJianRenYuan"));
                        }

                        client_info_tip = sb.toString();
                        err_msg = null; // ok
                    } else {//姓名模糊检索

                        jsonName = ret.optJSONArray("result");
                        if (jsonName == null) {
                            err_msg = ret.getString("message");

                        }

                        for (int i = 0; i < jsonName.length(); i++) {
                            JSONObject jo = jsonName.getJSONObject(i);
                            clients.add(Constant.gson.fromJson(jo.toString(), ClientBean.class));
                        }
                    }
                } catch (Exception e) {
                    MyLog.LOGE(e);
                    client_bean = null;
//                    if (TextUtils.isEmpty(err_msg)) {
//                        err_msg = TheApp.sInst.getString(R.string.tip_common_err);
//                    }
                }
            }

        });
    }

    @Override
    public void onClick(View v) {

        if (et_address == v) {
            final AddressDialog ad = new AddressDialog(act, null,
                    new AddressDialog.OnAddressOK() {
                        @Override
                        public void onAddressOk(AddressDialog ad) {
                            et_address.setText(
                                    ad.addr.toString());

                            new_client_address = ad.addr;
                            String dizhi = ad.addr.toString();
                            if (client_order_bean != null) {
                                client_order_bean.diZhi = dizhi;
                            }
                            if (client_bean != null) {
                                client_bean.diZhi = dizhi;
                            }
                        }
                    });
            ad.show();
            return;
        }
        int id = v.getId();
        if (R.id.tv_right == id) {
            //
//            onCreateUser();//原
//            act.getPVC().push(new NewClient(act));
            String phone = et_phone.getText().toString().trim();
            String name = et_name.getText().toString().trim();
            act.getPVC().replace(new NewClient(act, phone, name));

        } else if (R.id.iv_back == id) {
            callback.onClickCancel();
        } else if (R.id.btn_reset == id) {
            // clear info
            client_bean = null; // reset
            updateClientInfo();

        } else if (R.id.btn_search == id) {
            final String phone = et_phone.getText().toString().trim();
            final String name = et_name.getText().toString().trim();
            final String number = tv_client_no.getText().toString().trim();
            final boolean has_phone = !TextUtils.isEmpty(phone);
            final boolean has_name = !TextUtils.isEmpty(name);
            final boolean has_number = !TextUtils.isEmpty(number);

            if (!has_name && !has_phone && !has_number) {
                Utils.toastLONG("请输入电话、客户编号或姓名");
                return;
            }
            if (has_phone) {
                searchKehu(phone, 1);
            } else if (has_name) {
                searchKehu(name, 3);
            } else {
                searchKehu(number, 2);
            }

        } else if (R.id.btn_ok == id) {
            if (add_user) {
                //final String addr = et_address.getText().toString().trim();
                if (new_client_address == null) { //TextUtils.isEmpty(addr)) {
                    Utils.toastLONG("请填写地址");
                    return;
                }
                final String name = et_name.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    Utils.toastLONG("请填写姓名");
                    return;
                }
                final String phone = et_phone.getText().toString().trim();
                if (TextUtils.isEmpty(phone) || phone.length() < 6) {
                    Utils.toastLONG("请填写电话");
                    return;
                }
                final boolean can_qianfei = cb_qianfei.isChecked();
                BackTask.post(new CZBackTask(act) {
                    @Override
                    protected void parseResult(JSONObject jdata) throws Exception {
                        int code = jdata.optInt("code");
                        if (code == 200) {

                            //JSONObject reslut = jdata.getJSONObject("result");
                            client_bean = new ClientBean();
                            client_bean.telNum = phone;
                            client_bean.userName = name;
                            client_bean.diZhi = new_client_address.toString();
                            try {
                                IDNameBean t = (IDNameBean) sp_type.getSelectedItem();
                                //client_bean.leiXing = t == null ? ClientBean.ClientType.PuTong.name :
                                //        t.name;
                                client_bean.type = t.id;
                            } catch (Exception e) {
                            }
//                            client_bean.louCeng = (louCeng);
                            client_bean.id = jdata.getLong("result");
                            client_bean.shiFouXuYaoAnJian = true;
                            client_bean.shiFouYunXuQianKuan = can_qianfei;
                        } else {
                            throw new Exception();
                        }
                    }

                    @Override
                    protected String getInputParam() throws Exception {
                        JSONObject j = new JSONObject();
                        j.put("shengFen", new_client_address.provinceName);
                        j.put("quXian", new_client_address.countyName);
                        j.put("cun", new_client_address.villageName);
                        j.put("xiangZhen", new_client_address.townName);
                        // 1: 可以欠费, 2: 不能欠费
                        j.put("shiFouYunXuQianFei", can_qianfei ? 1 : 2);
                        j.put("diShi", new_client_address.cityName);
                        j.put("diZhi", new_client_address.detailAddress);
                        j.put("keHuXingMing", name);
                        j.put("telNum", phone);

                        IDNameBean type = (IDNameBean) sp_type.getSelectedItem();
                        j.put("leiXing", type.id);
                        return j.toString();
                    }

                    @Override
                    protected String getURL() {
                        return "keHu/peiSongYuanChuangJianKeHu";
                    }

                    @Override
                    protected void runFront2() {
                        if (client_bean != null) {
                            Utils.toastLONG("新建用户成功！");
                            if (client_order_bean != null) {
                                client_order_bean.fromClientBean(client_bean);
//                                if (client_order_bean.address != null && TextUtils.isEmpty(client_order_bean.client_bean.xiangXiDiZhi)) {
//                                    client_order_bean.client_bean.xiangXiDiZhi =
//                                            client_order_bean.address; //.getShortAddress();
//                                }
                            }
                            callback.onClickOk();
                        }
                    }
                });
                return;
            }
            if (client_bean == null) {
                return;
            }
            if (client_order_bean != null) {
                client_order_bean.fromClientBean(client_bean); //.client_bean = client_bean;
//                if (client_order_bean.address != null && TextUtils.isEmpty(client_order_bean.client_bean.xiangXiDiZhi)) {
//                    client_order_bean.client_bean.xiangXiDiZhi = client_order_bean.address; //.getShortAddress();
//                }
            }
            // next page
            callback.onClickOk();
        }
    }


}
