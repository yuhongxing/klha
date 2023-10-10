package com.czsy.print;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;
import android.text.TextUtils;

import com.czsy.TheApp;
import com.czsy.bean.CommonOrderBean;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.UUID;

import mylib.app.MyLog;
import mylib.utils.FileUtils;
import mylib.utils.Utils;

public class PrintManager { // implements Runnable {
    public static final String ACTION_CONNECT_STATUS = "action.connect.status";
    private static final String BT_ADD_FILE = "BT_ADD_FILE";
    private String saved_bt_address; // saved bt address
    private BluetoothDevice bt_dev;
    private final BluetoothAdapter bt_adapter;

    private PrintManager() {
        saved_bt_address = (String) FileUtils.getObject(BT_ADD_FILE, String.class);
        //TheApp.sHandler.postDelayed(this, 1000);

        IntentFilter ifi = new IntentFilter();
        ifi.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        ifi.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        TheApp.sInst.registerReceiver(recv, ifi);

        bt_adapter = BluetoothAdapter.getDefaultAdapter();
        if (!TextUtils.isEmpty(saved_bt_address)) {
            bt_dev = bt_adapter.getRemoteDevice(saved_bt_address);
            if (bt_dev == null || bt_dev.getBondState() != BluetoothDevice.BOND_BONDED) {
                FileUtils.removeObjcect(BT_ADD_FILE);
                bt_dev = null;
                saved_bt_address = null;
            }
        }
    }

    private static PrintManager sInst;

    public static PrintManager get() {
        if (sInst == null) {
            sInst = new PrintManager();
        }
        return sInst;
    }


//    private void connect() {
//        try {
//            TheApp.sHandler.removeCallbacks(this);
//            if (isConnected()) {
//                return;
//            }
//            if (TextUtils.isEmpty(saved_bt_address)) {
//                TheApp.sHandler.postDelayed(this, 3000);
//                return;
//            }
//            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
//            BluetoothDevice dev = adapter.getRemoteDevice(saved_bt_address);
//            bt_dev = dev;
//        } catch (Exception e) {
//            MyLog.LOGE(e);
//        }
//    }
    // scan bt

    private BroadcastReceiver recv = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String act = intent == null ? null : intent.getAction();
            if (act == null) {
                return;
            }
            checkBT(intent);
        }
    };

    private void checkBT(Intent intent) {
        if (bt_dev != null) {
            bt_dev = null;
        }
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        if (device != null && device.getBondState() == BluetoothDevice.BOND_BONDED) {
            saved_bt_address = device.getAddress();
            FileUtils.saveObject(BT_ADD_FILE, saved_bt_address);
            bt_dev = device;
        }
    }

//    private boolean isConnected() {
//        //return (bt_dev != null && bt_dev.getBondState() == BluetoothDevice.BOND_BONDED);
//        return bt_adapter != null && BluetoothAdapter.STATE_ON == bt_adapter.getState();
//    }

//    @Override
//    public void run() {
//        connect();
//    }

    ///////////// do print, run in back
    public boolean printInBack(final CommonOrderBean order) {
        if (bt_dev == null || bt_dev.getBondState() != BluetoothDevice.BOND_BONDED) {
            Utils.toastLONG("请连接蓝牙打印机");
            Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            TheApp.sInst.startActivity(intent);
            return false;
        }
        // connect ,
        BluetoothSocket socket = null;
        OutputStream out = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String SPP_UUID = "00001101-0000-1000-8000-00805f9b34fb";

            socket = bt_dev.createRfcommSocketToServiceRecord(UUID.fromString(SPP_UUID));
            socket.connect();
            out = socket.getOutputStream();

            PrintUtils.setOutputStream(out);
            PrintUtils.selectCommand(PrintUtils.RESET);
            PrintUtils.selectCommand(PrintUtils.LINE_SPACING_DEFAULT);
            PrintUtils.selectCommand(PrintUtils.ALIGN_CENTER);
            PrintUtils.printText("昆仑华大能源\n\n");
            //PrintUtils.selectCommand(PrintUtils.DOUBLE_HEIGHT_WIDTH);
            //PrintUtils.printText("桌号：1号桌\n\n");

            PrintUtils.selectCommand(PrintUtils.NORMAL);
            PrintUtils.selectCommand(PrintUtils.ALIGN_LEFT);

//            PrintUtils.printText(PrintUtils.printTwoData("订单编号",
//                    (order.dingDanHao) + "\n"));
//            PrintUtils.printText(PrintUtils.printTwoData("客户姓名",
//                    order.keHuMing + "\n"));
//            PrintUtils.printText(PrintUtils.printTwoData("客户地址",
//                    order.diZhi + "\n"));
//            PrintUtils.printText(PrintUtils.printTwoData("订单日期",
//                    sdf.format(System.currentTimeMillis()) + "\n"));
            PrintUtils.printText(order.toString());
            PrintUtils.printText("\n\n\n");
            try {
                out.close();
                socket.close();
            } catch (Exception e) {
                MyLog.LOGE(e);
            }
            return true;

        } catch (Throwable t) {
            Utils.toastLONG("打印机异常，请重新连接蓝牙打印机");
            Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            TheApp.sInst.startActivity(intent);

        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Throwable t) {
                    MyLog.LOGE(t);
                }
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (Throwable t) {
                    MyLog.LOGE(t);
                }
            }
        }
        return false;
    }
}
