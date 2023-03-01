package com.scnu.repository.req;

public class StudentQueryReq extends PageReq {

    private String loginName;

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    @Override
    public String toString() {
        return "StudentQueryReq{" +
                "loginName='" + loginName + '\'' +
                "} " + super.toString();
    }
}
