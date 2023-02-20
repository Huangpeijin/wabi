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
    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody EbookSaveReq req){
        CommonResp resp = new CommonResp<>();
        ebookService.save(req);
        return resp;
    }



}
