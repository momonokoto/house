package com.zpark.config;

import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

@Data
@org.springframework.context.annotation.Configuration
@ConfigurationProperties(prefix = "qiniu")
public class QiniuConfig {
    private String accessKey;
    private String secretKey;
    private String bucket;
    private String domain;
    private String region;

    @Bean
    public Auth auth() {
        return Auth.create(accessKey, secretKey);
    }

    @Bean
    public UploadManager uploadManager() {
        Configuration cfg = new Configuration(getRegionObject());
        return new UploadManager(cfg);
    }

    private Region getRegionObject() {
        switch (region) {
            case "huabei":
                return Region.huabei();
            case "huanan":
                return Region.huanan();
            case "beimei":
                return Region.beimei();
            case "xinjiapo":
                return Region.xinjiapo();
            default:
                return Region.huadong();
        }
    }


}
