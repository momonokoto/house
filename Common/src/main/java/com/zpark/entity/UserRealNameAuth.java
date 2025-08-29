package com.zpark.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户实名认证信息实体类
 */
@TableName("user_real_name_auth") // MyBatis-Plus：指定实体类映射到的数据库表名
@Data // Lombok：自动生成 getter, setter, toString, equals, hashCode 方法
@NoArgsConstructor // Lombok：生成无参构造函数
@AllArgsConstructor // Lombok：生成全参构造函数
@Schema(description = "用户实名认证信息") // Swagger：为整个实体类添加描述
public class UserRealNameAuth {

    /**
     * 主键ID
     * 使用 IdType.ASSIGN_ID 策略，ID由MyBatis-Plus自动生成（雪花算法）
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID", example = "1799277085186000001") // Swagger：字段描述和示例
    private Long id;

    @TableField("user_id") // MyBatis-Plus：对应数据库列名
    @Schema(description = "关联用户ID", example = "50001", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    @TableField("real_name")
    @Schema(description = "姓名", example = "张三", maxLength = 64, requiredMode = Schema.RequiredMode.REQUIRED)
    private String realName;

    @TableField("id_card_number")
    @Schema(description = "身份证号", example = "330xxxxxxxxxxxxxxx", maxLength = 32, requiredMode = Schema.RequiredMode.REQUIRED)
    private String idCardNumber;

    @TableField("phone_number")
    @Schema(description = "手机号", example = "13812345678", maxLength = 20, requiredMode = Schema.RequiredMode.REQUIRED)
    private String phoneNumber;

    @TableField("auth_status")
    @Schema(description = "认证状态：0-待认证，1-认证成功，2-认证失败", example = "1", defaultValue = "0")
    private Integer authStatus;

    @TableField("auth_time")
    @Schema(description = "认证成功时间", example = "2023-10-26 14:30:00")
    private LocalDateTime authTime;

    /**
     * 创建时间
     * 注意：对于这种由数据库自动填充的字段（如 `CURRENT_TIMESTAMP`），
     * 如果你希望MyBatis-Plus在插入时忽略它并让数据库自动处理，
     * 可以不加 `@TableField` 注解或使用 `@TableField(fill = FieldFill.INSERT)`。
     * 但如果你的数据库也设置了 `ON UPDATE CURRENT_TIMESTAMP`，
     * 且你希望MyBatis-Plus在更新时也忽略，则需要更精细的配置。
     * 这里我们假设数据库会正确处理，实体类只是映射。
     */
    @TableField("create_time")
    @Schema(description = "创建时间", example = "2023-10-26 14:00:00")
    private LocalDateTime createTime;

    /**
     * 更新时间
     * 同上，如果希望数据库自动填充，MyBatis-Plus默认会传递null值，让数据库处理。
     * 如果需要MyBatis-Plus自动填充，可以使用 `FieldFill.INSERT_UPDATE`。
     */
    @TableField("update_time")
    @Schema(description = "更新时间", example = "2023-10-26 14:35:00")
    private LocalDateTime updateTime;

    // 注意：在使用MyBatis-Plus时，通常不再需要JPA的@PrePersist和@PreUpdate注解
    // 因为MyBatis-Plus有自己的字段填充策略和拦截器机制来处理创建/更新时间等公共字段。
    // 如果你同时使用了MyBatis-Plus和JPA，这可能会导致冲突或意外行为。
}