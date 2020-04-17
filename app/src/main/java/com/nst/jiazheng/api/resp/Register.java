package com.nst.jiazheng.api.resp;

import java.io.Serializable;

/**
 * 创建者     彭龙
 * 创建时间   2020/4/10 10:19 AM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
public class Register implements Serializable {

    /**
     * token : eyJ0eXAiOiJKd3QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE1ODY0MjM1OTAsImV4cCI6MTU4OTAxNTU5MCwib2lkIjpudWxsLCJ1aWQiOiIxMyIsImZsYWciOiJ1c2VyIiwic2lnbnVyZSI6ImM1MWNlNDEwYzEyNGExMGUwZGI1ZTRiOTdmYzJhZjM5In0.TStSrSSsHpXNrvgpbQW_nDNQ9Q01s2dKMadrvabDVT8
     * ry_token : R5nKjz7uvU4jMsuxYFW2BDy1/Ju4MZYiuprhEWI7eaPjGFvwP25u8stIsY5tgx4P5UnYs6kj2gQ=
     * user_id : 13
     * type : 1
     * udid : dgjzrj_user_13
     */

    public String token;
    public String ry_token;
    public int user_id;
    public int type;
    public String udid;
}
