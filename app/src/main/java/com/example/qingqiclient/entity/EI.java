package com.example.qingqiclient.entity;

/**
 * Created by Administrator on 2017/10/17 0017.
 * 这个类主要是为了接收网络请求返回的json包而编写的
 * json返回的是下面这种元素组成的数组：
 */

public class EI {
    private Long id;
    private String awb;
    private String tel;
    private String tms;
    private String address;
    private Long state;
    private Long userid;
    private Long smsaddress;

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

    public String getTms() {
        return tms;
    }

    public void setTms(String tms) {
        this.tms = tms;
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
}
