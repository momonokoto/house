package com.zpark.deepseek.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @PackageName: com.zpark.deepseek.util
 * @ClassName: ResponseUtils
 * @Description:
 * @author: RZS
 * @date: 2025/6/16  10:37
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseUtils<T> implements Serializable {
	private Integer code;
	private String msg;
	private T data;
}
