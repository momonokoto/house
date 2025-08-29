package com.zpark.dto;

import lombok.Data;

@Data
public class ReSetPasswordDto {

    private String email;

    private String code;

    private String password;

}
