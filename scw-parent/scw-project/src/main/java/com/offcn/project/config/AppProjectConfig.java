package com.offcn.project.config;

import com.offcn.dycommon.util.OssTemplate;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppProjectConfig {

    @Bean
    @ConfigurationProperties(prefix = "oss")
    public OssTemplate getOssTemplate(){
        return new OssTemplate();
    }
}
