package com.zpark.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zpark.dto.ReSetPasswordDto;
import com.zpark.dto.SendEmailDto;
import com.zpark.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xx
 * @since 2025-03-13
 */

@Mapper
public interface UserMapper_Auth extends BaseMapper<User> {

    @Select("SELECT COUNT(*) FROM user WHERE username = #{username} OR email = #{email}")
    int selectuser(SendEmailDto sendEmailDto);

    @Select("SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END " +
            "FROM user WHERE email = #{email}")
    Boolean isEmailExist(String email);

    @Update("UPDATE user SET password = #{password} WHERE email = #{email}")
    Integer reSetPasswordByEmail(ReSetPasswordDto reSetPasswordDto);

    @Select("SELECT username FROM user WHERE email = #{email} LIMIT 1")
    String selectUsernameByEmail(String email);

    @Select("SELECT * FROM user WHERE username = #{username} LIMIT 1")
    User selectByUsername(String username);

    @Select("SELECT status FROM user WHERE username = #{username}")
    Integer selectUserStatus(String username);

    @Insert("INSERT INTO user(id,username,password,email,role,status) " +
            "SELECT #{id},#{username},#{password},#{email},#{role},1 " +
            "WHERE NOT EXISTS (SELECT 1 FROM user WHERE username = #{username} OR email = #{email})")
    int register(User user);


    @Update("update user " +
            "set status = #{status} " +
            "where username = #{username} " +
            "and exists (select 1 from (select * from user) as tmp where username = #{username} and status in (0,1))")
    Integer setUserStatus(String username, Integer status);

    @Select("select id from user where username = #{username} limit 1")
    Long selectId(User user);


}
