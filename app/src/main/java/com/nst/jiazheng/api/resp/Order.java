package com.nst.jiazheng.api.resp;

/**
 * 创建者     彭龙
 * 创建时间   2020/4/11 2:42 PM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
public class Order {
    /**
     * daijie : 0
     * yijie : 0
     * jinxing : 0
     * daique : 0
     */

    public int daijie;
    public int yijie;
    public int jinxing;
    public int daique;

    /**
     * id : 211
     * order_no : U202002231806233666
     * num : 0
     * address : dasdsa
     * status : 6
     * pay_price : 12.00
     * jie_time : 0
     * stime : 0
     * etime : 0
     * cancel_time : 0
     * time : 2020-02-16 9:00+-+2020-02-16 18:00
     * ctime : 1582640972
     * serve_type_name : null
     * serve_type_price : null
     * serve_type_units : null
     * staff_name :
     * StatusText : 已完成
     */

    public int id;
    public String order_no;
    public int num;
    public String address;
    public int status;
    public String pay_price;
    public int jie_time;
    public int stime;
    public int etime;
    public int cancel_time;
    public String time;
    public int ctime;
    public String serve_type_name;
    public String serve_type_price;
    public String serve_type_units;
    public String staff_name;
    public String StatusText;
    public String money;
    public String msg;
    public String title;
    public String name;
    public String content;
    public String headimgurl;
    public int age;
    public int sex;  //性别 1男 2女 0未知
    public String mobile;
    public String nickname;
    public int is_certification;  //是否实名认证 1.是 2.否
    public int job_age; //从业年限
}
