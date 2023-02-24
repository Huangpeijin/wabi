package com.scnu.repository.controller;

import com.scnu.repository.req.EbookQueryReq;
import com.scnu.repository.req.EbookSaveReq;
import com.scnu.repository.resp.CommonResp;
import com.scnu.repository.resp.EbookQueryResp;
import com.scnu.repository.resp.PageResp;
import com.scnu.repository.service.EbookService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/ebook")
public class EbookController {

    @Resource
    private EbookService ebookService;

    @GetMapping("/list")
    public CommonResp list(@Valid EbookQueryReq req){
        CommonResp<PageResp<EbookQueryResp>> resp = new CommonResp<>();
        PageResp<EbookQueryResp> list =  ebookService.list(req);
        resp.setContent(list);
        return resp;
    }
    @PostMapping("/save") //保存接口名字
    public CommonResp save(@Valid @RequestBody EbookSaveReq req){
        CommonResp resp = new CommonResp<>();
        ebookService.save(req);//调用save方法
        return resp;
    }

    //删除一般都是按id来删除的，因为id是主键.请求接口传1，拿到数据id就是1，存在映射关系。
    @DeleteMapping ("/delete/{id}") //删除接口名字
    public CommonResp delete(@PathVariable Long id){
        ebookService.delete(id);//调用delete方法，将id传进去,delete方法在服务层写
        CommonResp resp = new CommonResp<>();
        return resp;
    }
}
