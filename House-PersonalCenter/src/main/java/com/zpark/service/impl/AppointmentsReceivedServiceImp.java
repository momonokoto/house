package com.zpark.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zpark.entity.Appointment;
import com.zpark.mapper.IAppointmentsReceivedMapper;
import com.zpark.service.IAppointmentsReceivedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentsReceivedServiceImp implements IAppointmentsReceivedService {

    @Autowired
    private IAppointmentsReceivedMapper appointmentsReceivedMapper;

    @Override
    public org.springframework.data.domain.Page<Appointment> findAppointmentsReceived(int pageNum, int pageSize, Long ownerId) {
        // 创建MyBatis Plus分页对象
        Page<Appointment> mybatisPage = new Page<>(pageNum, pageSize);
        
        // 执行分页查询
        List<Appointment> records = appointmentsReceivedMapper.findAppointmentsReceived(mybatisPage, ownerId);
        
        // 转换为Spring Data Page对象
        return new PageImpl<>(
            records, 
            PageRequest.of(pageNum - 1, pageSize), 
            mybatisPage.getTotal()
        );
    }
}