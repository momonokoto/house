package com.zpark.service.es;

import com.zpark.entity.House;
import com.zpark.es.HouseEs;
import com.zpark.repository.HouseEsRepository;
import com.zpark.service.IHouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EsSyncService {

    @Autowired
    private HouseEsRepository houseEsRepository; // Es

    @Autowired
    private IHouseService houseService; // mysql

    /**
     * 同步所有房源数据到 Elasticsearch
     */
    public void syncAllHousesToEs() {
        List<House> houses = houseService.list(); // 从数据库获取所有数据

        List<HouseEs> houseEsList = houses.stream()
                .map(this::convertToEsEntity)
                .collect(Collectors.toList());

        houseEsRepository.saveAll(houseEsList); // 批量写入 ES
        System.out.println("✅ 成功同步 " + houseEsList.size() + " 条房源数据到 Elasticsearch");
    }

    /**
     * 单个数据转换：House -> HouseEs
     */
    private HouseEs convertToEsEntity(House house) {
        HouseEs houseEs = new HouseEs();
        houseEs.setHouseId(house.getHouseId().longValue());
        houseEs.setOwnerId(house.getOwnerId());
        houseEs.setTitle(house.getTitle());
        houseEs.setAddress(house.getAddress());
        houseEs.setArea(house.getArea());
        houseEs.setRoomNumber(house.getRoomNumber());
        houseEs.setRoomType(house.getRoomType());
        houseEs.setRent(house.getRent().doubleValue());
        houseEs.setLatitude(house.getLatitude());
        houseEs.setLongitude(house.getLongitude());
        houseEs.setStatus(house.getStatus());
        houseEs.setRegion(house.getRegion());
        houseEs.setDescription(house.getDescription());
        houseEs.setImg(house.getImg());
        houseEs.setVideo(house.getVideo());
        houseEs.setElevator(house.getElevator());
        houseEs.setDetailedAddress(house.getDetailedAddress());
        houseEs.setVerify(house.getVerify());
        houseEs.setCreateTime(house.getCreateTime());
        houseEs.setIs_deleted((int) house.getIsDeleted());

        // 初始化地理位置
        houseEs.initLocation();

        return houseEs;
    }
}