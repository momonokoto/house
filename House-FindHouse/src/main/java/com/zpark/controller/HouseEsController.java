package com.zpark.controller;


import com.zpark.es.HouseEs;
import com.zpark.service.es.IHouseEsService;
import com.zpark.vo.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@Tag(name = "首页房源查找ES接口", description = "房源搜索接口")
public class HouseEsController {

    @Autowired
    private IHouseEsService houseEsService;



    @GetMapping("/findByTitleLike")
    @Operation(summary = "分词高亮查询", description = "用于首页根据标题和地址分词查询")
    @Parameters({
            @Parameter(name = "query", description = "搜索参数, 根据title和address参数分词查询", required = true),
            @Parameter(name = "Authorization", description = "认证令牌", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string", example = "Bearer 1234567")),
            @Parameter(name = "pageNum", description = "当前页", required = true),
            @Parameter(name = "pageSize", description = "每页数量", required = true),
            @Parameter(name = "region", description = "地区", required = true)
    })
    public PageResponse<HouseEs> searchHouses(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int pageNum,
            @RequestParam(defaultValue = "9") int pageSize
            ) throws IOException {
        return houseEsService.searchByKeyword(query, pageNum, pageSize);
    }

}
