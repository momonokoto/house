package com.zpark.controller;


import com.zpark.dto.AppointmentDto;
import com.zpark.service.IAppointmentAddService;
import com.zpark.utils.ResponseResult;
import com.zpark.utils.StatusCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 预约表 前端控制器
 * </p>
 *
 * @author XMC
 * @since 2025-04-27
 */
@RestController
@RequestMapping("/appointment")
@Tag(name = "房源预约接口", description = "房源预约接口")
public class AppointmentController {
    @Autowired
    private IAppointmentAddService appointmentService;
    @PostMapping("/{houseId}")
    @Operation (summary = "预约房源", description = "预约房源")
    @Parameters({
            @Parameter(name = "Authorization", description = "认证令牌", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string", example = "Bearer 1234567")),
            @Parameter(name = "houseId", description = "房源ID", required = true, in = ParameterIn.PATH, schema = @Schema(type = "string", example = "1"))
    })
    public ResponseResult<AppointmentDto> appointment(@RequestBody AppointmentDto appointmentDto, @PathVariable Long houseId, HttpServletRequest request){
        int row = appointmentService.save(appointmentDto, houseId, request);
        System.out.println(row);
        if (row < 1){
            return new ResponseResult<>(StatusCode.FAILURE.value, "预约失败", null);
        }else if (row == 1000){
            return new ResponseResult<>(StatusCode.FAILURE.value, "预约失败，用户不存在", null);
        } else if (row == 1001){
            return new ResponseResult<>(StatusCode.FAILURE.value, "预约失败，房源不存在", null);
        } else if (row == 1002){
            return new ResponseResult<>(StatusCode.SUCCESS.value, "您已经预约过了", null);
        }else if (row == 1003){
            return new ResponseResult<>(StatusCode.FAILURE.value, "预约失败，房源信息异常", null);
        }else if (row == 1004){
            return new ResponseResult<>(StatusCode.FAILURE.value, "预约失败，参数错误", null);
        }else if (row == 1005){
            return new ResponseResult<>(StatusCode.FAILURE.value, "预约失败，日期格式错误，应为：yyyy-MM-dd HH:mm", null);
        }
        return new ResponseResult<>(StatusCode.SUCCESS.value, "预约成功", appointmentDto);
    }

}