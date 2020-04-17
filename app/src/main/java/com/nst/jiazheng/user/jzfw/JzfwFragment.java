package com.nst.jiazheng.user.jzfw;

import android.Manifest;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.nst.jiazheng.R;
import com.nst.jiazheng.api.Api;
import com.nst.jiazheng.api.resp.Addr;
import com.nst.jiazheng.api.resp.IndexMap;
import com.nst.jiazheng.api.resp.Register;
import com.nst.jiazheng.api.resp.Resp;
import com.nst.jiazheng.api.resp.Worker;
import com.nst.jiazheng.base.BaseFragment;
import com.nst.jiazheng.base.DpUtil;
import com.nst.jiazheng.base.Layout;
import com.nst.jiazheng.base.SpUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import razerdp.basepopup.BasePopupWindow;

/**
 * 创建者     彭龙
 * 创建时间   2020/4/2 6:01 PM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
@Layout(layoutId = R.layout.fragment_jzfw)
public class JzfwFragment extends BaseFragment implements AMap.OnCameraChangeListener, AMap.OnMarkerClickListener, Inputtips.InputtipsListener {
    @BindView(R.id.map)
    MapView map;
    @BindView(R.id.dingwei)
    ImageView dingwei;
    @BindView(R.id.rg)
    RadioGroup rg;
    @BindView(R.id.clear)
    ImageView clear;
    @BindView(R.id.content)
    EditText content;
    @BindView(R.id.addrlist)
    RecyclerView addrlist;
    private AMap aMap;
    private UiSettings uiSettings;
    private MyLocationStyle myLocationStyle;
    private LatLng latLng;
    private Register mUserInfo;
    private int currentType = 1;
    private CameraPosition currentPosition;
    private Map<String, Worker> mWorkers;
    private GeocodeSearch mSearch;
    private AddrAdapter mAdapter;
    private int pickPotion;

    @Override
    protected void init() {
        mUserInfo = (Register) SpUtil.readObj("userInfo");
        requestPermission();
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                content.setText("");
            }
        });
        dingwei.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(23.02067, 113.75179), 16));
            }
        });
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.srgj:
                        currentType = 1;
                        break;
                    case R.id.jzgs:
                        currentType = 2;
                        break;
                }
                setOverlay(currentPosition);
            }
        });

        content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                onInput(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(RecyclerView.VERTICAL);
        addrlist.setLayoutManager(manager);
        mAdapter = new AddrAdapter(R.layout.item_addr, null);
        addrlist.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                hideInput();
                Tip tip = mAdapter.getData().get(position);
                String address = tip.getName();
                Log.e("address", address);
                pickPotion = position;
                mAdapter.notifyDataSetChanged();
                addrlist.setVisibility(View.GONE);
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(tip.getPoint().getLatitude(), tip.getPoint().getLongitude()), 16));
//                    geoMarker.setPosition(new LatLng(address.getLatLonPoint().getLatitude(), address.getLatLonPoint().getLongitude());
                String addressName = "经纬度值:" + tip.getPoint() + "\n位置描述:"
                        + tip.getDistrict() + tip.getAddress();
                Log.e("123", addressName);
            }
        });
    }

    private void requestPermission() {
        new RxPermissions(this)
                .requestEach(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe((p) -> {
                    if (p.granted) {
                        initMap();
                    } else if (p.shouldShowRequestPermissionRationale) {
                        requestPermission();
                    } else {
                        getAppDetailSettingIntent();
                    }
                });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        map.onCreate(savedInstanceState);
    }

    private void initMap() {
        if (aMap == null) {
            aMap = map.getMap();
        }
        uiSettings = aMap.getUiSettings();
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);//定位一次，且将视角移动到地图中心点。
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_myloc));
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        uiSettings.setMyLocationButtonEnabled(false);
        uiSettings.setZoomControlsEnabled(false);//控制比例尺控件是否显示
        uiSettings.setScaleControlsEnabled(false);//控制比例尺控件是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        aMap.moveCamera(CameraUpdateFactory.zoomTo(16));//设置中心点和缩放比例
        aMap.setOnMyLocationChangeListener(new AMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                latLng = new LatLng(latitude, longitude);
                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
            }
        });
        aMap.setOnCameraChangeListener(this);
        aMap.setOnMarkerClickListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        map.onDestroy();
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        currentPosition = cameraPosition;
        setOverlay(cameraPosition);
    }

    private void setOverlay(CameraPosition cameraPosition) {
        if (null == cameraPosition)
            return;
        OkGo.<String>post(Api.serverApi).params("api_name", "index_map").params("token", mUserInfo.token).params("type", currentType)
                .params("lng", 113.75179).params("lat", 23.02067)
//                .params("lng", cameraPosition.target.longitude).params("lat", cameraPosition.target.latitude)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Resp<IndexMap> resp = new Gson().fromJson(response.body(), new TypeToken<Resp<IndexMap>>() {
                        }.getType());
                        if (resp.code == 1) {
                            aMap.clear(true);
                            mWorkers = new HashMap<>();
                            for (Worker worker : resp.data.list) {
                                mWorkers.put(String.valueOf(worker.id), worker);
                                aMap.addMarker(new MarkerOptions().title(String.valueOf(worker.id)).position(new LatLng(worker.lat, worker.lng)).icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_srgj))));
                            }
                        }
                    }
                });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        showMarkerInfoWindow(mWorkers.get(marker.getTitle()));
        return true;
    }

    private void showMarkerInfoWindow(Worker worker) {
        MarkerInfoWindow markerInfoWindow = new MarkerInfoWindow(this);
        markerInfoWindow.setPopupGravity(BasePopupWindow.GravityMode.RELATIVE_TO_ANCHOR, Gravity.CENTER_HORIZONTAL).setBackPressEnable(true)
                .setOutSideDismiss(true).setOffsetY(DpUtil.dip2px(15)).showPopupWindow(rg);
        markerInfoWindow.initView(worker);
        markerInfoWindow.setCallback(new MarkerInfoWindow.MarkInfoCallback() {
            @Override
            public void callBack() {
                worker.type = currentType;
                Bundle bundle = new Bundle();
                bundle.putSerializable("worker", worker);
                overlay(RequestServeActivity.class, bundle);
            }
        });
    }

    private void onInput(CharSequence charSequence) {
        addrlist.setVisibility(View.VISIBLE);
        pickPotion = -1;
        InputtipsQuery inputquery = new InputtipsQuery(charSequence.toString(), "");
        inputquery.setCityLimit(true);//限制在当前城市
        Inputtips inputTips = new Inputtips(getActivity(), inputquery);
        inputTips.setInputtipsListener(this);
        inputTips.requestInputtipsAsyn();
    }

    @Override
    public void onGetInputtips(List<Tip> list, int i) {
        mAdapter.setList(list);
    }

    class AddrAdapter extends BaseQuickAdapter<Tip, BaseViewHolder> {

        public AddrAdapter(int layoutResId, List<Tip> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, Tip tip) {
            baseViewHolder.setText(R.id.name, tip.getName()).setText(R.id.addr, tip.getAddress());
            CheckBox cb = baseViewHolder.getView(R.id.cb);

            if (baseViewHolder.getLayoutPosition() == pickPotion) {
                cb.setChecked(true);
            } else {
                cb.setChecked(false);
            }
            cb.setEnabled(false);
        }
    }
}
