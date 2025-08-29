package com.zpark.dto;


import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(name = "预约信息", description = "预约信息")
public class AppointmentDto implements Serializable {
    /**
     * 预约时间
     */
    @TableField("date")
    @Schema(description = "预约时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "2023-05-01 12:00")
    // 指定 JSON 字符串的日期时间格式，以便Jackson正确解析
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime date;

    @TableField("message")
    @Schema(description = "预约留言", requiredMode = Schema.RequiredMode.REQUIRED, example = "你好明天下午有时间吗")
    private String message;

    @TableField("phone")
    @Schema(description = "预约人电话", requiredMode = Schema.RequiredMode.REQUIRED, example = "15692712421")
    private Long phone;

    @TableField("name")
    @Schema(description = "预约人姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "谢先生")
    private String name;
}
