package com.zpark.controller;

import com.zpark.dto.AddContactDto;
import com.zpark.dto.PrivateMessageDto;
import com.zpark.entity.JwtResponse;
import com.zpark.entity.Message;
import com.zpark.service.MessageProducerService;
import com.zpark.service.MessageService;
import com.zpark.utils.RedisUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/messages")
@Tag(name = "RabbitMQ接口", description = "RabbitMQ接口")
public class MessageController {


    @Autowired
    private AuthController authController;
    private final MessageProducerService messageProducerService;
    private final MessageService messageService;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedisUtil redisUtil;

    public MessageController(MessageProducerService messageProducerService,
                             MessageService messageService,
                             StringRedisTemplate stringRedisTemplate,
                             RedisUtil redisUtil) { // 添加构造函数参数
        this.messageProducerService = messageProducerService;
        this.messageService = messageService;
        this.stringRedisTemplate = stringRedisTemplate;
        this.redisUtil = redisUtil;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/system/private")
    @Parameter(
            name = "Authorization",
            description = "认证令牌",
            required = true,
            in = ParameterIn.HEADER,
            schema = @Schema(type = "string", example = "Bearer xxx.yyy.zzz")
    )
    public ResponseEntity<?> sendSystemMessage(
        @Parameter(description = "消息内容传输对象", required = true)
        @RequestBody PrivateMessageDto privateMessageDto) {
        try {
            messageProducerService.sendPrivateMessage_System(privateMessageDto.getReceiver(), privateMessageDto.getContent());
            return ResponseEntity.ok().body("发送成功");
        } catch (Exception e) {
            log.error("发送系统消息失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body("消息发送失败: " + e.getMessage());
        }
    }


    @PostMapping("/static/private")
    @Operation(summary = "发送私信_静态队列", description = "向指定用户发送私信消息",
            responses = {
                    @ApiResponse(responseCode = "200", description = "发送成功"),
                    @ApiResponse(responseCode = "400", description = "发送失败")
            })
    @Parameter(
            name = "Authorization",
            description = "认证令牌",
            required = true,
            in = ParameterIn.HEADER,
            schema = @Schema(type = "string", example = "Bearer xxx.yyy.zzz")
    )
    public ResponseEntity<?> sendPrivateMessage_static(
            @Parameter(description = "消息内容传输对象", required = true)
            @RequestBody PrivateMessageDto privateMessageDto) {
        try {
            messageProducerService.sendPrivateMessage_static(privateMessageDto.getReceiver(), privateMessageDto.getContent());
            return ResponseEntity.ok().body("发送成功");
        } catch (Exception e) {
            log.error("发送私信失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body("消息发送失败: " + e.getMessage());
        }
    }

    @PostMapping("/private")
    @Operation(summary = "发送私信", description = "向指定用户发送点对点私信消息",
            responses = {
                    @ApiResponse(responseCode = "200", description = "发送成功"),
                    @ApiResponse(responseCode = "400", description = "发送失败"),
                    @ApiResponse(responseCode = "401", description = "未认证用户")
            })
    @Parameter(
            name = "Authorization",
            description = "认证令牌",
            required = true,
            in = ParameterIn.HEADER,
            schema = @Schema(type = "string", example = "Bearer xxx.yyy.zzz")
    )
    public ResponseEntity<?> sendPrivateMessage(
            @Parameter(description = "消息内容传输对象", required = true)
            @Valid @RequestBody PrivateMessageDto privateMessageDto) {  // 添加参数校验

        // 1. 安全获取当前用户（避免重复调用SecurityContext）
        String sender = SecurityContextHolder.getContext().getAuthentication().getName();
        if (sender == null || sender.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }


        try {
            long timestamp = Instant.now().getEpochSecond();  // 统一时间戳

            // 2.1 发送消息到MQ（异步处理）
            messageProducerService.sendPrivateMessage(
                    privateMessageDto.getReceiver(),
                    privateMessageDto.getContent()
            );

            redisUtil.addAndTrimRecentContact(sender, privateMessageDto.getReceiver(), timestamp,20);

            return ResponseEntity.ok()
                    .body(Map.of("status", "success", "message", "消息发送成功"));

        } catch (MessageDeliveryException e) {
            log.warn("消息接收方 {} 离线，已存储到待推送队列", privateMessageDto.getReceiver());
            return ResponseEntity.accepted().body("消息已保存，待对方上线后推送");
        } catch (Exception e) {
            log.error("私信发送失败 - 发送方: {}, 接收方: {}, 错误: {}",
                    sender, privateMessageDto.getReceiver(), e.getMessage());
            return ResponseEntity.badRequest().body("消息发送失败");
        }
    }





    @PostMapping("/history/private")
    @Operation(summary = "获取私聊记录", description = "查询与指定用户的所有私聊历史消息",
               responses = {
                   @ApiResponse(responseCode = "200", description = "查询成功"),
                   @ApiResponse(responseCode = "500", description = "服务器错误")
               })
    @Parameter(
            name = "Authorization",
            description = "认证令牌",
            required = true,
            in = ParameterIn.HEADER,
            schema = @Schema(type = "string", example = "Bearer xxx.yyy.zzz")
    )
    public ResponseEntity<?> getPrivateHistory(
        @Parameter(description = "认证用户信息", hidden = true)
        @AuthenticationPrincipal UserDetails userDetails,
        @Parameter(description = "对方用户名", required = true)
        @RequestParam String otherUser) {
        try {
            String currentUser = userDetails.getUsername();
            return ResponseEntity.ok(messageService.getPrivateHistory(currentUser, otherUser,20));
        } catch (Exception e) {
            log.error("获取私聊记录失败: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("获取历史消息失败");
        }
    }

    @PostMapping("/history/recent-private")
    @Operation(summary = "获取最近联系人的私聊记录", description = "查询与最近联系人的私聊历史消息",
        responses = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "400", description = "参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器错误")
        })
    @Parameter(
        name = "Authorization",
        description = "认证令牌",
        required = true,
        in = ParameterIn.HEADER,
        schema = @Schema(type = "string", example = "Bearer xxx.yyy.zzz")
    )
    public ResponseEntity<?> getRecentPrivateHistory(
        @Parameter(description = "对方用户名", required = true)
        @RequestParam String otherUser,
        @Parameter(description = "最老消息ID(用于去重)", required = false)
        @RequestParam(required = false) String oldestMessageId) {
        try {
            System.out.println("otherUser"+otherUser);
            String currentUser= SecurityContextHolder.getContext().getAuthentication().getName();

            System.out.println("currentUser"+currentUser);
            List<Message> messages = messageService.getPrivateHistory(currentUser, otherUser, 100);
            System.out.println("messages"+messages);
            System.out.println("oldestid"+oldestMessageId);
            if (oldestMessageId != null && !oldestMessageId.isEmpty()) {
                // 修改：获取参考消息的时间戳进行过滤
                Message refMessage = messageService.getMessageById(oldestMessageId);
                System.out.println("refMessage"+refMessage);
                if (refMessage != null) {
                    messages = messages.stream()
                        .filter(msg -> msg.getTimestamp().before(refMessage.getTimestamp()))
                        .collect(Collectors.toList());
                }
            }
            System.out.println("messages"+messages);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            log.error("获取最近联系人私聊记录失败: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "获取历史消息失败",
                "solution", "请稍后重试"
            ));
        }
    }

    @PostMapping("/getMessages")
    @Operation(summary = "获取历史消息", description = "滚动加载更旧的历史消息",
            responses = {
                    @ApiResponse(responseCode = "200", description = "查询成功"),
                    @ApiResponse(responseCode = "500", description = "服务器错误")
            })
    @Parameter(
            name = "Authorization",
            description = "认证令牌",
            required = true,
            in = ParameterIn.HEADER,
            schema = @Schema(type = "string", example = "Bearer xxx.yyy.zzz")
    )
    public ResponseEntity<Map<String, Object>> getMessages(
            @Parameter(description = "聊天对象用户名", required = true)
            @RequestParam String receiver,
            @Parameter(description = "当前最旧消息ID", required = true)
            @RequestParam String message_id) {
        String sender = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Message> messages = messageService.getMessages(sender, receiver, message_id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "status", "success",
                        "data", messages,
                        "hasMore", messages.size() >= 20 // 假设每页20条
                ));
    }

