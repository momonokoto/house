package com.zpark.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zpark.entity.House;
import com.zpark.dto.HouseQueryConditionsDto;
import com.zpark.entity.User;
import com.zpark.service.IHouseFindAllService;
import com.zpark.service.IUserService;
import com.zpark.utils.ResponseResult;
import com.zpark.utils.StatusCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 房源信息表 前端控制器
 * </p>
 *
 * @author XMC
 * @since 2025-04-24
 */
@RestController
@RequestMapping("/house")
@Tag(name = "房源查找接口", description = "房源搜索及房源详细查看")
public class HouseController {

    @Autowired
    private IHouseFindAllService houseService;

    @Autowired
    private IUserService userService;


//    分页查询
    @PostMapping("/find")
    @Operation(summary = "房源查询", description = "分页展示房源")
    @Parameters({
            @Parameter(name = "currentPage", description = "当前页码"),
            @Parameter(name = "pageSize", description = "每页显示的记录数"),
            @Parameter(
                    name = "Authorization",
                    description = "认证令牌",
                    required = true,
                    in = ParameterIn.HEADER,
                    schema = @Schema(type = "string", example = "Bearer 12345678")
            )
    })
    public ResponseResult<Page<House>> findAll(
            @RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
            @RequestParam(value = "pageSize", defaultValue = "12") Integer pageSize,
            @RequestBody(required = false) HouseQueryConditionsDto house
    ){
        System.out.println( house);
        Page<House> houses = houseService.findAll(currentPage, pageSize, house);
        return new ResponseResult<>(StatusCode.SUCCESS.value, "查询成功", houses);
    }


    @GetMapping("/detail/{id}")
    @Operation(summary = "房源详情", description = "根据房源id查询房源详情")
    @Parameter(name = "id", description = "房源id", required = true)
    @Parameters({
            @Parameter(
                    name = "Authorization",
                    description = "认证令牌",
                    required = true,
                    in = ParameterIn.HEADER,
                    schema = @Schema(type = "string", example = "Bearer 12345678")
            )
    })
    public ResponseResult<House> findById(@PathVariable(value = "id") Integer id){
        House house = houseService.getById(id);
        if (house == null) return new ResponseResult<>(StatusCode.FAILURE.value, "查询失败，该房源不存在", null);
        return new ResponseResult<>(StatusCode.SUCCESS.value, "查询成功", house);
    }

    //通过houseId获取房东用户名
    @GetMapping("/getOwnerName/{houseId}")
    @Operation(summary = "获取房东用户名", description = "根据houseId获取房东用户名")
    @Parameters({
            @Parameter(
                    name = "Authorization",
                    description = "认证令牌",
                    required = true,
                    in = ParameterIn.HEADER,
                    schema = @Schema(type = "string", example = "Bearer 12345678")
            )
    })
    public ResponseResult<String> getOwnerName(@PathVariable(value = "houseId") Integer houseId){
        House house = houseService.getById(houseId);
        Long ownerName = house.getOwnerId();
        User user = userService.getById(ownerName);
        String username = user.getUsername();
        if (ownerName == null) return new ResponseResult<>(StatusCode.FAILURE.value, "查询失败，该房东不存在", null);
        return new ResponseResult<>(StatusCode.SUCCESS.value, "查询成功", username);
    }


}

