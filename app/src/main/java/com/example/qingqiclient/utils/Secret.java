package com.example.qingqiclient.utils;

/**
 * Created by Administrator on 2017/10/16 0016.
 * 用于存储用户的手机号和密码，前期先这么做，后期再找其他方式
 */

public class Secret {

    private static String tel;
    private static String password;

    public static String getTel() {
        return tel;
    }

    public static void setTel(String tel) {
        Secret.tel = tel;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        Secret.password = password;
    }
}
