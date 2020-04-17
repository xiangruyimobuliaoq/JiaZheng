package com.nst.jiazheng.api.resp;

import java.io.Serializable;

/**
 * 创建者     彭龙
 * 创建时间   2020/4/16 2:07 PM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
public class Comment implements Serializable {
    /**
     * id : 17
     * content : 的点点滴滴
     * score : 4.5
     * ctime : 1583398325
     * user_id : 2
     * name : OceanAuyoung
     * headimgurl : http://q.qlogo.cn/qqapp/100546062/BD02D4929A60168BA0147677D02AB59D/100
     */
    public int id;
    public String content;
    public String score;
    public long ctime;
    public int user_id;
    public String name;
    public String headimgurl;
}
