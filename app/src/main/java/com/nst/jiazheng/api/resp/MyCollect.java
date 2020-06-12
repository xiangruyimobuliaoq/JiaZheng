package com.nst.jiazheng.api.resp;

import java.util.List;

/**
 * 创建者     彭龙
 * 创建时间   2020/4/17 4:15 PM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
public class MyCollect {
    /**
     * id : 95
     * user_id : 14
     * private_id : 0
     * company_id : 1
     * type : 2
     * ctime : 1587091278
     * info : {"id":1,"logo":"http://dgjzrj.app.xiaozhuschool.com/public/uploads/imgs/20200324\\818467e503b4ba84dbc3a402cc3c8106.jpg","name":"AAA家政公司","score":"5.0","lng":"113.768295","lat":"23.018881","calc_range":1.4,"serve_type":[{"id":26,"title":"FFFFF"},{"id":27,"title":"EEEEE"},{"id":28,"title":"DDDDD"},{"id":36,"title":"ZZZZZ"},{"id":39,"title":"vvvvv"}]}
     */
    public int id;
    public int user_id;
    public int private_id;
    public int company_id;
    public int type;
    public long ctime;
    public Worker info;
}
