package com.scnu.repository.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.scnu.repository.domain.Ebook;
import com.scnu.repository.domain.EbookExample;
import com.scnu.repository.mapper.EbookMapper;
import com.scnu.repository.req.EbookQueryReq;
import com.scnu.repository.req.EbookSaveReq;
import com.scnu.repository.resp.EbookQueryResp;
import com.scnu.repository.resp.PageResp;
import com.scnu.repository.util.CopyUtil;
import com.scnu.repository.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class EbookService {

    private static final Logger LOG = LoggerFactory.getLogger(EbookService.class);
    @Resource
    private EbookMapper ebookMapper;

    @Resource
    private SnowFlake snowFlake;

    public PageResp<EbookQueryResp> list(EbookQueryReq req){
        //在这个列表接口设置支持分页,两个参数，查第一页，每页查三条，现在这个查询就支持分页了。
        EbookExample ebookExample = new EbookExample();
        EbookExample.Criteria criteria = ebookExample.createCriteria();
        if(!ObjectUtils.isEmpty(req.getName())){
            criteria.andNameLike("%"+req.getName()+"%");
        }
        PageHelper.startPage(req.getPage(),req.getSize());
        List<Ebook> ebookList = ebookMapper.selectByExample(ebookExample);
        PageInfo<Ebook> pageInfo = new PageInfo<>(ebookList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());
//        List<EbookResp> respList = new ArrayList<>();
//        for (Ebook ebook : ebookList) {
////            EbookResp ebookResp = new EbookResp();
////            BeanUtils.copyProperties(ebook,ebookResp);
        //对象复制
//            EbookResp ebookResp = CopyUtil.copy(ebook, EbookResp.class);
//            respList.add(ebookResp);
//        }

        //列表复制
        List<EbookQueryResp> list = CopyUtil.copyList(ebookList, EbookQueryResp.class);

        PageResp<EbookQueryResp> pageResp =new PageResp();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return  pageResp;
    }
    /**
     * 保存
     **/
     public void save(EbookSaveReq req){
         Ebook ebook=CopyUtil.copy(req,Ebook.class);
         if (ObjectUtils.isEmpty(req.getId())){
             //新增
             ebook.setId(snowFlake.nextId());
             ebookMapper.insert(ebook);
         }else {
             //通过键值去更新
             ebookMapper.updateByPrimaryKey(ebook);
         }
     }
}
