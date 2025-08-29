package com.zpark.service;

import com.zpark.dto.RealNameAuthenticationRequestBodyDto;
import com.zpark.utils.ResponseResult;
import jakarta.servlet.http.HttpServletRequest;

//实名认证
public interface IRealNameAuthenticationService {
    public ResponseResult<String> realNameAuthentication(RealNameAuthenticationRequestBodyDto requestBody, HttpServletRequest request);
}
