package com.zpark.controller;


import com.zpark.dto.RentalRefundDto;
import com.zpark.service.IRentalRefundService;
import com.zpark.utils.ResponseResult;
import com.zpark.utils.StatusCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/alipay")
@Tag(name = "支付宝退款接口", description = "支付宝退款接口")
@Slf4j
public class AlipayTradeApiRefundController {

    @Autowired
    private IRentalRefundService rentalRefundService;
    @PostMapping("/refund")
    @Operation(description = "支付宝退款接口")
    @Parameters(value = {
            @Parameter(name = "Authorization", description = "认证令牌", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string", example = "Bearer 1234567"))
    })
    public ResponseResult<String> refund(@RequestBody RentalRefundDto refund) throws Exception {
        String result = rentalRefundService.refund(refund);
        if (result.equals("500")){
            return new ResponseResult<>(StatusCode.UNKNOWN.value, "服务器未知错误", null);
        }else {
            return new ResponseResult<>(StatusCode.SUCCESS.value, "退款成功", result);
        }
    }
}
