package com.zpark.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zpark.entity.House;
import com.zpark.mapper.HouseMapper_AdPu;
import com.zpark.service.IHouseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 房源信息表 服务实现类
 * </p>
 *
 * @author fufuking
 * @since 2025-04-25
 */
@Service
public class HouseServiceImpl extends ServiceImpl<HouseMapper_AdPu, House> implements IHouseService {

    @Autowired
    HouseMapper_AdPu houseMapper;


    @Override
    public Integer housePublish(House house) {
        return houseMapper.housePublish(house);
    }

    private static final Logger logger = LoggerFactory.getLogger(HouseServiceImpl.class);
    @Override
    public Integer setHouseStatus(Integer houseId, Integer status) {
        logger.info("进入状态更新方法 houseId={}, status={}", houseId, status);  // 新增方法入口日志

        logger.info("开始执行状态更新 houseId={}, status={}", houseId, status);  // 替换System.out为日志输出
        if (logger.isDebugEnabled()) {
            logger.debug("原始调试信息 - houseId: {}, status: {}", houseId, status);  // 保留原始调试信息但改用日志
        }
        if (houseId == null || houseId <= 0) {
            throw new IllegalArgumentException("房源ID不合法");
        }
        try {
            logger.debug("Executing status update for houseId={}, status={}", houseId, status);  // 补充调试日志
            int result = houseMapper.setHouseStatus(houseId, status);
            logger.info("数据库更新完成，影响行数: {}", result);  // 提升结果日志级别到INFO
            return result;
        } catch (DataAccessException e) {
            logger.error("数据库操作失败 houseId={}, status={} | 错误类型: {} | 错误信息: {}",
                    houseId, status, e.getClass().getSimpleName(), e.getMessage());  // 增强错误日志细节
            throw new RuntimeException("数据库操作异常: " + e.getCause().getMessage(), e);  // 携带原始异常信息
        }
    }

    @Override
    public Integer setHouseVerify(Integer houseId, Integer verify) {
        logger.info("进入状态更新方法 houseId={}, verify={}", houseId, verify);  // 新增方法入口日志

        logger.info("开始执行状态更新 houseId={}, verify={}", houseId, verify);  // 替换System.out为日志输出
        if (logger.isDebugEnabled()) {
            logger.debug("原始调试信息 - houseId: {}, verify: {}", houseId, verify);  // 保留原始调试信息但改用日志
        }
        if (houseId == null || houseId <= 0) {
            throw new IllegalArgumentException("房源ID不合法");
        }
        try {
            logger.debug("Executing verify update for houseId={}, verify={}", houseId, verify);  // 补充调试日志
            int result = houseMapper.setHouseVerify(houseId, verify);
            logger.info("数据库更新完成，影响行数: {}", result);  // 提升结果日志级别到INFO
            return result;
        } catch (DataAccessException e) {
            logger.error("数据库操作失败 houseId={}, verify={} | 错误类型: {} | 错误信息: {}",
                    houseId, verify, e.getClass().getSimpleName(), e.getMessage());  // 增强错误日志细节
            throw new RuntimeException("数据库操作异常: " + e.getCause().getMessage(), e);  // 携带原始异常信息
        }
    }

}
