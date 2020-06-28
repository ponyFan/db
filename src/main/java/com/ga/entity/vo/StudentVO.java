package com.ga.entity.vo;

import com.ga.annotion.checkparam.NotNull;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "测试对象student")
public class StudentVO {

    @ApiModelProperty(value = "学生id", dataType = "String")
    @NotNull("id 不能为空")
    private String id;

    @ApiModelProperty(value = "年龄", dataType = "String")
    @NotNull("年龄不能为空")
    private String age;

    @ApiModelProperty(value = "姓名", dataType = "String")
    @NotNull("姓名不能为空")
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id='" + id + '\'' +
                ", age='" + age + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
