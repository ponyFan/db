package com.ga.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ga.dao.StudentDAO;
import com.ga.entity.dto.StudentDO;
import com.ga.entity.vo.StudentVO;
import com.ga.service.StudentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author zelei.fan
 * @date 2019/11/28 10:02
 * @description
 */

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentDAO studentDAO;

    @Override
    public void insert(StudentVO studentVO) {
        StudentDO studentDO = new StudentDO();
        BeanUtils.copyProperties(studentVO, studentDO);
        int insert = studentDAO.insert(studentDO);
    }

    /**
     * 事务注意项：Transactional注解要加在实现类或实现方法上，不能单独写在普通方法上，否则不起作用；
     * 原因：是由于事务是通过aop实现的，所以spring默认检测的是实现类，对其他普通方法不起作用
     * @param studentVO
     */
    @Override
    @Transactional
    public void testTransaction(StudentVO studentVO) {
        studentDAO.delete(studentVO.getName());
        StudentDO studentDO = new StudentDO();
        BeanUtils.copyProperties(studentVO, studentDO);
        studentDAO.insert(studentDO);
    }

    @Override
    public IPage<StudentDO> selectPage(int page, int limit, String name) {
        QueryWrapper<StudentDO> wrapper = new QueryWrapper<>();
        wrapper.select(new String[]{"name"});
        wrapper.eq("name", name);
        IPage<StudentDO> iPage = studentDAO.selectPage(new Page<>(page, limit), wrapper);
        return iPage;
    }
}
