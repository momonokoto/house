package com.zpark.service;

import com.alipay.api.AlipayApiException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.zpark.dto.OrderDto;
import com.zpark.utils.ResponseOrder;
import com.zpark.utils.ResponseResult;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 预下单业务层接口
 */
public interface IPayService {

	/**
	 * 创建订单，生成用户访问支付宝的跳转链接
	 * 模拟创建订单操作，订单参数固定写死
	 * @return 生成用户访问支付宝的跳转链接
	 */
	ResponseResult<ResponseOrder> createOrder2(OrderDto orderDto, HttpServletRequest request) throws AlipayApiException;


	ResponseResult<String> alipayTradeQuery(String outTradeNo) throws JsonProcessingException, AlipayApiException;



}
