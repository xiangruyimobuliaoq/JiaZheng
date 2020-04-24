package com.nst.jiazheng.user.wdgj;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.nst.jiazheng.R;
import com.nst.jiazheng.api.Api;
import com.nst.jiazheng.api.resp.MyCollect;
import com.nst.jiazheng.api.resp.Register;
import com.nst.jiazheng.api.resp.Resp;
import com.nst.jiazheng.api.resp.ServeType;
import com.nst.jiazheng.base.BaseFragment;
import com.nst.jiazheng.base.Layout;
import com.nst.jiazheng.base.SpUtil;
import com.nst.jiazheng.user.jzfw.RequestServeActivity;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 创建者     彭龙
 * 创建时间   2020/4/2 6:01 PM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
@Layout(layoutId = R.layout.fragment_wdgj)
public class WdgjFragment extends BaseFragment implements AMapLocationListener {

    @BindView(R.id.nearbylist)
    RecyclerView nearbylist;
    @BindView(R.id.rg)
    RadioGroup rg;
    private int currentType = 1;
    private Register mUserInfo;
    //声明mlocationClient对象
    public AMapLocationClient mlocationClient;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    private NearbyAdapter mAdapter;

    @Override
    protected void init() {
        mUserInfo = (Register) SpUtil.readObj("userInfo");
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.srgj:
                        currentType = 1;
                        getData();
                        break;
                    case R.id.jzgs:
                        currentType = 2;
                        getData();
                        break;
                }
            }
        });
        requestPermission();
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setOrientation(RecyclerView.VERTICAL);
        nearbylist.setLayoutManager(manager);
        mAdapter = new NearbyAdapter(R.layout.item_worker, null);
        nearbylist.setAdapter(mAdapter);
        getData();
    }

    private void requestPermission() {
        new RxPermissions(this)
                .requestEach(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe((p) -> {
                    if (p.granted) {
                        initLocation();
                    } else if (p.shouldShowRequestPermissionRationale) {
                        requestPermission();
                    } else {
                        getAppDetailSettingIntent();
                    }
                });
    }

    private void initLocation() {
        mlocationClient = new AMapLocationClient(mContext);
//初始化定位参数
        mLocationOption = new AMapLocationClientOption();
//设置定位监听
        mlocationClient.setLocationListener(this);
//设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
//设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
// 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
// 在定位结束后，在合适的生命周期调用onDestroy()方法
// 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
//启动定位
        mlocationClient.startLocation();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mlocationClient.onDestroy();
    }

    private void getData() {
        mAdapter.setList(null);
        OkGo.<String>post(Api.serverApi).params("api_name", "my_collect").params("token", mUserInfo.token).params("type", currentType)
                .params("lng", 113.75179).params("lat", 23.02067)
//                .params("lng", cameraPosition.target.longitude).params("lat", cameraPosition.target.latitude)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Resp<List<MyCollect>> resp = new Gson().fromJson(response.body(), new TypeToken<Resp<List<MyCollect>>>() {
                        }.getType());
                        toast(resp.msg);
                        if (resp.code == 1) {
                            mAdapter.setList(resp.data);
                        }
                    }
                });
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                aMapLocation.getLatitude();//获取纬度
                aMapLocation.getLongitude();//获取经度
                aMapLocation.getAccuracy();//获取精度信息
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
            }
        }
    }

    class NearbyAdapter extends BaseQuickAdapter<MyCollect, BaseViewHolder> {
        public NearbyAdapter(int layoutResId, List<MyCollect> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, MyCollect worker) {
            RecyclerView mTypelist = baseViewHolder.getView(R.id.typelist);
            Glide.with(getContext()).load(worker.info.logo).error(R.mipmap.ic_tx).into((CircleImageView) baseViewHolder.getView(R.id.tx));
            baseViewHolder.setText(R.id.counts, worker.info.OrderCount + "单")
                    .setText(R.id.point, worker.info.score + "分")
                    .setText(R.id.length, "[" + worker.info.calc_range + "km]")
                    .setText(R.id.nickname, worker.info.name);
            GridLayoutManager manager = new GridLayoutManager(getContext(), 4);
            mTypelist.setLayoutManager(manager);
            TypeAdapter adapter = new TypeAdapter(R.layout.item_servetype, worker.info.serve_type);
            mTypelist.setAdapter(adapter);
            baseViewHolder.getView(R.id.btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    worker.info.type = currentType;
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("worker", worker.info);
                    overlay(RequestServeActivity.class, bundle);
                }
            });
        }
    }

    class TypeAdapter extends BaseQuickAdapter<ServeType, BaseViewHolder> {

        public TypeAdapter(int layoutResId, List<ServeType> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, ServeType serveType) {
            baseViewHolder.setText(R.id.text, serveType.title);
        }
    }
}
