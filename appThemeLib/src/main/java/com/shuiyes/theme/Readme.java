package com.shuiyes.theme;

/**
 * 基于 LayoutInflaterFactory 主题/皮肤 切换方案
 *
 * 思路：添加 LayoutInflaterFactory 钩子后，监听 View 的inflater 事件，解析 View 的 AttributeSet，
 * 缓存 View 的 background textColor text src thumb 及其资源名称，主题切换后，遍历缓存的 View List，
 * 更新皮肤包(Resources) 里面相同名称的资源
 *
 * 优点：
 * 1、View 直接更新，不需要重建Activity
 * 2、APP 只需要修改 Activity 继承 BaseThemeActivity，其他没有任何修改
 *
 * 缺点：
 * 1、要换肤的的资源，皮肤包和应用包的资源名称必须一致
 * 2、selector color 命名必须以 selector 开头
 * 3、代码设置的 setImageDrawable(Drawable) setBackgroundDrawable(Drawable) setText(Sting) 暂不支持更新
 *    可使用 setImageResource(int) setBackgroudResource(int) setText(int) 代替
 * 4、布局复杂情况，首次启动会比无主题下稍微耗时
 * 5、图片等资源是统一替换，不支持部分资源替换(支持需要添加AP 布局自定义属性值)
 *
 * ps:和网上版本比，优化了实战中发现的部分问题，如View 资源是由 Style 配置的问题，代码动态设置资源问题等
 */
public interface Readme {
}
