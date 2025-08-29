package com.zpark.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;


@Data
@Schema(name = "编辑个人资料", description = "编辑个人资料所需参数")
public class UserEditDto implements Serializable {
    @NotBlank(message = "昵称不能为空")
    @Size(min = 2, max = 20, message = "昵称长度必须在2到20个字符之间，前端可以做校验")
    @Schema(description = "用户昵称")
    private String userNickname;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "个人简介")
    @Size(max = 300, message = "个人简介长度必须小于300个字符，前端可以做校验")
    private String introduction;
}
