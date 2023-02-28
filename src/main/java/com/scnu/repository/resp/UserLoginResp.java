package com.scnu.repository.resp;

import com.fasterxml.jackson.annotation.JsonFormat;

public class UserLoginResp {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    private String loginName;

    private String name;

//    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String tokenAdmin;

    private String tokenTeacher;

    private String tokenStudent;

    public String getTokenStudent() {
        return tokenStudent;
    }

    public void setTokenStudent(String tokenStudent) {
        this.tokenStudent = tokenStudent;
    }

    public String getTokenTeacher() {
        return tokenTeacher;
    }

    public void setTokenTeacher(String tokenTeacher) {
        this.tokenTeacher = tokenTeacher;
    }

    public String getTokenAdmin() {
        return tokenAdmin;
    }

    public void setTokenAdmin(String tokenAdmin) {
        this.tokenAdmin = tokenAdmin;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "UserLoginResp{" +
                "id=" + id +
                ", loginName='" + loginName + '\'' +
                ", name='" + name + '\'' +
                ", tokenAdmin='" + tokenAdmin + '\'' +
                ", tokenTeacher='" + tokenTeacher + '\'' +
                ", tokenStudent='" + tokenStudent + '\'' +
                '}';
    }
}
