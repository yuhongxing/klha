package com.czsy.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.czsy.CZBackTask;
import com.czsy.Constant;
import com.czsy.android.R;
import com.czsy.bean.Address2Bean;

import mylib.app.BackTask;
import mylib.app.BaseActivity;
import mylib.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class AddressDialog extends Dialog implements View.OnClickListener {
    public Address2Bean addr;

    private final TextView tv_sheng, tv_shi,
            tv_qu, tv_xiang, tv_cun, et_detail_address;
    final BaseActivity act;

    final private OnAddressOK ok_task;

    public static interface OnAddressOK {
        void onAddressOk(AddressDialog ad);
    }

    public AddressDialog(@NonNull BaseActivity context, Address2Bean addr, OnAddressOK ok) {
        super(context, android.R.style.Theme_DeviceDefault_Light_NoActionBar);
        act = context;
        ok_task = ok;
        this.addr = addr;
        if (this.addr == null) {
            this.addr = new Address2Bean();
        }
        setContentView(R.layout.address_dialog);
        et_detail_address = findViewById(R.id.et_detail_address);
        tv_sheng = findViewById(R.id.tv_sheng);
        tv_sheng.setOnClickListener(this);
        tv_shi = findViewById(R.id.tv_shi);
        tv_shi.setOnClickListener(this);
        tv_qu = findViewById(R.id.tv_qu);
        tv_qu.setOnClickListener(this);
        tv_xiang = findViewById(R.id.tv_xiang);
        tv_xiang.setOnClickListener(this);
        tv_cun = findViewById(R.id.tv_cun);
        tv_cun.setOnClickListener(this);

        findViewById(R.id.btn_cancel).setOnClickListener(this);
        findViewById(R.id.btn_ok).setOnClickListener(this);

        fillTextViews();
    }

    private void fillTextViews() {
        tv_sheng.setText(addr.getSheng());
        tv_shi.setText(addr.getShi());
        tv_qu.setText(addr.getQu());
        tv_xiang.setText(addr.getXiang());
        tv_cun.setText(addr.getCun());

    }

    @Override
    public void onClick(final View v) {
        final int id = v.getId();
        if (R.id.btn_cancel == id) {
            dismiss();
            return;
        } else if (R.id.btn_ok == id) {
            if (addr.provinceId != null && addr.cityId != null
                    && addr.countyId != null && addr.townId != null
                    && addr.villageId != null
            ) {
                addr.detailAddress = et_detail_address.getText().toString();
                if (TextUtils.isEmpty(addr.detailAddress)) {
                    Utils.toastLONG("请输入详细地址");
                    et_detail_address.requestFocus();
                    return;
                }
                ok_task.onAddressOk(AddressDialog.this);
                dismiss();

            } else {
                Utils.toastSHORT("请选择地址");
            }
            return;
        }
        act.showProgress();
        BackTask.post(new CZBackTask(act) {

            List<Address2Bean> list;

            @Override
            protected void runFront2() {
                //fillTextViews();
                List<String> data = new LinkedList<>();
                for (Address2Bean add : list) {
                    if (v == tv_sheng) {
                        data.add(add.getSheng());
                        addr.cityId = addr.countyId = addr.townId = addr.villageId = null;
                    } else if (v == tv_shi) {
                        data.add(add.getShi());
                        addr.countyId = addr.townId = addr.villageId = null;
                    } else if (v == tv_qu) {
                        data.add(add.getQu());
                        addr.townId = addr.villageId = null;
                    } else if (v == tv_xiang) {
                        data.add(add.getXiang());
                        addr.villageId = null;
                    } else if (v == tv_cun) {
                        data.add(add.getCun());
                    }
                }
                AlertDialog.Builder ab = new AlertDialog.Builder(act);
                ab.setItems(data.toArray(new String[0]), new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Address2Bean a = list.get(i);
                        dialogInterface.dismiss();
                        if (v == tv_sheng) {
                            addr.provinceId = a.provinceId;
                            addr.provinceName = a.provinceName;
                            addr.cityId = addr.countyId = addr.townId = addr.villageId = null;
                            addr.cityName = addr.countyName = addr.townName = addr.villageName = null;
                        } else if (v == tv_shi) {
                            addr.cityId = a.cityId;
                            addr.cityName = a.cityName;
                            addr.countyId = addr.townId = addr.villageId = null;
                            addr.countyName = addr.townName = addr.villageName = null;
                        } else if (v == tv_qu) {
                            addr.countyId = a.countyId;
                            addr.countyName = a.countyName;
                            addr.townId = addr.villageId = null;
                            addr.townName = addr.villageName = null;
                        } else if (v == tv_xiang) {
                            addr.townId = a.townId;
                            addr.townName = a.townName;
                            addr.villageId = null;
                            addr.villageName = null;
                        } else if (v == tv_cun) {
                            addr.villageId = a.villageId;
                            addr.villageName = a.villageName;
                        }
                        fillTextViews();

                    }
                });
                ab.show();
            }

            @Override
            protected void parseResult(JSONObject jdata) throws Exception {
                JSONArray ja = jdata.getJSONArray("result");
                list = new LinkedList<>();
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject jo = ja.getJSONObject(i);
                    list.add(Constant.gson.fromJson(jo.toString(), Address2Bean.class));
                }


//                IDNameBean b = Constant.gson.fromJson(jdata.getJSONObject("result").toString()
//                        , IDNameBean.class);
//                if (v == tv_sheng) {
//                    addr.provinceId = b;
//                    addr.cityId = addr.countyId = addr.townId = addr.villageId = 0;
//                } else if (v == tv_shi) {
//                    addr.cityId = b;
//                    addr.countyId = addr.townId = addr.villageId = 0;
//                } else if (v == tv_qu) {
//                    addr.countyId = b;
//                    addr.townId = addr.villageId = 0;
//                } else if (v == tv_xiang) {
//                    addr.townId = b;
//                    addr.villageId = 0;
//                } else if (v == tv_cun) {
//                    addr.villageId = b;
//                }
            }


            @Override
            protected String getInputParam() throws Exception {
                JSONObject j = new JSONObject();
                if (v == tv_sheng) {
                } else if (v == tv_shi) {
                    j.put("provinceId", addr.provinceId);
                } else if (v == tv_qu) {
                    j.put("cityId", addr.cityId);
                } else if (v == tv_xiang) {
                    j.put("countyId", addr.countyId);
                } else if (v == tv_cun) {
                    j.put("townId", addr.townId);
                }
                return j.toString();
            }

            @Override
            protected String getURL() {
                if (v == tv_sheng) {
                    return "area/getProvinceList";

                } else if (v == tv_shi) {
                    if (addr.provinceId == null) {
                        err_msg = ("请选择省份");
                        return null;
                    }
                    return "area/getCityByProvId";
                } else if (v == tv_qu) {
                    if (addr.cityId == null) {
                        err_msg = ("请选择城市");
                        return null;
                    }
                    return "area/getCountyByCityId";
                } else if (v == tv_xiang) {
                    if (addr.provinceId == null) {
                        err_msg = ("请选择区");
                        return null;
                    }
                    return "area/getTownByCountyId";
                } else if (v == tv_cun) {
                    if (addr.provinceId == null) {
                        err_msg = ("请选择乡");
                        return null;
                    }
                    return "area/getVillageByTownId";
                }
                err_msg = act.getString(R.string.tip_common_err);
                return null;
            }


        });
    }
}
