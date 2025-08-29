package com.zpark.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zpark.dto.ChangePasswordConditionDto;
import com.zpark.dto.UserEditDto;
import com.zpark.entity.User;
import com.zpark.mapper.IUserEditMapper;
import com.zpark.service.IUserEditService;
import com.zpark.service.JwtUserDetailsService;
import com.zpark.utils.GetUserFromToken;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author XMC
 * @since 2025-04-28
 */
@Service
public class UserEditServiceImpl extends ServiceImpl<IUserEditMapper, User> implements IUserEditService {

    @Autowired
    private GetUserFromToken getUserFromToken;
    @Autowired
    private IUserEditMapper iUserEditMapper;
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @Override
    public boolean updateUser(UserEditDto userEditDto, HttpServletRequest request) {
        //token中获取用户
        User userFromToken = getUserFromToken.getUser(request);
        User user = new User();
        user.setId(userFromToken.getId());
        if (StringUtils.isNotEmpty(userEditDto.getUserNickname())){
            user.setUserNickname(userEditDto.getUserNickname());
        }
        if (StringUtils.isNotEmpty(userEditDto.getEmail())){
            user.setEmail(userEditDto.getEmail());
        }
        if (StringUtils.isNotEmpty(userEditDto.getPhone())){
            user.setPhone(userEditDto.getPhone());
        }
        if (StringUtils.isNotEmpty(userEditDto.getIntroduction())){
            user.setIntroduction(userEditDto.getIntroduction());
        }
        int row = iUserEditMapper.updateById(user);
        if (row < 0) return false;
//        清理缓存里的旧用户名
        jwtUserDetailsService.evictUserCache(userFromToken.getUsername());
        return true;
    }

    @Override
    public int changePassword(ChangePasswordConditionDto passwordCondition, HttpServletRequest request) {
        User userFromToken = getUserFromToken.getUser(request);
                //密码加密
        String encryptedPassword = DigestUtil.sha256Hex(passwordCondition.getOldPassword());
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getId, userFromToken.getId());
        lambdaQueryWrapper.eq(User::getPassword, encryptedPassword);
        User dbUser = iUserEditMapper.selectOne(lambdaQueryWrapper);
        if (dbUser == null){
            return 1001;
        }else {
            if (!passwordCondition.getNewPassword().equals(passwordCondition.getConfirmNewPassword())){
                return 1002;
            }
            if (passwordCondition.getNewPassword().equals(passwordCondition.getOldPassword())){
                return 1003;
            }
            dbUser.setId(userFromToken.getId());
            dbUser.setPassword(DigestUtil.sha256Hex(passwordCondition.getNewPassword()));
            int row = iUserEditMapper.updateById(dbUser);
            if (row < 1){
                return 1004;
            }
            //清理缓存里的旧密码
            jwtUserDetailsService.evictUserCache(userFromToken.getUsername());
            return 0;
        }
    }
}
