package com.zpark.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeFastpayRefundQueryModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.request.AlipayTradeFastpayRefundQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zpark.config.AlipayConfig;
import com.zpark.dto.RentalRefundDto;
import com.zpark.entity.Order;
import com.zpark.entity.RentalRefund;
import com.zpark.mapper.IRentalRefundMapper;
import com.zpark.service.IOrderService;
import com.zpark.service.IRentalRefundService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public class RentalRefundServiceImpl extends ServiceImpl<IRentalRefundMapper, RentalRefund> implements IRentalRefundService {
    @Autowired
    private AlipayConfig alipayConfig;

    @Autowired
    private IRentalRefundMapper rentalRefundMapper;

    @Autowired
    private IOrderService orderService;

    @Override
    @Transactional
    public String refund(RentalRefundDto refundDto) throws AlipayApiException, JsonProcessingException {
        //根据订单id获取订单信息
        Order order = orderService.getById(refundDto.getOrderId());
        if (order == null){
            throw new RuntimeException("订单不存在");
        }

        // 初始化SDK
        AlipayClient alipayClient = alipayConfig.getAlipayClient();

        // 构造请求参数以调用接口
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();

        AlipayTradeRefundModel model = new AlipayTradeRefundModel();

        // 设置商户订单号
        model.setOutTradeNo(order.getOrderNo());

        // 设置支付宝交易号
        model.setTradeNo(order.getTradeNo());

        // 设置退款金额
        model.setRefundAmount(order.getRentAmount().toString());

        // 设置退款原因说明
        model.setRefundReason(refundDto.getRefundReason());

        // 设置退款请求号
        model.setOutRequestNo("REF"+System.currentTimeMillis()+ RandomStringUtils.randomNumeric(4));

        // 设置查询选项
        List<String> queryOptions = new ArrayList<>();
        queryOptions.add("refund_detail_item_list");
        model.setQueryOptions(queryOptions);


        request.setBizModel(model);
        // 第三方代调用模式下请设置app_auth_token
        // request.putOtherTextParam("app_auth_token", "<-- 请填写应用授权令牌 -->");

        AlipayTradeRefundResponse response = alipayClient.execute(request);
        //  设置退款单参数并插入数据库
        RentalRefund refund = new RentalRefund();
        //避免前端更改退款id
        refund.setRefundId(null);
        //  设置退款单号
        refund.setRefundNo(model.getOutRequestNo());
        //  支付宝交易号
        refund.setTradeNo(model.getTradeNo());
        //  设置订单编号
        refund.setOrderNo(model.getOutTradeNo());
        //关联的订单id
        refund.setOrderId(refundDto.getOrderId());
        // 设置退款金额
        refund.setApplyAmount(refundDto.getApplyAmount());
        //  设置退款原因
        refund.setRefundReason(refundDto.getRefundReason());
        //  租客id
        refund.setTenantId(order.getTenantId());
        //  房东id
        refund.setLandlordId(order.getLandlordId());
        //  房源id
        refund.setPropertyId(order.getPropertyId());
        //  插入数据库
        int i = rentalRefundMapper.insert(refund);
        if (i > 0) log.info("退款单插入数据库成功");
        else log.info("退款单插入数据库失败");

        if (response.isSuccess()) {
            System.out.println("退款调用成功");
            //更改退款状态为成功退款
            if (changeRefundStatus(2, model.getTradeNo(), model.getOutTradeNo(), model.getOutRequestNo())){
                log.info("退款状态更改成功");
            }else {
                log.info("退款状态更改失败");
            }
            return response.getBody();
        } else {
            //如果调用失败，查询支付宝的接口确定退款结果
            String result = alipayTradeFastpayRefundQuery(refund);
            //  解析结果为json
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(result);
            // 提取 alipay_trade_refund_response
            JsonNode refundResponse = rootNode.get("alipay_trade_refund_response");
            String  refundStatus = refundResponse.get("refund_status").asText();
            //  判断退款状态
            if (refundStatus.equals("REFUND_SUCCESS")){
                //更改退款状态为成功退款
                if (changeRefundStatus(2, model.getTradeNo(), model.getOutTradeNo(), model.getOutRequestNo())){
                    log.info("退款状态更改成功");
                }else {
                    log.info("退款状态更改失败");
                }
                return "success";
            }
            return "500";
        }
    }

    //退款查询
    @Override
    @Transactional
    public String alipayTradeFastpayRefundQuery(RentalRefund refund) throws AlipayApiException {
        // 初始化SDK
        AlipayClient alipayClient = alipayConfig.getAlipayClient();

        // 构造请求参数以调用接口
        AlipayTradeFastpayRefundQueryRequest request = new AlipayTradeFastpayRefundQueryRequest();
        AlipayTradeFastpayRefundQueryModel model = new AlipayTradeFastpayRefundQueryModel();

        // 设置支付宝交易号
        model.setTradeNo(refund.getTradeNo());

        // 设置商户订单号
        model.setOutTradeNo(refund.getOrderNo());

        // 设置退款请求号
        model.setOutRequestNo(refund.getRefundNo());

        // 设置查询选项
        List<String> queryOptions = new ArrayList<>();
        queryOptions.add("refund_detail_item_list");
        model.setQueryOptions(queryOptions);

        request.setBizModel(model);
        // 第三方代调用模式下请设置app_auth_token
        // request.putOtherTextParam("app_auth_token", "<-- 请填写应用授权令牌 -->");

        AlipayTradeFastpayRefundQueryResponse response = alipayClient.execute(request);
        System.out.println(response.getBody());

        if (response.isSuccess()) {
            System.out.println("调用成功："+ response.getBody());
            return response.getBody();
        } else {
            System.out.println("调用失败");
            return "500";
        }
    }

    @Override
    @Transactional
    //更改退款状态
    public boolean changeRefundStatus(Integer refundStatus, String tradeNo, String orderNo, String refundNo) {
        LambdaQueryWrapper<RentalRefund> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RentalRefund::getRefundNo, refundNo);
        queryWrapper.eq(RentalRefund::getTradeNo, tradeNo);
        queryWrapper.eq(RentalRefund::getOrderNo, orderNo);
        RentalRefund newRefund = new RentalRefund();
        newRefund.setRefundStatus(2);
        return rentalRefundMapper.update(newRefund, queryWrapper) > 0;
    }
}

