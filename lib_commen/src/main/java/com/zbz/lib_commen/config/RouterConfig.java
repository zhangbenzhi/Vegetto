package com.zbz.lib_commen.config;

/**
 * @author 张本志
 * @date 2020/12/27 18:43
 * @description router
 * attention: 不同module的一级路径必须不同，否则会导致一个moudle中的一级路径失效
 * https://www.houtouke.com/20410.html
 */
public class RouterConfig {

    public interface SplashModule {
        String ADRouter = "/splash/vegetto/ad";
    }

    public interface MainModule {
        String MainRouter = "/main/vegetto/main";
    }
}
