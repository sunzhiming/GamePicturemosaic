package com.sunzhiming.gamepicturemosaic;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

/**
 * Created by sunzhiming on 2016/8/19.
 */
public class JieShaoActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_jieshao);
    }
}
