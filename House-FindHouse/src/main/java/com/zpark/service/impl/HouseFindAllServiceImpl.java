package com.zpark.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zpark.entity.House;
import com.zpark.dto.HouseQueryConditionsDto;
import com.zpark.mapper.IHouseFindAllMapper;
import com.zpark.service.IHouseFindAllService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HouseFindAllServiceImpl extends ServiceImpl<IHouseFindAllMapper, House> implements IHouseFindAllService {

    @Autowired
    private IHouseFindAllMapper houseFindAllMapper;

    @Override
    public Page<House> findAll(Integer currentPage, Integer pageSize, HouseQueryConditionsDto house) {
        // 设置默认分页参数
        int pageNum = currentPage == null || currentPage < 1 ? 1 : currentPage;
        int size = pageSize == null || pageSize < 1 ? 10 : pageSize;

        // 构建查询条件
        LambdaQueryWrapper<House> queryWrapper = new LambdaQueryWrapper<>();
        // 排除已删除、下架、未审核房源
        queryWrapper.ne(House::getIsDeleted, 1)
                .ne(House::getStatus, 2)
                .eq(House::getVerify, 1);

        // 添加排序
        queryWrapper.orderByDesc(House::getUpdateTime, House::getCreateTime);

        // 动态添加查询条件
        if (house != null) {
            if (StringUtils.isNotBlank(house.getRegion())) {
                queryWrapper.like(House::getRegion, house.getRegion());
            }
            if (StringUtils.isNotBlank(house.getDetailedAddress())) {
                queryWrapper.like(House::getDetailedAddress, house.getDetailedAddress());
            }
            if (house.getMinPrice() != null && house.getMaxPrice() != null) {
                queryWrapper.between(House::getRent, house.getMinPrice(), house.getMaxPrice());
            }
            if (house.getMinArea() != null && house.getMaxArea() != null) {
                queryWrapper.between(House::getArea, house.getMinArea(), house.getMaxArea());
            }
            if (StringUtils.isNotBlank(house.getRoomType())) {
                queryWrapper.eq(House::getRoomType, house.getRoomType()); // 精确匹配更高效
            }
            if (StringUtils.isNotBlank(house.getRentType())) {
                queryWrapper.eq(House::getRentType, house.getRentType()); // 精确匹配更高效
            }
        }

        // 执行分页查询
        return houseFindAllMapper.selectPage(new Page<>(pageNum, size), queryWrapper);
    }
}