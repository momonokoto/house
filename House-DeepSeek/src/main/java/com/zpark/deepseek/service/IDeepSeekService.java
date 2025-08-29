package com.zpark.deepseek.service;

import reactor.core.publisher.Flux;

/**
 * @PackageName: com.zpark.deepseek.service
 * @ClassName: IDeepSeekService
 * @Description:
 * @author: RZS
 * @date: 2025/6/16  9:57
 */
public interface IDeepSeekService {

	/**
	 * 聊天,DeepSeek对话（chat）
	 * @param text 用户输入
	 * @return 聊天结果
	 */
	String chat(String text);

	/**
	 * 聊天,DeepSeek对话（chat）,基于流处理
	 * @param text
	 * @return
	 */
	Flux<String> chatStream(String text);
}
