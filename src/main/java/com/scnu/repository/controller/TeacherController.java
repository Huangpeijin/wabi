package com.scnu.repository.controller;

import ch.qos.logback.classic.Logger;
import com.alibaba.fastjson.JSONObject;
import com.scnu.repository.req.*;
import com.scnu.repository.resp.*;
import com.scnu.repository.service.TeacherService;
import com.scnu.repository.util.SnowFlake;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/teacher")
public class TeacherController {
    //创建并引入Log
    private static final Logger LOG = (Logger) LoggerFactory.getLogger(TeacherController.class);

    @Resource
    private TeacherService teacherService;

    @Resource
    private RedisTemplate redisTemplate;//将登录信息保存到Redis里去

    @Resource
    private SnowFlake snowFlake;

    @GetMapping("/list")
    public CommonResp list(@Valid TeacherQueryReq req){
        CommonResp<PageResp<TeacherQueryResp>> resp = new CommonResp<>();
        PageResp<TeacherQueryResp> list =  teacherService.list(req);
        resp.setContent(list);
        return resp;
    }
//    @GetMapping("/list")
//    public CommonResp list(@Valid UserQueryReq req){
//        CommonResp<PageResp<UserQueryResp>> resp = new CommonResp<>();
//        PageResp<UserQueryResp> list =  userService.list(req);
//        resp.setContent(list);
//        return resp;
//    }
    @PostMapping("/save") //保存接口名字
    public CommonResp save(@Valid @RequestBody TeacherSaveReq req){
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp resp = new CommonResp<>();
        teacherService.save(req);//调用save方法
        return resp;
    }

    //删除一般都是按id来删除的，因为id是主键.请求接口传1，拿到数据id就是1，存在映射关系。
    @DeleteMapping ("/delete/{id}") //删除接口名字
    public CommonResp delete(@PathVariable Long id){
        teacherService.delete(id);//调用delete方法，将id传进去,delete方法在服务层写
        CommonResp resp = new CommonResp<>();
        return resp;
    }
    @PostMapping("/reset-password")
    public CommonResp resetPassword(@Valid @RequestBody TeacherResetPasswordReq req) {
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp resp = new CommonResp<>();
        teacherService.resetPassword(req);
        return resp;
    }
    @PostMapping("/login")
    public CommonResp login(@Valid @RequestBody TeacherLoginReq req) {
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp<TeacherLoginResp> resp = new CommonResp<>();
        TeacherLoginResp teacherLoginResp = teacherService.login(req);

        Long tokenTeacher = snowFlake.nextId();//token随着用户信息需要返回给前端，所以需要在resp去定义token变量
        LOG.info("生成单点登录tokenTeacher：{}，并放入redis中", tokenTeacher);
        teacherLoginResp.setTokenTeacher(tokenTeacher.toString());
        redisTemplate.opsForValue().set(tokenTeacher.toString(), JSONObject.toJSONString(teacherLoginResp), 3600 * 24, TimeUnit.SECONDS);

        resp.setContent(teacherLoginResp);
        return resp;
    }
    @GetMapping("/logout/{tokenTeacher}")
    public CommonResp logout(@PathVariable String tokenTeacher) {
        CommonResp resp = new CommonResp<>();
        redisTemplate.delete(tokenTeacher);
        LOG.info("从redis中删除tokenTeacher: {}", tokenTeacher);
        return resp;
    }
}
