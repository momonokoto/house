package com.zpark.deepseek.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zpark.deepseek.service.impl.AiHouseService;
import com.zpark.deepseek.util.QueryParser;
import com.zpark.entity.House;
import com.zpark.service.IHouseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@RestController
@RequestMapping("/deepSeek")
@Slf4j
@Tag(name = "Ai接口")
public class OpenAIController {

	@Value("${deepseek.apiKeys}")
	private String API_KEY;

	@Value("${deepseek.baseUrl}")
	private String API_URL;

	@Autowired
	private IHouseService houseService;

	@Autowired
	private AiHouseService aiHouseService;

	// 用于保存每个用户的对话历史
	//https://api.deepseek.com/chat/completions 此接口为无状态接口,需要上下文连贯对话需要将历史聊天记录一并发送至接口中
	// 用于存储会话历史记录的映射，键为会话标识，值为该会话的消息列表
	// 每条消息为一个映射，包含消息的各种详细信息
	private final Map<String, List<Map<String, String>>> sessionHistory = new ConcurrentHashMap<>();


	// 线程池，用于执行异步任务，提高系统并发处理能力
	private final ExecutorService executorService = Executors.newCachedThreadPool();
	// JSON序列化和反序列化工具，用于处理JSON数据格式的会话消息
	private final ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * 处理聊天页面请求
	 * @param modelAndView 模型和视图对象，用于渲染页面
	 * @return 返回渲染后的聊天页面ModelAndView对象
	 */
	@GetMapping("/ai")
	@Operation(summary = "ai聊天接口")
	public ModelAndView chat(ModelAndView modelAndView) {
	    // 设置视图名称为"chat"，对应聊天页面的模板文件
	    modelAndView.setViewName("chat");
	    return modelAndView;
	}

	private String getRoomTypeDesc(String roomType) {
		switch (roomType) {
			case "1": return "一居室";
			case "2": return "二居室";
			case "3": return "三居室";
			default: return "三室以上";
		}
	}

	private String getRentTypeDesc(String rentType) {
		switch (rentType) {
			case "1": return "合租";
			case "2": return "整租";
			case "3": return "公寓";
			default: return "未知";
		}
	}


