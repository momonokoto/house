package com.zpark.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AddContactDto {

    @Schema(description = "要添加的联系人用户名", requiredMode = Schema.RequiredMode.REQUIRED)
   private String contactUsername;
}
