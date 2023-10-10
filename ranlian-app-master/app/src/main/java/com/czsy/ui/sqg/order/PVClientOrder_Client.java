package com.czsy.ui.sqg.order;

import android.content.Context;
import android.util.Log;

import com.czsy.INFCHandler;
import com.czsy.bean.ClientBean;
import com.czsy.bean.CommonOrderBean;
import com.czsy.ui.ClientPVObject;
import com.czsy.ui.MainActivity;
import com.czsy.ui.sqg.AbsPVGuestOrder;

import mylib.utils.Utils;

// 代客下单 - 客户信息
public class PVClientOrder_Client extends AbsPVGuestOrder implements ClientPVObject.ClientCallback, INFCHandler {
    final protected ClientPVObject client_pv;

    public PVClientOrder_Client(MainActivity a, CommonOrderBean b) {
        super(a, b);
        client_pv = new ClientPVObject(this, b == null ? null : b.toClientBean());
    }

    @Override
    protected void createMainView(Context ctx) {
        mMainView = client_pv.createMainView(act);
    }

    @Override
    public void onClickOk() {
        if (client_order_bean != null) {

            ClientBean cb = client_pv.client_bean;

            if (cb.userName == null || cb.telNum == null ){
                Utils.toastSHORT("请查询并选择客户");
                return;
            }

            client_order_bean.fromClientBean(cb);
            act.getPVC().push(new PVClientOrder_Empty(act, client_order_bean));
        }
    }

    @Override
    public void onClickCancel() {
        act.getPVC().pop();
    }

    @Override
    public void onNFCIntent(NFCInfo i) {

        Log.d("xing",i.chip_sn);
        client_pv.searchKehu(i.chip_sn,4);
    }
}
