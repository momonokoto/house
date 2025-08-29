package com.zpark.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.resend.services.emails.model.CreateEmailOptions;
import com.zpark.dto.ReSetPasswordDto;
import com.zpark.dto.SendEmailDto;
import com.zpark.entity.User;
import com.zpark.mapper.UserMapper_Auth;
import com.zpark.service.IUserService;
import com.zpark.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xx
 * @since 2025-03-13
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper_Auth, User> implements IUserService {

    @Autowired
    private UserMapper_Auth userMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Value("${resend.apiKey}")
    private String apiKey;

    @Override
    public Long selectId(User user){
        return userMapper.selectId(user);
    }

    @Override
    public String password(String password) {
        return DigestUtil.sha256Hex(password);
    }

    @Override
    public Integer selectUserStatus(String username) {
        return userMapper.selectUserStatus(username);
    }

    @Override
    public int selectuser(SendEmailDto sendEmailDto) {
        if (userMapper.selectuser(sendEmailDto) != 0) {
            return 1;
        } else {
            return 0;
        }
    }


    @Override
    public User selectByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public Boolean isEmailExist(String email) {
        return userMapper.isEmailExist(email);
    }

    @Override
    public String selectUsernameByEmail(String email) {
        return userMapper.selectUsernameByEmail(email);
    }

    @Override
    public String reSetPasswordByEmail(ReSetPasswordDto reSetPasswordDto) {

        String value = (String) redisUtil.get("auth:repasswordcode:"+reSetPasswordDto.getEmail());
        if(!reSetPasswordDto.getCode().equals(value)){
            return "验证码错误";
        }
        reSetPasswordDto.setPassword(password(reSetPasswordDto.getPassword()));

        if(userMapper.reSetPasswordByEmail(reSetPasswordDto) != 0){
            redisUtil.delete("auth:repasswordcode:"+reSetPasswordDto.getEmail());
            return "重置密码成功";
        }else {
            return "重置密码失败";
        }
    }

    @Override
    public String register(User user) {

        String value = (String) redisUtil.get("auth:emailcode:"+user.getEmail());
        System.out.println("这里是emailcode"+value);
        if(!user.getCode().equals(value)){
            return "验证码错误";
        }

        user.setPassword(password(user.getPassword()));
        if (userMapper.register(user) != 0) {
                redisUtil.delete("auth:emailcode:"+user.getEmail());
            return "注册成功";
        } else {
            return "用户名或邮箱已存在";
        }

    }

    @Override
    public String repasswordemail(String email) {


        com.resend.Resend resend = new com.resend.Resend(apiKey);
        String code = String.valueOf((int)((Math.random()*9+1)* Math.pow(10,5)));

        redisUtil.setWithPrefix("auth:repasswordcode:",email,code,30, TimeUnit.MINUTES);

        CreateEmailOptions params = CreateEmailOptions.builder()
                .from("fufu@fufuking.top")
                .to(email)
                //.to("lihuijije545@gmail.com")
                .subject("HouseSysTem 验证码")
                .html("你正在尝试修改密码,验证码有效期为30分钟<br>"+
                        "<strong>"+code+"</strong>"+"<br/>"+"<strong>"+code+"</strong>"+"<br/>"+"<strong>"+code+"</strong>"+"<br/>"+
                        "若你正在尝试进行非法行为，就此收手，没人会受伤。<br/>"+"否则，<br/>"+
                        "我将，点燃星海"
                )
                .build();

        try {
            // 添加邮件发送操作
            resend.emails().send(params);
            return "发送成功";
        } catch (Exception e) {
            // 捕获并处理异常
            e.printStackTrace(); // 实际项目中应使用日志框架记录
            // 删除已存储的验证码
            redisUtil.delete("auth:repasswordcode:"+email);
            return "邮件发送失败，请稍后重试";
        }
    }



    @Override
    public String resendemail(SendEmailDto sendEmailDto) {

        if(selectuser(sendEmailDto)==1){
            return "用户名或邮箱已存在";
        }

        com.resend.Resend resend = new com.resend.Resend(apiKey);
        String code = String.valueOf((int)((Math.random()*9+1)* Math.pow(10,5)));
        
        redisUtil.setWithPrefix("auth:emailcode:",sendEmailDto.getEmail(),code,30, TimeUnit.MINUTES);
        
        CreateEmailOptions params = CreateEmailOptions.builder()
                .from("fufu@fufuking.top")
                .to(sendEmailDto.getEmail())
                //.to("lihuijije545@gmail.com")
                .subject("HouseSysTem 验证码")
                .html("这是你的验证码,有效期为30分钟<br>"+
                      "<strong>"+code+"</strong>"+"<br/>"+"<strong>"+code+"</strong>"+"<br/>"+"<strong>"+code+"</strong>"+"<br/>"+
                      "若你正在尝试进行非法行为，就此收手，没人会受伤。<br/>"+"否则，<br/>"+
                      "我将，点燃星海"
                     )
                .build();
        
        try {
            // 添加邮件发送操作
            resend.emails().send(params);
            return "发送成功";
        } catch (Exception e) {
            // 捕获并处理异常
            e.printStackTrace(); // 实际项目中应使用日志框架记录
            // 删除已存储的验证码
            redisUtil.delete("auth:emailcode:"+sendEmailDto.getEmail());
            return "邮件发送失败，请稍后重试";
        }
    }


    @Override
    public Integer setUserStatus(String username, Integer status) {
        return userMapper.setUserStatus(username,status);
    }




}