package com.scnu.repository.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.scnu.repository.domain.Teacher;
import com.scnu.repository.domain.TeacherExample;
import com.scnu.repository.exception.BusinessException;
import com.scnu.repository.exception.BusinessExceptionCode;
import com.scnu.repository.mapper.TeacherMapper;
import com.scnu.repository.req.TeacherLoginReq;
import com.scnu.repository.req.TeacherQueryReq;
import com.scnu.repository.req.TeacherResetPasswordReq;
import com.scnu.repository.req.TeacherSaveReq;
import com.scnu.repository.resp.PageResp;
import com.scnu.repository.resp.TeacherLoginResp;
import com.scnu.repository.resp.TeacherQueryResp;
import com.scnu.repository.util.CopyUtil;
import com.scnu.repository.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TeacherService {

    private static final Logger LOG = LoggerFactory.getLogger(TeacherService.class);
    @Resource
    private TeacherMapper teacherMapper;

    //实例化SnowFlake
    @Resource
    private SnowFlake snowFlake;

    public PageResp<TeacherQueryResp> list(TeacherQueryReq req,String login_name){
        //在这个列表接口设置支持分页,两个参数，查第一页，每页查三条，现在这个查询就支持分页了。
        TeacherExample teacherExample = new TeacherExample();
        TeacherExample.Criteria criteria = teacherExample.createCriteria();
        if(!ObjectUtils.isEmpty(login_name)){
            criteria.andLoginNameEqualTo(login_name);
//            criteria.andLoginNameLike("%"+req.getLoginName()+"%");
        }

        PageHelper.startPage(req.getPage(),req.getSize());
        List<Teacher> teacherList = teacherMapper.selectByExample(teacherExample);
        PageInfo<Teacher> pageInfo = new PageInfo<>(teacherList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());
//        List<TeacherResp> respList = new ArrayList<>();
//        for (Teacher teacher : teacherList) {
////            TeacherResp teacherResp = new TeacherResp();
////            BeanUtils.copyProperties(teacher,teacherResp);
        //对象复制
//            TeacherResp teacherResp = CopyUtil.copy(teacher, TeacherResp.class);
//            respList.add(teacherResp);
//        }

        //列表复制
        List<TeacherQueryResp> list = CopyUtil.copyList(teacherList, TeacherQueryResp.class);

        PageResp<TeacherQueryResp> pageResp =new PageResp();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return  pageResp;
    }
    /**
     * 保存，支持新增和更新，如果id有值说明是更新，如果id没值说明是新增
     **/
     public void save(TeacherSaveReq req){
         Teacher teacher=CopyUtil.copy(req,Teacher.class);
         if (ObjectUtils.isEmpty(req.getId())){
             Teacher teacherDB = selectByLoginName(req.getLoginName());
             if (ObjectUtils.isEmpty(teacherDB)) {
                 // 新增
                 teacher.setId(snowFlake.nextId());
//                 LOG.info("输出limitCode：{}",teacher.getLimitcode());
                 teacherMapper.insert(teacher);
             } else {
                 // 用户名已存在
                 throw new BusinessException(BusinessExceptionCode.USER_LOGIN_NAME_EXIST);
             }
         }else {
             // 更新
             teacher.setLoginName(null);
             teacher.setPassword(null);
             teacherMapper.updateByPrimaryKeySelective(teacher);
         }
     }
     //根据用户名去查
    public Teacher selectByLoginName(String LoginName) {
        TeacherExample teacherExample = new TeacherExample();
        TeacherExample.Criteria criteria = teacherExample.createCriteria();
        criteria.andLoginNameEqualTo(LoginName);
        //mybatis只能用list来接收
        List<Teacher> teacherList = teacherMapper.selectByExample(teacherExample);
        if (CollectionUtils.isEmpty(teacherList)) {
            return null;
        } else {
            return teacherList.get(0);
        }
    }
    /**
     * 删除
     **/
     public void delete(Long id){
//         System.out.println(id);
         //操作数据库的时候，我们一般会用到Maapper的方法,这里有根据主键来删除
         teacherMapper.deleteByPrimaryKey(id);
     }
    /**
     * 修改密码
     */
    public void resetPassword(TeacherResetPasswordReq req) {
        Teacher teacher = CopyUtil.copy(req, Teacher.class);//copy成数据库的实体
        teacherMapper.updateByPrimaryKeySelective(teacher);
    }
    /**
     * 教师登录
     */
    public TeacherLoginResp login(TeacherLoginReq req) {
        Teacher teacherDb = selectByLoginName(req.getLoginName());
        if (ObjectUtils.isEmpty(teacherDb)) {
            // 用户名不存在
            LOG.info("用户名不存在, {}", req.getLoginName());
            throw new BusinessException(BusinessExceptionCode.LOGIN_USER_ERROR);
        } else {
            if (teacherDb.getPassword().equals(req.getPassword())) {
                // 登录成功
                TeacherLoginResp teacherLoginResp = CopyUtil.copy(teacherDb, TeacherLoginResp.class);
                return teacherLoginResp;
            } else {
                // 密码不对
                LOG.info("密码不对, 输入密码：{}, 数据库密码：{}", req.getPassword(), teacherDb.getPassword());
                throw new BusinessException(BusinessExceptionCode.LOGIN_USER_ERROR);
            }
        }
    }

}
