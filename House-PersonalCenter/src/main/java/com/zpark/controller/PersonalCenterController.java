package com.zpark.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zpark.dto.ChangePasswordConditionDto;
import com.zpark.dto.HouseEditDto;
import com.zpark.dto.UserEditDto;
import com.zpark.entity.Appointment;
import com.zpark.entity.Collection;
import com.zpark.entity.House;
import com.zpark.entity.User;
import com.zpark.service.*;
import com.zpark.utils.GetUserFromToken;
import com.zpark.utils.ResponseResult;
import com.zpark.utils.StatusCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/personal_center")
@Tag(name = "个人中心接口", description = "个人中心所需的接口")
public class PersonalCenterController {
    @Autowired
    private IAppointmentMyService appointmentMyService;
    @Autowired
    private ICollectionMyService collectionMyService;
    @Autowired
    private IHouseMyPublishService houseMyPublishService;
    @Autowired
    private IUserEditService userEditService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IHouseEditService houseEditService;

    @Autowired
    private GetUserFromToken getUserFromToken;

    @Autowired
    private QiniuService qiniuService;

    @Autowired
    private MessageProducerService messageProducerService;

    @Autowired
    private IHouseService houseService;


//    我发布的房源
    @GetMapping("/my_house")
    @Operation(summary = "我发布的房源", description = "分页查询我发布的房源")
    @Parameters({
            @Parameter(name = "currentPage", description = "当前页码"),
            @Parameter(name = "Authorization", description = "认证令牌", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string", example = "Bearer XXXXXXX"))
    })
    public ResponseResult<Page<House>> house(@RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
                                             @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                             HttpServletRequest request){
        Page<House> housePage = houseMyPublishService.findList(currentPage, pageSize,request);
        return new ResponseResult<>(StatusCode.SUCCESS.value, "查询成功", housePage);
    }


    @GetMapping("/my_appointment")
    @Operation(summary = "我预约的房源", description = "分页查询我预约的房源")
    @Parameters({
            @Parameter(name = "currentPage", description = "当前页码"),
            @Parameter(name = "Authorization", description = "认证令牌", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string", example = "Bearer XXXXXXX"))
    })
    public ResponseResult<Page<Appointment>> appointment(@RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
                                                         @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                      HttpServletRequest request){
        Page<Appointment> appointmentPage = appointmentMyService.findAll(currentPage,pageSize ,request);
        return new ResponseResult<>(StatusCode.SUCCESS.value, "查询成功", appointmentPage);
    }


//    我的收藏
    @GetMapping("/my_collection")
    @Operation(summary = "我的收藏", description = "分页查询我的收藏")
    @Parameters({
            @Parameter(name = "currentPage", description = "当前页码"),
            @Parameter(name = "Authorization", description = "认证令牌", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string", example = "Bearer XXXXXXX"))
    })
    public ResponseResult<Page<Collection>> collection(@RequestParam(value = "currentPage",defaultValue = "1") Integer currentPage,
            @RequestParam Integer pageSize,
            HttpServletRequest request){
        Page<Collection> collectionPage = collectionMyService.findAll(currentPage, pageSize,request);
        return new ResponseResult<>(StatusCode.SUCCESS.value, "查询成功", collectionPage);
   }

   //修改个人资料
    @PutMapping("/update")
    @Operation(summary = "修改个人资料", description = "修改个人资料")
    @Parameters({
            @Parameter(name = "Authorization", description = "认证令牌", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string", example = "Bearer XXXXXXX"))
    })
    public ResponseResult<UserEditDto> update(@RequestBody UserEditDto userEditDto, HttpServletRequest request){
        boolean  row = userEditService.updateUser(userEditDto, request);
        if (!row){
            return new ResponseResult<>(StatusCode.FAILURE.value, "修改失败，请稍后再试", null);
        }
        return new ResponseResult<>(StatusCode.SUCCESS.value, "修改成功", userEditDto);
    }

    //修改密码
    @PutMapping("/change_password")
    @Operation(summary = "修改密码", description = "修改密码")
    @Parameters({
            @Parameter(name = "Authorization", description = "认证令牌", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string", example = "Bearer XXXXXXX"))
    })
    public ResponseResult<User> changePassword(@RequestBody ChangePasswordConditionDto user, HttpServletRequest request){
        int row = userEditService.changePassword(user, request);

        if (row == 1001){
            return new ResponseResult<>(StatusCode.FAILURE.value, "原密码错误", null);
        }
        if (row == 1002){
            return new ResponseResult<>(StatusCode.FAILURE.value, "两次密码不一致", null);
        }
        if (row == 1003){
            return new ResponseResult<>(StatusCode.FAILURE.value, "新密码与原密码一致", null);
        }
        return new ResponseResult<>(StatusCode.SUCCESS.value, "修改密码成功，请重新登陆", null);
    }

    //取消预约
    @PutMapping("/cancel_appointment/{appointmentId}")
    @Operation(summary = "取消预约", description = "取消预约")
    @Parameters({
            @Parameter(name = "appointmentId", description = "预约id", required = true),
            @Parameter(name = "Authorization", description = "认证令牌", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string", example = "Bearer XXXXXXX"))
    })
    public ResponseResult<Appointment> cancelAppointment(@PathVariable String appointmentId){
        Appointment appointment = new Appointment();
        appointment.setAppointmentId(appointmentId);
        appointment.setStatus(0);
        boolean row = appointmentMyService.updateById(appointment);
        if (!row){
            return new ResponseResult<>(StatusCode.FAILURE.value, "取消预约失败", null);
        }
        Appointment myAppointment = appointmentMyService.getById(appointmentId);
        String username = userService.getById(myAppointment.getOwnerId()).getUsername();
        House house = houseService.getById(myAppointment.getHouseId());
        messageProducerService.sendPrivateMessage_System(username,
                "您的预约被取消了，请注意！！！"
                + "标题：" + house.getTitle()
                + "地址：" + house.getAddress()
                + "预约人：" + myAppointment.getName()
                + "预约时间：" + myAppointment.getDate()
                + "预约人电话：" + myAppointment.getPhone()
                + "预约人留言：" + myAppointment.getMessage()
        );
        return new ResponseResult<>(StatusCode.SUCCESS.value, "取消预约成功", null);
    }


    //下架房源
    @PutMapping("/cancel_house/{houseId}")
    @Operation(summary = "下架房源", description = "下架房源")
    @Parameters({
            @Parameter(name = "houseId", description = "房源id", required = true),
            @Parameter(name = "Authorization", description = "认证令牌", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string", example = "Bearer XXXXXXX"))
    })
    public ResponseResult<House> cancelHouse(@PathVariable Integer houseId){
        House house = new House();
        house.setHouseId(houseId);
        house.setStatus(2);
        boolean row = houseEditService.updateById(house);
        if (!row){
            return new ResponseResult<>(StatusCode.FAILURE.value, "下架失败", null);
        }
        return new ResponseResult<>(StatusCode.SUCCESS.value, "下架成功", null);
    }

    //编辑房源信息
    @PutMapping("/edit_house/{houseId}")
    @Operation(summary = "编辑房源信息", description = "编辑房源信息")
    @Parameters({
            @Parameter(name = "houseId", description = "房源id", required = true),
            @Parameter(name = "Authorization", description = "认证令牌", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string", example = "Bearer XXXXXXX"))
    })
    public ResponseResult<House> editHouse(@PathVariable Integer houseId, @RequestBody HouseEditDto houseEditDto){
        House house = new House();
        house.setHouseId(houseId);
        house.setTitle(houseEditDto.getTitle());
        house.setAddress(houseEditDto.getAddress());
        house.setArea(houseEditDto.getArea());
        house.setRoomNumber(houseEditDto.getRoomNumber());
        house.setRoomType(houseEditDto.getRoomType());
        house.setRent(houseEditDto.getRent());
        house.setRegion(houseEditDto.getRegion());
        house.setDescription(houseEditDto.getDescription());
        house.setImg(houseEditDto.getImg());
        house.setVideo(houseEditDto.getVideo());
        house.setElevator(houseEditDto.getElevator());
        house.setDetailedAddress(houseEditDto.getDetailedAddress());
        boolean row = houseEditService.updateById(house);
        if (!row){
            return new ResponseResult<>(StatusCode.FAILURE.value, "编辑失败", null);
        }
        return new ResponseResult<>(StatusCode.SUCCESS.value, "编辑成功", house);
    }


    //通过token获取用户全部信息
    @PostMapping("/get_user_info")
    @Operation(summary = "获取用户信息", description = "获取用户信息")
    @Parameters({
        @Parameter(name = "Authorization", description = "认证令牌", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string", example = "Bearer XXXXXXX"))
    })
    public ResponseResult<User> getUserInfo(HttpServletRequest request){
        Long userId = getUserFromToken.getUser(request).getId();
        User user = userService.getById(userId);
        if (user == null){
            return new ResponseResult<>(StatusCode.FAILURE.value, "获取用户信息失败", null);
        }
        return new ResponseResult<>(StatusCode.SUCCESS.value, "获取用户信息成功", user);
    }

    //上传头像
    @PostMapping(value = "/upload_avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "上传头像", description = "上传头像")
    @Parameters({
            @Parameter(name = "Authorization", description = "认证令牌", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string", example = "Bearer XXXXXXX"))
    })
    public ResponseResult<String> uploadAvatar(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        User user = getUserFromToken.getUser(request);
        String imageFileName = qiniuService.uploadImage(file, null);
        user.setId(user.getId());
        user.setAvatar(imageFileName);
        boolean result = userService.saveOrUpdate(user); // 调用 saveOrUpdate 方法
        if (result) {
            return new ResponseResult<>(StatusCode.SUCCESS.value, "上传成功", imageFileName);
        } else {
            return new ResponseResult<>(StatusCode.FAILURE.value, "上传失败", null);
        }
    }

    //预约我的
    @GetMapping("/appointment_my")
    @Operation(summary = "预约我的", description = "预约我的")
    @Parameters({
            @Parameter(name = "Authorization", description = "认证令牌", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string", example = "Bearer XXXXXXX"))
    })
    public ResponseResult<List<Appointment>> appointmentMy(HttpServletRequest request){
        Long userId = getUserFromToken.getUser(request).getId();
        LambdaQueryWrapper<Appointment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Appointment::getOwnerId, userId);
        List<Appointment> appointmentList = appointmentMyService.list(queryWrapper);
        return new ResponseResult<>(StatusCode.SUCCESS.value, "获取预约信息成功", appointmentList);
    }


    //绑定支付宝账号
    @PostMapping("/bind_alipay")
    @Operation(summary = "绑定支付宝账号", description = "绑定支付宝账号")
    @Parameters({
            @Parameter(name = "Authorization", description = "认证令牌", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string", example = "Bearer XXXXXXX")),
            @Parameter(name = "alipayId", description = "支付宝账号", required = true)
    })
    public ResponseResult<String> bindAlipay(String alipayId, HttpServletRequest request){
        User user = getUserFromToken.getUser(request);
        user.setId(user.getId());
        user.setAlipayId(alipayId);
        boolean result = userService.saveOrUpdate(user);
        if (result) {
            return new ResponseResult<>(StatusCode.SUCCESS.value, "绑定成功", null);
        } else {
            return new ResponseResult<>(StatusCode.FAILURE.value, "绑定失败", null);
        }
    }

    //更改预约状态
    @PutMapping("/change_appointment_status")
    @Operation(summary = "更改预约状态", description = "更改预约状态")
    @Parameters({
            @Parameter(name = "appointmentId", description = "预约id", required = true),
            @Parameter(name = "status", description = "状态", required = true),
            @Parameter(name = "Authorization", description = "认证令牌", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string", example = "Bearer XXXXXXX"))
    })
    public boolean changeAppointmentStatus(@RequestParam String appointmentId, @RequestParam Integer status){
        Appointment appointment = new Appointment();
        appointment.setAppointmentId(appointmentId);
        appointment.setStatus(status);
        return appointmentMyService.updateById(appointment);
    }
}
