package com.nst.jiazheng.api.resp;
/**
 * 创建者     彭龙
 * 创建时间   2020/4/10 9:18 AM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
public class Resp<T> {
    /**
     * code : 1
     * msg : 发送成功
     * data : []
     */
    public int code;
    public String msg;
    public T data;
}
