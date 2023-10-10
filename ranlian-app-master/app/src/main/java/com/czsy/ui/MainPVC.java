package com.czsy.ui;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;

import com.czsy.ui.sqg.PVSQGApp;

import mylib.app.MyLog;
import mylib.ui.AbstractPageView;
import mylib.ui.PageViewContainer;
import mylib.utils.Utils;

public class MainPVC extends PageViewContainer {
    final MainActivity act;

    public MainPVC(MainActivity context) {
        super(context);
        act = context;
    }

    /**
     * 返回到主页面
     * @return
     */
    public MainPVC popTo1() {
        if (mCurPage == null || mViewStack.isEmpty()) {
            return this;
        }
        mCurPage.onDetach(true);
        mCurPage = null;
        while (true) {
            mCurPage = mViewStack.pop();
            if (mViewStack.empty()) {
                break;
            }
            mCurPage.onDetach(true);
        }
        Context ctx = getContext();
        removeAllViews();
        if (ctx instanceof Activity) {
            Utils.hideIME((Activity) ctx);
        }
        if (null != mCurPage) {
            final View v = mCurPage.getView(ctx);
            addView(v);
            mCurPage.onAttach(false); // call onAttach quickly
//            if (mCurPage instanceof PVSQGApp) {
//                PVSQGApp ppv = (PVSQGApp) mCurPage;
//                ppv.reloadHuiZong();
//            }
        }


        return this;
    }

    /**
     * 前进一个页面(打开一个新的页面)
     * @param page （目标页面）
     */
    @Override
    public void push(AbstractPageView page) {
        super.push(page);
//        MyLog.LOGD("Push: " + page.getClass().getName());
        Log.i("page","Push: " + page.getClass().getName());
        act.checkNFC(true);
    }

    /**
     * 返回上一页面
     * @return
     */
    @Override
    public AbstractPageView pop() {
        AbstractPageView ret = super.pop();
        if (ret != null) {
//            MyLog.LOGD("Pop: " + ret.getClass().getName());
            Log.i("page","Pop: " + ret.getClass().getName());
        }
        act.checkNFC(true);
        return ret;
    }

    public void popMe(AbstractPageView pv) {
        if (getCurrentPage() == pv) {
            pop();
        }
    }

    /**
     * 替换当前页面（相当于关闭当前页面，打开新的页面）
     * @param page （目标页面）
     */
    @Override
    public void replace(AbstractPageView page) {
        super.replace(page);
//        MyLog.LOGD("Replace: " + page.getClass().getName());
        Log.i("page","Replace: " + page.getClass().getName());
        act.checkNFC(true);
    }
}
