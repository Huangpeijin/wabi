package com.scnu.repository.controller;

import ch.qos.logback.classic.Logger;
import com.alibaba.fastjson.JSONObject;
import com.scnu.repository.req.StudentLoginReq;
import com.scnu.repository.req.StudentQueryReq;
import com.scnu.repository.req.StudentResetPasswordReq;
import com.scnu.repository.req.StudentSaveReq;
import com.scnu.repository.resp.CommonResp;
import com.scnu.repository.resp.PageResp;
import com.scnu.repository.resp.StudentLoginResp;
import com.scnu.repository.resp.StudentQueryResp;
import com.scnu.repository.service.StudentService;
import com.scnu.repository.util.SnowFlake;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/student")
public class StudentController {
    //创建并引入Log
    private static final Logger LOG = (Logger) LoggerFactory.getLogger(StudentController.class);

    @Resource
    private StudentService studentService;

    @Resource
    private RedisTemplate redisTemplate;//将登录信息保存到Redis里去

    @Resource
    private SnowFlake snowFlake;

    @GetMapping("/list")
    public CommonResp list(@Valid StudentQueryReq req){
        CommonResp<PageResp<StudentQueryResp>> resp = new CommonResp<>();
        PageResp<StudentQueryResp> list =  studentService.list(req);
        resp.setContent(list);
        return resp;
    }
    @PostMapping("/save") //保存接口名字
    public CommonResp save(@Valid @RequestBody StudentSaveReq req){
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp resp = new CommonResp<>();
        studentService.save(req);//调用save方法
        return resp;
    }

    //删除一般都是按id来删除的，因为id是主键.请求接口传1，拿到数据id就是1，存在映射关系。
    @DeleteMapping ("/delete/{id}") //删除接口名字
    public CommonResp delete(@PathVariable Long id){
        studentService.delete(id);//调用delete方法，将id传进去,delete方法在服务层写
        CommonResp resp = new CommonResp<>();
        return resp;
    }
    @PostMapping("/reset-password")
    public CommonResp resetPassword(@Valid @RequestBody StudentResetPasswordReq req) {
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp resp = new CommonResp<>();
        studentService.resetPassword(req);
        return resp;
    }
    @PostMapping("/login")
    public CommonResp login(@Valid @RequestBody StudentLoginReq req) {
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp<StudentLoginResp> resp = new CommonResp<>();
        StudentLoginResp studentLoginResp = studentService.login(req);

        Long tokenStudent = snowFlake.nextId();//token随着用户信息需要返回给前端，所以需要在resp去定义token变量
        LOG.info("生成单点登录tokenStudent：{}，并放入redis中", tokenStudent);
        studentLoginResp.setTokenStudent(tokenStudent.toString());
        redisTemplate.opsForValue().set(tokenStudent.toString(), JSONObject.toJSONString(studentLoginResp), 3600 * 24, TimeUnit.SECONDS);

        resp.setContent(studentLoginResp);
        return resp;
    }
    @GetMapping("/logout/{tokenStudent}")
    public CommonResp logout(@PathVariable String tokenStudent) {
        CommonResp resp = new CommonResp<>();
        redisTemplate.delete(tokenStudent);
        LOG.info("从redis中删除tokenStudent: {}", tokenStudent);
        return resp;
    }
}