	// 创建一个处理聊天请求的端点，使用Server-Sent Events (SSE) 实时流式传输回答
	@PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter chat(@RequestBody String question) throws JsonProcessingException {
		// 获取房源列表
		List<House> houseList = aiHouseService.searchByUserQuestion(question);
		List<String> descriptions = new ArrayList<>();
		for (House house : houseList) {
			String desc = String.format("【%s %s】月租 %.0f 元，%s，%s",
					house.getRegion(),
					house.getDetailedAddress(),
					house.getRent().doubleValue(),
					getRoomTypeDesc(house.getRoomType()),
					getRentTypeDesc(house.getRentType())
			);
			descriptions.add(desc);
		}



//		// 将房源转换为 JSON 字符串（可以使用 Jackson 或 Gson）
//		ObjectMapper mapper = new ObjectMapper();
//		String houseJson = mapper.writeValueAsString(houseList);


		// 构造系统提示语
		String systemPrompt = "你是一个租房平台的智能助手爱莉希雅。\n\n";

		if (descriptions.isEmpty()) {
			systemPrompt += "暂时没有找到符合条件的房源，请引导用户补充更具体的查询条件...";
		} else {
			systemPrompt += String.format("我刚刚为你筛选出了 %d 个符合要求的房源，请根据以下信息为用户推荐：\n\n", descriptions.size());
			for (int i = 0; i < descriptions.size(); i++) {
				systemPrompt += (i + 1) + ". " + descriptions.get(i) + "\n";
			}

			systemPrompt += "\n请从中选择最适合当前用户需求的 1~2 个房源，并说明推荐理由, 数据中没有的信息不要给用户说明。\n" +
					"如果用户没有提供明确偏好，可以询问他们是否需要更便宜、更大或更靠近地铁的房源。\n" +
					"鼓励用户提供更多细节以便更精准推荐。";
		}



		String userId = "123"; // 或者从 token 中解析出用户 ID

		// 创建一个 SseEmitter 对象，用于流式传输数据给客户端
		SseEmitter emitter = new SseEmitter(-1L);
		// 使用多线程处理每个聊天请求，以避免阻塞其他请求
		String finalSystemPrompt = systemPrompt;
		executorService.execute(() -> {
			try {
				log.info("流式回答开始, 问题: {}", question);
				// 获取当前用户的对话历史
				List<Map<String, String>> messages =
						sessionHistory.getOrDefault(userId, new ArrayList<>());

				// 添加用户的新问题到对话历史
				Map<String, String> userMessage = new HashMap<>();
				// 将用户的角色添加到消息中，以便于后续处理或记录
				userMessage.put("role", "user");
				// 将用户的问题添加到消息内容中，用于跟踪和处理用户请求
				userMessage.put("content", question);

				// 添加系统消息到对话历史，用于提供上下文或提示
				Map<String, String> systemMessage = new HashMap<>();
				// 将系统角色和内容添加到系统消息中
				systemMessage.put("role", "system");
				systemMessage.put("content", finalSystemPrompt);

				// 将用户消息和系统消息添加到消息列表中
				messages.add(userMessage);
				messages.add(systemMessage);

				// 调用 Deepseek API
				try (CloseableHttpClient client = HttpClients.createDefault()) {
					// 创建HttpPost请求对象，指定目标URL为API_URL
					HttpPost request = new HttpPost(API_URL + "/chat/completions");

					// 设置请求头，指定内容类型为application/json，表示请求体的格式为JSON
					request.setHeader("Content-Type", "application/json");

					// 设置请求头，添加Authorization字段，使用Bearer token认证方式，API_KEY为实际的密钥
					request.setHeader("Authorization", "Bearer " + API_KEY);
					// 准备 API 请求数据
					Map<String, Object> requestMap = new HashMap<>();
					// 设置请求使用的模型为"deepseek-chat"
					requestMap.put("model", "deepseek-ai/DeepSeek-V3");
					// 以下行被注释掉，如果需要使用"deepseek-reasoner"模型，可以取消注释
					// requestMap.put("model", "deepseek-reasoner");
					// 设置请求的消息列表，包含对话历史
					requestMap.put("messages", messages); // 包含对话历史
					// 设置请求为流式处理模式
					requestMap.put("stream", true);

					// 将请求数据序列化为 JSON
					// 将请求参数Map转换为JSON字符串
					String requestBody = objectMapper.writeValueAsString(requestMap);
					// 创建一个StringEntity对象，用于封装请求体，并指定字符编码为UTF-8
					request.setEntity(new StringEntity(requestBody, StandardCharsets.UTF_8));
					// 发送请求并处理响应
					try (CloseableHttpResponse response = client.execute(request);
					     BufferedReader reader = new BufferedReader(
							     new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8))) {
						StringBuilder aiResponse = new StringBuilder();
						String line;
						// 逐行读取响应数据
						while ((line = reader.readLine()) != null) {
							// 检查行是否以 "data: " 开头，这通常表示包含我们要处理的数据
							if (line.startsWith("data: ")) {
							    System.err.println(line);
							    // 提取实际的 JSON 数据，去除前导的 "data: " 部分
							    String jsonData = line.substring(6);
							    // 检查 JSON 数据是否表示处理已完成
							    if ("[DONE]".equals(jsonData)) {
							        break;
							    }
							    // 解析 JSON 数据并提取 AI 的回复内容
							    JsonNode node = objectMapper.readTree(jsonData);
							    String content = node.path("choices")
							            .path(0)
							            .path("delta")
							            .path("content")
							            .asText("");
							    // 如果提取的内容不为空，则发送给客户端
							    if (!content.isEmpty()) {
							        // 流式传输 AI 的回复内容给客户端
							        emitter.send(content);
							        // 收集 AI 的回复以便于后续处理或记录
							        aiResponse.append(content);
							    }
							}
						}

						// 将 AI 的回复添加到对话历史
						Map<String, String> aiMessage = new HashMap<>();
						// 将AI助手的角色和回复内容添加到消息列表中
						aiMessage.put("role", "assistant"); // 设置消息的角色为助手
						aiMessage.put("content", aiResponse.toString()); // 设置消息的内容为AI的回复
						messages.add(aiMessage); // 将AI的消息添加到消息列表中

						// 更新会话状态
						sessionHistory.put(userId, messages);

						log.info("流式回答结束, 问题: {}", question);
						// 完成此次 SSE 事件的发送
						emitter.complete();
					}
				} catch (Exception e) {
					log.error("处理 Deepseek 请求时发生错误", e);
					// 在发生错误时，向客户端发送错误信息并完成 SSE 事件
					emitter.completeWithError(e);
				}
			} catch (Exception e) {
				log.error("处理 Deepseek 请求时发生错误", e);
				// 在发生错误时，向客户端发送错误信息并完成 SSE 事件
				emitter.completeWithError(e);
			}
		});
		return emitter;
	}

}

