package com.nst.jiazheng.api.resp;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * 创建者     彭龙
 * 创建时间   2020/4/24 11:04 AM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
public class Message implements MultiItemEntity {


    /**
     * id : 14
     * content : 管理员超级管理员已取消订单，订单编号:U202002251229262576
     * type : 1
     * status : 2
     * company_id : 0
     * ctime : 1583394255
     * title : 订单通知
     * alert : 0
     */

    public int id;
    public String content;
    public int type;
    public int status;
    public int company_id;
    public long ctime;
    public String title;
    public int alert;

    @Override
    public int getItemType() {
        return 0;
    }
}
