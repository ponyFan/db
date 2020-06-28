package com.ga.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * @author zelei.fan
 * @date 2019/12/18 8:51
 * @description 配置静态访问路径，使静态文件可以通过 http://ip:port/文件路径 直接访问
 */
@Configuration
public class FileConfig extends WebMvcConfigurationSupport {

    @Value("${upload.file.path}")
    private String path;

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (!path.endsWith("/")){
            path = path + "/";
        }
        /*设置本地磁盘路径，如path = /root/tmp */
        registry.addResourceHandler(path + "**").addResourceLocations("file:" + path);
        /*设置静态文件如html、js、css、jpg等*/
        registry.addResourceHandler("/**").addResourceLocations("/");
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/resources/")
                .addResourceLocations("classpath:/static/")
                .addResourceLocations("classpath:/public/")
                .addResourceLocations("classpath:/META-INF/resources/");
        /*registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");*/
    }
}
