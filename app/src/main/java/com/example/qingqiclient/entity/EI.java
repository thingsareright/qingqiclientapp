package com.example.qingqiclient.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/17 0017.
 * 这个类主要是为了接收网络请求返回的json包而编写的
 * 同时为了方便信息在活动中传递，我们将这个类序列化
 */

public class EI implements Serializable{
    private Long id;
    private String awb;
    private String tel;
    private String sms;
    private String address;
    private Long state;
    private Long userid;
    private Long smsaddress;
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAwb() {
        return awb;
    }

    public void setAwb(String awb) {
        this.awb = awb;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getSms() {
        return sms;
    }

    public void setSms(String sms) {
        this.sms = sms;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getState() {
        return state;
    }

    public void setState(Long state) {
        this.state = state;
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public Long getSmsaddress() {
        return smsaddress;
    }

    public void setSmsaddress(Long smsaddress) {
        this.smsaddress = smsaddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