    @PostMapping("/addContact")
    @Operation(summary = "添加联系人", description = "添加联系人",
            responses = {
                    @ApiResponse(responseCode = "200", description = "添加成功"),
                    @ApiResponse(responseCode = "400", description = "参数错误"),
                    @ApiResponse(responseCode = "404", description = "用户不存在"),
                    @ApiResponse(responseCode = "500", description = "服务器错误")
            })
    @Parameter(
            name = "Authorization",
            description = "认证令牌",
            required = true,
            in = ParameterIn.HEADER,
            schema = @Schema(type = "string", example = "Bearer xxx.yyy.zzz")
    )
    public ResponseEntity<?> addContact(
        @Parameter(description = "联系人信息", required = true)
        @RequestBody AddContactDto addContactDto) {  {
        try {
            long timestamp = Instant.now().getEpochSecond();  // 统一时间戳
            String user=SecurityContextHolder.getContext().getAuthentication().getName();
            System.out.println(user+"添加联系人"+addContactDto.getContactUsername());
            redisUtil.addAndTrimRecentContact(user, addContactDto.getContactUsername(), timestamp,20);
            return ResponseEntity.ok()
                    .body(Map.of("status", "success", "message", "联系人添加成功"));

        } catch (Exception e) {
            log.error("添加联系人失败: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("添加联系人失败");
        }
    }
    }



    @DeleteMapping("/deleteContacts")
    @Operation(summary = "删除某个最近联系人", description = "删除某个最近联系人",
            responses = {
                    @ApiResponse(responseCode = "200", description = "删除成功"),
                    @ApiResponse(responseCode = "400", description = "参数错误"),
                    @ApiResponse(responseCode = "404", description = "联系人不存在"),
                    @ApiResponse(responseCode = "500", description = "服务器错误")
            })
    @Parameter(
            name = "Authorization",
            description = "认证令牌",
            required = true,
            in = ParameterIn.HEADER,
            schema = @Schema(type = "string", example = "Bearer xxx.yyy.zzz")
    )
    public ResponseEntity<?> deleteContacts(
        @Parameter(description = "认证用户信息", hidden = true)
        @AuthenticationPrincipal UserDetails userDetails,
        @Parameter(description = "联系人信息", required = true)
        @RequestBody Map<String, String> requestBody) {
        try {
            String currentUser = userDetails.getUsername();
            String contactUsername = requestBody.get("contactUsername");
            if (contactUsername == null || contactUsername.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "缺少必要参数",
                    "solution", "请提供contactUsername参数"
                ));
            }
            
            if (!redisUtil.removeRecentContact(currentUser, contactUsername)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "error", "联系人不存在",
                    "solution", "请检查联系人用户名是否正确"
                ));
            }

            return ResponseEntity.ok().body(Map.of(
                "status", "success",
                "message", "删除成功"
            ));
        } catch (Exception e) {
            log.error("删除联系人失败: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "删除联系人失败",
                "solution", "请稍后重试"
            ));
        }
    }


    @GetMapping("/recentContacts")
    @Operation(summary = "获取最近联系人", description = "获取最近联系人",
            responses = {
                    @ApiResponse(responseCode = "200", description = "查询成功"),
                    @ApiResponse(responseCode = "500", description = "服务器错误")
            })
    @Parameter(
            name = "Authorization",
            description = "认证令牌",
            required = true,
            in = ParameterIn.HEADER,
            schema = @Schema(type = "string", example = "Bearer xxx.yyy.zzz")
    )
    public ResponseEntity<?> getRecentContact(
            @Parameter(description = "认证用户信息", hidden = true)
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String currentUser = userDetails.getUsername();
            Map<String, Double> contactsWithTime = redisUtil.getRecentContactsMap(currentUser, 20);
//            contactsWithTime.forEach((contactId, timestamp) -> {
//                System.out.printf("联系人: %s, 最后交互时间: %s%n",
//                        contactId, Instant.ofEpochSecond(timestamp.longValue()));
//            });
            return ResponseEntity.ok(contactsWithTime);
        } catch (Exception e) {
            log.error("获取最近联系人失败: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("获取最近联系人失败");
        }
    }


    @PostMapping("/markAsRead")
    @Operation(summary = "标记消息为已读", description = "将指定用户的聊天记录标记为已读",
            responses = {
                @ApiResponse(responseCode = "200", description = "标记成功"),
                @ApiResponse(responseCode = "500", description = "服务器错误")
            })
    @Parameter(
            name = "Authorization",
            description = "认证令牌",
            required = true,
            in = ParameterIn.HEADER,
            schema = @Schema(type = "string", example = "Bearer xxx.yyy.zzz")
    )
    public ResponseEntity<?> markMessagesAsRead(
        @Parameter(description = "认证用户信息", hidden = true)
        @AuthenticationPrincipal UserDetails userDetails,
        @Parameter(description = "对方用户名", required = true)
        @RequestParam String otherUser) {
        try {
            String currentUser = userDetails.getUsername();
            messageService.markMessagesAsRead(currentUser, otherUser);
            return ResponseEntity.ok().body("消息已标记为已读");
        } catch (Exception e) {
            log.error("标记消息为已读失败: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("标记消息为已读失败");
        }
    }

    @PostMapping("/tokenisok")
    @Operation(summary = "检查Token是否正常", description = "检查Token是否正常")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "令牌正常",
                    content = @Content(schema = @Schema(example = "{\"message\":\"Token is valid\"}"))),
            @ApiResponse(responseCode = "401", description = "令牌无效",
                    content = @Content(schema = @Schema(example = "{\"error\":\"Invalid token\"}"))),
            @ApiResponse(responseCode = "500", description = "服务端异常",
                    content = @Content(schema = @Schema())
            )
    })
    public ResponseEntity<?> checkToken(HttpServletRequest httpServletRequest) {
        try {
            String user = SecurityContextHolder.getContext().getAuthentication().getName();
            String token = httpServletRequest.getHeader("Authorization");
            if (user == null || token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid token"));
            }
            else {
                token = token.substring(7);
                if(redisUtil.get("auth:accesstoken:"+user).equals(token)){
                    return ResponseEntity.ok(Map.of("message", "Token is valid"));
                }else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(Map.of("error", "Invalid token"));
                }
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Invalid token"));
        }
    }

    @PostMapping("/websocket/auth")
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
    public ResponseEntity<?> webscoketAuth() {
        try {
            String usernmae= SecurityContextHolder.getContext().getAuthentication().getName();
            // 7. 获取最近联系人消息
            Map<String, List<Message>> recentMessages = messageService.getRecentContactsMessages(usernmae, 10);
            // 创建统一响应对象
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("recentMessages", recentMessages);
            return ResponseEntity.ok()
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





}
