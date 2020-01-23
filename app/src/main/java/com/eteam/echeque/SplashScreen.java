package com.eteam.echeque;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent intent = new Intent(this,MainActivity.class);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                startActivity(intent);
                finish();
        }
        },2000);

    }
}
