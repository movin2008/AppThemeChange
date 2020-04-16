package com.shuiyes.theme.skin;

import android.content.Context;
import android.content.res.Resources;

class SkinPackage {

    private String packageName;
    private Resources resources;

    public SkinPackage(Context context) {
        this.packageName = context.getPackageName();
        this.resources = context.getResources();
    }

    public SkinPackage(String packageName, Resources resources) {
        this.packageName = packageName;
        this.resources = resources;
    }

    public String getPackageName() {
        return packageName;
    }

    public Resources getResources() {
        return resources;
    }
}
