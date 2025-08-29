package com.zpark.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

//    @Bean
//    public RestTemplate restTemplate() {
//        // 可以进行一些配置，例如设置超时时间
//        // SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
//        // requestFactory.setConnectTimeout(5000); // 连接超时5秒
//        // requestFactory.setReadTimeout(5000);    // 读取超时5秒
//        // return new RestTemplate(requestFactory);
//        return new RestTemplate();
//    }
}