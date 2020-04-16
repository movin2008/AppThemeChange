package com.shuiyes.theme.util;

import android.content.res.TypedArray;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;

import java.lang.reflect.Field;

public class ReflexUtil {

    public static Object getImageViewDeclaredField(ImageView imageView, String fieldName){
        try {
            Field field = ImageView.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return  field.get(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getViewDeclaredField(View view, String fieldName){
        try {
            Field field = View.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return  field.get(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getInternalR$styleable(String fieldName){
        try {
            Class clz = Class.forName("com.android.internal.R$styleable");
            Field field = clz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return (int) field.get(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static TypedValue getTypedArrayValue(TypedArray a, String fieldName){
        try {
            Field field = TypedArray.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return  (TypedValue) field.get(a);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
