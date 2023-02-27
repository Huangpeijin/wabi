package com.scnu.repository.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.scnu.repository.domain.Content;
import com.scnu.repository.domain.Doc;
import com.scnu.repository.domain.DocExample;
import com.scnu.repository.exception.BusinessException;
import com.scnu.repository.exception.BusinessExceptionCode;
import com.scnu.repository.mapper.ContentMapper;
import com.scnu.repository.mapper.DocMapper;
import com.scnu.repository.mapper.DocMapperCust;
import com.scnu.repository.req.DocQueryReq;
import com.scnu.repository.req.DocSaveReq;
import com.scnu.repository.resp.DocQueryResp;
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
public class DocService {

    private static final Logger LOG = LoggerFactory.getLogger(DocService.class);
    @Resource
    private DocMapper docMapper;

    @Resource
    private ContentMapper contentMapper;

    @Resource
    private DocMapperCust docMapperCust;

    //实例化SnowFlake
    @Resource
    private SnowFlake snowFlake;

    @Resource
    public RedisUtil redisUtil;

    @Resource
    public WsService wsService;

    public List<DocQueryResp> all(Long ebookId){
        //在这个列表接口设置支持分页,两个参数，查第一页，每页查三条，现在这个查询就支持分页了。
        DocExample docExample = new DocExample();
        docExample.createCriteria().andEbookIdEqualTo(ebookId);
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
    @Transactional
     public void save(DocSaveReq req){
         Doc doc=CopyUtil.copy(req,Doc.class);
         Content content=CopyUtil.copy(req, Content.class);//复制前端传来的内容
         if (ObjectUtils.isEmpty(req.getId())){
             //新增保存，需要自己去生成一个id，id有几种算法，一种最简单的自增、一种uid，一种是下面的雪花算法
             doc.setId(snowFlake.nextId());//在domain里的doc用雪花算法生成一个id
             //初始的时候，阅读数和点赞数都为0
             doc.setViewCount(0);
             doc.setVoteCount(0);
             docMapper.insert(doc);//在文档数据库插入文档的内容，这时的id是long类型，就是这里丢失的精度。
//             System.out.println(doc.getId());
             content.setId(doc.getId());//设置content的id跟doc的id一样
             contentMapper.insert(content);//在文档内容数据库插入文档内容的内容
         }else {
             //编辑保存（更新）
             docMapper.updateByPrimaryKey(doc);
             int count = contentMapper.updateByPrimaryKeyWithBLOBs(content);
             //如果在数据库没有这个条数，则插入一条。
             if (count==0){
                 contentMapper.insert(content);
             }
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

    /**
     * 富文本内容查询
     **/
    public String findContent(Long id) {
        Content content = contentMapper.selectByPrimaryKey(id);
        // 文档阅读数+1
        docMapperCust.increaseViewCount(id);
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
//         docMapperCust.increaseVoteCount(id);
        //如果项目有会员体系，远程IP可以替换为会员ID
        // 远程IP+doc.id作为key，24小时内不能重复
        String ip = RequestContext.getRemoteAddr();
        //第一次点赞就会校验key是否存在，根据doc.id和IP，第一次校验肯定不存在，不存在就会把key放入redis里去
        if (redisUtil.validateRepeat("DOC_VOTE_" + id + "_" + ip, 5000)) {
            docMapperCust.increaseVoteCount(id);
        } else {
            throw new BusinessException(BusinessExceptionCode.VOTE_REPEAT);
        }
        // 推送消息
//       Doc docDb = docMapper.selectByPrimaryKey(id);
//        webSocketServer.sendInfo("【" + docDb.getName() + "】被点赞！");
//       String logId = MDC.get("LOG_ID");
//        wsService.sendInfo("【" + docDb.getName() + "】被点赞！",logId);
        // rocketMQTemplate.convertAndSend("VOTE_TOPIC", "【" + docDb.getName() + "】被点赞！");
    }

        public void updateEbookInfo(){
            docMapperCust.updateEbookInfo();
    }

}
