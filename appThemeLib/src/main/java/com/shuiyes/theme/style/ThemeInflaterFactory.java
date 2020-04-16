package com.shuiyes.theme.style;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.v4.view.LayoutInflaterFactory;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;

import com.shuiyes.theme.SkinTheme;
import com.shuiyes.theme.ResourceAttr;
import com.shuiyes.theme.util.Common;
import com.shuiyes.theme.util.ReflexUtil;
import com.shuiyes.theme.util.SystemProperties;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 主题方案 = 主题资源包是预安装的
 */
public class ThemeInflaterFactory implements LayoutInflaterFactory {

    final String TAG = this.getClass().getSimpleName();
    public static final boolean DEBUG = false;

    public static final String PKG_SKIN_BLUE = "com.shuiyes.theme.blue";
    public static final String PKG_SKIN_RED = "com.shuiyes.theme.red";

    private String mTheme;
    private Context mContext, mResourceContext;

    public ThemeInflaterFactory(Context context) {
        mContext = context;
        mTheme = SystemProperties.get(Common.THEME_OVERLAY, SkinTheme.DEFAULT.toString());
        initThemeContextIfNeed();
    }

    public boolean isThemeChanged() {
        String theme = SystemProperties.get(Common.THEME_OVERLAY, SkinTheme.DEFAULT.toString());
        if (theme.equals(mTheme)) {
            return false;
        } else {
            mTheme = theme;
            return initThemeContextIfNeed();
        }
    }

    public boolean initThemeContextIfNeed() {
        try {
            final Context newContext;
            Log.d(TAG, "initThemeContext " + mTheme);
            if (SkinTheme.BLUE.toString().equals(mTheme)) {
                newContext = getContextBlue();
            } else if (SkinTheme.RED.toString().equals(mTheme)) {
                newContext = getContextRed();
            } else {
                newContext = mContext;
            }
            if (mResourceContext == null || mResourceContext != newContext) {
                mResourceContext = newContext;
                return true;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    private HashMap<String, Context> mResourceContexts = new HashMap<String, Context>(2);

    private Context getContextBlue() throws PackageManager.NameNotFoundException {
        Context context = mResourceContexts.get(SkinTheme.BLUE.toString());
        if (context == null) {
            context = mContext.createPackageContext(PKG_SKIN_BLUE, Context.CONTEXT_IGNORE_SECURITY | Context.CONTEXT_INCLUDE_CODE);
            mResourceContexts.put(SkinTheme.BLUE.toString(), context);
        }

        return context;
    }

    private Context getContextRed() throws PackageManager.NameNotFoundException {
        Context context = mResourceContexts.get(SkinTheme.RED.toString());
        if (context == null) {
            context = mContext.createPackageContext(PKG_SKIN_RED, Context.CONTEXT_IGNORE_SECURITY | Context.CONTEXT_INCLUDE_CODE);
            mResourceContexts.put(SkinTheme.RED.toString(), context);
        }

        return context;
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
        ThemeView skinView = new ThemeView(view, resAttrs);
        skinView.apply(mResourceContext);
        mSkinViews.add(skinView);

        return view;
    }

    private List<ThemeView> mSkinViews = new ArrayList<>();

    public void refreshAllView() {
        Log.i(TAG, "refreshAllView(" + mResourceContext + ") size=" + mSkinViews.size());
        for (ThemeView item : mSkinViews) {
            item.apply(mResourceContext);
        }
    }

    public void cleared() {
        mSkinViews.clear();
        mResourceContexts.clear();
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

//        if ("ImageView".equals(name)) {
//            int n = attrs.getAttributeCount();
//            for (int i = 0; i < n; i++) {
//                if ("src".equals(attrs.getAttributeName(i))) {
//                    int resid = attrs.getAttributeResourceValue(i, 0);
//                    if (resid > 0) {
//
//                        final String resName = mContext.getResources().getResourceEntryName(resid);
//
//                        ImageView view = new ImageView(context, attrs);
//                        // 从资源APK 加载
//                        if (invalidateImageDrawable(view, resName)) {
//                            mResourceNames.put(view.hashCode(), resName);
//                            return view;
//                        }
//                    }
//                }
//            }
//        }

//    private SparseArray<String> mResourceNames = new SparseArray<String>();
//
//    private void invalidateImageViewIfNeed(ImageView view) {
//        String resName = mResourceNames.get(view.hashCode());
//        if (!TextUtils.isEmpty(resName)) {
//            invalidateImageDrawable(view, resName);
//        }
//    }

//    public void refreshAllView(View view) {
//        if (view instanceof ImageView) {
//            invalidateImageViewIfNeed((ImageView) view);
//        } else if (view instanceof Button) {
//            invalidateButtonIfNeed((Button) view);
//        } else if (view instanceof TextView) {
//            invalidateTextViewIfNeed((TextView) view);
//        } else if (view instanceof ViewGroup) {
//            ViewGroup vp = (ViewGroup) view;
//            invalidateViewGroupIfNeed(vp);
//
//            int childCount = vp.getChildCount();
//            for (int i = 0; i < childCount; i++) {
//                refreshAllView(vp.getChildAt(i));
//            }
//        }
//    }

//    public void  refreshViewRootImpl(){
//            try {
//                Class clz = Class.forName("android.view.ViewRootImpl");
//                Method requestLayout = clz.getDeclaredMethod("requestLayout");
//                requestLayout.setAccessible(true);
//                requestLayout.invoke(getWindow().getDecorView().getParent());
//                Method invalidate = clz.getDeclaredMethod("invalidate");
//                invalidate.setAccessible(true);
//                invalidate.invoke(getWindow().getDecorView().getParent());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//    }

}