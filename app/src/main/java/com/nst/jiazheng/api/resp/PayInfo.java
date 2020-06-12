package com.nst.jiazheng.api.resp;

import com.google.gson.annotations.SerializedName;

/**
 * 创建者     彭龙
 * 创建时间   2020/4/28 2:40 PM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
public class PayInfo {

    /**
     * order_no : U202004281445202759
     * total_price : 2.00
     * pay_price : 2.00
     * coupon_money : 0
     * coupon_count : 0
     * money : 11000.00
     */

    public String order_no;
    public String total_price;
    public String pay_price;
    public int coupon_money;
    public int coupon_count;
    public String money;
    public String result;


    /**
     * appId : wx1e241c6325a6d297
     * nonceStr : xr9lzk47cyswt2n3hefov0ujbqd8p5i1
     * timeStamp : 1591751740
     * package : Sign=WXPay
     * appid : wx1e241c6325a6d297
     * noncestr : xr9lzk47cyswt2n3hefov0ujbqd8p5i1
     * timestamp : 1591751740
     * partnerid : 1580673081
     * prepayid : wx10091540491285270a441df31097711800
     * sign : 68E4BE0AB55953D73F9726725E19CD75
     * partnerId : 1580673081
     * prepayId : wx10091540491285270a441df31097711800
     */

    public String appId;
    public String nonceStr;
    public String timeStamp;
    @SerializedName("package")
    public String packageX;
    public String appid;
    public String noncestr;
    public String timestamp;
    public String partnerid;
    public String prepayid;
    public String sign;
    public String partnerId;
    public String prepayId;
}
