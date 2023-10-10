package mylib.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.czsy.android.R;

import mylib.app.AndroidApp;

public class Global {
    public static final String UNKNOWN = "unknown";
    public static int gShowingActivityCnt = 0;

    public final static int EVT_TIMEOUT = 300;

    public static class UI {
        public final static Animation sAnimInRight, sAnimOutRight, sAnimInLeft, sAnimOutLeft;

        static {
            sAnimInRight = AnimationUtils.loadAnimation(AndroidApp.sInst, R.anim.push_in_right);
            sAnimOutRight = AnimationUtils.loadAnimation(AndroidApp.sInst, R.anim.push_out_right);

            sAnimInLeft = AnimationUtils.loadAnimation(AndroidApp.sInst, R.anim.push_in_left);
            sAnimOutLeft = AnimationUtils.loadAnimation(AndroidApp.sInst, R.anim.push_out_left);

        }
    }

    public static final int _1k = (1 << 10);
    public static final int _1m = (1 << 20);

    public static final long _1min = (1000 * 60);

    public static final long _1hour = (_1min * 60);

    public static final long _1day = (_1hour * 24);

    // / utils
    public volatile static int sWidth, sHeight;

    public volatile static float sDensity, sDensityDpi;

    @SuppressWarnings("deprecation")
    public static void updateScreenSize() {
        WindowManager wm = (WindowManager) AndroidApp.sInst.getSystemService(Context.WINDOW_SERVICE);
        Display dis = wm.getDefaultDisplay();
        sWidth = dis.getWidth();
        sHeight = dis.getHeight();
    }

    // global static
    static {
        updateScreenSize();
        WindowManager wm = (WindowManager) AndroidApp.sInst.getSystemService(Context.WINDOW_SERVICE);
        Display dis = wm.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        dis.getMetrics(dm);
        sDensity = dm.density;
        sDensityDpi = dm.densityDpi;
    }


}

