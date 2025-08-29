package com.zpark.controller;

import com.zpark.service.es.EsSyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/es")
@Tag(name = "数据同步接口", description = "数据同步接口, 管理员使用，不时之需")
public class EsSyncController {

    @Autowired
    private EsSyncService esSyncService;

    @GetMapping("/sync")
    @Operation(summary = "mysql同步到es", description = "将数据库中的房源信息数据同步到ES中，管理员使用的接口")
    @PreAuthorize("hasRole('ADMIN')")
    @Parameter(name = "Authorization", description = "认证令牌", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string", example = "Bearer XXXXXXX"))
    public String syncDataToEs() {
        esSyncService.syncAllHousesToEs();
        return "🔄 数据同步完成";
    }
}