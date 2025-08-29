package com.zpark.deepseek.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zpark.deepseek.util.QueryParser;
import com.zpark.dto.HouseQueryConditionsDto;
import com.zpark.entity.House;
import com.zpark.service.IHouseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;

@Service
public class AiHouseService {

    private static final Logger log = LoggerFactory.getLogger(AiHouseService.class);

    @Autowired
    private IHouseService houseService;

    public List<House> searchByUserQuestion(String question) {
        HouseQueryConditionsDto dto = QueryParser.parse(question);
        log.info("查询条件：{}", dto);
        // 判断是否没有任何查询条件
        if (isDtoEmpty(dto)) {
//            throw new IllegalArgumentException("请提供具体的查询条件");
            // 或者返回空列表
             return Collections.emptyList();
        }

        return houseService.list(new LambdaQueryWrapper<House>()
                .like(dto.getRegion() != null, House::getRegion, dto.getRegion())
                .like(dto.getDetailedAddress() != null && !dto.getDetailedAddress().isEmpty(),
                        House::getDetailedAddress, dto.getDetailedAddress())
                .ge(dto.getMinPrice() != null, House::getRent, dto.getMinPrice())
                .le(dto.getMaxPrice() != null, House::getRent, dto.getMaxPrice())
                .ge(dto.getMinArea() != null, House::getArea, dto.getMinArea())
                .le(dto.getMaxArea() != null, House::getArea, dto.getMaxArea())
                .eq(dto.getRoomType() != null, House::getRoomType, dto.getRoomType())
                .eq(dto.getRentType() != null, House::getRentType, dto.getRentType()));
    }
    private boolean isDtoEmpty(HouseQueryConditionsDto dto) {
        return
                dto.getRegion() == null &&
                        dto.getDetailedAddress() == null &&
                        dto.getMinPrice() == null &&
                        dto.getMaxPrice() == null &&
                        dto.getMinArea() == null &&
                        dto.getMaxArea() == null &&
                        dto.getRoomType() == null &&
                        dto.getRentType() == null;
    }
}