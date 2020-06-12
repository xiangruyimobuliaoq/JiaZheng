package com.nst.jiazheng.api.resp;

import com.contrarywind.interfaces.IPickerViewData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 创建者     彭龙
 * 创建时间   2020/4/13 11:47 AM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
public class Addr implements Serializable , IPickerViewData {
    public String addr;
    public double lat;
    public double lng;
    /**
     * id : 110101
     * name : 东城区
     * pid : 110100
     * type : 3
     */
    public int id;
    public String name;
    public int pid;
    public int type;

    public List<Addr> mAddrs = new ArrayList<>();

    public Addr() {
    }

    public Addr(String addr, double lat, double lng) {
        this.addr = addr;
        this.lat = lat;
        this.lng = lng;
    }

    @Override
    public String getPickerViewText() {
        return name;
    }
}
