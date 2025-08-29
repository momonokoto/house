package com.zpark.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zpark.dto.ChangePasswordConditionDto;
import com.zpark.dto.UserEditDto;
import com.zpark.entity.User;
import jakarta.servlet.http.HttpServletRequest;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author XMC
 * @since 2025-04-28
 */
public interface IUserEditService extends IService<User> {
    boolean updateUser(UserEditDto userEditDto, HttpServletRequest request);
    int changePassword(ChangePasswordConditionDto user, HttpServletRequest request);
}
