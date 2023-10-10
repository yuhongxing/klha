package mylib.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;

public abstract class AbstractPageView {
    //protected PageViewContainer mContainer;

    protected View mMainView;

    public AbstractPageView() { //PageViewContainer container) {
        //mContainer = container;
    }

    abstract protected void createMainView(Context ctx);


    public void onAttach(boolean firstShow) {

    }

    public void onDetach(boolean lastShow) {

    }

    public View getView(Context ctx) {
        if (mMainView == null) {
            createMainView(ctx);
        }
        return mMainView;
    }

    public void onSaveInstanceState(Bundle outState) {
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
    }

    public void onStart() {
    }

    public void onStop() {
    }

    public void onPause() {
    }

    public void onResume() {
    }

    public boolean doBackPressed() {
        return false;
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        return false;
    }

    public String getName() {
        return getClass().getSimpleName();
    }

    //
    public Animation getPushInAnim() {
        return null;
    }

    public Animation getPopOutAnim() {
        return null;
    }

    public Animation getPopUpAnim() {
        return null;
    }

    public Animation getPushDownAnim() {
        return null;
    }
}
