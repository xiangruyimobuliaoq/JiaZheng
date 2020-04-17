package com.nst.jiazheng.api.resp;

import java.io.Serializable;
import java.util.List;

/**
 * 创建者     彭龙
 * 创建时间   2020/4/11 2:43 PM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
public class Worker implements Serializable {
    /**
     * id : 7
     * name : 张无忌
     * lat : 23.01442892217
     * lng : 113.74535857655
     * logo : https://wx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTJkz85rdGfEsiaYh6p2RYNGbnsdmfcFWibR19ZXukay1STdJqvt7F1xYC
     yHcOEo2RCQa9egSnYTZSyQ/132
     * score : 5.0
     * calc_range : 1
     * serve_type : [{"id":31,"title":"呃呃呃呃呃呃"},{"id":32,"title":"哦哦哦哦哦"},{"id":35,"title":"啊啊啊啊啊啊"},{"id":40,"title":"ABC"}]
     * OrderCount : 0
     */
    public int id;
    public String name;
    public double lat;
    public double lng;
    public String logo;
    public float score;
    public float calc_range;
    public int OrderCount;
    public List<ServeType> serve_type;
    public int type;


    /**
     * headimgurl : https://wx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTJkz85rdGfEsiaYh6p2RYNGbnsdmfcFWibR19ZXukay1STdJqvt7F1xYC
     yHcOEo2RCQa9egSnYTZSyQ/132
     * age : 50
     * job_age : 20
     * sex : 1
     * ServeType : [{"id":31,"title":"呃呃呃呃呃呃"},{"id":32,"title":"哦哦哦哦哦"},{"id":35,"title":"啊啊啊啊啊啊"},{"id":40,"title":"ABC"}]
     * OrderCount : 0
     * comment_list : [{"id":17,"content":"的点点滴滴","score":"4.5","ctime":1583398325,"user_id":2,"name":"OceanAuyoung","headimgurl":"http://q.qlogo.cn/qqapp/100546062/BD02D4929A60168BA0147677D02AB59D/100"}]
     * address :
     * is_collect : 0
     */

    public String headimgurl;
    public int age;
    public int job_age;
    public int sex;
    public String address;
    public int is_collect;
    public List<ServeType> servetype;
    public List<Comment> comment_list;
    public int staff_count;
}
