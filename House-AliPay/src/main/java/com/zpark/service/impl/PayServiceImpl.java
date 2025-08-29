package com.zpark.service.impl;


import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zpark.config.AlipayConfig;
import com.zpark.dto.OrderDto;
import com.zpark.entity.House;
import com.zpark.entity.Order;
import com.zpark.entity.User;
import com.zpark.mapper.IOrderMapper;
import com.zpark.service.IHouseService;
import com.zpark.service.IOrderService;
import com.zpark.service.IPayService;
import com.zpark.utils.ResponseOrder;
import com.zpark.utils.GetUserFromToken;
import com.zpark.utils.ResponseResult;
import com.zpark.utils.StatusCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


/**
 * 预下单接口实现类
 */
@Service
@Slf4j
// 预下单服务实现类
public class PayServiceImpl extends ServiceImpl<IOrderMapper, Order> implements IPayService {
	 @Autowired
	 private AlipayConfig  alipayConfig;

	 @Autowired
	 private IOrderService orderService;

	 @Autowired
	 private IHouseService houseService;

	 @Autowired
	 private GetUserFromToken getUserFromToken;

	@Override
	@Transactional
	//创建订单
	public ResponseResult<ResponseOrder> createOrder2(OrderDto orderDto, HttpServletRequest rq) throws AlipayApiException {
		// 初始化SDK
		AlipayClient alipayClient = alipayConfig.getAlipayClient();

		// 构造请求参数以调用接口
		AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
		request.setNotifyUrl( alipayConfig.getPayNotifyUrl());
		request.setReturnUrl(alipayConfig.getPayReturnUrl());
		AlipayTradePagePayModel model = new AlipayTradePagePayModel();

		House house = houseService.getById(orderDto.getPropertyId());

		User user = getUserFromToken.getUser(rq);

		// 设置商户订单号ORD+时间戳+4位随机数
		model.setOutTradeNo("ORD"+System.currentTimeMillis()+ RandomStringUtils.randomNumeric(4));


		// 设置订单总金额
		model.setTotalAmount(house.getRent().multiply(BigDecimal.valueOf(orderDto.getLeaseDuration())).toString());

		// 设置订单标题
		model.setSubject(house.getTitle());

		// 设置产品码
		model.setProductCode("FAST_INSTANT_TRADE_PAY");

		// 设置PC扫码支付的方式
		model.setQrPayMode("1");

		// 设置商户自定义二维码宽度
		model.setQrcodeWidth(100L);

		//设置订单超时时间为30分钟
		model.setTimeoutExpress("30m");

		request.setBizModel(model);
		// 第三方代调用模式下请设置app_auth_token
		// request.putOtherTextParam("app_auth_token", "<-- 请填写应用授权令牌 -->");

		//AlipayTradePagePayResponse response = alipayClient.pageExecute(request, "POST");
		// 如果需要返回GET请求，请使用
		AlipayTradePagePayResponse response = alipayClient.pageExecute(request, "GET");
		String link = response.getBody();
		ResponseOrder pageRedirectionData = new ResponseOrder();

		if (response.isSuccess()) {
			System.out.println("调用成功");
			System.out.println(pageRedirectionData);
			Order order = new Order();
			//避免前端更改订单号
			order.setOrderId(null);
			//订单标题
			order.setOrderTitle(model.getSubject());
			//房源ID
			order.setPropertyId(orderDto.getPropertyId());
			//订单类型
			order.setOrderType(orderDto.getOrderType());
			//租金
			order.setRentAmount(house.getRent());
			//租客ID
			order.setTenantId(user.getId());
			//房东id
			order.setLandlordId(house.getOwnerId());
			//计算总金额
			order.setTotalAmount(house.getRent().multiply(BigDecimal.valueOf(orderDto.getLeaseDuration())));
			order.setLeaseStartDate(orderDto.getLeaseStartDate());
			//计算结束时间
			order.setLeaseEndDate(orderDto.getLeaseStartDate().plusMonths(orderDto.getLeaseDuration()));
			//支付宝交易号
			order.setOrderNo(model.getOutTradeNo());

			//将创建的订单保存到数据库
			try {
				if (orderService.save(order)){
					//订单插入成功，返回支付宝支付链接
					pageRedirectionData.setLink(link);
					pageRedirectionData.setOrderNo(order.getOrderNo());
					return new ResponseResult<>(StatusCode.SUCCESS.value, "创建订单成功", pageRedirectionData);
				}
			}catch (Exception e){
				log.error("创建订单失败: " + e.getMessage());
				return new ResponseResult<>(StatusCode.FAILURE.value, "创建订单失败", null);
			}
		}
		System.out.println("调用失败");
		// sdk版本是"4.38.0.ALL"及以上,可以参考下面的示例获取诊断链接
		// String diagnosisUrl = DiagnosisUtils.getDiagnosisUrl(response);
		// System.out.println(diagnosisUrl);
		return new ResponseResult<>(StatusCode.FAILURE.value, "系统繁忙稍后再试", null);
	}

