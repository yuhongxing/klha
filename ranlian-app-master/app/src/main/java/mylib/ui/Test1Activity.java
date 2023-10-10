package mylib.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.czsy.android.R;
import com.czsy.ui.MainActivity;
import com.czsy.ui.MainPVC;

import mylib.app.BaseActivity;

public class Test1Activity extends BaseActivity {

    @Override
    public MainPVC getPVC() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pv_setting1);
    }
}