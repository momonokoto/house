package com.zpark.config;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories; // 如果您使用了Spring Data Elasticsearch

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.zpark.repository") // 替换为您的Repository包
public class ElasticsearchConfig {

    // 从 application.properties 或 application.yml 获取 ES 地址
    // @Value("${spring.elasticsearch.uris}") // Spring Boot 3.x 推荐这样配置
    private final String[] elasticsearchUris = {"http://121.41.130.155:9200"}; // 替换为您的实际配置

    @Bean
    public RestClient getRestClient() {
        // 对于 Elasticsearch 8.x，推荐使用 RestClient.builder()
        // 并且不需要手动设置 Content-Type，RestClientTransport 会处理
        HttpHost[] httpHosts = new HttpHost[elasticsearchUris.length];
        for (int i = 0; i < elasticsearchUris.length; i++) {
            httpHosts[i] = HttpHost.create(elasticsearchUris[i]);
        }
        return RestClient.builder(httpHosts).build();
    }

    @Bean
    public ElasticsearchTransport getElasticsearchTransport() {
        // 创建 ObjectMapper 并设置命名策略为 SNAKE_CASE（下划线命名 -> 驼峰）
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        // 使用 JacksonJsonpMapper 确保 JSON 序列化/反序列化正确
        return new RestClientTransport(
                getRestClient(),
                new JacksonJsonpMapper()
        );
    }

    @Bean
    public ElasticsearchClient getElasticsearchClient() {
        return new ElasticsearchClient(getElasticsearchTransport());
    }
}
