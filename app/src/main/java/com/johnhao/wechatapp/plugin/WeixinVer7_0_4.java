package com.johnhao.wechatapp.plugin;

public class WeixinVer7_0_4 extends WeixinVerBase{

    public WeixinVer7_0_4() {
        initLog();
        appcache();
        initAppBrandMenu();
        initAppBrandSysConfig();
    }

    @Override
    protected void initLog() {
        WXLOG_CLS_PLATFORMTOOLS_LOG = "com.tencent.mm.sdk.platformtools.ab";
        WXLOG_SIMPLE_FUN_LOG_F = "f";
        WXLOG_SIMPLE_FUN_LOG_E = "e";
        WXLOG_SIMPLE_FUN_LOG_W = "w";
        WXLOG_SIMPLE_FUN_LOG_I = "i";
        WXLOG_SIMPLE_FUN_LOG_D = "d";
        WXLOG_SIMPLE_FUN_LOG_V = "v";
        WXLOG_SIMPLE_FUN_LOG_K = "c";
        WXLOG_SIMPLE_FUN_LOG_L = "b";
        ABLOG_CLS_JSAPI_PARMA0 = "com.tencent.mm.plugin.appbrand.s";    // 小程序Log方法的参数0
        ABLOG_CLS_JSAPI_LOG = "com.tencent.mm.plugin.appbrand.jsapi.ar";
        ABLOG_SIMPLE_FUN_JSAPI_LOG = "a";

    }

    @Override
    protected void appcache() {
        WX_CLS_APPCACHE = "com.tencent.mm.plugin.appbrand.appcache.aw";
        WX_METHOD_APPCACHE = "a";
    }


    protected void initAppBrandSysConfig() {
        ABI_CLS_APPBRAND_APPBRANDSYSCONFIG = "com.tencent.luggage.sdk.config.AppBrandSysConfigLU";
        ABI_METHOD_APPBRANDSYSCONFIG_STRING_TOSTRING = "toString";
        ABI_CLS_APPBRAND_WXAPKGWRAPPINGINFO = "com.tencent.mm.plugin.appbrand.appcache.WxaPkgWrappingInfo";
        ABI_CLS_APPBRAND_INIT_CONFIG = "com.tencent.mm.plugin.appbrand.o$6";
        ABI_SIMPLE_FUN_APPBRAND_INIT_CONFIG = "a";
        ABI_FIELD_CONFIG_APPBRAND_DEBUG = "fqw";
        ABI_FIELD_CONFIG_APPBRAND_WXAPKGWRAPPINGINFO = "frm";
        ABI_FIELD_CONFIG_APPBRAND_APPID = "appId";
        ABI_FIELD_CONFIG_APPBRAND_GAMENAME = "bKC";
        ABI_FIELD_WRAPPINGINFO_APPBRAND_DEBUG = "fih";
    }

    @Override
    protected void initAppBrandMenu() {
        ABI_CLS_APPBRAND_MENU_ADD_PARMA1 = "com.tencent.mm.plugin.appbrand.page.u";
        ABI_CLS_APPBRAND_MENU_ADD_PARMA2 = "com.tencent.mm.ui.base.l";

        ABI_SIMPLE_FUN_APPBRAND_MENU_ADD = "f";                                                     // 小程序添加菜单方法名
        ABI_CLS_APPBRAND_MENU_CHICK_PARMA1 = ABI_CLS_APPBRAND_MENU_ADD_PARMA1;                      // 小程序菜单点击参数1
        ABL_CLS_APPBRAND_MENU_CHICK_PARMA3 = "com.tencent.mm.plugin.appbrand.menu.n";               // 小程序菜单点击参数3
        ABI_SIMPLE_FUN_APPBRAND_MENU_CHICK = "a";                                                   // 小程序菜单点击方法名
        ABI_CLS_APPBRAND_MENU_WIDGET_BASE = ABI_CLS_APPBRAND_MENU_ADD_PARMA2;
        ABI_SIMPLE_FUN_APPBRAND_MENU_ADD_WIDGET_BASE = "e";                                         // 小程序菜单UI基类添加控件方法名
    }


    @Override
    protected void initAppBrandMenuItem() {
        ABI_CLS_APPBRAND_MENU_DEBUG = "com.tencent.mm.plugin.appbrand.menu.MenuDelegate_EnableDebug";   // 小程序 开启/关闭调试 菜单
        ABI_CLS_APPBRAND_MENU_FORWARD = "com.tencent.mm.plugin.appbrand.menu.m";
        ABI_CLS_APPBRAND_MENU_DISPLAYDEBUGGING = "com.tencent.mm.plugin.appbrand.menu.l";
        ABI_CLS_APPBRAND_MENU_APPID = "com.tencent.mm.plugin.appbrand.menu.d";
        ABI_CLS_APPBRAND_MENU_BASE_SHOW = "com.tencent.mm.plugin.appbrand.page.u";
        ABI_SIMPLE_FUN_APPBRAND_MENU_BASE_SHOW = "a";
    }

}
