package com.scnu.repository.controller;

import com.scnu.repository.req.CategoryQueryReq;
import com.scnu.repository.req.CategorySaveReq;
import com.scnu.repository.resp.CommonResp;
import com.scnu.repository.resp.CategoryQueryResp;
import com.scnu.repository.resp.PageResp;
import com.scnu.repository.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    @GetMapping("/list")
    public CommonResp list(@Valid CategoryQueryReq req){
        CommonResp<PageResp<CategoryQueryResp>> resp = new CommonResp<>();
        PageResp<CategoryQueryResp> list =  categoryService.list(req);
        resp.setContent(list);
        return resp;
    }
    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody CategorySaveReq req){
        CommonResp resp = new CommonResp<>();
        categoryService.save(req);
        return resp;
    }



}
