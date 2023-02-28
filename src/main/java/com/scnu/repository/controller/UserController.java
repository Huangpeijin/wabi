package com.scnu.repository.controller;

import ch.qos.logback.classic.Logger;
import com.alibaba.fastjson.JSONObject;
import com.scnu.repository.req.UserLoginReq;
import com.scnu.repository.req.UserQueryReq;
import com.scnu.repository.req.UserResetPasswordReq;
import com.scnu.repository.req.UserSaveReq;
import com.scnu.repository.resp.CommonResp;
import com.scnu.repository.resp.PageResp;
import com.scnu.repository.resp.UserLoginResp;
import com.scnu.repository.resp.UserQueryResp;
import com.scnu.repository.service.UserService;
import com.scnu.repository.util.SnowFlake;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
public class UserController {
    //创建并引入Log
    private static final Logger LOG = (Logger) LoggerFactory.getLogger(UserController.class);

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate redisTemplate;//将登录信息保存到Redis里去

    @Resource
    private SnowFlake snowFlake;

    @GetMapping("/list")
    public CommonResp list(@Valid UserQueryReq req){
        CommonResp<PageResp<UserQueryResp>> resp = new CommonResp<>();
        PageResp<UserQueryResp> list =  userService.list(req);
        resp.setContent(list);
        return resp;
    }
    @PostMapping("/save") //保存接口名字
    public CommonResp save(@Valid @RequestBody UserSaveReq req){
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp resp = new CommonResp<>();
        userService.save(req);//调用save方法
        return resp;
    }

    //删除一般都是按id来删除的，因为id是主键.请求接口传1，拿到数据id就是1，存在映射关系。
    @DeleteMapping ("/delete/{id}") //删除接口名字
    public CommonResp delete(@PathVariable Long id){
        userService.delete(id);//调用delete方法，将id传进去,delete方法在服务层写
        CommonResp resp = new CommonResp<>();
        return resp;
    }
    @PostMapping("/reset-password")
    public CommonResp resetPassword(@Valid @RequestBody UserResetPasswordReq req) {
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp resp = new CommonResp<>();
        userService.resetPassword(req);
        return resp;
    }
    @PostMapping("/login")
    public CommonResp login(@Valid @RequestBody UserLoginReq req) {
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp<UserLoginResp> resp = new CommonResp<>();
        UserLoginResp userLoginResp = userService.login(req);

        Long tokenAdmin = snowFlake.nextId();//token随着用户信息需要返回给前端，所以需要在resp去定义token变量
        LOG.info("生成单点登录tokenAdmin：{}，并放入redis中", tokenAdmin);
        userLoginResp.setTokenAdmin(tokenAdmin.toString());
        redisTemplate.opsForValue().set(tokenAdmin.toString(), JSONObject.toJSONString(userLoginResp), 3600 * 24, TimeUnit.SECONDS);

        resp.setContent(userLoginResp);
        return resp;
    }
    @PostMapping("/loginTeacher")
    public CommonResp loginTeacher(@Valid @RequestBody UserLoginReq req) {
        //将密码进行加密并放入到请求参数
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp<UserLoginResp> resp = new CommonResp<>();

        UserLoginResp userLoginResp = userService.loginTeacher(req);
        Long tokenTeacher = snowFlake.nextId();//token随着用户信息需要返回给前端，所以需要在resp去定义token变量
        LOG.info("生成单点登录tokenTeacher：{}，并放入redis中", tokenTeacher);
        //将tokenTeacher的值放入到 userLoginResp中
        userLoginResp.setTokenTeacher(tokenTeacher.toString());
        redisTemplate.opsForValue().set(tokenTeacher.toString(), JSONObject.toJSONString(userLoginResp), 3600 * 24, TimeUnit.SECONDS);

        resp.setContent(userLoginResp);
        return resp;
    }
    @PostMapping("/loginStudent")
    public CommonResp loginStudent(@Valid @RequestBody UserLoginReq req) {
        //将密码进行加密并放入到请求参数
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp<UserLoginResp> resp = new CommonResp<>();

        UserLoginResp userLoginResp = userService.loginTeacher(req);
        Long tokenStudent = snowFlake.nextId();//token随着用户信息需要返回给前端，所以需要在resp去定义token变量
        LOG.info("生成单点登录tokenStudent：{}，并放入redis中", tokenStudent);
        //将tokenTeacher的值放入到 userLoginResp中
        userLoginResp.setTokenStudent(tokenStudent.toString());
        redisTemplate.opsForValue().set(tokenStudent.toString(), JSONObject.toJSONString(userLoginResp), 3600 * 24, TimeUnit.SECONDS);

        resp.setContent(userLoginResp);
        return resp;
    }

    @GetMapping("/logout/{tokenAdmin}")
    public CommonResp logout(@PathVariable String tokenAdmin) {
        CommonResp resp = new CommonResp<>();
        redisTemplate.delete(tokenAdmin);
        LOG.info("从redis中删除tokenAdmin: {}", tokenAdmin);
        return resp;
    }
    //教师端退出
    @GetMapping("/logoutTeacher/{tokenTeacher}")
    public CommonResp logoutTeacher(@PathVariable String tokenTeacher) {
        CommonResp resp = new CommonResp<>();
        redisTemplate.delete(tokenTeacher);
        LOG.info("从redis中删除tokenTeacher: {}", tokenTeacher);
        return resp;
    }
    //学生端退出
    @GetMapping("/logoutStudent/{tokenStudent}")
    public CommonResp logoutStudent(@PathVariable String tokenStudent) {
        CommonResp resp = new CommonResp<>();
        redisTemplate.delete(tokenStudent);
        LOG.info("从redis中删除tokenStudent: {}", tokenStudent);
        return resp;
    }
}
