package mylib.app;

import android.util.Log;
import com.czsy.android.BuildConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyLog {
    public final static int max_1log_size = ((1 << 10) * 2 - 10);
    public final static boolean DEBUG = BuildConfig.DEBUG;

    public static final int DBG_LOGD = 1;
    public static final int DBG_LOGI = 2;
    public static final int DBG_LOGW = 3;
    public static final int DBG_LOGE = 4;
    public final static String TAG = "czgas_";

    private static FileOutputStream fileWriter;
    public final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    static {
        if (DEBUG) {
            try {
                File f = new File("/sdcard/" + TAG + ".txt");
                if (!f.exists()) {
                    f.createNewFile();
                }
                fileWriter = new FileOutputStream(f);
            } catch (Throwable e) { // skip any error
                fileWriter = null;
            }
        }
    }

    private static void logFile(final String s, final Throwable e) {

        if (fileWriter == null || s == null) {
            return;
        }
        AndroidApp.sInst.getBackHandler().post(new Runnable() {

            @Override
            public void run() {
                try {
                    final StringBuilder sb = new StringBuilder();
                    sb.append(s == null ? "" : s);
                    sb.append('\n');
                    if (e != null) {
                        StringWriter out = new StringWriter();
                        PrintWriter pw = new PrintWriter(out);
                        e.printStackTrace(pw);
                        sb.append(out.toString());
                    }

                    Date d = new Date();
                    fileWriter.write("------->>>>>>>>>   ".getBytes());
                    fileWriter.write(sdf.format(d).getBytes());
                    fileWriter.write('\n');
                    fileWriter.write(sb.toString().getBytes());
                    fileWriter.write('\n');
                    fileWriter.flush();
                } catch (Exception e) {
                }
            }
        });


    }

    public static void LOGE(final String str) {
        if (DEBUG) {
            Log.e(TAG, str);
            logFile("[Err] " + str, null);
        }

    }

    public static void LOGE(final String str, final Throwable e) {
        if (DEBUG) {
            Log.e(TAG, str, e);
            logFile("[Err] " + str, e);
        }

    }

    public static void LOGE(final Throwable e) {
        if (DEBUG) {
            Log.e(TAG, "", e);
            logFile("[Err] " + e.toString(), e);
        }

    }

    public static void LOGD(String str) {
        if (DEBUG && str != null) {
            int start = 0, end = str.length();
            do {
                if (end - start < max_1log_size) {
                    Log.d(TAG, str.substring(start, end));
                    start = end;
                } else {
                    Log.d(TAG, str.substring(start, start + max_1log_size));
                    start += max_1log_size;
                }
            } while (start < end);

            logFile("[Debug] " + str, null);
        }
    }

    public static void LOGD(String str,boolean isNetWork) {
        if (DEBUG && str != null) {
            int start = 0, end = str.length();
            do {
                if (end - start < max_1log_size) {
                    Log.d("NetWork", str.substring(start, end));
                    start = end;
                } else {
                    Log.d("NetWork", str.substring(start, start + max_1log_size));
                    start += max_1log_size;
                }
            } while (start < end);
        }
    }


    public static void LOGW(String str) {
        if (DEBUG && str != null) {
            int start = 0, end = str.length();
            do {
                if (end - start < max_1log_size) {
                    Log.w(TAG, str.substring(start, end));
                    start = end;
                } else {
                    Log.w(TAG, str.substring(start, start + max_1log_size));
                    start += max_1log_size;
                }
            } while (start < end);

            logFile("[Warn] " + str, new Exception());
        }
    }

    public static void LOGW(Throwable t) {
        if (DEBUG) {
            Log.w(TAG, "", t);
            logFile("[Warn] " + t.toString(), t);
        }
    }

    public static void LOGW(String s, Throwable t) {
        if (DEBUG) {
            Log.w(TAG, s, t);
            logFile("[Warn] " + s, t);
        }
    }

    public static void LOGD(String str, Throwable t) {
        if (DEBUG) {
            Log.d(TAG, str, t);
            logFile("[Debug] " + str, t);
        }
    }

}
