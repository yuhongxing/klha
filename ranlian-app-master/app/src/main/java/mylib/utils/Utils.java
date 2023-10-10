package mylib.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.czsy.TheApp;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import mylib.app.AndroidApp;
import mylib.app.MyLog;

@SuppressLint("NewApi")
public class Utils {

    public static Toast toast;

    public static String getProcessName() {
        int pid = android.os.Process.myPid();
        try {
            ActivityManager mActivityManager = (ActivityManager) AndroidApp.sInst
                    .getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
                if (appProcess.pid == pid) {
                    return appProcess.processName;
                }
            }
        } catch (Throwable t) {
        }
        return null;
    }


    public static String bytesToHexString(byte[] bytes) {
        if (bytes == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (byte b : bytes) {
            int val = b & 0xff;
            if (val < 0x10) {
                sb.append("0");
            }
            sb.append(Integer.toHexString(val));
        }
        return sb.toString();
    }


    public static String md5(String str) {
        StringBuffer sb = new StringBuffer();
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] digest = md5.digest(str.getBytes());
            sb.append(bytesToHexString(digest));
        } catch (NoSuchAlgorithmException e) {
            MyLog.LOGW(e.toString());
        }
        return sb.toString();
    }

    public static String md5(byte[] str) {
        StringBuffer sb = new StringBuffer();
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] digest = md5.digest(str);
            sb.append(bytesToHexString(digest));
        } catch (NoSuchAlgorithmException e) {
            MyLog.LOGW(e.toString());
        }
        return sb.toString();
    }


    public static void runInMain(Runnable run) {
        if (isMainThread()) {
            run.run();
        } else {
            AndroidApp.sHandler.post(run);
        }
    }

    /**
     * 显示资源文本
     *
     * @param id
     */
    public static void toast(final int id) {
        runInMain(new Runnable() {

            @Override
            public void run() {
                if (toast == null) {
                    toast = Toast.makeText(AndroidApp.sInst, AndroidApp.sInst.getString(id), Toast.LENGTH_SHORT);
                } else {
                    toast.setText(AndroidApp.sInst.getString(id));
                }
                toast.show();
//                Toast.makeText(AndroidApp.sInst, AndroidApp.sInst.getString(id), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 长显示
     *
     * @param id
     */
    public static void toastLONG(final String id) {
        runInMain(new Runnable() {

            @Override
            public void run() {
                if (toast == null) {
                    toast = Toast.makeText(AndroidApp.sInst, id, Toast.LENGTH_LONG);
                } else {
                    toast.setText(id);
                }
                toast.show();
//                Toast.makeText(AndroidApp.sInst, id, Toast.LENGTH_LONG).show();
            }
        });
    }


    /**
     * 短显示
     *
     * @param id
     */
    public static void toastSHORT(final String id) {
        runInMain(new Runnable() {

            @Override
            public void run() {

                if (toast == null) {
                    toast = Toast.makeText(AndroidApp.sInst, id, Toast.LENGTH_SHORT);
                } else {
                    toast.setText(id);
                }
                toast.show();

//                Toast.makeText(AndroidApp.sInst, id, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void hideIME(Activity ctx) {
        View v = ctx.getCurrentFocus();
        InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v == null ? null : v.getWindowToken(), 0);
    }

    public static void hideIME(Context act, View v) {
        InputMethodManager imm = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static void showIME(View v) {
        v.requestFocus();
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
    }

    public static void applyTheme(Activity act, boolean light) {
        if (Build.VERSION.SDK_INT >= 14) {
            act.setTheme(!light ? android.R.style.Theme_DeviceDefault_NoActionBar
                    : android.R.style.Theme_DeviceDefault_Light_NoActionBar);
        } else if (Build.VERSION.SDK_INT >= 10) {
            act.setTheme(
                    !light ? android.R.style.Theme_Holo_NoActionBar : android.R.style.Theme_Holo_Light_NoActionBar);
        } else {
            act.setTheme(!light ? android.R.style.Theme_NoTitleBar : android.R.style.Theme_Light_NoTitleBar);
        }
    }


    public static void close(Cursor c) {
        if (c != null) {
            try {
                c.close();
            } catch (Throwable e) {
                // skip errors
            }
        }

    }

    public static void close(OutputStream c) {
        if (c != null) {
            try {
                c.close();
            } catch (Throwable e) {
                // skip errors
            }
        }

    }

    public static void close(InputStream c) {
        if (c != null) {
            try {
                c.close();
            } catch (Throwable e) {
                // skip errors
            }
        }

    }

    public static void close(Reader c) {
        if (c != null) {
            try {
                c.close();
            } catch (Throwable e) {
                // skip errors
            }
        }

    }

    public static void close(Writer c) {
        if (c != null) {
            try {
                c.close();
            } catch (Throwable e) {
                // skip errors
            }
        }

    }

    public static void close(Socket c) {
        if (c != null) {
            try {
                c.close();
            } catch (Throwable e) {
                // skip errors
            }
        }
    }

    public static boolean isMainThread() {
        return Thread.currentThread().getId() == Looper.getMainLooper().getThread().getId();
    }

    public static void checkMainThread(boolean isMain) {
        if (MyLog.DEBUG) {
            boolean mainThread = isMainThread();
            if ((isMain != mainThread)) {
                throw new IllegalStateException(isMain ? "需要在主线程操作！" : "不能在主线程操作！");
            }
        }
    }

    /**
     * 收起键盘
     *
     * @param act   当前窗口
     * @param et_id 要收起的 EditText
     */
    public static void hideSoftInputFromWindow(Activity act, EditText et_id) {
        InputMethodManager imm = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et_id.getWindowToken(), 0);
    }

    /**
     * 获取当前手机系统版本号
     *
     * @return 系统版本号
     */
    public static String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 获取手机型号
     *
     * @return 手机型号
     */
    public static String getSystemModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取手机厂商
     *
     * @return 手机厂商
     */
    public static String getDeviceBrand() {
        return android.os.Build.BRAND;
    }

    /**
     * 设置文本中某一区间的字体颜色和点击事件
     * 拼接而成
     *
     * @param startStr          前段文字
     * @param spanStr           要设置属性的文字
     * @param endStr            后段文字
     * @param textView          TextView控件
     * @param textClickListener 点击监听
     */
    public static void setTextSpan(String startStr, String spanStr, String endStr, TextView textView, final TextClickListener textClickListener) {
        SpannableString clickStr = new SpannableString(spanStr);
        clickStr.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                textClickListener.onClick();
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.parseColor("#1E90FF"));
                ds.setUnderlineText(false);
            }
        }, 0, clickStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.append(startStr);
        textView.append(clickStr);
        textView.append(endStr);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setFocusable(false);//解决设置了setMovementMethod导致onItemclick无效的问题
    }

    // 文字点击监听
    public interface TextClickListener {
        void onClick();
    }

    /**
     * 打电话
     * @param phone 手机号
     */
    public static void toCall(String phone) {
        try {
            Intent i = new Intent();
            i.setAction(Intent.ACTION_DIAL);
            i.setData(Uri.parse("tel:" + phone));
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            TheApp.sInst.startActivity(i);
        } catch (Throwable t) {
            Utils.toastLONG("无法拨打" + phone);
        }
    }


}
