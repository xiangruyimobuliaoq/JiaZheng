package com.nst.jiazheng.im;

import android.net.Uri;
import android.util.Log;

import com.nst.jiazheng.api.resp.Register;
import com.nst.jiazheng.api.resp.UserCenter;
import com.nst.jiazheng.base.SpUtil;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;

/**
 * 创建者     彭龙
 * 创建时间   2020/5/18 10:06 AM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
public class ImManager {


    public static void connect(String token, UserCenter data) {
        RongIMClient.connect(token, new RongIMClient.ConnectCallbackEx() {
            @Override
            public void OnDatabaseOpened(RongIMClient.DatabaseOpenStatus databaseOpenStatus) {
                Log.e("ImManager", databaseOpenStatus.getValue() +"   "+ databaseOpenStatus.toString());
            }

            @Override
            public void onTokenIncorrect() {

            }

            @Override
            public void onSuccess(String userId) {
                Log.e("ImManager", userId);
                RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {
                    @Override
                    public UserInfo getUserInfo(String s) {
                        UserInfo userInfo = new UserInfo(userId, data.nickname, Uri.parse(data.headimgurl));
                        RongIM.getInstance().refreshUserInfoCache(userInfo);
                        return userInfo;
                    }
                }, true);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });
    }
}
