package com.nst.jiazheng.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

/**
 * 创建者     彭龙
 * 创建时间   2016/5/27 12:03
 * 描述	      ${TODO}
 * <p/>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class SpUtil {
    public static Context mContext;
    private static SharedPreferences userInfo;

    public static void putString(String key, String value) {
        userInfo.edit().putString(key, value).commit();
    }

    public static String getString(String key, String defValue) {
        return userInfo.getString(key, defValue);
    }

    public static void putInt(String key, int value) {
        userInfo.edit().putInt(key, value).commit();
    }

    public static int getInt(String key, int defValue) {
        return userInfo.getInt(key, defValue);
    }

    public static void putBoolean(String key, boolean value) {
        userInfo.edit().putBoolean(key, value).commit();
    }

    public static boolean getBoolean(String key, boolean defValue) {
        return userInfo.getBoolean(key, defValue);
    }

    public static void saveObj(String key, Object value) {
        SharedPreferences preferences = mContext.getSharedPreferences("base64",
                Context.MODE_PRIVATE);
        // 创建字节输出流
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            // 创建对象输出流，并封装字节流
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            // 将对象写入字节流
            oos.writeObject(value);
            // 将字节流编码成base64的字符窜
            String obj_Base64 = Base64.encodeToString(baos.toByteArray(), 1);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(key, obj_Base64);
            editor.apply();
        } catch (IOException e) {
            // TODO Auto-generated
        }
        Log.i("ok", "存储成功");
    }

    public static Object readObj(String key) {
        Object value = null;
        SharedPreferences preferences = mContext.getSharedPreferences("base64",
                Context.MODE_PRIVATE);
        String productBase64 = preferences.getString(key, "");

        //读取字节
        byte[] base64 = Base64.decode(productBase64.getBytes(), 1);

        //封装到字节流
        ByteArrayInputStream bais = new ByteArrayInputStream(base64);
        try {
            //再次封装
            ObjectInputStream bis = new ObjectInputStream(bais);
            try {
                //读取对象
                value = bis.readObject();
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (StreamCorruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return value;
    }

    public static void init(App app) {
        mContext = app;
        userInfo = mContext.getSharedPreferences("userInfo", 0);
    }
}
