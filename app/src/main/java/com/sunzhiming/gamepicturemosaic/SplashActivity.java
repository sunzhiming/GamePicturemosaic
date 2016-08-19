package com.sunzhiming.gamepicturemosaic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by sunzhiming on 2016/8/19.
 */
public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aplash);

        new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
