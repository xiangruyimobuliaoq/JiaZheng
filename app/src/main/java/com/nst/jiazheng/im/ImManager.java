package com.nst.jiazheng.im;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import com.nst.jiazheng.api.resp.Register;
import com.nst.jiazheng.api.resp.UserCenter;
import com.nst.jiazheng.base.SpUtil;

import io.rong.imkit.RongIM;
import io.rong.imkit.model.UIConversation;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
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
                Log.e("ImManager", databaseOpenStatus.getValue() + "   " + databaseOpenStatus.toString());
            }

            @Override
            public void onTokenIncorrect() {
            }

            @Override
            public void onSuccess(String userId) {
                Log.e("ImManager", userId);
                RongIM.setUserInfoProvider(s -> {
                    UserInfo userInfo = new UserInfo(userId, data.nickname, Uri.parse(data.headimgurl));
                    RongIM.getInstance().refreshUserInfoCache(userInfo);
                    return userInfo;
                }, true);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
            }
        });
        RongIM.setConnectionStatusListener(connectionStatus -> {
            if (connectionStatus == RongIMClient.ConnectionStatusListener.ConnectionStatus.DISCONNECTED) {
                connect(token, data);
            }
        });
        RongIM.setOnReceiveMessageListener(new RongIMClient.OnReceiveMessageListener() {
            @Override
            public boolean onReceived(Message message, int i) {
                return false;
            }
        });
        RongIM.setConversationListBehaviorListener(new RongIM.ConversationListBehaviorListener() {
            @Override
            public boolean onConversationPortraitClick(Context context, Conversation.ConversationType conversationType, String s) {
                UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(s);
                RongIM.getInstance().refreshUserInfoCache(userInfo);
                return false;
            }

            @Override
            public boolean onConversationPortraitLongClick(Context context, Conversation.ConversationType conversationType, String s) {
                return false;
            }

            @Override
            public boolean onConversationLongClick(Context context, View view, UIConversation uiConversation) {
                return false;
            }

            @Override
            public boolean onConversationClick(Context context, View view, UIConversation uiConversation) {
                return false;
            }
        });
        RongIM.setConversationClickListener(new RongIM.ConversationClickListener() {
            @Override
            public boolean onUserPortraitClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo, String s) {
                return false;
            }

            @Override
            public boolean onUserPortraitLongClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo, String s) {
                return false;
            }

            @Override
            public boolean onMessageClick(Context context, View view, Message message) {
                return false;
            }

            @Override
            public boolean onMessageLinkClick(Context context, String s, Message message) {
                return false;
            }

            @Override
            public boolean onMessageLongClick(Context context, View view, Message message) {
                return false;
            }
        });
    }
}
