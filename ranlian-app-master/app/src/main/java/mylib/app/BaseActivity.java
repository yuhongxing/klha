package mylib.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.czsy.TheApp;
import com.czsy.android.R;
import mylib.ui.PageViewContainer;
import mylib.utils.Global;
import mylib.utils.Utils;

abstract public class BaseActivity extends Activity {

    private ProgressDialog mProgressDialog = null;

//    private Runnable progressTask = new Runnable() {
//        @Override
//        public void run() {
//            TheApp.sHandler.removeCallbacks(progressTask);
//            hideProgress();
//        }
//    };

    public void showProgress() {
        showProgress(getString(R.string.loading_data));
    }

    public void showProgress(boolean cancel) {
        showProgress(getString(R.string.loading_data),cancel);

    }

    public void showProgress(String msg) {
        showProgress(msg, true);
    }

    public void showProgress(String msg, boolean cancel) {
        if (isFinishing()) {
            return;
        }
        hideIME();
        try {
            MyLog.LOGW("show progress");
            if (mProgressDialog == null) {
                mProgressDialog = new ProgressDialog(this);
            }
            mProgressDialog.setMessage(msg);
            mProgressDialog.setCancelable(cancel);
            if (!mProgressDialog.isShowing()){
                mProgressDialog.show();
                Log.d("existGangPing","mProgressDialog----show");
            }else {
                Log.d("existGangPing","mProgressDialog----no show");
            }
//            TheApp.sHandler.removeCallbacks(progressTask);
//            TheApp.sHandler.postDelayed(progressTask, 3000);
        } catch (Exception e) {
        }
    }

    public void hideProgress() {
        if (isFinishing()) {
            return;
        }

        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                MyLog.LOGW("hide progress");
                mProgressDialog.dismiss();
            }
        } catch (Exception e) {
        }
    }

    public static class EventTypes {
        public EventTypes(Enum<?>[] p1, EventHandler p2) {
            mEvts = p1;
            mHandler = p2;
        }

        final public Enum<?>[] mEvts;

        final public EventHandler mHandler;
    }

    protected Pair<Boolean, Boolean> applyTheme() {
        return new Pair<Boolean, Boolean>(true, true);
    }

    //private static boolean tipDialog = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Pair<Boolean, Boolean> ret = applyTheme();
        if (ret.first) {
            Utils.applyTheme(this, ret.second);
        }

        //FileUtils.checkDir();
        EventTypes evtHandler = getEvents();
        if (evtHandler != null) {
            Enum<?>[] evts = evtHandler.mEvts;
            EventHandler handler = evtHandler.mHandler;
            if (evts == null || handler == null) {
                MyLog.LOGW("evts or handler is null!");
            } else {
                EventHandler.addEventHandler(evts, handler);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventTypes evtHandler = getEvents();
        if (evtHandler != null) {
            Enum<?>[] evts = evtHandler.mEvts;
            EventHandler handler = evtHandler.mHandler;
            if (evts != null && handler != null) {
                EventHandler.removeEventHandler(evts, handler);
            }
        }
        PageViewContainer pvc = getPVC();
        if (pvc != null) {
            pvc.clear();
        }
        hideProgress();
    }

	/*@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode != KeyEvent.KEYCODE_BACK)
			return super.onKeyDown(keyCode, event);
		if (!doBackPressed()) {
			finish();
		}
		return true;
	}*/

    public void onBackPressed() {
        if (!doBackPressed()) {
            super.onBackPressed();
        }
    }

    protected boolean doBackPressed() {
        PageViewContainer pvc = getPVC();
        if (pvc != null) {
            return pvc.doBackPressed();
        }
        return false;
    }

    protected EventTypes getEvents() {
        return null;
    }

    protected void onPause() {
        super.onPause();
        if (getPVC() != null) {
            getPVC().onPause();
        }
    }

    protected void onResume() {
        super.onResume();
        if (getPVC() != null) {
            getPVC().onResume();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Global.gShowingActivityCnt--;
        if (getPVC() != null) {
            getPVC().onStop();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Global.gShowingActivityCnt++;
        if (getPVC() != null) {
            getPVC().onStart();
        }
    }

    public void hideIME() {
        View v = getCurrentFocus();
        if (null == v)
            return;
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public void showIME(final View vv) {
        View v = vv;
        if (null == v) {
            v = getCurrentFocus();
            if (null == v)
                return;
        }
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).showSoftInput(v, InputMethodManager.SHOW_FORCED);
    }

    protected abstract PageViewContainer getPVC();

    @Override
    public boolean isFinishing() {
        boolean ret = super.isFinishing();
        if (ret) {
            return true;
        }
        if (Build.VERSION.SDK_INT >= 17) {
            return super.isDestroyed();
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PageViewContainer pvc = getPVC();
        if (pvc != null) {
            pvc.onActivityResult(requestCode, resultCode, data);
        }

    }

}
