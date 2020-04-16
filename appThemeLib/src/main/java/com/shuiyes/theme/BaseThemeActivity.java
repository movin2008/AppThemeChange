package com.shuiyes.theme;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.LayoutInflaterCompat;
import android.view.LayoutInflater;

import com.shuiyes.theme.style.ThemeInflaterFactory;

public class BaseThemeActivity extends Activity {
    private ThemeInflaterFactory mResourceFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mResourceFactory = new ThemeInflaterFactory(getApplicationContext());
        LayoutInflaterCompat.setFactory(LayoutInflater.from(this), mResourceFactory);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mResourceFactory.cleared();
        mResourceFactory = null;
    }

    // 测试用 5s 检查一次主题变化
    private Runnable mTestRunnable = new Runnable() {
        @Override
        public void run() {
            mResourceFactory.refeshViewIfNeed();
            mHandler.postDelayed(mTestRunnable, 5555);
        }
    };

    private android.os.Handler mHandler = new android.os.Handler();
    @Override
    protected void onResume() {
        super.onResume();
        mHandler.postDelayed(mTestRunnable, 5555);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(mTestRunnable);
    }
}
