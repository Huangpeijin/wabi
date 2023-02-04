package com.scnu.repository.controller;

import com.scnu.repository.req.EbookReq;
import com.scnu.repository.resp.CommonResp;
import com.scnu.repository.resp.EbookResp;
import com.scnu.repository.resp.PageResp;
import com.scnu.repository.service.EbookService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/ebook")
public class EbookController {

    @Resource
    private EbookService ebookService;

    @GetMapping("/list")
    public CommonResp list(EbookReq req){
        CommonResp<PageResp<EbookResp>> resp = new CommonResp<>();
        PageResp<EbookResp> list =  ebookService.list(req);
        resp.setContent(list);
        return resp;
    }



}
