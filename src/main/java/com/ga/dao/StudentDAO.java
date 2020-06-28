package com.ga.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ga.entity.dto.StudentDO;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Mapper
@Repository
public interface StudentDAO extends BaseMapper<StudentDO> {

    @Insert("insert into student (age, name) values (#{stu.age}, #{stu.name})")
    int insert(@Param("stu") StudentDO student);

    @Select("select * from student")
    List<StudentDO> selectAll();

    @Delete("delete from student where name = #{name}")
    void delete(@Param("name") String name);

    @Insert("<script>" +
            "insert into face_file " +
            "(id,data_type,device_ip,push_time,push_id,file_path,collect_time,id_card,type,status,insert_time,update_time,deleted) " +
            "values " +
            "<foreach collection='list' item='t' separator=','> " +
            "(UUID(), #{t.dataType}, #{t.deviceIp}, #{t.pushTime}, #{t.pushId}, #{t.filePath}, #{t.collectTime}, #{t.idCard}, #{t.type}, #{t.status}, #{t.insertTime}, #{t.updateTime}, #{t.deleted})" +
            "</foreach> " +
            "</script>")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insertBatch(@Param("list") List<Object> faceDataDOS);

    @Insert("insert into face_file " +
            "(id,data_type,device_ip,push_time,push_id,file_path,collect_time,id_card,type,status,insert_time,update_time,deleted) " +
            "values " +
            "(#{id}, #{dataType}, #{deviceIp}, #{pushTime}, #{pushId}, #{filePath}, #{collectTime}, #{idCard}, #{type}, #{status}, #{insertTime}, #{updateTime}, #{deleted})")
    @SelectKey(keyProperty = "id", resultType = String.class, before = true,
            statement = "select replace(uuid(), '-', '') as id from dual")
    int insertInfo(Objects t);
}
