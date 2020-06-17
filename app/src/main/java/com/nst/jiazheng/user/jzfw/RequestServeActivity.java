package com.nst.jiazheng.user.jzfw;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.nst.jiazheng.R;
import com.nst.jiazheng.api.Api;
import com.nst.jiazheng.api.resp.Addr;
import com.nst.jiazheng.api.resp.CalculatePrice;
import com.nst.jiazheng.api.resp.Order;
import com.nst.jiazheng.api.resp.Register;
import com.nst.jiazheng.api.resp.Resp;
import com.nst.jiazheng.api.resp.ServeType;
import com.nst.jiazheng.api.resp.UpFile;
import com.nst.jiazheng.api.resp.Worker;
import com.nst.jiazheng.base.BaseToolBarActivity;
import com.nst.jiazheng.base.GlideEngine;
import com.nst.jiazheng.base.Layout;
import com.nst.jiazheng.base.SpUtil;
import com.nst.jiazheng.login.LoginActivity;
import com.nst.jiazheng.user.AddrPickActivity;
import com.nst.jiazheng.user.PayActivity;
import com.nst.jiazheng.user.PhotoWindow;
import com.nst.jiazheng.user.SendServeActivity;
import com.nst.jiazheng.user.grzx.OrderDetailsDaijiedanActivity;
import com.nst.jiazheng.user.wdgj.CompanyInfoActivity;
import com.nst.jiazheng.user.wdgj.WorkerInfoActivity;
import com.nst.jiazheng.worker.widget.ConfirmWindow;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 创建者     彭龙
 * 创建时间   2020/4/12 9:09 AM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
@Layout(layoutId = R.layout.activity_requestserve)
public class RequestServeActivity extends BaseToolBarActivity {

