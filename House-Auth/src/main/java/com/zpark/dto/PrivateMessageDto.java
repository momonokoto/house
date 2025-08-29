package com.zpark.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PrivateMessageDto {

    @Schema(description = "接收者用户名", requiredMode = Schema.RequiredMode.REQUIRED)
    private String receiver;

    @Schema(description = "消息", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;
}
