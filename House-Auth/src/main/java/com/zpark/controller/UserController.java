package com.zpark.controller;


import com.zpark.dto.ReturnUser;
import com.zpark.entity.User;
import com.zpark.service.IUserService;
import com.zpark.utils.JwtUtil;
import com.zpark.utils.RedisUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author xx
 * @since 2025-03-13
 */
@RestController
@RequestMapping("/api/get")
@Tag(name = "获取登录用户信息", description = "获取登录用户信息")
public class UserController {


    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private IUserService userService;


    @GetMapping("/getUser")
    @Operation(
            summary = "获取当前登录用户信息",
            description = "通过Authorization头中的JWT令牌获取当前登录用户的详细信息",
            security = {},
            responses = {
                @ApiResponse(
                    responseCode = "200", 
                    description = "成功获取用户信息",
                    content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ReturnUser.class)
                    )
                )
            }
    )
    @Parameter(
            name = "Authorization",
            description = "认证令牌",
            required = true,
            in = ParameterIn.HEADER,
            schema = @Schema(type = "string", example = "Bearer xxx.yyy.zzz")
    )
    public ResponseEntity<?> getUser(
        @Parameter(
            name = "Authorization",
            description = "Bearer token认证信息",
            required = true,
            example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
            hidden = true
        )
        HttpServletRequest request) {
        String requestTokenHeader = request.getHeader("Authorization");
        String token = null;
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            token = requestTokenHeader.substring(7);
        }
        String username= jwtUtil.getUsernameFromToken(token);
        User user1=userService.selectByUsername(username);
        ReturnUser returnUser=new ReturnUser(user1.getId(),user1.getUsername(),user1.getPhone(),user1.getUserNickname(),user1.getEmail(),user1.getRole(),user1.getStatus(),user1.getAuthorities(),user1.getIntroduction(),user1.getRealNameStatus());
        return ResponseEntity.ok().body(returnUser);

    }


}
