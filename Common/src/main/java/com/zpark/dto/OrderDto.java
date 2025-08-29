package com.zpark.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(description = "订单实体")
@TableName("rental_orders")
public class OrderDto {
    @Schema(description = "房源ID", example = "21", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long propertyId;

    @Schema(description = "订单类型(1长租,2短租)", example = "1")
    private Integer orderType;

    @Schema(description = "选择租期开始日期", example = "2023-01-01")
    private LocalDate leaseStartDate;

    @Schema(description = "租期: 前端给用户选择1个月，3个月， 6,个月，12,个月", example = "2")
    private Integer leaseDuration;

}