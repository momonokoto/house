package com.zpark.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 房源信息表
 * </p>
 *
 * @author fufuking
 * @since 2025-05-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("house")
@Schema(name = "House", description = "房源信息表")
public class House implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "房源 ID")
    @TableId(value = "house_id", type = IdType.AUTO)
    private Integer houseId;

    @Schema(description = "房东 ID")
    @TableField("owner_id")
    private Long ownerId;

    @Schema(description = "标题", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "房屋地址", requiredMode = Schema.RequiredMode.REQUIRED)
    private String address;

    @Schema(description = "房屋面积", requiredMode = Schema.RequiredMode.REQUIRED)
    private Float area;

    @Schema(description = "房间数", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer roomNumber;

    @Schema(description = "户型户型：\n" +
            "'1' 一室，\n" +
            "'2' 二室，\n" +
            "'3' 三室，\n" +
            "'4' 三室以上，\n", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("room_type")
    private String roomType;

    @Schema(description = "租凭方式" +
            "'1'合租，'2'整租，'3'公寓", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("rent_type")
    private String rentType;

    @Schema(description = "租金", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal rent;

    @Schema(description = "房屋的纬度坐标，用于地图定位")
    private Float latitude;

    @Schema(description = "房屋的经度坐标，用于地图定位")
    private Float longitude;

    @Schema(description = "房屋状态（0-待租，1-已租, 2-已下架）", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer status;

    @Schema(description = "地区", requiredMode = Schema.RequiredMode.REQUIRED)
    private String region;

    @Schema(description = "房源描述", requiredMode = Schema.RequiredMode.REQUIRED)
    private String description;

    @Schema(description = "图片")
    private String img;

    @Schema(description = "视频")
    private String video;

    @Schema(description = "是否电梯", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer elevator;

    @Schema(description = "详细地址", requiredMode = Schema.RequiredMode.REQUIRED)
    private String detailedAddress;

    @Schema(description = "审核状态 （0-待审核 1-已审核）")
    @TableField(value = "verify", insertStrategy = FieldStrategy.DEFAULT)
    private Integer verify = 0;

    @Schema(description = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @Schema(description = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @Schema(description = "是否被删除1已删除0未删除", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField(value = "is_deleted")
    private byte isDeleted;
}