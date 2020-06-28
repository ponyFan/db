package com.ga.entity.dto;

import com.baomidou.mybatisplus.annotation.TableName;

@TableName("student")
public class StudentDO {

    private String age;

    private String name;

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
                ", age='" + age + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
