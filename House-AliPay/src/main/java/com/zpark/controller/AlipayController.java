package com.zpark.controller;

import com.alipay.api.AlipayApiException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.zpark.dto.OrderDto;
import com.zpark.service.IHouseService;
import com.zpark.service.IOrderService;
import com.zpark.service.IPayService;
import com.zpark.utils.ResponseOrder;
import com.zpark.utils.GetUserFromToken;
import com.zpark.utils.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/alipay")
@Slf4j
@Tag(name = "支付宝支付接口", description = "返回支付宝付款链接，前端直接跳转链接即可")
public class AlipayController {

	@Autowired
	private IPayService payService;

	@Autowired
	private GetUserFromToken getUserFromToken;

	@Autowired
	private IHouseService houseService;

	@Autowired
	private IOrderService orderService;

	@PostMapping("/create_order")
	@Operation(description = "创建订单, 返回一个支付链接，前端得到链接打开即可支付")
	@Parameter(name = "Authorization", description = "认证令牌", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string", example = "Bearer XXXXXXX"))
	public ResponseResult<ResponseOrder> createOrder(@RequestBody OrderDto order, HttpServletRequest request) throws AlipayApiException, JsonProcessingException {
		System.out.println("IPayService 类路径: " + IPayService.class.getProtectionDomain().getCodeSource().getLocation());
		return payService.createOrder2(order, request);
	}

	@PostMapping("/tradeQuery")
	@Operation(summary = "我已支付，查询订单")
	@Parameters({
			@Parameter(name = "orderNo", description = "订单编号，数据库对应order_no", required = true),
			@Parameter(name = "Authorization", description = "认证令牌", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string", example = "Bearer XXXXXXX"))
	})
	public ResponseResult<String> tradeQuery(String orderNo) throws AlipayApiException, JsonProcessingException {
		return payService.alipayTradeQuery(orderNo);
	}
}
