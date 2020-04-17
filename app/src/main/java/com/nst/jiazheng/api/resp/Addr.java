package com.nst.jiazheng.api.resp;

import java.io.Serializable;

/**
 * 创建者     彭龙
 * 创建时间   2020/4/13 11:47 AM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
public class Addr implements Serializable {
    public String addr;
    public double lat;
    public double lng;

    public Addr(String addr, double lat, double lng) {
        this.addr = addr;
        this.lat = lat;
        this.lng = lng;
    }
}
