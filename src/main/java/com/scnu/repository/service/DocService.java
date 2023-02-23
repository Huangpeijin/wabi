package com.scnu.repository.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.scnu.repository.domain.Doc;
import com.scnu.repository.domain.DocExample;
import com.scnu.repository.mapper.DocMapper;
import com.scnu.repository.req.DocQueryReq;
import com.scnu.repository.req.DocSaveReq;
import com.scnu.repository.resp.DocQueryResp;
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
public class DocService {

    private static final Logger LOG = LoggerFactory.getLogger(DocService.class);
    @Resource
    private DocMapper docMapper;

    //实例化SnowFlake
    @Resource
    private SnowFlake snowFlake;

    public List<DocQueryResp> all(){
        //在这个列表接口设置支持分页,两个参数，查第一页，每页查三条，现在这个查询就支持分页了。
        DocExample docExample = new DocExample();
        //给查询加排序
        docExample.setOrderByClause("sort asc");
        List<Doc> docList = docMapper.selectByExample(docExample);

        //列表复制
        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);
        return  list;
    }
    public PageResp<DocQueryResp> list(DocQueryReq req){
        //在这个列表接口设置支持分页,两个参数，查第一页，每页查三条，现在这个查询就支持分页了。
        DocExample docExample = new DocExample();
        docExample.setOrderByClause("sort asc");
        DocExample.Criteria criteria = docExample.createCriteria();
/*        //这里可以根据名称查询分类，后期在实现也行，在请求参数实体类加个getName
        if(!ObjectUtils.isEmpty(req.getName())){
            criteria.andNameLike("%"+req.getName()+"%");
        }*/
        PageHelper.startPage(req.getPage(),req.getSize());
        List<Doc> docList = docMapper.selectByExample(docExample);
        PageInfo<Doc> pageInfo = new PageInfo<>(docList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());
//        List<DocResp> respList = new ArrayList<>();
//        for (Doc doc : docList) {
////            DocResp docResp = new DocResp();
////            BeanUtils.copyProperties(doc,docResp);
        //对象复制
//            DocResp docResp = CopyUtil.copy(doc, DocResp.class);
//            respList.add(docResp);
//        }

        //列表复制
        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);

        PageResp<DocQueryResp> pageResp =new PageResp();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return  pageResp;
    }
    /**
     * 保存，支持新增和更新，如果id有值说明是更新，如果id没值说明是新增
     **/
     public void save(DocSaveReq req){
         Doc doc=CopyUtil.copy(req,Doc.class);
         if (ObjectUtils.isEmpty(req.getId())){
             //新增保存，需要自己去生成一个id，id有几种算法，一种最简单的自增、一种uid，一种是下面的雪花算法
             doc.setId(snowFlake.nextId());
             docMapper.insert(doc);
         }else {
             //编辑保存（更新）
             docMapper.updateByPrimaryKey(doc);
         }
     }

    /**
     * 删除
     **/
     public void delete(Long id){
         //操作数据库的时候，我们一般会用到Maapper的方法,这里有根据主键来删除
         docMapper.deleteByPrimaryKey(id);
     }
     /**
     * 重载删除
     **/
     public void delete(List<String> ids){
         //创建条件
         DocExample docExample = new DocExample();
         DocExample.Criteria criteria = docExample.createCriteria();
         criteria.andIdIn(ids);
         //根据条件进行删除，这样的话就只需要执行一个SQL就可以实行批量删除
         docMapper.deleteByExample(docExample);
     }
}
