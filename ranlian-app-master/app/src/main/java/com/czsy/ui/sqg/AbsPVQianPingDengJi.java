package com.czsy.ui.sqg;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.czsy.android.R;
import com.czsy.bean.ProductManager;
import com.czsy.ui.AbsPVBase;
import com.czsy.ui.MainActivity;

import java.util.LinkedList;
import java.util.List;

import mylib.ui.list.AbstractAdapter;

/**
 * 欠瓶登记
 */
abstract public class AbsPVQianPingDengJi extends AbsPVBase implements View.OnClickListener {

    public static class QianPingInfo {
        public ProductManager.ProductBean product_bean;
        public int amount;
    }

    ListView list_view;
    TextView tv_name;
    TextView et_amount;

    public AbsPVQianPingDengJi(MainActivity a) {
        super(a);
    }

    @Override
    protected void createMainView(Context ctx) {
        mMainView = View.inflate(ctx, R.layout.pv_qian_ping_deng_ji, null);
        for (int id : new int[]{R.id.iv_back, R.id.btn_ok, R.id.tv_name, R.id.btn_add}) {
            mMainView.findViewById(id).setOnClickListener(this);
        }
        list_view = mMainView.findViewById(R.id.list_view);
        list_view.setAdapter(adapter);
        tv_name = mMainView.findViewById(R.id.tv_name);
        et_amount = mMainView.findViewById(R.id.et_amount);

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
                        adapter.getData().remove(i);
                        adapter.notifyDataSetChanged();
                    }
                });
                ab.show();
                return true;
            }
        });
    }

    private AbstractAdapter<QianPingInfo> adapter = new AbstractAdapter<QianPingInfo>() {
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            return null;
        }
    };
    private ProductManager.ProductBean cur_product;

    void updateProduct() {
        if (cur_product == null) {
            tv_name.setText(null);
        } else {
            tv_name.setText(cur_product.name);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (R.id.tv_name == id) {
            AlertDialog.Builder ab = new AlertDialog.Builder(act);
            final String[] items = new String[]{
                    "气瓶1", "气瓶2", "气瓶3"
            };
            ab.setSingleChoiceItems(
                    items, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            cur_product = ProductManager.getDefaultProduct();
                            updateProduct();

                        }
                    }
            );
            ab.show();
        } else if (R.id.iv_back == id) {
            act.getPVC().pop();
        } else if (R.id.btn_add == id) {
            if (cur_product == null) {
                return;
            }
            int amt = -1;
            try {
                amt = Integer.valueOf(et_amount.getText().toString());
            } catch (Exception e) {
                amt = -1;
            }
            if (amt <= 0) {
                et_amount.setText(null);
                return;
            }
            List<QianPingInfo> list = adapter.getData();
            if (list == null) {
                list = new LinkedList<>();
            }
            boolean found = false;
            for (QianPingInfo p : list) {
                if (p.product_bean.id == cur_product.id) {
                    p.amount += amt;
                    found = true;
                    break;
                }
            }
            if (!found) {
                QianPingInfo info = new QianPingInfo();
                info.amount = amt;
                info.product_bean = cur_product;
                list.add(info);
            }
            adapter.setData(list);
        } else if (R.id.btn_ok == id) {
            // get list

            List<QianPingInfo> list = adapter.getData();
            if (list == null || list.isEmpty()) {
                return;
            }
            onQianPing(list);
        }
    }

    public abstract void onQianPing(List<QianPingInfo> qian_ping_list);
}
