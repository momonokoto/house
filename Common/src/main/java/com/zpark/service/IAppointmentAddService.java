package com.zpark.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zpark.dto.AppointmentDto;
import com.zpark.entity.Appointment;
import jakarta.servlet.http.HttpServletRequest;

/**
 * <p>
 * 预约表 服务类
 * </p>
 *
 * @author XMC
 * @since 2025-04-27
 */
public interface IAppointmentAddService extends IService<Appointment> {
    int save(AppointmentDto appointmentDto, Long id, HttpServletRequest request);

}
