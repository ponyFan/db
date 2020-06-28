package com.ga.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ga.entity.dto.StudentDO;
import com.ga.entity.vo.StudentVO;

/**
 * @author zelei.fan
 * @date 2019/11/28 10:01
 * @description
 */
public interface StudentService {

    /**
     * 插入
     * @param studentVO
     */
    void insert(StudentVO studentVO);

    /**
     * 测试事务
     * @param studentVO
     */
    void testTransaction(StudentVO studentVO);

    /**
     * 分页
     * @param page
     * @param limit
     * @param name
     * @return
     */
    IPage<StudentDO> selectPage(int page, int limit, String name);
}
