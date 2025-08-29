package com.zpark.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zpark.dto.RealNameAuthenticationRequestBodyDto;
import com.zpark.entity.User;
import com.zpark.entity.UserRealNameAuth;
import com.zpark.mapper.IUserRealNameAuthMapper;
import com.zpark.service.IRealNameAuthenticationService;
import com.zpark.service.IUserService;
import com.zpark.service.MessageProducerService;
import com.zpark.utils.GetUserFromToken;
import com.zpark.utils.HttpUtils;
import com.zpark.utils.ResponseResult;
import com.zpark.utils.StatusCode;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.HashMap;
import java.util.Map;


@Service
public class RealNameAuthenticationServiceImpl implements IRealNameAuthenticationService {
    @Autowired
    private IUserService userService;
    @Autowired
    private GetUserFromToken getUserFromToken;

    @Autowired
    private IUserRealNameAuthMapper userRealNameAuthMapper;

    @Resource
    private MessageProducerService messageProducerService;
    private static final int MAX_BINDINGS_PER_ID_CARD = 3; // 最大绑定数量
    @Override
    public ResponseResult<String> realNameAuthentication(RealNameAuthenticationRequestBodyDto requestBody, HttpServletRequest request) {
        String host = "https://zpc.market.alicloudapi.com";
        String path = "/efficient/cellphone/post";
        String method = "POST";
        String appcode = "98aff47a72304ffea31a83a11c6a6bdf";
        Map<String, String> headers = new HashMap<>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        //根据API的要求，定义相对应的Content-Type
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        Map<String, String> querys = new HashMap<>();
        Map<String, String> bodys = new HashMap<>();
        bodys.put("mobile", requestBody.getMobile());
        bodys.put("idCard", requestBody.getIdCard());
        bodys.put("realName", requestBody.getRealName());
        try {
            /**
             * 重要提示如下:
             * HttpUtils请从
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
             * 下载
             *
             * 相应的依赖请参照
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
             */
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            System.out.println(response.toString());
            String result = EntityUtils.toString(response.getEntity());
            //  解析结果为json
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(result);
            // 提取需要参数
            JsonNode refundResponse = rootNode.get("result");
            String  VerificationResult = refundResponse.get("VerificationResult").asText();
            //校验是否已经实名或者达到上限
            if (check(requestBody, request).equals("success")){
                //信息匹配成功，认证成功
                if(VerificationResult.equals("1")){
                    Long userId = getUserFromToken.getUser(request).getId();
                    UserRealNameAuth userRealNameAuth = new UserRealNameAuth();
                    userRealNameAuth.setUserId(userId);
                    userRealNameAuth.setRealName(requestBody.getRealName());
                    userRealNameAuth.setIdCardNumber(DigestUtil.sha256Hex(requestBody.getIdCard()));
                    userRealNameAuth.setPhoneNumber(requestBody.getMobile());
                    userRealNameAuth.setAuthStatus(1);

                    User user = new User();
                    user.setId(userId);
                    user.setRealNameStatus(1);
                    userService.updateById(user);
                    int row = userRealNameAuthMapper.insert(userRealNameAuth);
                    if(row > 0){
                        messageProducerService.sendPrivateMessage_System(getUserFromToken.getUser(request).getUsername(), "您以实名认证成功！");
                        return new ResponseResult<>(StatusCode.SUCCESS.value, "实名认证成功", null);
                    }else {
                        return new ResponseResult<>(StatusCode.UNKNOWN.value, "服务器内部错误，请稍后再试", null);
                    }
                    //  信息匹配失败，认证失败
                }else if(VerificationResult.equals("-1")){
                    return new ResponseResult<>(StatusCode.FAILURE.value, "信息匹配失败，认证失败", null);
                }
            }else if (check(requestBody, request).equals("ALREADY_VERIFIED")){
                return new ResponseResult<>(StatusCode.FAILURE.value, "该用户已实名认证", null);
            }else {
                return new ResponseResult<>(StatusCode.FAILURE.value, "该用户实名认证已达上限", null);
            }
            //获取response的body
            System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseResult<>(StatusCode.UNKNOWN.value, "服务器内部错误，请稍后再试", null);
    }

    //检查是否已经实名或者实名达到上线
    public String check(RealNameAuthenticationRequestBodyDto requestBody, HttpServletRequest request){
        User user  = getUserFromToken.getUser(request);
        LambdaQueryWrapper<UserRealNameAuth> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserRealNameAuth::getUserId, user.getId());
        queryWrapper.eq(UserRealNameAuth::getAuthStatus, 1);
        UserRealNameAuth findUserRealNameAuth = userRealNameAuthMapper.selectOne(queryWrapper);
        if(findUserRealNameAuth != null){
            return "ALREADY_VERIFIED";
        } else {
            LambdaQueryWrapper<UserRealNameAuth> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(UserRealNameAuth::getIdCardNumber, DigestUtil.sha256Hex(requestBody.getIdCard()));
            queryWrapper1.eq(UserRealNameAuth::getRealName, requestBody.getRealName());
            Long count = userRealNameAuthMapper.selectCount(queryWrapper1);
            if(count >= MAX_BINDINGS_PER_ID_CARD){
                return "LIMIT_EXCEEDED";
            }else {
                return "success";
            }
        }
    }
}
