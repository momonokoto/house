package com.zpark.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Schema(name = "房源查询条件对象", description = "房源查询条件")
public class HouseQueryConditionsDto implements Serializable {

//   地区
    @Schema(description = "地区", example = "北京")
    private String region;
//   详细地址
    @Schema(description = "详细地址")
    private String detailedAddress;
//   最小价格
    @Schema(description = "最小价格")
    private BigDecimal minPrice;
//   最大价格
    @Schema(description = "最大价格")
    private BigDecimal maxPrice;
//   最小面积
    @Schema(description = "最小面积")
    private Float minArea;
//    最大面积
    @Schema(description = "最大面积")
    private Float maxArea;

    @Schema(description = "户型户型：\n" +
            "'1' 一室，\n" +
            "'2' 二室，\n" +
            "'3' 三室，\n" +
            "'4' 三室以上，\n")
    private String roomType;

    @Schema(description = "租凭方式" +
            "'1'合租，'2'整租，'3'公寓")
    private String rentType;

}
