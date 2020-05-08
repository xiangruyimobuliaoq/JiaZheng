package com.nst.jiazheng.base;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;

public abstract class BaseFragment extends Fragment {

    protected Context mContext;
    private LoadingDialog loadingDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Layout layout = getClass().getAnnotation(Layout.class);
        View view = null;
        if (layout.layoutId() > 0) {
            view = inflater.inflate(layout.layoutId(), container, false);
        }
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    /**
     * init all views and add events
     */
    protected abstract void init();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    /**
     * 转跳 没有finish
     *
     * @param classObj
     */
    public void overlay(Class<?> classObj) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setClass(mContext, classObj);
        startActivity(intent);
    }

    /**
     * 转跳 没有finish
     *
     * @param classObj
     * @param params
     */
    public void overlay(Class<?> classObj, Bundle params) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setClass(mContext, classObj);
        intent.putExtras(params);
        startActivity(intent);
    }

    /**
     * 转跳 没有finish
     *
     * @param classObj
     * @param requestCode
     */
    public void overlayForResult(Class<?> classObj, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setClass(mContext, classObj);
        startActivityForResult(intent, requestCode);
    }

    /**
     * 转跳 没有finish
     *
     * @param classObj
     * @param requestCode
     * @param params
     */
    public void overlayForResult(Class<?> classObj, int requestCode, Bundle params) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setClass(mContext, classObj);
        intent.putExtras(params);
        startActivityForResult(intent, requestCode);
    }

    public void startAndClearAll(Class<?> classObj) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setClass(mContext, classObj);
        startActivity(intent);
    }

    public void toast(String msg) {
        ToastHelper.showToast(msg, getActivity());
    }

    protected void toast(int msg_id) {
        ToastHelper.showToast(getString(msg_id), getActivity());
    }

    protected void showDialog(String title, boolean canCancel) {
        loadingDialog = new LoadingDialog(mContext);
        loadingDialog.setInterceptBack(canCancel);
        loadingDialog.setLoadingText(title).show();
    }

    protected void dismissDialog() {
        loadingDialog.close();
    }

    protected void requestPermissions(String permission) {
        RxPermissions permissions = new RxPermissions(this);
        permissions.requestEach(permission)
                .subscribe((p) -> {
                    if (p.granted) {

                    } else if (p.shouldShowRequestPermissionRationale) {

                    } else {
                        getAppDetailSettingIntent();
                    }
                });
    }

    public void getAppDetailSettingIntent() {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", mContext.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            intent.putExtra("com.android.settings.ApplicationPkgName", mContext.getPackageName());
        }
        startActivity(intent);
    }

    public void hideInput() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), 0);
    }
}
