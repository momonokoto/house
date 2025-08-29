package com.zpark.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zpark.dto.AppointmentDto;
import com.zpark.entity.Appointment;
import com.zpark.entity.House;
import com.zpark.entity.User;
import com.zpark.mapper.IAppointmentAddMapper;
import com.zpark.service.IAppointmentAddService;
import com.zpark.service.IHouseService;
import com.zpark.service.IUserService;
import com.zpark.service.MessageProducerService;
import com.zpark.utils.GetUserFromToken;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
public class AppointmentAddServiceImpl extends ServiceImpl<IAppointmentAddMapper, Appointment> implements IAppointmentAddService {
    @Resource
    private IHouseService houseService;

    @Resource
    private IAppointmentAddMapper iAppointmentAddMapper;

    @Resource
    private MessageProducerService messageProducerService;

    @Resource
    private GetUserFromToken getUserFromToken;

    @Resource
    private IUserService userService;


    @Override
    @Transactional
    public int save(AppointmentDto appointmentDto, Long houseId, HttpServletRequest request) {
        if (appointmentDto == null ||
                appointmentDto.getDate() == null ||
                appointmentDto.getPhone() == null ||
                appointmentDto.getName() == null ||
                appointmentDto.getMessage() == null) {
            return 1004;
        }

        //通过token获取用户
        User user = getUserFromToken.getUser(request);
        if (user == null){
            return 1000;
        }
        House house = houseService.getById(houseId);
        if (house == null){
            return 1001;
        }
        if(house.getOwnerId()==null){
            return 1003;
        }

        User owner = userService.getById(house.getOwnerId());

//        判断是否已经预约过了
        LambdaQueryWrapper<Appointment>  queryWrapperAppointment = new LambdaQueryWrapper<>();
        queryWrapperAppointment.eq(Appointment::getHouseId, houseId);
        queryWrapperAppointment.eq(Appointment::getTenantId, user.getId());
        queryWrapperAppointment.ne(Appointment::getStatus, 0);
        if (iAppointmentAddMapper.selectOne(queryWrapperAppointment) != null){
            return 1002;
        }
//        appointment.setAppointmentId(appointment.getAppointmentId());
        Appointment appointment  = new Appointment();
        //避免前端更改id
        appointment.setAppointmentId(null);
        //设置预约基本信息
        appointment.setTenantId(user.getId());
        appointment.setCreateTime(LocalDateTime.now());
        appointment.setHouseId(houseId);
        appointment.setOwnerId(house.getOwnerId());
        appointment.setStatus(2);
        //设置预约信息
        appointment.setMessage(appointmentDto.getMessage());
        appointment.setPhone(appointmentDto.getPhone());
        appointment.setName(appointmentDto.getName());
        appointment.setDate(appointmentDto.getDate());
        //给房东发送预约消息
        messageProducerService.sendPrivateMessage_System(owner.getUsername(),
      "您有一条新的预约看房，请注意查看"
              //预约的房源信息
            + "房源标题：" + house.getTitle()
            + "房源地址：" + house.getAddress()
            + "预约人：" + appointment.getName()
            + "预约时间：" + appointment.getDate()
            + "预约人电话：" + appointment.getPhone()
            + "预约人留言：" + appointment.getMessage()
        );
        return iAppointmentAddMapper.insert(appointment);
    }
}