	//用户已经支付但是订单状态没有改变，点击我已支付进行查询
	public ResponseResult<String> alipayTradeQuery(String outTradeNo) throws JsonProcessingException, AlipayApiException {
		// 初始化SDK
		AlipayClient alipayClient = alipayConfig.getAlipayClient();

		// 构造请求参数以调用接口
		AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
		AlipayTradeQueryModel model = new AlipayTradeQueryModel();

		// 设置订单支付时传入的商户订单号
		model.setOutTradeNo(outTradeNo);

		// 从订单中获取支付宝交易号
		Order order = orderService.getOne(new LambdaQueryWrapper<Order>().eq(Order::getOrderNo, outTradeNo));
		if (order != null && order.getTradeNo() != null) {
			// 设置支付宝交易号
			model.setTradeNo(order.getTradeNo());
		}

		// 设置查询选项
		List<String> queryOptions = new ArrayList<>();
		queryOptions.add("trade_settle_info");
		model.setQueryOptions(queryOptions);

		request.setBizModel(model);
		// 第三方代调用模式下请设置app_auth_token
		// request.putOtherTextParam("app_auth_token", "<-- 请填写应用授权令牌 -->");

		AlipayTradeQueryResponse response = alipayClient.execute(request);
		System.out.println(response.getBody());

		if (response.isSuccess()) {
			System.out.println("调用成功");
			//  解析结果为json
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode rootNode = objectMapper.readTree(response.getBody());
			// 提取 alipay_trade_refund_response
			JsonNode refundResponse = rootNode.get("alipay_trade_query_response");
			String refundStatus = refundResponse.get("trade_status").asText();
			if (refundStatus.equals("TRADE_SUCCESS")) {
				// 查询成功,修改订单状态为支付成功
				orderService.userChangePaymentStatus(1, outTradeNo);
				return new ResponseResult<>(StatusCode.SUCCESS.value, "查询成功", null);
			} else if (refundStatus.equals("WAIT_BUYER_PAY")) {
				// 等待买家付款
				return new ResponseResult<>(StatusCode.SUCCESS.value, "等待买家付款", null);
			} else if (refundStatus.equals("TRADE_CLOSED")) {
				// 交易已关闭
				return new ResponseResult<>(StatusCode.SUCCESS.value, "交易已关闭", null);
			} else {
				return new ResponseResult<>(StatusCode.FAILURE.value, "查询失败,稍后再试", null);
			}
		} else {
			System.out.println("调用失败");
			// sdk版本是"4.38.0.ALL"及以上,可以参考下面的示例获取诊断链接
			// String diagnosisUrl = DiagnosisUtils.getDiagnosisUrl(response);
			// System.out.println(diagnosisUrl);
			return new ResponseResult<>(StatusCode.FAILURE.value, "查询失败，稍后再试", null);
		}
	}
}
