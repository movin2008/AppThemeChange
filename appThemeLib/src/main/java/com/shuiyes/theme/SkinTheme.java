package com.shuiyes.theme;

public enum SkinTheme {

    DEFAULT,
    BLUE,
    RED;

    @Override
    public String toString() {
        return super.name().toLowerCase();
    }

}
