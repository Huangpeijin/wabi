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
import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    @GetMapping("/all")
    public CommonResp all(){
        CommonResp<List<CategoryQueryResp>> resp = new CommonResp<>();
        List<CategoryQueryResp> list =  categoryService.all();
        resp.setContent(list);
        return resp;
    }
    @GetMapping("/list")
    public CommonResp list(@Valid CategoryQueryReq req){
        CommonResp<PageResp<CategoryQueryResp>> resp = new CommonResp<>();
        PageResp<CategoryQueryResp> list =  categoryService.list(req);
        resp.setContent(list);
        return resp;
    }
    @PostMapping("/save") //保存接口名字
    public CommonResp save(@Valid @RequestBody CategorySaveReq req){
        CommonResp resp = new CommonResp<>();
        categoryService.save(req);//调用save方法
        return resp;
    }

    //删除一般都是按id来删除的，因为id是主键.请求接口传1，拿到数据id就是1，存在映射关系。
    @DeleteMapping ("/delete/{id}") //删除接口名字
    public CommonResp delete(@PathVariable Long id){
        CommonResp resp = new CommonResp<>();
        categoryService.delete(id);//调用delete方法，将id传进去,delete方法在服务层写
        return resp;
    }



}
