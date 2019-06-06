package com.johnhao.wechatapp.plugin;

public abstract class WeixinVerBase {

    /**
     * 配置微信log及小程序log
     */

    protected abstract void initLog();

    // 微信Log及小程序Log
    public String WXLOG_CLS_PLATFORMTOOLS_LOG="com.tencent.mm.sdk.platformtools.w";               // 微信Log类
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

}
