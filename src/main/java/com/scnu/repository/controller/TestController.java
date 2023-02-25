package com.scnu.repository.controller;

import ch.qos.logback.classic.Logger;
import com.scnu.repository.domain.Test;
import com.scnu.repository.service.TestService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
public class TestController {
    private static final Logger LOG = (Logger) LoggerFactory.getLogger(TestController.class);
    @Value("${test.hello:TEST}")
    private  String testhello;
    @Resource
    private TestService testService;

    //从容器中取出redisTemplate
    @Resource
    private RedisTemplate redisTemplate;

    @GetMapping("/hello/{key}")
//    public Object get(@PathVariable String key){
//
//            Object object = redisTemplate.opsForValue().get(key);
////            object.get();
////            LOG.info("key: {}, value: {}", key, object);
//            return object;
////             return  "hello world!"+object;
//
////        ValueOperations value = redisTemplate.opsForValue();
//////        System.out.println(value.get("284542528508792832"));
////        return  "hello world!"+testhello+value.get("284540995650064384");
//    }
    @PostMapping ("/hello/post")
    public String helloPost(String name){
        return  "hello world! Post,"+name;
    }
    @GetMapping("/test/list")
    public List<Test> list(){
        return testService.list();
    }

    @RequestMapping("/redis/set/{key}/{value}")
    public String set(@PathVariable Long key, @PathVariable String value) {
        redisTemplate.opsForValue().set(key, value, 3600, TimeUnit.SECONDS);
        LOG.info("key: {}, value: {}", key, value);
        return "success";
    }

    @RequestMapping("/redis/get/{key}")
    public Object get(@PathVariable String key) {
        Object object = redisTemplate.opsForValue().get(key);
        LOG.info("key: {}, value: {}", key, object);
        return object;
    }

}
