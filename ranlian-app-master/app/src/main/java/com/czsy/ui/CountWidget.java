package com.czsy.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.czsy.android.R;

// 数量控件
public class CountWidget extends FrameLayout implements View.OnClickListener {
    public CountWidget(Context context) {
        super(context);
    }

    public CountWidget(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CountWidget(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private EditText et_value;
    //private int value = 1;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        removeAllViews();
        addView(View.inflate(getContext(), R.layout.count_widget, null));
        findViewById(R.id.btn_minus).setOnClickListener(this);
        findViewById(R.id.btn_add).setOnClickListener(this);
        et_value = findViewById(R.id.et_value);
        et_value.setText("1");

        et_value.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().isEmpty()){

                    int val = Integer.parseInt(s.toString());
                    getCont.cont(val);
                }
            }
        });
    }

    public int getValue() {
        try {
            int v = Integer.valueOf(et_value.getText().toString().trim());
            return v;
        } catch (Exception e) {

        }
        return 1;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        int val = getValue();
        if (val < 1) {
            val = 1;
        }
        if (R.id.btn_minus == id) {
            if (val <= 1) {
                return;
            }
            val--;
            et_value.setText(String.valueOf(val));

            getCont.cont(val);
        } else if (R.id.btn_add == id) {
            val++;
            et_value.setText(String.valueOf(val));
            getCont.cont(val);
        }

    }

    public interface GetCont{
        void cont(int cont);
    }

    private GetCont getCont;

    public void setCallBack(GetCont getCont){
        this.getCont = getCont;
    }
}
