package com.zpark.deepseek.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * @PackageName: com.zpark.deepseek.dto
 * @ClassName: MessageDto
 * @Description:
 * @author: RZS
 * @date: 2025/6/16  10:01
 */
@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDto implements Serializable {
	private String role; // 角色
	private String content; // 消息的内容, 请求对话的内容
}
