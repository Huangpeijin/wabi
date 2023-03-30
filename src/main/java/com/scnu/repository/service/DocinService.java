package com.scnu.repository.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.scnu.repository.domain.*;
import com.scnu.repository.exception.BusinessException;
import com.scnu.repository.exception.BusinessExceptionCode;
import com.scnu.repository.mapper.ContentMapper;
import com.scnu.repository.mapper.DocinMapper;
import com.scnu.repository.mapper.DocinMapperCust;
import com.scnu.repository.req.DocinQueryReq;
import com.scnu.repository.req.DocinSaveReq;
import com.scnu.repository.resp.DocinQueryResp;
import com.scnu.repository.resp.PageResp;
import com.scnu.repository.util.CopyUtil;
import com.scnu.repository.util.RedisUtil;
import com.scnu.repository.util.RequestContext;
import com.scnu.repository.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DocinService {

    private static final Logger LOG = LoggerFactory.getLogger(DocinService.class);
    @Resource
    private DocinMapper docinMapper;

    @Resource
    private ContentMapper contentMapper;

    @Resource
    private DocinMapperCust docinMapperCust;

    //实例化SnowFlake
    @Resource
    private SnowFlake snowFlake;

    @Resource
    public RedisUtil redisUtil;

    @Resource
    public WsService wsService;

    public List<DocinQueryResp> all(Long ebookId){
        //在这个列表接口设置支持分页,两个参数，查第一页，每页查三条，现在这个查询就支持分页了。
        DocinExample docinExample = new DocinExample();
        docinExample.createCriteria().andEbookIdEqualTo(ebookId);
        //给查询加排序
        docinExample.setOrderByClause("sort asc");
        List<Docin> docinList = docinMapper.selectByExample(docinExample);

        //列表复制
        List<DocinQueryResp> list = CopyUtil.copyList(docinList, DocinQueryResp.class);
        return  list;
    }
    public PageResp<DocinQueryResp> list(DocinQueryReq req){
        //在这个列表接口设置支持分页,两个参数，查第一页，每页查三条，现在这个查询就支持分页了。
        DocinExample docinExample = new DocinExample();
        docinExample.setOrderByClause("sort asc");
        DocinExample.Criteria criteria = docinExample.createCriteria();
/*        //这里可以根据名称查询分类，后期在实现也行，在请求参数实体类加个getName
        if(!ObjectUtils.isEmpty(req.getName())){
            criteria.andNameLike("%"+req.getName()+"%");
        }*/
        PageHelper.startPage(req.getPage(),req.getSize());
        List<Docin> docinList = docinMapper.selectByExample(docinExample);
        PageInfo<Docin> pageInfo = new PageInfo<>(docinList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());
//        List<DocinResp> respList = new ArrayList<>();
//        for (Docin docin : docinList) {
////            DocinResp docinResp = new DocinResp();
////            BeanUtils.copyProperties(docin,docinResp);
        //对象复制
//            DocinResp docinResp = CopyUtil.copy(docin, DocinResp.class);
//            respList.add(docinResp);
//        }

        //列表复制
        List<DocinQueryResp> list = CopyUtil.copyList(docinList, DocinQueryResp.class);

        PageResp<DocinQueryResp> pageResp =new PageResp();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return  pageResp;
    }
    /**
     * 保存，支持新增和更新，如果id有值说明是更新，如果id没值说明是新增
     **/
    @Transactional
    public void save(DocinSaveReq req){
        Docin docin=CopyUtil.copy(req,Docin.class);
        // 使用selectByPrimaryKey方法查询id是否存在
        Docin oldDocin = docinMapper.selectByPrimaryKey(docin.getId());
        if(oldDocin == null){
            // 如果不存在，使用insertSelective方法插入
            docinMapper.insertSelective(docin);
        }else{
            // 如果存在，使用updateByPrimaryKeySelective方法更新
            docinMapper.updateByPrimaryKeySelective(docin);
        }
    }

    /**
     * 删除
     **/
     public void delete(Long id){
         //操作数据库的时候，我们一般会用到Maapper的方法,这里有根据主键来删除
         docinMapper.deleteByPrimaryKey(id);
     }
     /**
     * 重载删除
     **/
     public void delete(List<String> ids){
         //创建条件
         DocinExample docinExample = new DocinExample();
         DocinExample.Criteria criteria = docinExample.createCriteria();
         criteria.andIdIn(ids);
         //根据条件进行删除，这样的话就只需要执行一个SQL就可以实行批量删除
         docinMapper.deleteByExample(docinExample);
     }
    /**
     * 内容查询
     **/
    public String findContent(Long id) {
        Content content = contentMapper.selectByPrimaryKey(id);
        // 文章阅读数+1
        docinMapperCust.increaseViewCount(id);
        if (ObjectUtils.isEmpty(content)) {
            return "";
        } else {
            return content.getContent();
        }
    }
    /**
     * 点赞
     */
    public void vote(Long id) {
        String ip = RequestContext.getRemoteAddr();
        //第一次点赞就会校验key是否存在，根据docin.id和IP，第一次校验肯定不存在，不存在就会把key放入redis里去
        if (redisUtil.validateRepeat("DOC_VOTE_" + id + "_" + ip, 5000)) {
            docinMapperCust.increaseVoteCount(id);
        } else {
            throw new BusinessException(BusinessExceptionCode.VOTE_REPEAT);
        }
    }

    public void updateEbookInfo(){
            docinMapperCust.updateEbookInfo();
    }
}
