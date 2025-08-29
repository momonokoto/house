package com.zpark.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
public class WebScketLoginDto {
    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;
    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    private String token;
}
