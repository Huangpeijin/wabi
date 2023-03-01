package com.scnu.repository.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.scnu.repository.domain.Student;
import com.scnu.repository.domain.StudentExample;
import com.scnu.repository.exception.BusinessException;
import com.scnu.repository.exception.BusinessExceptionCode;
import com.scnu.repository.mapper.StudentMapper;
import com.scnu.repository.req.StudentLoginReq;
import com.scnu.repository.req.StudentQueryReq;
import com.scnu.repository.req.StudentResetPasswordReq;
import com.scnu.repository.req.StudentSaveReq;
import com.scnu.repository.resp.PageResp;
import com.scnu.repository.resp.StudentLoginResp;
import com.scnu.repository.resp.StudentQueryResp;
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
public class StudentService {

    private static final Logger LOG = LoggerFactory.getLogger(StudentService.class);
    @Resource
    private StudentMapper studentMapper;

    //实例化SnowFlake
    @Resource
    private SnowFlake snowFlake;

//    public PageResp<StudentQueryResp> list(StudentQueryReq req){
//        //在这个列表接口设置支持分页,两个参数，查第一页，每页查三条，现在这个查询就支持分页了。
//        StudentExample studentExample = new StudentExample();
//        StudentExample.Criteria criteria = studentExample.createCriteria();
//        if(!ObjectUtils.isEmpty(req.getLoginName())){
////            criteria.andLoginNameEqualTo("%"+req.getLoginName()+"%");
//            criteria.andLoginNameLike("%"+req.getLoginName()+"%");
//        }
//
//        PageHelper.startPage(req.getPage(),req.getSize());
//        List<Student> studentList = studentMapper.selectByExample(studentExample);
//        PageInfo<Student> pageInfo = new PageInfo<>(studentList);
//        LOG.info("总行数：{}", pageInfo.getTotal());
//        LOG.info("总页数：{}", pageInfo.getPages());
//        //列表复制
//        List<StudentQueryResp> list = CopyUtil.copyList(studentList, StudentQueryResp.class);
//
//        PageResp<StudentQueryResp> pageResp =new PageResp();
//        pageResp.setTotal(pageInfo.getTotal());
//        pageResp.setList(list);
//        return  pageResp;
//    }
    public PageResp<StudentQueryResp> liststu(StudentQueryReq req,String login_name){
        //在这个列表接口设置支持分页,两个参数，查第一页，每页查三条，现在这个查询就支持分页了。
        StudentExample studentExample = new StudentExample();
        StudentExample.Criteria criteria = studentExample.createCriteria();
        if(!ObjectUtils.isEmpty(login_name)){
            criteria.andLoginNameEqualTo(login_name);
//            criteria.andLoginNameLike("%"+req.getLoginName()+"%");
        }

        PageHelper.startPage(req.getPage(),req.getSize());
        List<Student> studentList = studentMapper.selectByExample(studentExample);
        PageInfo<Student> pageInfo = new PageInfo<>(studentList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());
        //列表复制
        List<StudentQueryResp> list = CopyUtil.copyList(studentList, StudentQueryResp.class);
        PageResp<StudentQueryResp> pageResp =new PageResp();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return  pageResp;
    }
    /**
     * 保存，支持新增和更新，如果id有值说明是更新，如果id没值说明是新增
     **/
     public void save(StudentSaveReq req){
         Student student=CopyUtil.copy(req,Student.class);
         if (ObjectUtils.isEmpty(req.getId())){
             Student studentDB = selectByLoginName(req.getLoginName());
             if (ObjectUtils.isEmpty(studentDB)) {
                 // 新增
                 student.setId(snowFlake.nextId());
//                 LOG.info("输出limitCode：{}",student.getLimitcode());
                 studentMapper.insert(student);
             } else {
                 // 用户名已存在
                 throw new BusinessException(BusinessExceptionCode.USER_LOGIN_NAME_EXIST);
             }
         }else {
             // 更新
             student.setLoginName(null);
             student.setPassword(null);
             studentMapper.updateByPrimaryKeySelective(student);
         }
     }
     //根据用户名去查
    public Student selectByLoginName(String LoginName) {
        StudentExample studentExample = new StudentExample();
        StudentExample.Criteria criteria = studentExample.createCriteria();
        criteria.andLoginNameEqualTo(LoginName);
        //mybatis只能用list来接收
        List<Student> studentList = studentMapper.selectByExample(studentExample);
        if (CollectionUtils.isEmpty(studentList)) {
            return null;
        } else {
            return studentList.get(0);
        }
    }
    /**
     * 删除
     **/
     public void delete(Long id){
//         System.out.println(id);
         //操作数据库的时候，我们一般会用到Maapper的方法,这里有根据主键来删除
         studentMapper.deleteByPrimaryKey(id);
     }
    /**
     * 修改密码
     */
    public void resetPassword(StudentResetPasswordReq req) {
        Student student = CopyUtil.copy(req, Student.class);//copy成数据库的实体
        studentMapper.updateByPrimaryKeySelective(student);
    }
    /**
     * 学生登录
     */
    public StudentLoginResp login(StudentLoginReq req) {
        Student studentDb = selectByLoginName(req.getLoginName());
        if (ObjectUtils.isEmpty(studentDb)) {
            // 用户名不存在
            LOG.info("用户名不存在, {}", req.getLoginName());
            throw new BusinessException(BusinessExceptionCode.LOGIN_USER_ERROR);
        } else {
            if (studentDb.getPassword().equals(req.getPassword())) {
                // 登录成功
                StudentLoginResp studentLoginResp = CopyUtil.copy(studentDb, StudentLoginResp.class);
                return studentLoginResp;
            } else {
                // 密码不对
                LOG.info("密码不对, 输入密码：{}, 数据库密码：{}", req.getPassword(), studentDb.getPassword());
                throw new BusinessException(BusinessExceptionCode.LOGIN_USER_ERROR);
            }
        }
    }

}
