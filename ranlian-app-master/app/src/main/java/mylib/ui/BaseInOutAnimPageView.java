package mylib.ui;

import android.view.animation.Animation;
import mylib.utils.Global;

public abstract class BaseInOutAnimPageView extends AbstractPageView {

	public Animation getPushInAnim() { // new in
		return Global.UI.sAnimInRight;
	}

	public Animation getPopOutAnim() { // old out
		return Global.UI.sAnimOutRight;
	}

	public Animation getPopUpAnim() { // old from stack
		return Global.UI.sAnimInLeft;
	}

	public Animation getPushDownAnim() { // old 2 stack
		return Global.UI.sAnimOutLeft;
	}
}
