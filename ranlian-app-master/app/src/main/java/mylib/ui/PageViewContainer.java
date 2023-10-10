package mylib.ui;

import java.util.Iterator;
import java.util.Stack;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ViewFlipper;
import mylib.app.MyLog;
import mylib.utils.Utils;

public class PageViewContainer extends ViewFlipper {
	@Override
	protected void onDetachedFromWindow() {
		try {
			super.onDetachedFromWindow();
		} catch (IllegalArgumentException e) {
			// crash when portriat changes
			MyLog.LOGE(e);
		}
	}

	public PageViewContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public void onStop() {
		if (mCurPage != null) {
			mCurPage.onStop();
		}
	}

	public void onPause() {
		if (mCurPage != null) {
			mCurPage.onPause();
		}
	}

	public void onResume() {
		if (mCurPage != null) {
			mCurPage.onResume();
		}
	}

	public void onStart() {
		if (mCurPage != null) {
			mCurPage.onStart();
		}
	}

	public PageViewContainer(Context context) {
		super(context);
		init();
	}

	protected void init() {
		removeAllViews();
	}

	protected AbstractPageView mCurPage;

	public AbstractPageView getCurrentPage() {
		return mCurPage;
	}

	protected Stack<AbstractPageView> mViewStack = new Stack<AbstractPageView>();

	public int getViewCount() {
		return mViewStack.size() + (mCurPage == null ? 0 : 1);
	}

	public void clear() {

		if (null != mCurPage) {
			mCurPage.onDetach(true);
		}
		try {
			removeAllViews(); // zzw: FIXME?? err
		} catch (Throwable t) {
			MyLog.LOGE(t);
		}
		mCurPage = null;
		for (AbstractPageView pv : mViewStack) {
			pv.onDetach(true);
		}
		mViewStack.clear();

	}

	public void clearAndPush(AbstractPageView page) {
		//push(page);
		//mViewStack.clear();
		clear();

		push(page);
	}

	/**
	private static class AnimTask implements AnimationListener {
		AnimTask(AbstractPageView pv, boolean attach) {
			mPV = pv;
			mAttach = attach;
		}
	
		private AbstractPageView mPV;
	
		private boolean mAttach;
	
		@Override
		public void onAnimationStart(Animation animation) {
	
		}
	
		@Override
		public void onAnimationRepeat(Animation animation) {
	
		}
	
		@Override
		public void onAnimationEnd(Animation animation) {
			if (null == mPV) {
				return;
			}
			if (mAttach) {
				mPV.onAttach();
			} else {
				mPV.onDetach();
			}
		}
	};
	**/
	public void replace(final AbstractPageView page) {
		boolean hasCurPage = null != mCurPage;
		Context ctx = getContext();
        if (hasCurPage) {
            mCurPage.onDetach(true);
        }
		if (ctx instanceof Activity) {
			Utils.hideIME((Activity) ctx);
		}
		removeAllViews();
		if (null != page) {
			final View v = page.getView(ctx);
			addView(v);
			Animation anim = page.getPushInAnim();
			if (null != anim && hasCurPage) {
				v.startAnimation(anim);
			}
			page.onAttach(true); // call onAttach quickly
		}
		mCurPage = page;
	}
	public void push(final AbstractPageView page) {
		boolean hasCurPage = null != mCurPage;
		Context ctx = getContext();
		if (hasCurPage) {
			mViewStack.push(mCurPage);
			View v = mCurPage.getView(ctx);
			Animation anim = mCurPage.getPushDownAnim();
			mCurPage.onDetach(false);
			if (null != anim) {
				//anim.setAnimationListener(new AnimTask(mCurPage, false));
				v.startAnimation(anim);
			}
		}
		if (ctx instanceof Activity) {
			Utils.hideIME((Activity) ctx);
		}
		removeAllViews();
		if (null != page) {
			final View v = page.getView(ctx);
			addView(v);
			Animation anim = page.getPushInAnim();
			if (null != anim && hasCurPage) {
				v.startAnimation(anim);
			}
			page.onAttach(true); // call onAttach quickly
		}
		mCurPage = page;
	}

//	public boolean prompt(Class<? extends AbstractPageView> pvc) {
//		if (mCurPage == null) {
//			return false;
//		}
//		if (mCurPage.getClass() == pvc) {
//			return true;
//		}
//		Iterator<AbstractPageView> iter = mViewStack.iterator();
//		while (iter.hasNext()) {
//			AbstractPageView apv = iter.next();
//			if (apv.getClass() == pvc) {
//				iter.remove();
//				push(apv);
//				return true;
//			}
//		}
//		return false;
//	}

	public void popTo(AbstractPageView apv) {

		if (mCurPage == null || mCurPage == apv || apv == null) {
			return;
		}
		AbstractPageView oldPage = mCurPage;
		mCurPage.onDetach(true);
		while (!mViewStack.isEmpty()) {
			mCurPage = mViewStack.pop();
			if (mCurPage == apv) {
				break;
			} else {
				mCurPage.onDetach(true);
				mCurPage = null;
			}

		}
		Context ctx = getContext();
		if (ctx instanceof Activity) {
			Utils.hideIME((Activity) ctx);
		}
		removeAllViews();
		Animation anim = oldPage.getPopOutAnim();
		if (null != anim) {
			oldPage.getView(ctx).startAnimation(anim);
		}
		if (mCurPage != null) { // found
			anim = mCurPage.getPopUpAnim();
			if (null != anim) {
				mCurPage.getView(ctx).startAnimation(anim);
			}
		} else {

		}
	}

	public AbstractPageView pop() {
		boolean hasPV = !mViewStack.isEmpty();
		AbstractPageView old = mCurPage;
		Context ctx = getContext();
		if (null != mCurPage) {
			mCurPage.onDetach(true);
			if (hasPV) {
				View v = mCurPage.getView(ctx);
				Animation anim = mCurPage.getPopOutAnim();
				if (null != anim) {
					v.startAnimation(anim);
				}
			}
			mCurPage = null;
		}
		if (ctx instanceof Activity) {
			Utils.hideIME((Activity) ctx);
		}
		removeAllViews();
		if (hasPV) {
			mCurPage = mViewStack.pop();
			final View v = mCurPage.getView(ctx);
			addView(v);
			Animation anim = mCurPage.getPopUpAnim();
			if (null != anim) {
				v.startAnimation(anim);
			}
			mCurPage.onAttach(false);
		}
		return old;
	}

	public boolean doBackPressed() {
		if (null == mCurPage) {
			return false;
		}
		boolean handled = mCurPage.doBackPressed();
		//boolean hasPV = !mViewStack.isEmpty();
		if (!handled) {
			if (!mViewStack.isEmpty()) {
				pop(); // handle it!
				return true;
			}
			return false;
		}
		return true;
		/*if (null == mCurPage) {
			return false;
		}
		if (mViewStack.isEmpty()) {
			return mCurPage.doBackPressed();
		}
		boolean handled = mCurPage.doBackPressed();
		if (!handled) {
			pop();
		}
		return true;*/
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (mCurPage == null) {
			return;
		}
		mCurPage.onActivityResult(requestCode, resultCode, data);
	}
}
