package com.nst.jiazheng.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.nst.jiazheng.api.WechatEvent;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;

import androidx.annotation.Nullable;

import static com.nst.jiazheng.api.Api.APP_ID;

/**
 * 创建者     彭龙
 * 创建时间   2020/6/7 3:49 PM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {


    private IWXAPI api;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, APP_ID);
        if (!api.handleIntent(getIntent(), this)) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                int errCode = bundle.getInt("_wxapi_baseresp_errcode");
                if (errCode == 0) {
                    final String code = bundle.getString("_wxapi_sendauth_resp_token");
                    final String state = bundle.getString("_wxapi_sendauth_resp_state");
                    if (code != null) {
                        Log.e("123", code + "" + state);
                        EventBus.getDefault().post(new WechatEvent(state, code));
                    }
                }
            }
            finish();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (!api.handleIntent(getIntent(), this)) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                int errCode = bundle.getInt("_wxapi_baseresp_errcode");
                if (errCode == 0) {
                    final String code = bundle.getString("_wxapi_sendauth_resp_token");
                    final String state = bundle.getString("_wxapi_sendauth_resp_state");
                    if (code != null) {
                        Log.e("123", code + "" + state);
                        EventBus.getDefault().post(new WechatEvent(state, code));
                    }
                }
            }
            finish();
        }
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        Log.e("123", baseResp.errStr);
        if (baseResp.errCode == BaseResp.ErrCode.ERR_OK) {
            if (baseResp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {
                SendAuth.Resp authResp = (SendAuth.Resp) baseResp;
                EventBus.getDefault().post(new WechatEvent(authResp.state, authResp.code));
            }
        }
        finish();
    }
}
