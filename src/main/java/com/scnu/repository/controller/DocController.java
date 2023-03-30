package com.scnu.repository.controller;

import com.scnu.repository.domain.Doc;
import com.scnu.repository.req.DocQueryReq;
import com.scnu.repository.req.DocSaveReq;
import com.scnu.repository.resp.CommonResp;
import com.scnu.repository.resp.DocQueryResp;
import com.scnu.repository.resp.PageResp;
import com.scnu.repository.service.DocService;
import com.scnu.repository.util.ConstUtil;
import com.scnu.repository.util.FilesUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/doc")
public class DocController {

    @Resource
    private DocService docService;

    @GetMapping("/all/{ebookId}")
    public CommonResp all(@PathVariable Long ebookId){
        CommonResp<List<DocQueryResp>> resp = new CommonResp<>();
        List<DocQueryResp> list =  docService.all(ebookId);
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
        Doc savedDoc = docService.save(req); // 调用save方法并获取返回的doc对象
        resp.setId(savedDoc.getId()); // 将id赋值到resp对象中
        resp.setEbookId(savedDoc.getEbookId());
        resp.setParent(savedDoc.getParent());
        resp.setName(savedDoc.getName());
        resp.setSort(savedDoc.getSort());
        resp.setViewCount(savedDoc.getViewCount()); // 将viewCount赋值到resp对象中
        resp.setVoteCount(savedDoc.getVoteCount()); // 将voteCount赋值到resp对象中
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

    @GetMapping("/find-content/{id}")
    public CommonResp findContent(@PathVariable Long id) {
        CommonResp<String> resp = new CommonResp<>();
        String content = docService.findContent(id);
        String textvalue = docService.findTextvalue(id);
        resp.setContent(content);
        resp.setTextvalue(textvalue);
        return resp;
    }
    @GetMapping("/vote/{id}")
    public CommonResp vote(@PathVariable Long id) {
        CommonResp commonResp = new CommonResp();
        docService.vote(id);
        return commonResp;
    }

    @PostMapping("/uploadimg")
    public String img(@RequestParam(value = "file") MultipartFile file){
        try{
            return FilesUtil.uploadImg(file,
                    ConstUtil.SAVE_IMG_PATH,
                    Objects.requireNonNull(file.getOriginalFilename()),
                    ConstUtil.REQUEST_IMG_PATH);
        }catch (Exception e){
            e.printStackTrace();
            return ConstUtil.IMG_UPLOAD_FAILURE;
        }
    }
}
