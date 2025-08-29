package com.zpark.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zpark.entity.Order;
import jakarta.servlet.http.HttpServletRequest;

import java.math.BigInteger;
import java.util.List;

public interface IOrderService extends IService<Order> {
    boolean ChangePaymentStatus(Integer paymentStatus, String tradeNo, String orderNo);

    boolean userChangePaymentStatus(Integer paymentStatus, String tradeNo);

    boolean InsertTradeNo(String tradeNo, String orderNo);

    List<Order> getAll(HttpServletRequest request);
}
