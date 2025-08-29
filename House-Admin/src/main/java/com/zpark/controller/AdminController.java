//package com.zpark.controller;
//
//import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
//import com.zpark.dto.AppointmentDto;
//import com.zpark.entity.Appointment;
//import com.zpark.entity.House;
//import com.zpark.entity.User;
//import com.zpark.service.*;
//import com.zpark.utils.ResponseResult;
//import com.zpark.utils.StatusCode;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.Parameter;
//import io.swagger.v3.oas.annotations.Parameters;
//import io.swagger.v3.oas.annotations.enums.ParameterIn;
//import io.swagger.v3.oas.annotations.media.Content;
//import io.swagger.v3.oas.annotations.media.Schema;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.responses.ApiResponses;
//import io.swagger.v3.oas.annotations.security.SecurityRequirement;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import jakarta.validation.constraints.Max;
//import jakarta.validation.constraints.Min;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.Map;
//import java.util.function.Supplier;
//
//@RestController
//@RequestMapping("/api/admin")
//@Tag(name = "管理员接口", description = "管理员接口")
//public class AdminController {
//    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
//
//    @Autowired
//    private JwtUserDetailsService jwtUserDetailsService;
//
//    @Autowired
//    private IHouseService houseService;
//
//    @Autowired
//    private IUserService userService;
//
//    @Autowired
//    private IAppointmentMyService appointmentMyService;
//
//    @Autowired
//    private IOrderService orderService;
//
//    // 新增通用操作方法
//    private ResponseEntity<?> handleHouseOperation(Supplier<Integer> operation, String operationName, Object paramValue, String paramName) {
//        try {
//            logger.info("Attempting to {} {} for {}: {}", operationName, paramValue, paramName.toLowerCase(), paramValue);
//            Integer affectedRows = operation.get();
//
//            if (affectedRows == null || affectedRows == 0) {
//                String errorMsg = paramName + "操作失败（记录不存在或数据库错误）";
//                logger.error("{} with {}: {}", errorMsg, paramName.toLowerCase(), paramValue);
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
//                    "code", HttpStatus.INTERNAL_SERVER_ERROR.value(),
//                    "message", errorMsg
//                ));
//            }
//
//            return ResponseEntity.ok().body(Map.of(
//                "code", HttpStatus.OK.value(),
//                "message", "操作成功",
//                "data", affectedRows
//            ));
//        } catch (IllegalArgumentException e) {
//            logger.warn("Invalid parameter: {}", e.getMessage());
//            return ResponseEntity.badRequest().body(Map.of(
//                "code", HttpStatus.BAD_REQUEST.value(),
//                "message", e.getMessage()
//            ));
//        } catch (Exception e) {
//            logger.error("操作失败 {}{}: {}", paramName, paramValue, e.getMessage(), e);
//            return ResponseEntity.internalServerError().body(Map.of(
//                "code", HttpStatus.INTERNAL_SERVER_ERROR.value(),
//                "message", "服务器内部错误",
//                "detail", e.getCause() != null ? e.getCause().getMessage() : e.getMessage()
//            ));
//        }
//    }
//
//    @PostMapping("/setHouseStatus")
//    @Operation(
//            summary = "设置房源出租状态",
//            description = "设置房源出租状态（0-待出租 1-已出租）",
//            security = @SecurityRequirement(name = "Bearer Authentication")
//    )
//    @Parameters({
//            @Parameter(
//                    name = "Authorization",
//                    description = "认证令牌",
//                    required = true,
//                    in = ParameterIn.HEADER,
//                    schema = @Schema(type = "string",example = "Bearer xxx.yyy.zzz")
//            ),
//            @Parameter(
//                    name = "status",
//                    description = "房源状态（0-待出租 1-已出租）",
//                    required = true,
//                    schema = @Schema(type = "integer", allowableValues = {"0", "1"})
//            )
//    })
//    public ResponseEntity<?> setHouseStatus(
//            @RequestParam("houseId") @Min(1) Integer houseId,
//            @RequestParam("status") @Min(0) @Max(1) Integer status) {
//        return handleHouseOperation(
//            () -> houseService.setHouseStatus(houseId, status),
//            "设置状态",
//            status,
//            "houseId"
//        );
//    }
//
//    @PostMapping("/setHouseVerify")
//    @Operation(
//            summary = "设置房源审核状态",
//            description = "设置房源审核或禁用状态（-1-未通过,0-待审核 1-已审核）",
//            security = @SecurityRequirement(name = "Bearer Authentication")
//    )
//    @Parameters({
//            @Parameter(
//                    name = "Authorization",
//                    description = "认证令牌",
//                    required = true,
//                    in = ParameterIn.HEADER,
//                    schema = @Schema(type = "string",example = "Bearer xxx.yyy.zzz")
//            ),
//            @Parameter(
//                    name = "verify",
//                    description = "设置房源审核或禁用状态（-1-未通过,0-待审核 1-已审核）",
//                    required = true,
//                    schema = @Schema(type = "integer", allowableValues = {"-1","0", "1"})
//            )
//    })
//    public ResponseEntity<?> setHouseVerity(
//            @RequestParam("houseId") @Min(1) Integer houseId,
//            @RequestParam("verify") @Min(-1) @Max(1) Integer verify) {
//        return handleHouseOperation(
//            () -> houseService.setHouseVerify(houseId, verify),
//            "设置审核状态",
//            verify,
//            "houseId"
//        );
//    }
//
//
//    @PostMapping("/setUserStatus")
//    @Operation(
//            summary = "设置用户状态",
//            description = "设置用户状态（1-正常 0-禁用）",
//            security = @SecurityRequirement(name = "Bearer Authentication")
//    )
//    @Parameters({
//            @Parameter(
//                    name = "Authorization",
//                    description = "认证令牌",
//                    required = true,
//                    in = ParameterIn.HEADER,
//                    schema = @Schema(type = "string",example = "Bearer xxx.yyy.zzz")
//            ),
//            @Parameter(
//                    name = "status",
//                    description = "用户状态（1:正常 0:禁用）",
//                    required = true,
//                    schema = @Schema(type = "integer", allowableValues = {"0", "1"})
//            )
//    })
//    public ResponseEntity<?> setUserStatus(
//            @RequestParam("username") String username,
//            @RequestParam("status") @Min(0) @Max(1) Integer status) {
//        return handleHouseOperation(
//            () -> (Integer) userService.setUserStatus(username, status),
//            "设置用户状态",
//            status,
//            "username"
//        );
//    }
//
//    @PostMapping("/evict-cache")
//    @Operation(
//            summary = "清除登录缓存",
//            description = "强制清除所有用户的 redis 登录缓存（通常用于紧急情况或缓存更新）",
//            security = @SecurityRequirement(name = "Bearer Authentication")  // 表明需要认证
//    )
//    @Parameters({
//            @Parameter(
//                    name = "Authorization",
//                    description = "认证令牌",
//                    required = true,
//                    in = ParameterIn.HEADER,
//                    schema = @Schema(type = "string", example = "Bearer xxx.yyy.zzz")
//            )
//    })
//    @ApiResponses({
//            @ApiResponse(
//                    responseCode = "200",
//                    description = "缓存清除成功",
//                    content = @Content(schema = @Schema(example = "User cache evicted successfully"))
//            ),
//            @ApiResponse(
//                    responseCode = "500",
//                    description = "缓存清除失败",
//                    content = @Content(schema = @Schema(example = "Failed to evict cache: 错误详情"))
//            )
//    })
//    public ResponseEntity<?> evictUserCache() {
//        try {
//            logger.info("Admin requested to evict user cache");
//            String result = jwtUserDetailsService.evictAllUserCache();
//            logger.info("User cache evicted successfully");
//            return ResponseEntity.ok().body("User cache evicted successfully");
//        } catch (CacheEvictionException e) {
//            logger.error("Failed to evict user cache: {}", e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Failed to evict cache: " + e.getMessage());
//        } catch (Exception e) {
//            logger.error("Unexpected error during cache eviction: {}", e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("An unexpected error occurred: " + e.getMessage());
//        }
//    }
//
//
//
//    // 定义自定义异常
//    public static class CacheEvictionException extends RuntimeException {
//        public CacheEvictionException(String message) {
//            super(message);
//        }
//    }
//
//
//
//
//    @GetMapping("/findAllUsers")
//    @Operation(
//            summary = "查询所有用户信息",
//            description = "分页查询所有用户信息"
//    )
//    @Parameters({
//            @Parameter(
//                    name = "currentPage",
//                    description = "当前页码",
//                    required = true,
//                    schema = @Schema(type = "integer", minimum = "1")
//            ),
//            @Parameter(
//                    name = "pageSize",
//                    description = "每页数量",
//                    required = true,
//                    schema = @Schema(type = "integer", minimum = "1")
//            ),
//            @Parameter(
//                    name = "Authorization",
//                    description = "认证令牌",
//                    required = true,
//                    in = ParameterIn.HEADER,
//                    schema = @Schema(type = "string", example = "Bearer xxx.yyy.zzz")
//            )
//    })
//    public ResponseResult<List<User>> findAllUser(
//            @RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
//            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize
//    ){
//        Page<User> page = new Page<>(currentPage, pageSize);
//        List<User> users = userService.list(page);
//        return new ResponseResult<>(StatusCode.SUCCESS.value, "查询成功", users);
//    }
//
//
//    @GetMapping("/findAllHouses")
//    @Operation(
//            summary = "查询所有房源信息",
//            description = "分页查询所有房源信息"
//    )
//    @Parameters({
//            @Parameter(
//                    name = "currentPage",
//                    description = "当前页码",
//                    required = true,
//                    schema = @Schema(type = "integer", minimum = "1")
//            ),
//            @Parameter(
//                    name = "pageSize",
//                    description = "每页数量",
//                    required = true,
//                    schema = @Schema(type = "integer", minimum = "1")
//            ),
//            @Parameter(
//                    name = "Authorization",
//                    description = "认证令牌",
//                    required = true,
//                    in = ParameterIn.HEADER,
//                    schema = @Schema(type = "string", example = "Bearer xxx.yyy.zzz")
//            )
//    })
//    public ResponseResult<Page<House>> findAllHouse(
//            @RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
//            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize
//    ){
//        Page<House> page = houseService.page(new Page<>(currentPage, pageSize));
//        return new ResponseResult<>(StatusCode.SUCCESS.value, "查询成功", page);
//    }
//
//
//    //查询所有预约
//    @GetMapping("/findAllAppointment")
//    @Operation(
//            summary = "查询所有预约信息",
//            description = "分页查询所有预约信息"
//    )
//    @Parameters({
//            @Parameter(
//                    name = "currentPage",
//                    description = "当前页码",
//                    required = true,
//                    schema = @Schema(type = "integer", minimum = "1")
//            ),
//            @Parameter(
//                    name = "pageSize",
//                    description = "每页数量",
//                    required = true,
//                    schema = @Schema(type = "integer", minimum = "1")
//            ),
//            @Parameter(
//                    name = "Authorization",
//                    description = "认证令牌",
//                    required = true,
//                    in = ParameterIn.HEADER,
//                    schema = @Schema(type = "string", example = "Bearer xxx.yyy.zzz")
//            )
//    })
//    public ResponseResult<Page<Appointment>> findAllAppointment(
//            @RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
//            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize
//    ){
//        Page<Appointment> page = appointmentMyService.page(new Page<>(currentPage, pageSize));
//        return new ResponseResult<>(StatusCode.SUCCESS.value, "查询成功", page);
//    }
//
//    //修改预约信息
//    @PutMapping("/updateAppointment")
//    @Operation(
//            summary = "修改预约信息",
//            description = "修改预约信息"
//    )
//    @Parameters({
//            @Parameter(
//                    name = "Authorization",
//                    description = "认证令牌",
//                    required = true,
//                    in = ParameterIn.HEADER,
//                    schema = @Schema(type = "string", example = "Bearer xxx.yyy.zzz")
//            )
//    })
//    public ResponseResult<Appointment> updateAppointment(@RequestBody AppointmentDto appointmentDto){
//        Appointment appointment = new Appointment();
//        appointment.setMessage(appointmentDto.getMessage());
//        appointment.setPhone(appointmentDto.getPhone());
//        appointment.setName(appointmentDto.getName());
//        appointment.setDate(appointmentDto.getDate());
//        boolean update = appointmentMyService.updateById(appointment);
//        if (update){
//            return new ResponseResult<>(StatusCode.SUCCESS.value, "修改成功", null);
//        }
//        return new ResponseResult<>(StatusCode.FAILURE.value, "修改失败", appointment);
//    }
//}