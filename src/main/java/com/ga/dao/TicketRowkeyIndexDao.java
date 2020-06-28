package com.ga.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ga.entity.dto.TicketRowkeyIndexDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zelei.fan
 * @date 2020/4/7 11:33
 * @description
 */
@Mapper
@Repository
public interface TicketRowkeyIndexDao extends BaseMapper<TicketRowkeyIndexDO> {

    @Insert("<script> " +
            "insert into ticket_rowkey_index_copy3 " +
            "(insert_time, code, street, id_card) " +
            "values " +
            "<foreach collection='dos' item='t' separator=','> " +
            "(#{t.insertTime}, #{t.code}, #{t.street}, #{t.idCard})  " +
            "</foreach> " +
            "</script> ")
    int insertBatch(@Param("dos") List<TicketRowkeyIndexDO> list);
}
