package com.zpark.deepseek.service.impl;

import com.zpark.deepseek.config.DeepSeekConfig;
import com.zpark.deepseek.dto.MessageDto;
import com.zpark.deepseek.service.IDeepSeekService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @PackageName: com.zpark.deepseek.service.impl
 * @ClassName: DeepSeekServiceImpl
 * @Description:
 * @author: RZS
 * @date: 2025/6/16  9:58
 */
@Service
public class DeepSeekServiceImpl implements IDeepSeekService {

	@Autowired
	private DeepSeekConfig deepSeekConfig;
	@Autowired
	private RestTemplate deepseekRestTemplate;
	@Autowired
	private WebClient deepSeekWebClient;

	@Override
	public String chat(String text) {
		// 1、构造请求参数
		Map<String, Object> params = new HashMap<>();
		// 设置对话模型
		params.put("model", "deepseek-chat");
		// 设置对话参数（对话的消息列表）
		List<MessageDto> messages = new ArrayList<>();
		messages.add(new MessageDto("user", text));
		params.put("messages", messages);

		// 创建请求参数对象
		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(params);

		// 2、发起请求
		ResponseEntity<Map> response = deepseekRestTemplate.postForEntity(
				deepSeekConfig.getBaseUrl() + "/chat/completions",
				entity,
				Map.class
		);
		System.out.println(response.getBody());
		// 3、获取请求结果
		return response.getBody().get("choices").toString();
	}

	@Override
	public Flux<String> chatStream(String text) {
		// 1、构造请求参数
		Map<String, Object> params = new HashMap<>();
		// 设置对话模型
		params.put("model", "deepseek-chat");
		// 设置对话参数（对话的消息列表）
		List<MessageDto> messages = new ArrayList<>();
		messages.add(new MessageDto("user", text));
		params.put("messages", messages);
		// 设置流式返回
		params.put("stream", true);

		// 发起请求
		Flux<String> flux = deepSeekWebClient.post()
				.uri("/chat/completions")
				.bodyValue(params)
				.retrieve()
				.bodyToFlux(String.class);
		return flux;
	}
}
