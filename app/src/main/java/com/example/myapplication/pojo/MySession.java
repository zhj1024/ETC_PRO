package com.example.myapplication.pojo;

import android.app.Application;

public class MySession extends Application {
    private String session_token;
    private String id;
    private String Number;
    private String fileUrl;
    private String create;
    private String phone;
    private String berth;
    private String selectParameter;   //服务器的选中参数

    public String getSelectParameter() {
        return selectParameter;
    }

    public void setSelectParameter(String selectParameter) {
        this.selectParameter = selectParameter;
    }

    public String getBerth() {
        return berth;
    }

    public void setBerth(String berth) {
        this.berth = berth;
    }

    public String getCreate() {
        return create;
    }

    public void setCreate(String create) {
        this.create = create;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSession_token() {
        return session_token;
    }

    public void setSession_token(String session_token) {
        this.session_token = session_token;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        Number = number;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    @Override
    public void onCreate(){

        super.onCreate();
    }
}
