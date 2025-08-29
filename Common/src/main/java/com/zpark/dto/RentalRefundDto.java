package com.zpark.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "租房退款请求参数")
@Slf4j
public class RentalRefundDto {

    @Schema(description = "关联订单ID，需要退款的订单id", example = "1932450603288416258")
    private String orderId;

    @Schema(description = "申请退款金额，不能大于订单的交易金额", example = "5000.00")
    private BigDecimal applyAmount;

    @Schema(description = "退款原因", example = "提前退租", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String refundReason;
}