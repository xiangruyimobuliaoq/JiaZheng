package com.nst.jiazheng.api;

/**
 * 创建者     彭龙
 * 创建时间   2020/6/10 10:32 AM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
public class WechatEvent {
    public String state ;
    public String code ;

    public WechatEvent(String state, String code) {
        this.state = state;
        this.code = code;
    }
}
