package com.example.clover;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

/** P1: Activity Lifecycle demo */
public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "CloverLifecycle";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "SplashActivity: onCreate()");
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            SharedPreferences prefs = getSharedPreferences("CloverPrefs", MODE_PRIVATE);
            boolean first = prefs.getBoolean("is_first_launch", true);
            startActivity(new Intent(this, first ? RegistrationActivity.class : MainActivity.class));
            finish();
        }, 2000);
    }

    @Override protected void onStart() { super.onStart(); Log.d(TAG, "SplashActivity: onStart()"); }
    @Override protected void onResume() { super.onResume(); Log.d(TAG, "SplashActivity: onResume()"); }
    @Override protected void onPause() { super.onPause(); Log.d(TAG, "SplashActivity: onPause()"); }
    @Override protected void onStop() { super.onStop(); Log.d(TAG, "SplashActivity: onStop()"); }
    @Override protected void onDestroy() { super.onDestroy(); Log.d(TAG, "SplashActivity: onDestroy()"); }
    @Override protected void onRestart() { super.onRestart(); Log.d(TAG, "SplashActivity: onRestart()"); }
}
