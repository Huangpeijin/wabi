package com.scnu.repository.controller;

import com.scnu.repository.req.DocinQueryReq;
import com.scnu.repository.req.DocinSaveReq;
import com.scnu.repository.resp.CommonResp;
import com.scnu.repository.resp.DocinQueryResp;
import com.scnu.repository.resp.PageResp;
import com.scnu.repository.service.DocinService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/docin")
public class DocinController {

    @Resource
    private DocinService docinService;

    @GetMapping("/all/{ebookId}")
    public CommonResp all(@PathVariable Long ebookId){
        CommonResp<List<DocinQueryResp>> resp = new CommonResp<>();
        List<DocinQueryResp> list =  docinService.all(ebookId);
        resp.setContent(list);
        return resp;
    }
    @GetMapping("/list")
    public CommonResp list(@Valid DocinQueryReq req){
        CommonResp<PageResp<DocinQueryResp>> resp = new CommonResp<>();
        PageResp<DocinQueryResp> list =  docinService.list(req);
        resp.setContent(list);
        return resp;
    }
    @PostMapping("/save") //保存接口名字
    public CommonResp save(@Valid @RequestBody DocinSaveReq req){
        CommonResp resp = new CommonResp<>();
        docinService.save(req);//调用save方法
        return resp;
    }

    //删除一般都是按id来删除的，因为id是主键.请求接口传1，拿到数据id就是1，存在映射关系。
    @DeleteMapping ("/delete/{idsStr}") //删除接口名字
    public CommonResp delete(@PathVariable String idsStr){
        CommonResp resp = new CommonResp<>();
        //将String转成集合，用split根据逗号转成数组,然后再将数组转成一个list列表，转成列表后生成变量
        List<String> list  =  Arrays.asList(idsStr.split(","));
        docinService.delete(list);
        return resp;
    }

    @GetMapping("/find-content/{id}")
    public CommonResp findContent(@PathVariable Long id) {
        CommonResp<String> resp = new CommonResp<>();
        String content = docinService.findContent(id);
        resp.setContent(content);
        return resp;
    }
    @GetMapping("/vote/{id}")
    public CommonResp vote(@PathVariable Long id) {
        CommonResp commonResp = new CommonResp();
        docinService.vote(id);
        return commonResp;
    }


}
