package com.nst.jiazheng.base;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.nst.jiazheng.api.Api;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.rong.imkit.RongIM;


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
        OkGo.getInstance().setCacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST).init(this);
        RongIM.init(this, Api.ryKey);
    }
}
