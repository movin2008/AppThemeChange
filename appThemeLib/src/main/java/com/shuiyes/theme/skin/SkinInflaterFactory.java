package com.shuiyes.theme.skin;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.v4.view.LayoutInflaterFactory;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;

import com.shuiyes.theme.ResourceAttr;
import com.shuiyes.theme.SkinTheme;
import com.shuiyes.theme.util.Common;
import com.shuiyes.theme.util.ReflexUtil;
import com.shuiyes.theme.util.SystemProperties;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 皮肤方案 = 皮肤资源包是动态加载的
 */
public class SkinInflaterFactory implements LayoutInflaterFactory {

    final String TAG = this.getClass().getSimpleName();
    public static final boolean DEBUG = false;

    private String mTheme;
    private Context mContext;
    private SkinPackage mSkinPackage;

    public SkinInflaterFactory(Context context) {
        mContext = context;
        mTheme = SystemProperties.get(Common.THEME_OVERLAY, "");
        initThemeResourcesIfNeed();
    }

    public boolean isThemeChanged() {
        String theme = SystemProperties.get(Common.THEME_OVERLAY, "");
        if (theme.equals(mTheme)) {
            return false;
        } else {
            mTheme = theme;
            return initThemeResourcesIfNeed();
        }
    }

    public boolean initThemeResourcesIfNeed() {
        try {
            final SkinPackage newSkinPackage;
            Log.d(TAG, "initThemeContext " + mTheme);
            if (SkinTheme.BLUE.toString().equals(mTheme)) {
                newSkinPackage = getSkinPackage("/sdcard/skin.blue.apk", mTheme);
            } else if (SkinTheme.RED.toString().equals(mTheme)) {
                newSkinPackage = getSkinPackage("/sdcard/skin.red.apk", mTheme);
            } else {
                newSkinPackage = new SkinPackage(mContext);
            }
            if (mSkinPackage == null || mSkinPackage != newSkinPackage) {
                mSkinPackage = newSkinPackage;
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private HashMap<String, SkinPackage> mSkinPackages = new HashMap<String, SkinPackage>(2);

    private Resources createThemeResources(String dexPath) throws Exception {
        AssetManager assetManager = AssetManager.class.newInstance();
        Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
        addAssetPath.invoke(assetManager, dexPath);
        Resources superRes = mContext.getResources();
        Resources resources = new Resources(assetManager, superRes.getDisplayMetrics(), superRes.getConfiguration());
        return resources;
    }

    private SkinPackage getSkinPackage(String dexPath, String theme) throws Exception {
        SkinPackage skinPackage = mSkinPackages.get(theme);
        if (skinPackage == null) {

            PackageInfo packageInfo = mContext.getPackageManager().getPackageArchiveInfo(dexPath, 0);
            if (packageInfo == null) {
                return null;
            }

            Log.e("HAHA", "packageInfo " + packageInfo.packageName);
            Resources resources = createThemeResources(dexPath);
            mSkinPackages.put(theme, skinPackage = new SkinPackage(packageInfo.packageName, resources));
        }

        return skinPackage;
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {

        if (DEBUG) {
            Log.w(TAG, name + Common.attrsToStr(mContext, attrs));
        }

        View view = null;
        try {
            if (name.contains(".")) {
                // custom views
                Class<View> clz = (Class<View>) Class.forName(name);
                Constructor<View> con = clz.getConstructor(Context.class, AttributeSet.class);
                view = con.newInstance(context, attrs);
            } else {
                if (View.class.getSimpleName().equals(name)) {
                    // View.java
                    view = LayoutInflater.from(context).createView(name, "android.view.", attrs);
                }
                if (view == null) {
                    // TextView.java Button.java ImageView.java FrameLayout.java etc
                    view = LayoutInflater.from(context).createView(name, "android.widget.", attrs);
                }
                if (view == null) {
                    // WebView.java
                    view = LayoutInflater.from(context).createView(name, "android.webkit.", attrs);
                }
            }
        } catch (Exception e) {
            if (DEBUG) {
                Log.e(TAG, e.getLocalizedMessage());
            }
        }

        if (view == null) {
            return null;
        }

        Resources resources = context.getResources();
        int n = attrs.getAttributeCount();
        HashMap<String, ResourceAttr> resAttrs = new HashMap<String, ResourceAttr>();
        for (int i = 0; i < n; i++) {
            // @2130968662 @resId
            // String attrValue = attrs.getAttributeValue(i);
            // attrValue.startWith("@")
            int resId = attrs.getAttributeResourceValue(i, 0);
            if (resId > 0) {
                // pkg:type/entry
                // com.shuiyes.overlaytest.target:color/colorTest
                // com.shuiyes.overlaytest.target:color/text_color_selector
                // com.shuiyes.overlaytest.target:drawable/home_phone

                String attrName = attrs.getAttributeName(i);

                if ("style".equals(attrName)) {
                    // 获取 Style 设置的背景
                    final TypedArray a = context.obtainStyledAttributes(resId, new int[]{ReflexUtil.getInternalR$styleable("View_background")});

                    TypedValue value = ReflexUtil.getTypedArrayValue(a, "mValue");
                    String attrType = resources.getResourceTypeName(value.resourceId);
                    String resName = resources.getResourceEntryName(value.resourceId);
                    resAttrs.put(attrName, new ResourceAttr("background", attrType, resName));
                    if (DEBUG) {
                        Log.w(TAG, view.getClass().getSimpleName() + "[background=\"@" + attrType + "/" + resName + "\"]");
                    }

                    a.recycle();
                } else if ("background".equals(attrName) || "src".equals(attrName) || "textColor".equals(attrName) || "text".equals(attrName) || "thumb".equals(attrName)) {
                    String attrType = resources.getResourceTypeName(resId);
                    String resName = resources.getResourceEntryName(resId);
                    resAttrs.put(attrName, new ResourceAttr(attrName, attrType, resName));

                    if (DEBUG) {
                        Log.w(TAG, view.getClass().getSimpleName() + "[" + attrName + "=\"@" + attrType + "/" + resName + "\"]");
                    }
                }
            }
        }
        SkinView skinView = new SkinView(view, resAttrs);
        skinView.apply(mSkinPackage);
        mSkinViews.add(skinView);

        return view;
    }

    private List<SkinView> mSkinViews = new ArrayList<>();

    public void refreshAllView() {
        Log.i(TAG, "refreshAllView(" + mSkinPackage + ") size=" + mSkinViews.size());
        for (SkinView item : mSkinViews) {
            item.apply(mSkinPackage);
        }
    }

    public void cleared() {
        mSkinViews.clear();
        mSkinPackages.clear();
    }

    public void refeshViewIfNeed() {
        boolean changed = isThemeChanged();
        if (DEBUG) {
            Log.i(TAG, "refeshViewIfNeed: " + changed);
        }
        if (changed) {
            refreshAllView();
        }
    }

}