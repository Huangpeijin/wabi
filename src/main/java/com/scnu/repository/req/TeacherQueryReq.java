package com.scnu.repository.req;

public class TeacherQueryReq extends PageReq {

    private String loginName;

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    @Override
    public String toString() {
        return "TeacherQueryReq{" +
                "loginName='" + loginName + '\'' +
                "} " + super.toString();
    }
}
