package com.nst.jiazheng.base;

import android.app.Application;


/**
 * 创建者     彭龙
 * 创建时间   2020/4/10 9:21 AM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SpUtil.init(this);
        DpUtil.init(this);
    }
}
