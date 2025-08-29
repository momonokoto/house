package com.zpark.deepseek.config;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.Serializable;

/**
 * DeepSeek相关配置类
 */
@Data
@RequiredArgsConstructor
@Slf4j
@Configuration
public class DeepSeekConfig implements Serializable {
	@Value("${deepseek.apiKeys}")
	private String apiKey;
	@Value("${deepseek.baseUrl}")
	private String baseUrl;
	@Value("${deepseek.timeout}")
	private Integer timeout;
	@Value("${deepseek.readTimeout}")
	private Integer readTimeout;

	@Bean
	public RestTemplate deepSeekRestTemplate() {
		// 创建RestTemplate对象
		RestTemplate restTemplate = new RestTemplate();
		// 创建一个简单的客户端HTTP请求工厂
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		// 设置连接超时时间（单位：毫秒）
		factory.setConnectTimeout(timeout);
		// 设置读取超时时间（单位：毫秒）
		factory.setReadTimeout(readTimeout);
		// 为RestTemplate实例设置请求工厂，以应用超时设置
		restTemplate.setRequestFactory(factory);

		// 创建一个拦截器，用于在请求发送前添加必要的请求头
		restTemplate.getInterceptors().add((request, body, execution) -> {
		    // 添加Authorization头，使用Bearer令牌认证
		    request.getHeaders().add("Authorization", "Bearer " + apiKey);
		    // 指定Content-Type为application/json，表明请求体的类型
		    request.getHeaders().add("Content-Type", "application/json");
		    // 执行请求
		    return execution.execute(request, body);
		});

		// 返回配置了拦截器的RestTemplate实例
		return restTemplate;

	}

	/**
	 * 配置并创建一个WebClient实例，用于与DeepSeek服务进行通信
	 * 该方法主要目的是通过Spring的WebClient构建一个具有预设配置的HTTP客户端，
	 * 以便在发送请求时不需要每次都手动设置基础URL和通用的请求头信息
	 * @return WebClient 实例，配置了基础URL和通用请求头，用于发送HTTP请求
	 */
	@Bean
	public WebClient deepSeekWebClient() {
	    // 构建WebClient实例
	    return WebClient.builder()
	            // 设置基础URL，所有请求都将基于此URL
	            .baseUrl(this.getBaseUrl())
	            // 设置默认的Authorization头，使用Bearer令牌认证
	            .defaultHeader("Authorization", "Bearer " + apiKey)
	            // 设置默认的Content-Type头，指定发送请求时的内容类型
	            .defaultHeader("Content-Type", "application/json")
	            // 完成构建并返回WebClient实例
	            .build();
	}
}
