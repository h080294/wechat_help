package com.johnhao.wechatapp.plugin;

public abstract class WeixinVerBase {

    /**
     * 配置微信log及小程序log
     */

    protected abstract void initLog();

    // 微信Log及小程序Log
    public String WXLOG_CLS_PLATFORMTOOLS_LOG;               // 微信Log类
    public String WXLOG_SIMPLE_FUN_LOG_F;                                                        // 微信Log方法名
    public String WXLOG_SIMPLE_FUN_LOG_E;
    public String WXLOG_SIMPLE_FUN_LOG_W;
    public String WXLOG_SIMPLE_FUN_LOG_I;
    public String WXLOG_SIMPLE_FUN_LOG_D;
    public String WXLOG_SIMPLE_FUN_LOG_V;
    public String WXLOG_SIMPLE_FUN_LOG_K;
    public String WXLOG_SIMPLE_FUN_LOG_L;
    public String ABLOG_CLS_JSAPI_PARMA0;                                                     // 小程序Log方法的参数0
    public String ABLOG_CLS_JSAPI_LOG;                                                        // 小程序Log类
    public String ABLOG_SIMPLE_FUN_JSAPI_LOG;                                                 // 小程序Log方法名

    /**
     * 配置 小程序菜单添加及点击方法
     */
    protected abstract void appcache();
    public String WX_CLS_APPCACHE;                      // game.js的string
    public String WX_METHOD_APPCACHE;                   // 通常为a


    /**
     * 配置 小程序菜单添加及点击方法
     */
    protected abstract void initAppBrandMenu();

    // 小程序菜单修改有关
    public String ABI_CLS_APPBRAND_MENU_ADD_PARMA1;                      // 小程序添加菜单项参数1
    public String ABI_CLS_APPBRAND_MENU_ADD_PARMA2;                      // 小程序添加菜单项参数2
    public String ABI_SIMPLE_FUN_APPBRAND_MENU_ADD;                      // 小程序添加菜单方法名
    public String ABI_CLS_APPBRAND_MENU_CHICK_PARMA1;                    // 小程序菜单点击参数1
    public String ABL_CLS_APPBRAND_MENU_CHICK_PARMA3;                    // 小程序菜单点击参数3
    public String ABI_SIMPLE_FUN_APPBRAND_MENU_CHICK;                    // 小程序菜单点击方法名
    public String ABI_CLS_APPBRAND_MENU_WIDGET_BASE;                     // 小程序菜单UI基类
    public String ABI_SIMPLE_FUN_APPBRAND_MENU_ADD_WIDGET_BASE;          // 小程序菜单UI基类添加控件方法名


    /**
     *  配置小程序 AppBrandSysConfig 获取基础config
     */
    protected abstract void initAppBrandSysConfig();

    // 小程序AppBrandSysConfig获取及修改
    public String ABI_CLS_APPBRAND_APPBRANDSYSCONFIG;                             // AppBrandSysConfig 类
    public String ABI_METHOD_APPBRANDSYSCONFIG_STRING_TOSTRING;
    public String ABI_CLS_APPBRAND_WXAPKGWRAPPINGINFO;                   // WxaPkgWrappingInfo 类
    public String ABI_CLS_APPBRAND_INIT_CONFIG;                          // 初始化AppBrandSysConfig类
    public String ABI_SIMPLE_FUN_APPBRAND_INIT_CONFIG;                   // 初始化AppBrandSysConfig方法
    public String ABI_FIELD_CONFIG_APPBRAND_DEBUG;                       // AppBrandSysConfig中debug字段
    public String ABI_FIELD_CONFIG_APPBRAND_WXAPKGWRAPPINGINFO;          // AppBrandSysConfig中WxaPkgWrappingInfo字段
    public String ABI_FIELD_CONFIG_APPBRAND_APPID;                       // AppBrandSysConfig中appId字段
    public String ABI_FIELD_CONFIG_APPBRAND_GAMENAME;                    // AppBrandSysConfig中游戏名字字段
    public String ABI_FIELD_WRAPPINGINFO_APPBRAND_DEBUG;                 // WxaPkgWrappingInfo中debug字段


    /**
     * 配置修改的具体菜单
     */
    protected abstract void initAppBrandMenuItem();

    // 具体修改小程序有关的菜单
    public String ABI_CLS_APPBRAND_MENU_DEBUG;                           // 小程序 开启/关闭调试 菜单
    public String ABI_CLS_APPBRAND_MENU_FORWARD;                         // 小程序 转发 菜单
    public String ABI_CLS_APPBRAND_MENU_DISPLAYDEBUGGING;                // 小程序 显示调试信息 菜单
    public String ABI_CLS_APPBRAND_MENU_APPID;                           // 小程序 appId 菜单
    public String ABI_CLS_APPBRAND_MENU_BASE_SHOW;                       // 小程序菜单显示 调试控制 基类
    public String ABI_SIMPLE_FUN_APPBRAND_MENU_BASE_SHOW;                // 小程序菜单显示控制 方法名

}
