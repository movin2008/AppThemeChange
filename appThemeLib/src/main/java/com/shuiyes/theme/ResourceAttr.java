package com.shuiyes.theme;

public class ResourceAttr {
    // 属性名（例如：background、textColor）
    private String attrName;

    // 属性类型（例如：drawable、color）
    private String attrType;

    // 资源名称（例如：home_phone）
    private String resName;

    public ResourceAttr(String attrName, String attrType, String resName) {
        this.attrName = attrName;
        this.attrType = attrType;
        this.resName = resName;
    }

    public String getAttrName() {
        return attrName;
    }

    public String getAttrType() {
        return attrType;
    }

    public String getResName() {
        return resName;
    }

    public void setResName(String resName) {
        this.resName = resName;
    }
}
