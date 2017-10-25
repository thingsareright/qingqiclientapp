package com.example.qingqiclient.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/10/16 0016.
 */

public class Constant {
    //服务器域名或者IP地址
    private static final String server = "http://meng1.tunnel.qydev.com" ;

    //下面是一个常量数组，用于解释预选的快递短信收货地点
    public static List<String> smsAddress = new ArrayList<>();
    //下面是一个常量数组，用于解释预选的快递状态
    public static List<String> stateString = new ArrayList<>();

    static {
        smsAddress.add(0,"西门");
        smsAddress.add(1,"东门");
        smsAddress.add(2,"南门");
        smsAddress.add(3,"菊二分拣区");
        smsAddress.add(4,"菊二自助区（东）");
        smsAddress.add(5,"菊二自助区（北）");
        smsAddress.add(6,"菊二顺丰专区");
        smsAddress.add(7,"菊二2-2延时区");
        smsAddress.add(8,"菊二前台");
        smsAddress.add(9,"京东快递");
        smsAddress.add(10,"菊五自助一区");
        smsAddress.add(11,"菊五自助二区");
        smsAddress.add(12,"菊五分拣区");
        smsAddress.add(13,"荷园洗浴中心二楼");
        smsAddress.add(14,"其它");


        stateString.add(0,"未取货");
        stateString.add(1,"轻骑已取货");
        stateString.add(2,"抱歉，没取到货");
        stateString.add(3,"配送中");
        stateString.add(4,"已送达");
    }

    public static String getServer() {
        return server;
    }
}
