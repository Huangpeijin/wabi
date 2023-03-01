package com.scnu.repository.resp;

import com.fasterxml.jackson.annotation.JsonFormat;

public class TeacherLoginResp {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    private String loginName;

    private String name;

    private String tokenTeacher;


    public String getTokenTeacher() {
        return tokenTeacher;
    }

    public void setTokenTeacher(String tokenTeacher) {
        this.tokenTeacher = tokenTeacher;
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
        return "TeacherLoginResp{" +
                "id=" + id +
                ", loginName='" + loginName + '\'' +
                ", name='" + name + '\'' +
                ", tokenTeacher='" + tokenTeacher + '\'' +
                '}';
    }
}
