package com.zpark.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zpark.dto.ReSetPasswordDto;
import com.zpark.dto.SendEmailDto;
import com.zpark.entity.User;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xx
 * @since 2025-03-13
 */
public interface IUserService extends IService<User> {

     String password(String password);
     int selectuser(SendEmailDto sendEmailDto);
     String register(User user);

    Integer selectUserStatus(String username);

    User selectByUsername(String username);

    Boolean isEmailExist(String eamil);

    String selectUsernameByEmail(String email);

    String reSetPasswordByEmail(ReSetPasswordDto reSetPasswordDto);

     Long selectId(User user);

     String resendemail(SendEmailDto sendEmailDto);

     String repasswordemail(String email);

     Integer setUserStatus(String username,  Integer status);


}
