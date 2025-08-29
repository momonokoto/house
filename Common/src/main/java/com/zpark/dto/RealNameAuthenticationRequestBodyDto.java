package com.zpark.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(name = "实名认证请求参数", description = "实名认证请求参数")
public class RealNameAuthenticationRequestBodyDto {
    @Schema(description = "姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "谢茂驰")
    private String realName;

    @Schema(description = "身份证号", requiredMode = Schema.RequiredMode.REQUIRED, example = "522225200212183215")
    private String idCard;

    @Schema(description = "手机号", requiredMode = Schema.RequiredMode.REQUIRED, example = "15692712421")
    private String mobile;

}
