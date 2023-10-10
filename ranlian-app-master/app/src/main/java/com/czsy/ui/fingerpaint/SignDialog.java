package com.czsy.ui.fingerpaint;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.czsy.android.R;

public class SignDialog extends Dialog {
    public FingerPaintView finger_view;

    public SignDialog(@NonNull Context context) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        setContentView(R.layout.dialog_sign);
        finger_view = findViewById(R.id.finger_view);
    }

    public void setClickListener(View.OnClickListener click) {
        findViewById(R.id.iv_back).setOnClickListener(click);
        findViewById(R.id.btn_ok).setOnClickListener(click);
        findViewById(R.id.btn_reset).setOnClickListener(click);
    }
}
