package com.example.myapplication.pojo;

import java.util.Map;

/**
 * 登录
 */
public class LoginVo {
    private Integer code;
    private Map<String, String> data;
    private String msg;
    private Long t;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Long getT() {
        return t;
    }

    public void setT(Long t) {
        this.t = t;
    }
}
