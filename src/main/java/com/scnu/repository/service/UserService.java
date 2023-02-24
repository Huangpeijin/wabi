package com.scnu.repository.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.scnu.repository.domain.User;
import com.scnu.repository.domain.UserExample;
import com.scnu.repository.mapper.UserMapper;
import com.scnu.repository.req.UserQueryReq;
import com.scnu.repository.req.UserSaveReq;
import com.scnu.repository.resp.UserQueryResp;
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
public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);
    @Resource
    private UserMapper userMapper;

    //实例化SnowFlake
    @Resource
    private SnowFlake snowFlake;

    public PageResp<UserQueryResp> list(UserQueryReq req){
        //在这个列表接口设置支持分页,两个参数，查第一页，每页查三条，现在这个查询就支持分页了。
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        if(!ObjectUtils.isEmpty(req.getLoginName())){
            criteria.andLoginNameEqualTo(req.getLoginName());
        }
        PageHelper.startPage(req.getPage(),req.getSize());
        List<User> userList = userMapper.selectByExample(userExample);
        PageInfo<User> pageInfo = new PageInfo<>(userList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());
//        List<UserResp> respList = new ArrayList<>();
//        for (User user : userList) {
////            UserResp userResp = new UserResp();
////            BeanUtils.copyProperties(user,userResp);
        //对象复制
//            UserResp userResp = CopyUtil.copy(user, UserResp.class);
//            respList.add(userResp);
//        }

        //列表复制
        List<UserQueryResp> list = CopyUtil.copyList(userList, UserQueryResp.class);

        PageResp<UserQueryResp> pageResp =new PageResp();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return  pageResp;
    }
    /**
     * 保存，支持新增和更新，如果id有值说明是更新，如果id没值说明是新增
     **/
     public void save(UserSaveReq req){
         User user=CopyUtil.copy(req,User.class);
         if (ObjectUtils.isEmpty(req.getId())){
             //新增保存，需要自己去生成一个id，id有几种算法，一种最简单的自增、一种uid，一种是下面的雪花算法
             user.setId(snowFlake.nextId());
             userMapper.insert(user);
         }else {
             //编辑保存（更新）
             userMapper.updateByPrimaryKey(user);
         }
     }

    /**
     * 删除
     **/
     public void delete(Long id){
//         System.out.println(id);
         //操作数据库的时候，我们一般会用到Maapper的方法,这里有根据主键来删除
         userMapper.deleteByPrimaryKey(id);
     }
}
