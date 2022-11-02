package com.scnu.repository.controller;

import com.scnu.repository.domain.Test;
import com.scnu.repository.service.TestService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class TestController {
    @Value("${test.hello:TEST}")
    private  String testhello;
    @Resource
    private TestService testService;
    @GetMapping("/hello")
    public String hello(){
        return  "hello world!"+testhello;
    }
    @PostMapping ("/hello/post")
    public String helloPost(String name){
        return  "hello world! Post,"+name;
    }
    @GetMapping("/test/list")
    public List<Test> list(){
        return testService.list();
    }
}
