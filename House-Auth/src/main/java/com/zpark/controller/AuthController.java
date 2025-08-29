package com.zpark.controller;

import com.zpark.dto.*;
import com.zpark.entity.JwtResponse;
import com.zpark.entity.Message;
import com.zpark.entity.User;
import com.zpark.mapper.UserMapper_Auth;
import com.zpark.service.IUserService;
import com.zpark.service.JwtUserDetailsService;
import com.zpark.service.MessageService;
import com.zpark.service.UserQueueService;
import com.zpark.utils.JwtUtil;
import com.zpark.utils.RedisUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/user")
@Tag(name = "认证处理中心接口", description = "认证处理中心接口")
public class AuthController {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private IUserService userService;

    @Autowired
    private UserMapper_Auth userMapper_Auth;

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserQueueService userQueueService;

    @Autowired
    private  MessageService messageService;

    Logger logger = LoggerFactory.getLogger(AuthController.class);

    //发送验证码 传入username，email
    @PostMapping("/resendemail")
    @Operation(summary = "发送验证码", description = "向用户邮箱发送新的验证码（用于注册）")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "发送成功", 
            content = @Content(schema = @Schema(example = "{\"message\":\"发送成功\"}"))),
        @ApiResponse(responseCode = "400", description = "发送失败", 
            content = @Content(schema = @Schema(example = "{\"error\":\"邮箱无效\", \"solution\":\"请检查邮箱格式\"}"))),
        @ApiResponse(responseCode = "500", description = "服务端异常", 
            content = @Content(schema = @Schema(example = "{\"error\":\"邮件服务不可用\", \"documentation\":\"/api-docs\"}")))
    })
    public ResponseEntity<?> resendemail(@RequestBody  @Valid SendEmailDto sendemailDto){
        String temp=userService.resendemail(sendemailDto);
        if(temp.equals("发送成功")){
            // 修改为结构化响应
            return ResponseEntity.ok(Map.of("message", "发送成功"));
        }else if(temp.equals("用户名或邮箱已存在")){
            // 修改为结构化错误响应
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "用户名或邮箱已存在", 
                             "solution", "请使用其他用户名或邮箱"));
        }else{
            // 修改为结构化错误响应
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "发送失败"));
        }
    }

    //注册 传入username，password，email，emailcode
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "通过邮箱验证码完成用户注册")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "注册成功", 
            content = @Content(schema = @Schema(example = "{\"message\":\"注册成功\"}"))),
        @ApiResponse(responseCode = "400", description = "注册失败", 
            content = @Content(schema = @Schema(example = "{\"error\":\"验证码错误\", \"solution\":\"请检查邮箱中的验证码\"}"))),
        @ApiResponse(responseCode = "409", description = "用户已存在", 
            content = @Content(schema = @Schema(example = "{\"error\":\"用户名或邮箱已被注册\"}"))),
        @ApiResponse(responseCode = "500", description = "服务端异常", 
            content = @Content(schema = @Schema(example = "{\"error\":\"数据库操作失败\"}")))
    })
    public ResponseEntity<?> register(@RequestBody @Valid RegisterDto registerDto){
        User user=new User();
        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        user.setPassword(registerDto.getPassword());
        user.setCode(registerDto.getCode());
        user.setRole("USER");
        System.out.println(user);
        String x=userService.register(user);
        if(x.equals("注册成功")){
            // 修改为结构化响应
            return ResponseEntity.ok(Map.of("message", "注册成功"));
        }else if(x.equals("用户名或邮箱已存在")){
            // 处理409冲突状态
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", "用户名或邮箱已被注册"));
        }else if(x.equals("验证码错误")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "验证码错误",
                             "solution", "请检查邮箱中的验证码"));
        }
        else {
            // 修改为结构化错误响应
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "注册失败"));
        }
    }

    @PostMapping("/repasswordemail")
    @Operation(summary = "重置密码邮件", description = "发送重置密码验证码邮件")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "发送成功",
            content = @Content(schema = @Schema(example = "{\"message\":\"发送成功\"}"))),
        @ApiResponse(responseCode = "400", description = "发送失败",
            content = @Content(schema = @Schema(example = "{\"error\":\"邮箱不存在\", \"solution\":\"请检查邮箱是否注册\"}"))),
    })
    public ResponseEntity<?> resetPasswordemail(@RequestParam String email){
        if(!userService.isEmailExist(email)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("邮箱不存在,请检查邮箱是否注册");
        }
        String temp=userService.repasswordemail(email);
        if(temp.equals("发送成功")){
            return ResponseEntity.ok().body(temp);
        }else{
            return ResponseEntity.badRequest().body(temp);
        }

    }






    @PostMapping("/repassword")
    @Operation(summary = "通过邮箱验证码重置密码", description = "通过邮箱验证码重置密码")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "重置成功",
                    content = @Content(schema = @Schema(example = "{\"message\":\"重置成功\"}"))),
            @ApiResponse(responseCode = "400", description = "重置失败",
                    content = @Content(schema = @Schema(example = "{\"error\":\"验证码错误\", \"solution\":\"请检查邮箱中的验证码\"}"))),
            @ApiResponse(responseCode = "500", description = "服务端异常",
                    content = @Content(schema = @Schema(example = "{\"error\":\"获取用户名失败\"}"))),
    })
    public ResponseEntity<?> repasswordByEmail(@RequestBody ReSetPasswordDto reSetPasswordDto){
        String temp = userService.reSetPasswordByEmail(reSetPasswordDto);
        if(temp.equals("重置密码成功")){
            try {
                String username=userService.selectUsernameByEmail(reSetPasswordDto.getEmail());
                logger.info("重置密码成功，用户名："+username);
                jwtUserDetailsService.evictUserCache(username);
                return ResponseEntity.ok().body(Map.of("message", "重置成功"));
            } catch (Exception e) {
                logger.error("获取用户名失败，邮箱：" + reSetPasswordDto.getEmail(), e);
                return ResponseEntity.ok().body(Map.of(
                    "message", "密码重置成功",
                    "warning", "用户缓存清除失败"
                ));
            }
        }else if(temp.equals("验证码错误")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "验证码错误", "solution", "请检查邮箱中的验证码"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", temp));
        }
    }

    // 通用用户登录
    @PostMapping("/authenticate")
    @Operation(summary = "用户登录", description = "通用用户登录接口，返回JWT令牌")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "登录成功", 
            content = @Content(schema = @Schema(implementation = JwtResponse.class))),
        @ApiResponse(responseCode = "400", description = "请求参数错误", 
            content = @Content(schema = @Schema(example = "{\"error\":\"缺少必要参数\"}"))),
        @ApiResponse(responseCode = "401", description = "认证失败", 
            content = @Content(schema = @Schema(example = "{\"error\":\"用户名或密码错误\"}"))),
        @ApiResponse(responseCode = "403", description = "权限不足", 
            content = @Content(schema = @Schema(example = "{\"error\":\"账户已被禁用\"}"))),
        @ApiResponse(responseCode = "500", description = "服务端异常", 
            content = @Content(schema = @Schema(example = "{\"error\":\"认证服务不可用\"}")))
    })
    public ResponseEntity<?> createAuthenticationToken(@RequestBody @Valid LoginDto loginDto) {
        User user=new User();
        user.setUsername(loginDto.getUsername());
        user.setPassword(loginDto.getPassword());
        return processLogin(user, false);
    }




    // 管理员专用登录
    @PostMapping("/admin/authenticate")
    @Operation(summary = "管理员登录", description = "仅限管理员账号登录", security = {})
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "登录成功",
            content = @Content(schema = @Schema(implementation = JwtResponse.class))),
        @ApiResponse(responseCode = "401", description = "认证失败",
            content = @Content(schema = @Schema(example = "{\"error\":\"管理员凭证无效\"}"))),
        @ApiResponse(responseCode = "403", description = "权限不足",
            content = @Content(schema = @Schema(example = "{\"error\":\"非管理员账号\"}"))),
        @ApiResponse(responseCode = "500", description = "服务端异常",
            content = @Content(schema = @Schema(example = "{\"error\":\"管理员认证服务异常\"}")))
    })
    public ResponseEntity<?> createAdminAuthenticationToken(@RequestBody @Valid LoginDto loginDto) {

        User user=new User();
        user.setUsername(loginDto.getUsername());
        user.setPassword(loginDto.getPassword());
        return processLogin(user, true);
    }


    @PostMapping("/websocket/authbypassword")
    @Operation(summary = "用户登录", description = "通用用户登录接口，返回JWT令牌")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "登录成功",
                    content = @Content(schema = @Schema(implementation = JwtResponse.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误",
                    content = @Content(schema = @Schema(example = "{\"error\":\"缺少必要参数\"}"))),
            @ApiResponse(responseCode = "401", description = "认证失败",
                    content = @Content(schema = @Schema(example = "{\"error\":\"用户名或密码错误\"}"))),
            @ApiResponse(responseCode = "403", description = "权限不足",
                    content = @Content(schema = @Schema(example = "{\"error\":\"账户已被禁用\"}"))),
            @ApiResponse(responseCode = "500", description = "服务端异常",
                    content = @Content(schema = @Schema(example = "{\"error\":\"认证服务不可用\"}")))
    })
    public ResponseEntity<?> createWebsocketToken(@RequestBody @Valid WebScketLoginDto loginDto) {
        try {
            System.out.println("loginDto: " + loginDto);
            if(loginDto.getUsername()!=null&&loginDto.getPassword()!=null){
            authenticate(loginDto.getUsername(), loginDto.getPassword());
            }
            else {
                if (loginDto.getToken()!=null){
                    System.out.println("token存在");
                    String username = jwtUtil.getUsernameFromToken(loginDto.getToken());
                    loginDto.setUsername( username);
                    System.out.println("username: " + username);
                    String redisToken = (String)redisUtil.get("auth:accesstoken:"+ username);
                    String shatoken = DigestUtils.sha256Hex(loginDto.getToken());
                    System.out.println(shatoken.equals(redisToken));
                    if(!shatoken.equals(redisToken)){
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(Map.of("error", "认证失败: "));
                    }
                }
            }
            User user = userMapper_Auth.selectByUsername(loginDto.getUsername());

            final String webSocketToken = jwtUtil.createWebSocketToken(loginDto.getUsername(),user);
            boolean accessTokenStored = redisUtil.setWithPrefix("auth:websocket:accesstoken:",
                    loginDto.getUsername(), DigestUtils.sha256Hex(webSocketToken), 30, TimeUnit.MINUTES);
            if (!accessTokenStored) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to store token");
            }
            // 创建用户队列
            userQueueService.createUserQueue(loginDto.getUsername());
            userQueueService.registerUserQueueListener(loginDto.getUsername());
            // 7. 获取最近联系人消息
            Map<String, List<Message>> recentMessages = messageService.getRecentContactsMessages(loginDto.getUsername(), 10);


            // 创建统一响应对象
            Map<String, Object> response = new LinkedHashMap<>();

            response.put("accessToken", webSocketToken);
            response.put("recentMessages", recentMessages);



            return ResponseEntity.ok()
                    .body(response);

        }catch (BadCredentialsException e) {
            // 修改为结构化错误响应
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "用户名或密码错误"));
        } catch (Exception e) {
            // 修改为结构化错误响应
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "认证失败: " + e.getMessage()));
        }



    }

    // 统一的登录处理逻辑
    private ResponseEntity<?> processLogin(User user, boolean isAdminLogin) {
        try {

            if(userService.selectUserStatus(user.getUsername())==0){
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("用户已被冻结");
            }

            // 1. 认证用户名密码
            authenticate(user.getUsername(), user.getPassword());

            // 2. 获取完整用户信息
            user.setId(userService.selectId(user));
            UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(user.getUsername());

            user.setAuthorities(userDetails.getAuthorities());



            // 3. 如果是管理员登录，检查角色
            if (isAdminLogin && !hasAdminRole(userDetails)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Access denied: Administrator privileges required");
            }

            if(hasAdminRole(userDetails)){
                user.setRole("ADMIN");
            }else {
                user.setRole("USER");
            }



            // 4. 生成令牌
            final String accessToken = jwtUtil.createAccessToken(user);
            final String refreshToken = jwtUtil.createRefreshToken(user);

            // 5. 存储令牌到Redis
            if (!storeTokens(user.getUsername(), accessToken, refreshToken)) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to store token");
            }

            // 6. 更新用户活跃状态
            redisUtil.setttl(user.getUsername(), 3600);

            // 创建用户队列
            userQueueService.createUserQueue(user.getUsername());
            userQueueService.registerUserQueueListener(user.getUsername());

            // 7. 获取最近联系人消息
            Map<String, List<Message>> recentMessages = messageService.getRecentContactsMessages(user.getUsername(), 10);

            // 8. 构建响应
            ResponseCookie refreshTokenCookie = buildRefreshTokenCookie(refreshToken);

            User user1=userService.selectByUsername(user.getUsername());
            ReturnUser returnUser=new ReturnUser(user1.getId(),user1.getUsername(),user1.getPhone(),user1.getUserNickname(),user1.getEmail(),user1.getRole(),user1.getStatus(),user1.getAuthorities(),user1.getIntroduction(),user1.getRealNameStatus());

            // 创建统一响应对象
            Map<String, Object> response = new LinkedHashMap<>();

            response.put("accessToken", accessToken);
            response.put("User", returnUser);
            response.put("recentMessages", recentMessages);
            
            // 管理员额外信息
            if (isAdminLogin) {
                response.put("role", "ADMIN");
                response.put("message", "Admin login successful");
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                    .body(response);

        } catch (BadCredentialsException e) {
            // 修改为结构化错误响应
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "用户名或密码错误"));
        } catch (Exception e) {
            // 修改为结构化错误响应
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "认证失败: " + e.getMessage()));
        }
    }





    @PostMapping("/refresh-token")
    @Operation(summary = "刷新访问令牌", description = "通过有效的Refresh Token获取新的Access Token")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "刷新成功", 
            content = @Content(schema = @Schema(implementation = JwtResponse.class))),
        @ApiResponse(responseCode = "400", description = "请求参数错误", 
            content = @Content(schema = @Schema(example = "{\"error\":\"缺少刷新令牌\", \"solution\":\"请先登录或检查Cookie设置\", \"documentation\":\"/api-docs#/认证处理中心接口/refreshToken\"}"))),
        @ApiResponse(responseCode = "401", description = "令牌无效", 
            content = @Content(schema = @Schema(example = "{\"error\":\"无效的刷新令牌\", \"solution\":\"请重新登录获取新的刷新令牌\", \"documentation\":\"/api-docs#/认证处理中心接口/refreshToken\"}"))),
        @ApiResponse(responseCode = "500", description = "服务端异常", 
            content = @Content(schema = @Schema(example = "{\"error\":\"令牌刷新服务异常\"}"))),
    })
    public ResponseEntity<?> refreshToken(
        @Parameter(description = "刷新令牌（从Cookie自动携带）", required = true) 
        @CookieValue(name = "refresh_token", required = false) String refreshToken) {
        
        // 1. 检查Refresh Token是否存在
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                    "error", "缺少刷新令牌",
                    "solution", "请检查浏览器Cookie设置是否允许第三方Cookie，或重新登录",
                    "documentation", "/api-docs#/认证处理中心接口/refreshToken"
                ));
        }

        // 2. 验证Refresh Token有效性
        if (!jwtUtil.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                    "error", "无效的刷新令牌",
                    "solution", "请重新登录获取新的刷新令牌",
                    "documentation", "/api-docs#/认证处理中心接口/refreshToken"
                ));
        }

        try {

            String username = jwtUtil.getUsernameFromToken(refreshToken);

            // 2. 检查Redis中的Refresh Token是否匹配
            String storedRefresh = (String) redisUtil.get("auth:refreshtoken:" + username);

            if (!refreshToken.equals(storedRefresh)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("刷新令牌已失效");
            }

            // 3. 生成新的Access Token
            User user = new User();
            user.setUsername(username);
            user.setId(userService.selectId(user)); // 重新查询用户ID
            UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(user.getUsername());
            user.setAuthorities(userDetails.getAuthorities());
            if(hasAdminRole(userDetails)){
                user.setRole("ADMIN");
            }else {
                user.setRole("USER");
            }
            String newAccessToken = jwtUtil.createAccessToken(user);

            // 4. 更新Redis中的Access Token
            redisUtil.setWithPrefix("auth:accesstoken:", username, DigestUtils.sha256Hex(newAccessToken), 30, TimeUnit.MINUTES);

            return ResponseEntity.ok(new JwtResponse(newAccessToken));
        } catch (Exception e) {
            // 修改为结构化错误响应
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "令牌刷新失败: " + e.getMessage()));
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "使当前用户的Token失效")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "登出成功", 
            content = @Content(schema = @Schema(example = "{\"message\":\"Logout successful\", \"timestamp\": \"2023-10-01T12:00:00Z\"}"))),
        @ApiResponse(responseCode = "400", description = "请求参数错误", 
            content = @Content(schema = @Schema(example = "{\"error\":\"缺少刷新令牌\"}"))),
        @ApiResponse(responseCode = "401", description = "令牌无效", 
            content = @Content(schema = @Schema(example = "{\"error\":\"无效的访问令牌\"}"))),
        @ApiResponse(responseCode = "500", description = "服务端异常", 
            content = @Content(schema = @Schema(example = "{\"message\":\"Logout failed\"}")))
    })
    public ResponseEntity<?> logout(
            @Parameter(description = "退出登录")
            @CookieValue(value = "refresh_token") String refreshToken,
            HttpServletRequest request) {

        try {
            // 1. 从请求头获取access token（如果有）
            String authHeader = request.getHeader("Authorization");
            String accessToken = null;
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                accessToken = authHeader.substring(7);
            }


            // 2. 处理refresh token
            if (refreshToken != null && jwtUtil.validateToken(refreshToken)) {
                String username = jwtUtil.getUserFromToken(refreshToken).getUsername();
                userQueueService.unregisterUserQueueListener(username);                 //清理监听队列
                // 将refresh token加入黑名单（防止在过期前被重复使用）
                redisUtil.setWithPrefix("auth:blacklist:refresh:", refreshToken, "",
                        jwtUtil.getRemainingTime(refreshToken), TimeUnit.MILLISECONDS);

                // 清除相关缓存
                redisUtil.delete("auth:refreshtoken:" + username);
                jwtUserDetailsService.evictUserCache(username);
            }

            // 3. 处理access token（如果有）
            if (accessToken != null && jwtUtil.validateToken(accessToken)) {
                String username = jwtUtil.getUserFromToken(accessToken).getUsername();

                // 将access token加入黑名单（剩余有效期内不可用）
                redisUtil.setWithPrefix("auth:blacklist:access:", accessToken, "",
                        jwtUtil.getRemainingTime(accessToken), TimeUnit.MILLISECONDS);

                redisUtil.delete("auth:accesstoken:" + username);
            }

            // 通知客户端清除Cookie
            ResponseCookie cookie = ResponseCookie.from("refresh_token", "")
                    .httpOnly(true)
                    .secure(true)  // 生产环境应该启用
                    .path("/api/user/refresh-token")
                    .maxAge(0)
                    .sameSite("Strict")  // 防止CSRF
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(Map.of("message", "Logout successful","timestamp", Instant.now().toString()));

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "登出失败"));
        }
    }







    // 存储令牌到Redis
    private boolean storeTokens(String username, String accessToken, String refreshToken) {
        boolean accessTokenStored = redisUtil.setWithPrefix("auth:accesstoken:",
                username, DigestUtils.sha256Hex(accessToken), 30, TimeUnit.MINUTES);

        boolean refreshTokenStored = redisUtil.setWithPrefix("auth:refreshtoken:",
                username, refreshToken, 7, TimeUnit.DAYS);

        return accessTokenStored && refreshTokenStored;
    }




    // 构建Refresh Token的HttpOnly Cookie
    private ResponseCookie buildRefreshTokenCookie(String token) {
        return ResponseCookie.from("refresh_token", token)
                .httpOnly(true)
                .secure(true) // 生产环境启用
                .path("/api/user/refresh-token")
                .maxAge(7 * 24 * 60 * 60) // 7天
                .sameSite("Strict")
                .build();
    }

    // 认证方法
    private void authenticate(String username, String password) throws Exception {
        try {

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

        }catch (Exception e) {
            System.out.println("认证失败: " + e.getMessage());  // 打印具体异常
            throw e;
        }

    }

    // 检查管理员角色
    private boolean hasAdminRole(UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
    }


}





