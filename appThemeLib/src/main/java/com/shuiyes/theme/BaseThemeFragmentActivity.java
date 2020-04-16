package com.shuiyes.theme;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.LayoutInflaterCompat;
import android.view.LayoutInflater;

import com.shuiyes.theme.skin.SkinInflaterFactory;

public class BaseThemeFragmentActivity extends FragmentActivity {
    private SkinInflaterFactory mResourceFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mResourceFactory = new SkinInflaterFactory(getApplicationContext());
        LayoutInflaterCompat.setFactory(LayoutInflater.from(this), mResourceFactory);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mResourceFactory.cleared();
        mResourceFactory = null;
    }

}