    @BindView(R.id.tx)
    CircleImageView tx;
    @BindView(R.id.nickname)
    TextView nickname;
    @BindView(R.id.counts)
    TextView counts;
    @BindView(R.id.point)
    TextView point;
    @BindView(R.id.workerinfo)
    LinearLayout workerinfo;
    @BindView(R.id.addr)
    TextView addr;
    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.typelist)
    RecyclerView typelist;
    @BindView(R.id.cb)
    CheckBox cb;
    @BindView(R.id.content)
    EditText content;
    @BindView(R.id.submit)
    Button submit;
    @BindView(R.id.piclist)
    RecyclerView piclist;
    private List<UpFile> mUpFiles;
    private Register mUserInfo;
    private TypeAdapter mAdapter;
    private Addr mAddr;
    private String mSelectedTime;
    private ServeType mServeType;
    private int currentPosition = -1;
    private Worker mWorker;
    private String mOrder_id;
    private static final int ADDR_PICK = 1;
    private static final int PAY = 2;
    private PicAdapter mPicAdapter;

    @Override
    protected void init() {
        setTitle("预约服务");
        mUserInfo = (Register) SpUtil.readObj("userInfo");
        mWorker = (Worker) getIntent().getSerializableExtra("worker");
        nickname.setText(mWorker.name);
        counts.setText(mWorker.OrderCount + "单");
        point.setText(mWorker.score + "分");
        workerinfo.setOnClickListener(v -> {
            Bundle params = new Bundle();
            if (mWorker.type == 1) {
                params.putString("worker", mWorker.id);
                overlay(WorkerInfoActivity.class, params);
            } else {
                params.putSerializable("worker", mWorker);
                overlay(CompanyInfoActivity.class, params);
            }
        });
        addr.setOnClickListener(v -> {
            overlayForResult(AddrPickActivity.class, ADDR_PICK);
        });
        time.setOnClickListener(v -> {
            if (null == mServeType) {
                toast("请先选择服务");
                return;
            }
            Log.e("123", mServeType.serve_type_units_id + "");
            switch (mServeType.serve_type_units_id) {
                case 1:
                    pickHour();
                    break;
                case 2:
                    pickTime();
                    break;
                case 3:
                    pickDay();
                    break;
                case 4:
                    pickMonth();
                    break;
            }
        });
        submit.setOnClickListener(v -> {
            sendOrder();
        });
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        typelist.setLayoutManager(manager);
        mAdapter = new TypeAdapter(R.layout.item_servetype_big, null);
        typelist.setAdapter(mAdapter);
        getTypeList(mWorker);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                if (position != currentPosition) {
                    mServeType = mAdapter.getData().get(position);
                    currentPosition = position;
                    mAdapter.notifyDataSetChanged();
                    time.setText("");
                    mSelectedTime = null;
                    submit.setText("预约服务");
                }
            }
        });
        mUpFiles = new ArrayList<>();
        mUpFiles.add(new UpFile(null, null));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        piclist.setLayoutManager(gridLayoutManager);
        mPicAdapter = new PicAdapter(R.layout.item_pic, mUpFiles);
        piclist.setAdapter(mPicAdapter);
        mPicAdapter.setOnItemClickListener((adapter1, view, position) -> {
            if (position == 0) {
                if (mPicAdapter.getData().size() >= 5) {
                    toast("最多只能上传4张图片");
                    return;
                }
                new PhotoWindow(this).setListener(new PhotoWindow.OnConfirmClickListener() {
                    @Override
                    public void onCam(PhotoWindow confirmWindow) {
                        requestPermissionAndOpenCam();
                        confirmWindow.dismiss();
                    }

                    @Override
                    public void onGallery(PhotoWindow confirmWindow) {
                        requestPermissionAndOpenGallery();
                        confirmWindow.dismiss();
                    }
                }).setPopupGravity(Gravity.BOTTOM).showPopupWindow();
            }
        });
        mPicAdapter.setOnItemLongClickListener((adapter, view, position) -> {
            if (position != 0) {
                new ConfirmWindow(mContext)
                        .setContent("确认删除该图片吗?", "确认")
                        .setListener((ConfirmWindow window) -> {
                            mPicAdapter.removeAt(position);
                            window.dismiss();
                        })
                        .setPopupGravity(Gravity.CENTER)
                        .setBackPressEnable(true)
                        .setOutSideDismiss(true)
                        .showPopupWindow();
            }
            return true;
        });
        try {
            Glide.with(this).load(mWorker.logo).error(R.mipmap.ic_tx).into(tx);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendOrder() {
        if (TextUtils.isEmpty(mSelectedTime)) {
            toast("请选择服务时间");
            return;
        }
        if (null == mAddr) {
            toast("请选择地址");
            return;
        }
        String id = "";
        for (UpFile upFile : mUpFiles
        ) {
            if (!TextUtils.isEmpty(upFile.id)) {
                id += upFile.id + ",";
            }
        }
        if (id.endsWith(",")) {
            id = id.substring(0, id.length() - 1);
        }
        showDialog("正在提交", true);
        OkGo.<String>post(Api.serverApi).params("token", mUserInfo.token)
                .params("api_name", "server_sublimit")
                .params("id", mWorker.id)
                .params("type", mWorker.type)
                .params("time", mSelectedTime)
                .params("lng", mAddr.lng)
                .params("lat", mAddr.lat)
                .params("address", mAddr.addr)
                .params("content", content.getText().toString().trim())
                .params("serve_type_id", mServeType.id)
                .params("serve_img", id)
                .params("insurance", cb.isChecked() ? 1 : 2).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                dismissDialog();
                Resp<Order> resp = new Gson().fromJson(response.body(), new TypeToken<Resp<Order>>() {
                }.getType());
                toast(resp.msg);
                if (resp.code == 1) {
                    Bundle params = new Bundle();
                    String order_no = resp.data.order_no;
                    mOrder_id = resp.data.order_id;
                    params.putString("orderNo", order_no);
                    overlayForResult(PayActivity.class, PAY, params);
                    finish();
                } else if (resp.code == 101) {
                    SpUtil.putBoolean("isLogin", false);
                    startAndClearAll(LoginActivity.class);
                }
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                dismissDialog();
            }
        });
    }

    private void pickMonth() {
        List<String> smonth = new ArrayList<>();
        List<String> month = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
        long l1 = System.currentTimeMillis();
        for (long i = 0; i < 30; i++) {
            synchronized (this) {
                long time = l1 + i * 1000 * 60 * 60 * 24;
                smonth.add(format.format(new Date(time)));
            }
        }
        for (long j = 1; j <= 12; j++) {
            month.add(String.valueOf(j));
        }
        OptionsPickerView pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                String sm = smonth.get(options1);
                String m = month.get(options2);
                time.setText(sm + "--" + format.format(new Date(System.currentTimeMillis() + Long.parseLong(m) * 1000 * 3600 * 24 * 30)));
                SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                try {
                    mSelectedTime = f.format(format.parse(sm))
                            + "+-+" + f.format(new Date(format.parse(sm).getTime() + Long.parseLong(m) * 1000 * 3600 * 24 * 30));
                    calculatePrice();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }).setTitleText("选择起始日期和月数").setLabels(null, "个月", null).setDividerColor(getColor(R.color.line_2)).setTextColorCenter(getColor(R.color.text_1)).build();
        pvOptions.setNPicker(smonth, month, null);
        pvOptions.show();
    }

    private void calculatePrice() {
        OkGo.<String>post(Api.serverApi).params("token", mUserInfo.token)
                .params("api_name", "server_sum")
                .params("id", mWorker.id)
                .params("type", mWorker.type)
                .params("time", mSelectedTime)
                .params("serve_type_id", mServeType.id)
                .params("insurance", cb.isChecked() ? 1 : 2).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Resp<CalculatePrice> resp = new Gson().fromJson(response.body(), new TypeToken<Resp<CalculatePrice>>() {
                }.getType());
                toast(resp.msg);
                if (resp.code == 1) {
                    submit.setText("预约服务(总价: ¥ " + resp.data.total_price + ")");
                } else if (resp.code == 101) {
                    SpUtil.putBoolean("isLogin", false);
                    startAndClearAll(LoginActivity.class);
                }
            }
        });
    }

    private void pickDay() {
        List<String> day = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
        long l1 = System.currentTimeMillis();
        for (long i = 0; i < 30; i++) {
            synchronized (this) {
                long time = l1 + i * 1000 * 60 * 60 * 24;
                day.add(format.format(new Date(time)));
            }
        }
        OptionsPickerView pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                String sday = day.get(options1);
                String eday = day.get(options2);
                time.setText(sday + "--" + eday);
                SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                try {
                    mSelectedTime = f.format(format.parse(sday)) + "+-+" + f.format(format.parse(eday));
                    calculatePrice();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }).setTitleText("选择起止日期").setDividerColor(getColor(R.color.line_2)).setTextColorCenter(getColor(R.color.text_1)).build();
        pvOptions.setNPicker(day, day, null);
        pvOptions.show();
    }

    private void pickHour() {
        List<String> day = new ArrayList<>();
        List<String> startHour = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        SimpleDateFormat formath = new SimpleDateFormat("HH:mm");
        long l1 = System.currentTimeMillis();
        OptionsPickerView pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                String s = day.get(options1);
                String sh = startHour.get(options2);
                String eh = startHour.get(options3);
                time.setText(s + "  " + sh + "--" + eh);
                SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                try {
                    mSelectedTime = f.format(format1.parse(s + " " + sh)) + "+-+" + f.format(format1.parse(s + " " + eh));
                    Log.e("123", mSelectedTime);
                    calculatePrice();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }).setTitleText("选择起止时间").setDividerColor(getColor(R.color.line_2)).setTextColorCenter(getColor(R.color.text_1)).build();
        for (long i = 0; i < 30; i++) {
            synchronized (this) {
                long time = l1 + i * 1000 * 60 * 60 * 24;
                day.add(format.format(new Date(time)));
            }
        }
        for (long j = 0; j < 48; j++) {
            try {
                Date parse = format.parse(format.format(new Date(l1)));
                long l = parse.getTime() + j * 1000 * 60 * 30;
                startHour.add(formath.format(new Date(l)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        pvOptions.setNPicker(day, startHour, startHour);
        pvOptions.show();
    }

    private void pickTime() {
        TimePickerView pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                time.setText(new SimpleDateFormat("yyyy年MM月dd日 HH:mm").format(date));
                mSelectedTime = getTimes(date) + "+-+" + getTimes(date);
                calculatePrice();
            }
        }).setType(new boolean[]{true, true, true, true, true, false})
                .setCancelColor(getColor(R.color.text_3))
                .setSubmitColor(getColor(R.color.statusbar_blue)).build();
        pvTime.show();
    }

    private String getTimes(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return format.format(date);
    }

    private void getTypeList(Worker worker) {
        OkGo.<String>post(Api.serverApi).params("api_name", "server_type").params("token", mUserInfo.token)
                .params("type", worker.type).params("id", worker.id).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Resp<List<ServeType>> resp = new Gson().fromJson(response.body(), new TypeToken<Resp<List<ServeType>>>() {
                }.getType());
                if (resp.code == 1) {
                    mAdapter.setList(resp.data);
                } else if (resp.code == 101) {
                    SpUtil.putBoolean("isLogin", false);
                    startAndClearAll(LoginActivity.class);
                }
            }
        });
    }

    private void requestPermissionAndOpenCam() {
        new RxPermissions(this)
                .requestEach(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe((p) -> {
                    if (p.granted) {
                        openCam();
                    } else if (p.shouldShowRequestPermissionRationale) {
                        requestPermissionAndOpenCam();
                    } else {
                        getAppDetailSettingIntent();
                    }
                });
    }

    private void requestPermissionAndOpenGallery() {
        new RxPermissions(this)
                .requestEach(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe((p) -> {
                    if (p.granted) {
                        openGallery();
                    } else if (p.shouldShowRequestPermissionRationale) {
                        requestPermissionAndOpenGallery();
                    } else {
                        getAppDetailSettingIntent();
                    }
                });
    }

    private void openGallery() {
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .loadImageEngine(GlideEngine.createGlideEngine())
                .maxSelectNum(5 - mPicAdapter.getData().size())
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(List<LocalMedia> result) {
                        for (LocalMedia localMedia : result
                        ) {
                            if (Build.VERSION.SDK_INT == 29) {
                                upLoadFiles(localMedia.getAndroidQToPath());
                            } else {
                                upLoadFiles(localMedia.getPath());
                            }
                        }
                    }

                    @Override
                    public void onCancel() {
                        // onCancel Callback
                    }
                });
    }

    private void openCam() {
        PictureSelector.create(this)
                .openCamera(PictureMimeType.ofImage())
                .loadImageEngine(GlideEngine.createGlideEngine())
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(List<LocalMedia> result) {
                        LocalMedia localMedia = result.get(0);
                        if (Build.VERSION.SDK_INT == 29) {
                            upLoadFiles(localMedia.getAndroidQToPath());
                        } else {
                            upLoadFiles(localMedia.getPath());
                        }
                    }

                    @Override
                    public void onCancel() {
                        // onCancel Callback
                    }
                });
    }

    private void upLoadFiles(String path) {
        showDialog("正在上传", true);
        OkGo.<String>post(Api.baseApi).params("api_name", "uploadPic").params("img", new File(path)).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                dismissDialog();
                Resp<UpFile> resp = new Gson().fromJson(response.body(), new TypeToken<Resp<UpFile>>() {
                }.getType());
                toast(resp.msg);
                if (resp.code == 1) {
                    mPicAdapter.addData(new UpFile(resp.data.id, path));
                } else if (resp.code == 101) {
                    SpUtil.putBoolean("isLogin", false);
                    startAndClearAll(LoginActivity.class);
                }
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                dismissDialog();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADDR_PICK && resultCode == RESULT_OK) {
            mAddr = (Addr) data.getSerializableExtra("addr");
            this.addr.setText(mAddr.addr);
        } else if (requestCode == PAY && resultCode == RESULT_OK) {
            Bundle bundle = new Bundle();
            bundle.putString("id", mOrder_id);
            overlay(OrderDetailsDaijiedanActivity.class,bundle);
            finish();
        }
    }

    class TypeAdapter extends BaseQuickAdapter<ServeType, BaseViewHolder> {

        public TypeAdapter(int layoutResId, List<ServeType> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, ServeType serveType) {
            baseViewHolder.setText(R.id.title, serveType.title)
                    .setText(R.id.unit, serveType.price + "元/" + serveType.serve_type_units);
            CheckBox cb = baseViewHolder.getView(R.id.cb);
            if (baseViewHolder.getLayoutPosition() == currentPosition) {
                cb.setChecked(true);
            } else {
                cb.setChecked(false);
            }
            cb.setEnabled(false);
        }
    }

    class PicAdapter extends BaseQuickAdapter<UpFile, BaseViewHolder> {

        public PicAdapter(int layoutResId, List<UpFile> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, UpFile upfile) {
            ImageView pic = baseViewHolder.getView(R.id.pic);
            int position = baseViewHolder.getLayoutPosition();
            if (position == 0) {
                pic.setImageResource(R.mipmap.ic_upfile);
            } else {
                try {
                    Glide.with(RequestServeActivity.this).load(upfile.path).centerCrop().into(pic);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
