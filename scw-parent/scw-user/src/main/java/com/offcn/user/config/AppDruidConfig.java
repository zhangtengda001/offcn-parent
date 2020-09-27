package com.offcn.user.config;


import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/*
*
* 过滤器
* */
@Configuration
public class AppDruidConfig {

    @Bean
    @ConfigurationProperties(prefix  = "spring.datasource")
    public DataSource dataSource() throws Exception{
        DruidDataSource dataSource =new DruidDataSource();
        dataSource .setFilters("stat");
        return dataSource ;
    }

    @Bean
    public ServletRegistrationBean statViewServlet() {
        /*监控路径*/
        ServletRegistrationBean bean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
        /*创建map集合*/
        Map<String, String> initParams = new HashMap<>();
        /*指定用户名和密码*/
        initParams.put("loginUsername", "admin");
        initParams.put("loginPassword", "123456");
        // 默认就是允许所有访问
        initParams.put("allow", "");
        //设置
        bean.setInitParameters(initParams);
        return bean;
    }

    @Bean
    public FilterRegistrationBean webStatFilter() {
        FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setFilter(new WebStatFilter());
        Map<String, String> initParams = new HashMap<>();
        initParams.put("exclusions", "*.js,*.css,/druid/*");
        bean.setInitParameters(initParams);
        bean.setUrlPatterns(Arrays.asList("/*"));
        return bean;
    }
}
