package com.nst.jiazheng.map;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.nst.jiazheng.R;

import java.util.List;

import razerdp.basepopup.BasePopupWindow;

/**
 * 创建者     彭龙
 * 创建时间   2020/6/7 10:17 AM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
public class MapWindow extends BasePopupWindow {
    public static double lat;
    public static double lon;
    HisLocationBean bean;

    public MapWindow(Context context, HisLocationBean bean) {
        super(context);
        this.bean = bean;
    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.window_map);
    }

    @Override
    public void onViewCreated(View contentView) {
        super.onViewCreated(contentView);
        View map_toast_cancelbt = contentView.findViewById(R.id.map_toast_cancelbt);
        View map_toast_gaodebt = contentView.findViewById(R.id.map_toast_gaodebt);
        View map_toast_baidubt = contentView.findViewById(R.id.map_toast_baidubt);
        View map_toast_tencentbt = contentView.findViewById(R.id.map_toast_tencentbt);
        View map_toast_hinttv = contentView.findViewById(R.id.map_toast_hinttv);
        List<String> hasMap = new ThridMapUtil().hasMap(getContext());
        for (int i = 0; i < hasMap.size(); i++) {
            if (hasMap.get(i).contains("com.autonavi.minimap")) {
                map_toast_gaodebt.setVisibility(View.VISIBLE);
            } else if (hasMap.get(i).contains("com.baidu.BaiduMap")) {
                map_toast_baidubt.setVisibility(View.VISIBLE);
            } else if (hasMap.get(i).contains("com.tencent.map")) {
                map_toast_tencentbt.setVisibility(View.VISIBLE);
            }
        }
        if (hasMap.size() == 0) {
            map_toast_hinttv.setVisibility(View.VISIBLE);
        }
        map_toast_cancelbt.setOnClickListener(view1 -> {
            dismiss();
        });
        map_toast_gaodebt.setOnClickListener(view1 -> {
            toGaodeNavi(getContext(), bean);
            dismiss();
        });
        map_toast_baidubt.setOnClickListener(view1 -> {
            toBaidu(getContext(), bean);
            dismiss();
        });
        map_toast_tencentbt.setOnClickListener(view1 -> {
            toTencent(getContext(), bean);
            dismiss();
        });
    }

    public void toBaidu(Context context, HisLocationBean bean) {

        Intent intent = new Intent("android.intent.action.VIEW", android.net.Uri.parse("baidumap://map/geocoder?location=" + bean.getLat() + "," + bean.getLon()));
        context.startActivity(intent);
    }

    // 高德地图
    public void toGaodeNavi(Context context, HisLocationBean bean) {
        Intent intent = new Intent("android.intent.action.VIEW", android.net.Uri.parse("androidamap://route?sourceApplication=appName&slat=&slon=&sname=我的位置&dlat=" + bean.getLat() + "&dlon=" + bean.getLon() + "&dname=目的地&dev=0&t=2"));
        context.startActivity(intent);
    }

    // 腾讯地图
    public void toTencent(Context context, HisLocationBean bean) {
        Intent intent = new Intent("android.intent.action.VIEW", android.net.Uri.parse("qqmap://map/routeplan?type=drive&from=&fromcoord=&to=目的地&tocoord=" + bean.getLat() + "," + bean.getLon() + "&policy=0&referer=appName"));
        context.startActivity(intent);
    }
}
