package com.zpark.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zpark.entity.Appointment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface IAppointmentsReceivedMapper extends BaseMapper<Appointment> {

    @Select("select * from appointment where owner_id = #{ownerId}")
    List<Appointment> findAppointmentsReceived(
        @Param("page") Page<Appointment> page, 
        @Param("ownerId") Long ownerId
    );
}