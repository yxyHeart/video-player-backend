package com.yxy.videoplayerbackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 可根据需要指定具体的路径
                .allowedOrigins("http://localhost:6017/") // 允许的源（域名），可以使用 * 通配符表示允许所有域名
                .allowedMethods("GET", "POST", "PUT", "DELETE") // 允许的请求方法
                .allowedHeaders("*") // 允许的请求头
                .allowCredentials(true) // 是否允许发送身份凭证（如 cookies）
                .maxAge(3600); // 预检请求的有效期，单位为秒
    }
}