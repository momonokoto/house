package com.zpark.service;

import com.zpark.entity.Appointment;
import org.springframework.data.domain.Page;

public interface IAppointmentsReceivedService {
    // 修改方法参数为标准的pageNum和pageSize
    Page<Appointment> findAppointmentsReceived(int pageNum, int pageSize, Long ownerId);
}