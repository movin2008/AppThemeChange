package com.shuiyes.theme.style;

import android.content.Context;
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

class ThemeView {

    final String TAG = this.getClass().getSimpleName();
    public static final boolean DEBUG = false;

    private View view;
    private Context context;
    private HashMap<String, ResourceAttr> resAttrs;

    public ThemeView(View v, HashMap<String, ResourceAttr> resAttrs) {
        this.view = v;
        //view 重绘时回调
        v.getViewTreeObserver().addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
            @Override
            public void onDraw() {
                if (context == null) return;
                if (view instanceof ImageView) {
                    // 防止代码动态设置 ImageView 图片资源
                    invalidateImageDrawableIfNeed(context, (ImageView) view);
                }

                // 防止代码动态设置背景图片资源
                invalidateBackgroundDrawableIfNeed(context, view);
            }
        });
        this.resAttrs = resAttrs;
    }

    public void apply(Context context) {
        if (view == null || resAttrs == null) return;
        this.context = context;

        Set<String> keys = resAttrs.keySet();
        for (String key : keys) {
            ResourceAttr attr = resAttrs.get(key);
            String attrName = attr.getAttrName();
            String attrType = attr.getAttrType();
            String resName = attr.getResName();
            if ("background".equals(attrName)) {
                if ("color".equals(attrType)) {
                    invalidateBackgroundColor(context, view, resName);
                } else if ("drawable".equals(attrType)) {
                    invalidateBackgroundDrawable(context, view, resName);
                }
            } else if ("textColor".equals(attrName)) {
                if (view instanceof TextView && "color".equals(attrType)) {
                    invalidateTextColor(context, (TextView) view, resName);
                }
            } else if ("text".equals(attrName)) {
                if (view instanceof TextView && "string".equals(attrType)) {
                    invalidateText(context, (TextView) view, resName);
                }
            } else if ("src".equals(attrName)) {
                if (view instanceof ImageView && "drawable".equals(attrType)) {
                    invalidateImageDrawable(context, (ImageView) view, resName);
                }
            } else if ("thumb".equals(attrName)) {
                if (view instanceof SeekBar && "drawable".equals(attrType)) {
                    invalidateSeekBarThumb(context, (SeekBar) view, resName);
                }
            }else{
                // TODO To be perfected
            }
        }
    }

    private boolean invalidateSeekBarThumb(Context context, SeekBar view, String resName) {
        int resid = context.getResources().getIdentifier(resName, "drawable", context.getPackageName());
        if (resid > 0) {
            if (DEBUG) {
                Log.w(TAG, "invalidateSeekBarThumb " + resName);
            }
            view.setThumb(context.getDrawable(resid));
            return true;
        }
        return false;
    }

    private boolean invalidateImageDrawableIfNeed(Context context, ImageView view) {
        // setImageResource
        int prevResId = (int) ReflexUtil.getImageViewDeclaredField(view, "mResource");
        if (prevResId > 0) {
            String resName = view.getContext().getResources().getResourceEntryName(prevResId);
            int resid = context.getResources().getIdentifier(resName, "drawable", context.getPackageName());
            ResourceAttr attr = null;
            if (resid > 0 && (resid != prevResId || ((attr = resAttrs.get("src")) != null && !resName.equals(attr.getResName())))) {
                // 更新代码设置的 资源图片名
                if (attr != null) {
                    attr.setResName(resName);
                } else {
                    resAttrs.put("src", new ResourceAttr("src", "drawable", resName));
                }

                if (DEBUG) {
                    Log.w(TAG, "invalidateImageDrawableIfNeed " + resName);
                }
                view.setImageDrawable(context.getDrawable(resid));
                return true;
            }
        }

        // setImageDrawable 暂不支持
//        Drawable prevDrawable = (Drawable) ReflexUtil.getImageViewDeclaredField(view, "mDrawable");
//        if(prevDrawable != null){
//        }

        return false;
    }

    private boolean invalidateImageDrawable(Context context, ImageView view, String resName) {
        int resid = context.getResources().getIdentifier(resName, "drawable", context.getPackageName());
        if (resid > 0) {
            if (DEBUG) {
                Log.w(TAG, "invalidateImageDrawable " + resName);
            }
            view.setImageDrawable(context.getDrawable(resid));
            return true;
        }
        return false;
    }

    private boolean invalidateBackgroundDrawableIfNeed(Context context, View view) {
        // setBackgroundResource
        int prevResId = (int) ReflexUtil.getViewDeclaredField(view, "mBackgroundResource");
        if (prevResId > 0) {
            String resName = view.getContext().getResources().getResourceEntryName(prevResId);
            int resid = context.getResources().getIdentifier(resName, "drawable", context.getPackageName());
            if (resid > 0 && resid != prevResId) {
                // 更新代码设置的 背景图片名
                if (resAttrs.containsKey("background")) {
                    ResourceAttr attr = resAttrs.get("background");
                    attr.setResName(resName);
                } else {
                    resAttrs.put("background", new ResourceAttr("background", "drawable", resName));
                }

                if (DEBUG) {
                    Log.w(TAG, "invalidateBackgroundDrawableIfNeed " + resName);
                }
                view.setBackground(context.getDrawable(resid));
                return true;
            }
        }

        // setBackgroundDrawable 暂不支持
//        Drawable prevDrawable = (Drawable) ReflexUtil.getViewDeclaredField(view, "mBackground");
//        if(prevDrawable != null){
//        }

        return false;
    }

    private boolean invalidateBackgroundDrawable(Context context, View view, String resName) {
        int resid = context.getResources().getIdentifier(resName, "drawable", context.getPackageName());
        if (resid > 0) {
            if (DEBUG) {
                Log.w(TAG, "invalidateBackgroundDrawable " + resName);
            }
            view.setBackgroundDrawable(context.getDrawable(resid));
            return true;
        }
        return false;
    }

    private boolean invalidateBackgroundColor(Context context, View view, String resName) {
        int resid = context.getResources().getIdentifier(resName, "color", context.getPackageName());
        if (resid > 0) {
            if (DEBUG) {
                Log.w(TAG, "invalidateBackgroundColor " + resName);
            }
            if (resName.startsWith("selector")) {
                view.setBackgroundTintList(context.getColorStateList(resid));
            } else {
                view.setBackgroundColor(context.getColor(resid));
            }
            return true;
        }
        return false;
    }

    private boolean invalidateTextColor(Context context, TextView view, String resName) {
        int resid = context.getResources().getIdentifier(resName, "color", context.getPackageName());
        if (resid > 0) {
            if (DEBUG) {
                Log.w(TAG, "invalidateTextColor " + resName);
            }
            if (resName.startsWith("selector")) {
                view.setTextColor(context.getColorStateList(resid));
            } else {
                view.setTextColor(context.getColor(resid));
            }
            return true;
        }
        return false;
    }

    private boolean invalidateText(Context context, TextView view, String resName) {
        int resid = context.getResources().getIdentifier(resName, "string", context.getPackageName());
        if (resid > 0) {
            if (DEBUG) {
                Log.w(TAG, "invalidateText " + resName);
            }
            view.setText(context.getText(resid));
            return true;
        }
        return false;
    }

}