package com.nst.jiazheng.map;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建者     彭龙
 * 创建时间   2020/6/7 10:15 AM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
public class ThridMapUtil {
    // 检索地图软件
    public static boolean isAvilible(Context context, String packageName){
//获取packagemanager
        final PackageManager packageManager = context.getPackageManager();
//获取所有已安装程序的包信息
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
//用于存储所有已安装程序的包名
        List<String> packageNames = new ArrayList<String>();
//从pinfo中将包名字逐一取出，压入pName list中
        if(packageInfos != null){
            for(int i = 0; i < packageInfos.size(); i++){
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        }
//判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
        return packageNames.contains(packageName);
    }

    /**
     * 指定地图
     *百度地图包名：com.baidu.BaiduMap

     高德地图包名：com.autonavi.minimap

     腾讯地图包名：com.tencent.map

     谷歌地图 com.google.android.apps.maps
     *
     */
    public List<String> mapsList (){
        List<String> maps = new ArrayList<>();
        maps.add("com.baidu.BaiduMap");
        maps.add("com.autonavi.minimap");
        maps.add("com.tencent.map");
        return maps;
    }

    // 检索筛选后返回
    public  List<String> hasMap (Context context){
        List<String> mapsList = mapsList();
        List<String> backList = new ArrayList<>();
        for (int i = 0; i < mapsList.size(); i++) {
            boolean avilible = isAvilible(context, mapsList.get(i));
            if (avilible){
                backList.add(mapsList.get(i));
            }
        }
        return backList;
    }
}
