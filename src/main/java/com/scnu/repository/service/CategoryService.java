package com.scnu.repository.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.scnu.repository.domain.Category;
import com.scnu.repository.domain.CategoryExample;
import com.scnu.repository.mapper.CategoryMapper;
import com.scnu.repository.req.CategoryQueryReq;
import com.scnu.repository.req.CategorySaveReq;
import com.scnu.repository.resp.CategoryQueryResp;
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
public class CategoryService {

    private static final Logger LOG = LoggerFactory.getLogger(CategoryService.class);
    @Resource
    private CategoryMapper categoryMapper;

    //实例化SnowFlake
    @Resource
    private SnowFlake snowFlake;

    public PageResp<CategoryQueryResp> list(CategoryQueryReq req){
        //在这个列表接口设置支持分页,两个参数，查第一页，每页查三条，现在这个查询就支持分页了。
        CategoryExample categoryExample = new CategoryExample();
        CategoryExample.Criteria criteria = categoryExample.createCriteria();
/*        //这里可以根据名称查询分类，后期在实现也行，在请求参数实体类加个getName
        if(!ObjectUtils.isEmpty(req.getName())){
            criteria.andNameLike("%"+req.getName()+"%");
        }*/
        PageHelper.startPage(req.getPage(),req.getSize());
        List<Category> categoryList = categoryMapper.selectByExample(categoryExample);
        PageInfo<Category> pageInfo = new PageInfo<>(categoryList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());
//        List<CategoryResp> respList = new ArrayList<>();
//        for (Category category : categoryList) {
////            CategoryResp categoryResp = new CategoryResp();
////            BeanUtils.copyProperties(category,categoryResp);
        //对象复制
//            CategoryResp categoryResp = CopyUtil.copy(category, CategoryResp.class);
//            respList.add(categoryResp);
//        }

        //列表复制
        List<CategoryQueryResp> list = CopyUtil.copyList(categoryList, CategoryQueryResp.class);

        PageResp<CategoryQueryResp> pageResp =new PageResp();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return  pageResp;
    }
    /**
     * 保存，支持新增和更新，如果id有值说明是更新，如果id没值说明是新增
     **/
     public void save(CategorySaveReq req){
         Category category=CopyUtil.copy(req,Category.class);
         if (ObjectUtils.isEmpty(req.getId())){
             //新增保存，需要自己去生成一个id，id有几种算法，一种最简单的自增、一种uid，一种是下面的雪花算法
             category.setId(snowFlake.nextId());
             categoryMapper.insert(category);
         }else {
             //编辑保存（更新）
             categoryMapper.updateByPrimaryKey(category);
         }
     }

    /**
     * 删除
     **/
     public void delete(Long id){
         System.out.println(id);
         //操作数据库的时候，我们一般会用到Maapper的方法,这里有根据主键来删除
         categoryMapper.deleteByPrimaryKey(id);
     }
}
