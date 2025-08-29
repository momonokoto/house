package com.zpark.controller;

import com.zpark.dto.RealNameAuthenticationRequestBodyDto;
import com.zpark.service.impl.RealNameAuthenticationServiceImpl;
import com.zpark.utils.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController("/personal_center")
@Tag(name = "实名认证接口", description = "实名认证接口")
public class RealNameAuthenticationController {

    @Autowired
    private RealNameAuthenticationServiceImpl realNameAuthenticationServiceImpl;

    @Operation(summary = "实名认证", description = "实名认证")
    @PostMapping("/realNameAuthentication")
    public ResponseResult<String> realNameAuthentication(@RequestBody RealNameAuthenticationRequestBodyDto requestBody, HttpServletRequest request) {
        return realNameAuthenticationServiceImpl.realNameAuthentication(requestBody, request);
    }
}
