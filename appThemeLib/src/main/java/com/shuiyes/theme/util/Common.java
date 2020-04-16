package com.shuiyes.theme.util;

import android.content.Context;
import android.util.AttributeSet;

public class Common {

    public static final String THEME_OVERLAY = "persist.sys.theme.overlay";

    public static String attrsToStr(Context context, AttributeSet attrs) {
        int n = attrs.getAttributeCount();
        StringBuffer buf = new StringBuffer("[");
        for (int i = 0; i < n; i++) {
            int resId = attrs.getAttributeResourceValue(i, 0);
            if (resId > 0) {
                String type = context.getResources().getResourceTypeName(resId);
                String entry = context.getResources().getResourceEntryName(resId);
                buf.append(attrs.getAttributeName(i) + "=@" + type + "/" + entry + ((i == n - 1) ? ";" : "; "));
            } else {
                buf.append(attrs.getAttributeName(i) + "=" + attrs.getAttributeValue(i) + ((i == n - 1) ? ";" : "; "));
            }
        }
        return buf.append("]").toString();
    }

}
