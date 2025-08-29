package com.zpark.dto;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Data
public class ReturnUser {
    private Long id;

    private String username;


    private String phone;


    private String userNickname;


    private String email;


    private String role;


    private Integer status;


    private Collection<? extends GrantedAuthority> authorities;

    private String introduction;

    private Integer real_name_status;

    public ReturnUser(Long id, String username, String phone, String userNickname, String email, String role, Integer status, Collection<? extends GrantedAuthority> authorities, String introduction,Integer real_name_status) {
        this.id = id;
        this.username = username;
        this.phone = phone;
        this.userNickname = userNickname;
        this.email = email;
        this.role = role;
        this.status = status;
        this.authorities = authorities;
        this.introduction = introduction;
        this.real_name_status = real_name_status;

    }

}
