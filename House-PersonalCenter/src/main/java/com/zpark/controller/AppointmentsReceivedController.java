package com.zpark.controller;

import com.zpark.entity.Appointment;
import com.zpark.entity.User;
import com.zpark.service.IAppointmentsReceivedService;
import com.zpark.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/AmR")
@Tag(name = "房东接收到的预约", description = "房东接收到的预约")
public class AppointmentsReceivedController {

    @Autowired
    private IAppointmentsReceivedService appointmentsReceivedService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/get")
    @Parameter(
            name = "Authorization",
            description = "认证令牌",
            required = true,
            in = ParameterIn.HEADER,
            schema = @Schema(type = "string", example = "Bearer xxx.yyy.zzz")
    )
    public Page<Appointment> findAppointmentsReceived(
            @RequestParam(defaultValue = "1") int pageNum, 
            @RequestParam(defaultValue = "10") int pageSize,
            HttpServletRequest request) {
        
        // 分页参数校验
        if (pageNum < 1) pageNum = 1;
        if (pageSize < 1 || pageSize > 100) pageSize = 10;
        
        String token = request.getHeader("Authorization");
        token = token.substring(7);
        User user = jwtUtil.getUserFromToken(token);

        return appointmentsReceivedService.findAppointmentsReceived(pageNum, pageSize, user.getId());

    }
}
