package com.scnu.repository.req;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

public class TeacherLoginReq {

    @NotEmpty(message = "【用户名】不能为空")
    private String loginName;

    @NotEmpty(message = "【密码】不能为空")
    // @Length(min = 6, max = 20, message = "【密码】6~20位")
    @Pattern(regexp = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,32}$", message = "【密码】规则不正确")
    private String password;

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "TeacherLoginReq{" +
                "loginName='" + loginName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
