package com.zalbyte.theinstaller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import window.fullscreen;

public class LandingActivity extends AppCompatActivity {
    fullscreen full;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        full = new fullscreen(LandingActivity.this);
        logic();
    }
    private void logic()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(LandingActivity.this, MainActivity.class));
                LandingActivity.this.finish();
            }
        },2000);
    }
}