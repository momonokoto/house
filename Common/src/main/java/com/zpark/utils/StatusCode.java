package com.zpark.utils;

import lombok.Getter;

/**
 * 响应状态码
 */

@Getter
public enum StatusCode {
	SUCCESS(200, "操作成功"),
	FAILURE(400, "操作失败"),
	UNKNOWN(500, "服务器未知错误"),
	ERROR(500, "服务器内部错误"),
	PARAM_ERROR(400, "参数错误"),
	USER_NOT_FOUND(1000, "用户未登录"),
	HOUSE_NOT_FOUND(1001, "房源不存在"),
	APPOINTMENT_ALREADY_EXISTS(1002, "已存在有效的预约"),
	SMS_SEND_FAILED(1003, "短信发送失败"),
	SYSTEM_ERROR(5000, "系统异常");

	public final Integer value;
	public final String message;

	StatusCode(Integer value, String message) {
		this.value = value;
		this.message = message;
	}
}
