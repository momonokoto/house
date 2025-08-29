package com.zpark.service.impl;

import com.zpark.service.ICallBackService;
import com.zpark.service.IOrderService;
import com.zpark.service.IUserService;
import com.zpark.util.SignVerified;
import com.zpark.utils.GetUserFromToken;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CallBackServiceImpl implements ICallBackService {

    public static String PAY_NOTIFY_RESULT = "";

    @Autowired
    private SignVerified signVerified;

    @Autowired
    private IOrderService orderService;

//    @Autowired
//    private IAlipayTradeOrderSettle alipayTradeOrderSettle;

    @Autowired
    private GetUserFromToken getUserFromToken;

    @Autowired
    private IUserService userService;
    @Override
    public String payNotify(HttpServletRequest request) throws Exception {
        try {
            log.info("开始执行异步调用");
            /* 请根据商户自己项目逻辑处理，并返回success或者fail。*/
            if (signVerified.mysignVerified(request)) {//验证成功
                //获取支付宝的通知返回参数，可参考技术文档中服务器异步通知参数列表，如out_trade_no
                String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
                String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");
                //验签成功，更改支付状态为已支付: 0未支付，1已支付
                if (orderService.ChangePaymentStatus(1, out_trade_no, trade_no)) {
                    orderService.InsertTradeNo(trade_no, out_trade_no);
                    PAY_NOTIFY_RESULT = "success";
//                    //订单结算分账给平台服务费
//                    LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
//                    queryWrapper.eq(Order::getOrderNo, out_trade_no);
//                    queryWrapper.eq(Order::getTradeNo, trade_no);
//                    Order order = orderService.getOne(queryWrapper);
//                    Long userId = order.getTenantId();
//                    User user = userService.getById(userId);
//                    String result = alipayTradeOrderSettle.alipayTradeOrderSettle(out_trade_no, trade_no, user.getAlipayId());
//                    ObjectMapper objectMapper = new ObjectMapper();
//                    // 解析为Map<String, Object>
//                    Map<String, Object> jsonMap = objectMapper.readValue(result, Map.class);
//                    // 获取嵌套对象
//                    Map<String, Object> responseMap = (Map<String, Object>) jsonMap.get("alipay_trade_order_settle_response");
                    // 获取msg字段
//                    String msg = (String) responseMap.get("msg");
//                    System.out.println("responseMap的值: " + responseMap);
//                    System.out.println("msg的值: " + msg); // 输出: Success
                    log.info("结束执行异步调用");
                    return "success";
                }
                log.info("结束执行异步调用");
                PAY_NOTIFY_RESULT = "fail";
                return "fail";
            } else {//验证失败
                log.info("结束执行异步调用");
                PAY_NOTIFY_RESULT = "fail";
                return "fail";
            }
        }catch (Exception e){
            System.out.println(e);
            System.out.println( e.getMessage());
            return e.getMessage();
        }
    }

    @Override
    public String paySync(HttpServletRequest request) throws Exception {
        log.info("开始执行同步调用");
        if(signVerified.mysignVerified(request)){
            log.info("结束执行同步调用");
            return "success";
        }else {
            log.info("结束执行同步调用");
            return "fail";
        }
    }
}
