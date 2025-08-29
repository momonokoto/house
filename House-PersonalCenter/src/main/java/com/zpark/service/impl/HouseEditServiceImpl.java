package com.zpark.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zpark.entity.House;
import com.zpark.mapper.IHouseEditMapper;
import com.zpark.service.IHouseEditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HouseEditServiceImpl extends ServiceImpl<IHouseEditMapper, House> implements IHouseEditService {
}
