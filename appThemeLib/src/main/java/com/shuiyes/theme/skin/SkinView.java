package com.shuiyes.theme.skin;

import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.shuiyes.theme.ResourceAttr;
import com.shuiyes.theme.util.ReflexUtil;

import java.util.HashMap;
import java.util.Set;

class SkinView {

    final String TAG = this.getClass().getSimpleName();
    public static final boolean DEBUG = false;

    private View view;
    private SkinPackage mSkinPackage;
    private HashMap<String, ResourceAttr> resAttrs;

    public SkinView(View v, HashMap<String, ResourceAttr> resAttrs) {
        this.view = v;
        //view 重绘时回调
        v.getViewTreeObserver().addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
            @Override
            public void onDraw() {
                if (view instanceof ImageView) {
                    // 防止代码动态设置 ImageView 图片资源
                    invalidateImageDrawableIfNeed((ImageView) view);
                }

                // 防止代码动态设置背景图片资源
                invalidateBackgroundDrawableIfNeed(view);
            }
        });
        this.resAttrs = resAttrs;
    }

    public void apply(SkinPackage skinPackage) {
        if(skinPackage == null) return;
        mSkinPackage = skinPackage;

        Set<String> keys = resAttrs.keySet();
        for (String key : keys) {
            ResourceAttr attr = resAttrs.get(key);
            String attrName = attr.getAttrName();
            String attrType = attr.getAttrType();
            String resName = attr.getResName();
            if ("background".equals(attrName)) {
                if ("color".equals(attrType)) {
                    invalidateBackgroundColor(view, resName);
                } else if ("drawable".equals(attrType)) {
                    invalidateBackgroundDrawable(view, resName);
                }
            } else if ("textColor".equals(attrName)) {
                if (view instanceof TextView && "color".equals(attrType)) {
                    invalidateTextColor((TextView) view, resName);
                }
            } else if ("text".equals(attrName)) {
                if (view instanceof TextView && "string".equals(attrType)) {
                    invalidateText((TextView) view, resName);
                }
            } else if ("src".equals(attrName)) {
                if (view instanceof ImageView && "drawable".equals(attrType)) {
                    invalidateImageDrawable((ImageView) view, resName);
                }
            } else if ("thumb".equals(attrName)) {
                if (view instanceof SeekBar && "drawable".equals(attrType)) {
                    invalidateSeekBarThumb((SeekBar) view, resName);
                }
            }else{
                // TODO To be perfected
            }
        }
    }

    private boolean invalidateSeekBarThumb(SeekBar view, String resName) {
        int resid = mSkinPackage.getResources().getIdentifier(resName, "drawable", mSkinPackage.getPackageName());
        if (resid > 0) {
            if (DEBUG) {
                Log.w(TAG, "invalidateSeekBarThumb " + resName);
            }
            view.setThumb(mSkinPackage.getResources().getDrawable(resid));
            return true;
        }
        return false;
    }

    private boolean invalidateImageDrawableIfNeed(ImageView view) {
        // setImageResource
        int prevResId = (int) ReflexUtil.getImageViewDeclaredField(view, "mResource");
        if (prevResId > 0) {
            String resName = view.getContext().getResources().getResourceEntryName(prevResId);
            int resid = mSkinPackage.getResources().getIdentifier(resName, "drawable", mSkinPackage.getPackageName());
            ResourceAttr attr = null;
            // 资源 ID/名称 不同
            if (resid > 0 && (resid != prevResId || ((attr = resAttrs.get("src")) != null && !resName.equals(attr.getResName())))) {
                // 更新代码设置的 资源图片名
                if(attr != null){
                    attr.setResName(resName);
                } else {
                    resAttrs.put("src", new ResourceAttr("src", "drawable", resName));
                }

                if (DEBUG) {
                    Log.w(TAG, "invalidateImageDrawableIfNeed " + resName);
                }
                view.setImageDrawable(mSkinPackage.getResources().getDrawable(resid));
                return true;
            }
        }

        // setImageDrawable 暂不支持
//        Drawable prevDrawable = (Drawable) ReflexUtil.getImageViewDeclaredField(view, "mDrawable");
//        if(prevDrawable != null){
//        }

        return false;
    }

    private boolean invalidateImageDrawable(ImageView view, String resName) {
        int resid = mSkinPackage.getResources().getIdentifier(resName, "drawable", mSkinPackage.getPackageName());
        if (resid > 0) {
            if (DEBUG) {
                Log.w(TAG, "invalidateImageDrawable " + resName);
            }
            view.setImageDrawable(mSkinPackage.getResources().getDrawable(resid));
            return true;
        }
        return false;
    }

    private boolean invalidateBackgroundDrawableIfNeed(View view) {
        // setBackgroundResource
        int prevResId = (int) ReflexUtil.getViewDeclaredField(view, "mBackgroundResource");
        if (prevResId > 0) {
            String resName = view.getContext().getResources().getResourceEntryName(prevResId);
            int resid = mSkinPackage.getResources().getIdentifier(resName, "drawable", mSkinPackage.getPackageName());
            ResourceAttr attr = null;
            // 资源 ID/名称 不同
            if (resid > 0 && (resid != prevResId || ((attr = resAttrs.get("background")) != null && !resName.equals(attr.getResName())))) {
                // 更新代码设置的 资源图片名
                if(attr != null){
                    attr.setResName(resName);
                } else {
                    resAttrs.put("background", new ResourceAttr("background", "drawable", resName));
                }

                if (DEBUG) {
                    Log.w(TAG, "invalidateBackgroundDrawableIfNeed " + resName);
                }
                view.setBackground(mSkinPackage.getResources().getDrawable(resid));
                return true;
            }
        }

        // setBackgroundDrawable 暂不支持
//        Drawable prevDrawable = (Drawable) ReflexUtil.getViewDeclaredField(view, "mBackground");
//        if(prevDrawable != null){
//        }

        return false;
    }

    private boolean invalidateBackgroundDrawable(View view, String resName) {
        int resid = mSkinPackage.getResources().getIdentifier(resName, "drawable", mSkinPackage.getPackageName());
        if (resid > 0) {
            if (DEBUG) {
                Log.w(TAG, "invalidateBackgroundDrawable " + resName);
            }
            view.setBackgroundDrawable(mSkinPackage.getResources().getDrawable(resid));
            return true;
        }
        return false;
    }

    private boolean invalidateBackgroundColor(View view, String resName) {
        int resid = mSkinPackage.getResources().getIdentifier(resName, "color", mSkinPackage.getPackageName());
        if (resid > 0) {
            if (DEBUG) {
                Log.w(TAG, "invalidateBackgroundColor " + resName);
            }
            if (resName.startsWith("selector")) {
                view.setBackgroundTintList(mSkinPackage.getResources().getColorStateList(resid));
            } else {
                view.setBackgroundColor(mSkinPackage.getResources().getColor(resid));
            }
            return true;
        }
        return false;
    }

    private boolean invalidateTextColor(TextView view, String resName) {
        int resid = mSkinPackage.getResources().getIdentifier(resName, "color", mSkinPackage.getPackageName());
        if (resid > 0) {
            if (DEBUG) {
                Log.w(TAG, "invalidateTextColor " + resName);
            }
            if (resName.startsWith("selector")) {
                view.setTextColor(mSkinPackage.getResources().getColorStateList(resid));
            } else {
                view.setTextColor(mSkinPackage.getResources().getColor(resid));
            }
            return true;
        }
        return false;
    }

    private boolean invalidateText(TextView view, String resName) {
        int resid = mSkinPackage.getResources().getIdentifier(resName, "string", mSkinPackage.getPackageName());
        if (resid > 0) {
            if (DEBUG) {
                Log.w(TAG, "invalidateText " + resName);
            }
            view.setText(mSkinPackage.getResources().getText(resid));
            return true;
        }
        return false;
    }

}