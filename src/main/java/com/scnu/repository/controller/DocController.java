package com.scnu.repository.controller;

import com.scnu.repository.req.DocQueryReq;
import com.scnu.repository.req.DocSaveReq;
import com.scnu.repository.resp.DocQueryResp;
import com.scnu.repository.resp.CommonResp;
import com.scnu.repository.resp.PageResp;
import com.scnu.repository.service.DocService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/doc")
public class DocController {

    @Resource
    private DocService docService;

    @GetMapping("/all")
    public CommonResp all(){
        CommonResp<List<DocQueryResp>> resp = new CommonResp<>();
        List<DocQueryResp> list =  docService.all();
        resp.setContent(list);
        return resp;
    }
    @GetMapping("/list")
    public CommonResp list(@Valid DocQueryReq req){
        CommonResp<PageResp<DocQueryResp>> resp = new CommonResp<>();
        PageResp<DocQueryResp> list =  docService.list(req);
        resp.setContent(list);
        return resp;
    }
    @PostMapping("/save") //保存接口名字
    public CommonResp save(@Valid @RequestBody DocSaveReq req){
        CommonResp resp = new CommonResp<>();
        docService.save(req);//调用save方法
        return resp;
    }

    //删除一般都是按id来删除的，因为id是主键.请求接口传1，拿到数据id就是1，存在映射关系。
    @DeleteMapping ("/delete/{idsStr}") //删除接口名字
    public CommonResp delete(@PathVariable String idsStr){
        CommonResp resp = new CommonResp<>();
        //将String转成集合，用split根据逗号转成数组,然后再将数组转成一个list列表，转成列表后生成变量
        List<String> list  =  Arrays.asList(idsStr.split(","));
        docService.delete(list);
        return resp;
    }



}
