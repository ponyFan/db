package com.ga.common.sercurity;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * @author zelei.fan
 * @date 2020/7/8 15:52
 * @description
 */
@Repository
@Mapper
public interface UserDao extends BaseMapper {

    @Select("select * from job_user where username = #{username}")
    JobUser selectByName(String name);
}
