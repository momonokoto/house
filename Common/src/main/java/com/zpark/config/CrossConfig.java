package com.zpark.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CrossConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/**")
                .allowedOriginPatterns("http://localhost:2019", "http://47.76.101.199:2019", "http://www.xzzf.xyz/**", "http://www.xzzf.xyz:2019", "http://www.xzzf.xyz/entry/**", "http://www.xzzf.xyz/entry") //由于allowCredentials为true,所以不能使用*
                .allowedMethods("GET","HEAD","POST","PUT","DELETE","OPTIONS")
                .allowCredentials(true)
                .maxAge(3600)
                .allowedHeaders("*");
    }
}

