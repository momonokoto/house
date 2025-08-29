package com.zpark.mapper;

import com.zpark.entity.House;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

public interface HouseMapper_AdPu extends BaseMapper<House> {

    @Insert("INSERT INTO house(owner_id,title,address,area,room_Number,room_Type,rent,latitude,longitude,status,region,description,img,video,elevator,detailed_address,verify,create_time) " +
            "VALUES (#{ownerId},#{title},#{address},#{area},#{roomNumber},#{roomType},#{rent},#{latitude},#{longitude},#{status},#{region},#{description},#{img},#{video},#{elevator},#{detailedAddress},#{verify},#{createTime})")
    Integer housePublish(House house);

    @Update("update house " +
            "set status = #{status} " +
            "where house_id = #{houseId} " +
            "and exists (select 1 from (select * from house) as tmp where house_id = #{houseId} and status in (0,1))")
    Integer setHouseStatus(@Param("houseId") Integer houseId, @Param("status") Integer status);


    @Update("update house " +
            "set verify = #{verify} " +
            "where house_id = #{houseId} " +
            "and exists (select 1 from (select * from house) as tmp where house_id = #{houseId} and verify in (-1,0,1))")
    Integer setHouseVerify(@Param("houseId") Integer houseId, @Param("verify") Integer verify);
}