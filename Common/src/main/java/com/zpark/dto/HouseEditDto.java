package com.zpark.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Schema(name = "HouseEditDto", description = "修改房源信息")
public class HouseEditDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "标题", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "房屋地址", requiredMode = Schema.RequiredMode.REQUIRED)
    private String address;

    @Schema(description = "房屋面积", requiredMode = Schema.RequiredMode.REQUIRED)
    private Float area;

    @Schema(description = "房间数", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer roomNumber;

    @Schema(description = "户型", requiredMode = Schema.RequiredMode.REQUIRED)
    private String roomType;

    @Schema(description = "租金", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal rent;


    @Schema(description = "地区", requiredMode = Schema.RequiredMode.REQUIRED)
    private String region;

    @Schema(description = "房源描述", requiredMode = Schema.RequiredMode.REQUIRED)
    private String description;

    @Schema(description = "图片", requiredMode = Schema.RequiredMode.REQUIRED)
    private String img;

    @Schema(description = "视频", requiredMode = Schema.RequiredMode.REQUIRED)
    private String video;

    @Schema(description = "是否电梯", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer elevator;

    @Schema(description = "详细地址", requiredMode = Schema.RequiredMode.REQUIRED)
    private String detailedAddress;
}
