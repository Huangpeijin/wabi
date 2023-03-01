package com.scnu.repository.resp;

import com.fasterxml.jackson.annotation.JsonFormat;

public class StudentLoginResp {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    private String loginName;

    private String name;

    private String tokenStudent;


    public String getTokenStudent() {
        return tokenStudent;
    }

    public void setTokenStudent(String tokenStudent) {
        this.tokenStudent = tokenStudent;
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
        return "StudentLoginResp{" +
                "id=" + id +
                ", loginName='" + loginName + '\'' +
                ", name='" + name + '\'' +
                ", tokenStudent='" + tokenStudent + '\'' +
                '}';
    }
}
