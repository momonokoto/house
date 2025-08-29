package com.zpark.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.Collection;

/**
 * <p>
 * 用户实体类
 * </p>
 *
 * @author fufuking
 * @since 2025-04-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user")
@Schema(name = "User", description = "用户实体")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "手机号", minLength = 11, maxLength = 11)
    @Size(min = 11, max = 11, message = "手机号长度必须为11位，前端可以做校验")
    private String phone;

    @Schema(description = "支付宝账号")
    @TableField("alipay_id")
    private String alipayId;

    @Schema(description = "用户昵称", minLength = 2, maxLength = 20)
    @Size(min = 2, max = 20, message = "用户昵称长度必须在2到20个字符之间，前端可以做校验")
    private String userNickname;


    @Schema(description = "头像")
    @TableField("avatar")
    private String avatar;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "角色")
    private String role;

    @Schema(description = "状态")
    private Integer status;

    @TableField(exist = false)
    @Schema(description = "用户权限集合", hidden = true)
    private Collection<? extends GrantedAuthority> authorities;

    @TableField(exist = false)
    @Schema(description = "验证码", hidden = true)
    private String code;

    @Schema(description = "个人简介", maxLength = 300)
    @TableField("introduction")
    @Size(min = 0, max = 300, message = "个人简介长度必须小于300个字符，前端可以做校验")
    private String introduction;

    @Schema(description = "实名状态, 0未实名，1已经实名")
    @TableField("real_name_status")
    private Integer realNameStatus;

}