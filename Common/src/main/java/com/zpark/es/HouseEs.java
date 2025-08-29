package com.zpark.es;//package com.zpark.es;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import io.swagger.v3.oas.annotations.media.Schema;
//import lombok.Data;
//import org.springframework.data.annotation.Id;
//import org.springframework.data.elasticsearch.annotations.*;
//import org.springframework.data.elasticsearch.core.geo.GeoPoint;
//
//import java.io.Serializable;
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.List;
//
///**
// * 房源查询条件实体类，映射到Elasticsearch中的house索引
// */
//@Data
//@Document(indexName = "house_index")
//public class HouseEs implements Serializable {
//
//    private static final long serialVersionUID = 1L;
//
//
//    @JsonIgnore
//    @Field(enabled = false) // 不写入索引
//    private String _class;
//
//    @Schema(description = "房源 ID", hidden = true)
//    @Id // Elasticsearch文档ID
//    private String houseId;
//
//    @Schema(description = "房东 ID")
//    @Field(type = FieldType.Long) // 指定Elasticsearch字段类型
//    private Long ownerId;
//
//    @Schema(description = "标题", requiredMode = Schema.RequiredMode.REQUIRED)
//    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word") // 中文分词
//    private String title;
//
//    @Schema(description = "房屋地址", requiredMode = Schema.RequiredMode.REQUIRED)
//    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
//    private String address;
//
//    @Schema(description = "房屋面积", requiredMode = Schema.RequiredMode.REQUIRED)
//    @Field(type = FieldType.Float)
//    private Float area;
//
//    @Schema(description = "房间数", requiredMode = Schema.RequiredMode.REQUIRED)
//    @Field(type = FieldType.Integer)
//    private Integer roomNumber;
//
//    @Schema(description = "户型", requiredMode = Schema.RequiredMode.REQUIRED)
//    @Field(type = FieldType.Keyword) // 不分词，用于精确匹配
//    private String roomType;
//
//    @Schema(description = "租金", requiredMode = Schema.RequiredMode.REQUIRED)
//    @Field(type = FieldType.Double) // 使用Double类型存储金额更精确
//    private BigDecimal rent;
//
//    @Schema(description = "房屋的纬度坐标，用于地图定位")
//    @Field(type = FieldType.Float)
//    private Float latitude;
//
//    @Schema(description = "房屋的经度坐标，用于地图定位")
//    @Field(type = FieldType.Float)
//    private Float longitude;
//
//    @Schema(description = "房屋状态（1-待租，2-已租）", requiredMode = Schema.RequiredMode.REQUIRED)
//    @Field(type = FieldType.Integer)
//    private Integer status;
//
//    @Schema(description = "地区", requiredMode = Schema.RequiredMode.REQUIRED)
//    @Field(type = FieldType.Keyword)
//    private String region;
//
//    @Schema(description = "房源描述", requiredMode = Schema.RequiredMode.REQUIRED)
//    @Field(type = FieldType.Text, analyzer = "ik_max_word")
//    private String description;
//
//    @Schema(description = "图片")
//    @Field(type = FieldType.Keyword)
//    private String img;
//
//    @Schema(description = "视频")
//    @Field(type = FieldType.Keyword)
//    private String video;
//
//    @Schema(description = "是否电梯", requiredMode = Schema.RequiredMode.REQUIRED)
//    @Field(type = FieldType.Integer)
//    private Integer elevator;
//
//    @Schema(description = "详细地址", requiredMode = Schema.RequiredMode.REQUIRED)
//    @Field(type = FieldType.Text, analyzer = "ik_max_word")
//    private String detailedAddress;
//
//    @Schema(description = "审核状态 （0-待审核 1-已审核）")
//    @Field(type = FieldType.Integer)
//    private Integer verify = 0;
//
//    // 可选：添加地理位置字段，用于距离搜索
//    @GeoPointField
//    private GeoPoint location;
//
//    @Schema(description = "创建时间")
//    @Field(type = FieldType.Date, format = DateFormat.date_time, pattern = "yyyy-MM-dd HH:mm:ss")
//    private LocalDateTime createTime;
//
//    @Schema(description = "是否被删除")
//    @Field(type = FieldType.Integer)
//    private Integer is_deleted;
//
//
//    private List<String> titleHighlights;
//    private List<String> addressHighlights;
//    // 构造方法，用于设置地理位置
//    public HouseEs() {
//        // 默认构造函数
//    }
//
//    // 初始化地理位置
//    public void initLocation() {
//        if (latitude != null && longitude != null) {
//            this.location = new GeoPoint(latitude, longitude);
//        }
//    }
//}
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@Document(indexName = "house_index")
public class HouseEs implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Field(type = FieldType.Keyword, name = "house_id")
    @JsonProperty("house_id")
    private Long houseId;

    @Field(type = FieldType.Long, name = "owner_id")
    @JsonProperty("owner_id")
    private Long ownerId;

    @Field(type = FieldType.Text, name = "title", analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    @JsonProperty("title")
    private String title;

    @Field(type = FieldType.Text, name = "address",analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    @JsonProperty("address")
    private String address;

    @Field(type = FieldType.Float, name = "area")
    @JsonProperty("area")
    private Float area;

    @Field(type = FieldType.Integer, name = "room_number")
    @JsonProperty("room_number")
    private Integer roomNumber;

    @Field(type = FieldType.Keyword, name = "room_type")
    @JsonProperty("room_type")
    private String roomType;

    @Field(type = FieldType.Keyword, name = "rent_type")
    @JsonProperty("rent_type")
    private String rentType;

    @Field(type = FieldType.Double, name = "rent")
    @JsonProperty("rent")
    private Double rent;

    @Field(type = FieldType.Float, name = "latitude")
    @JsonProperty("latitude")
    private Float latitude;

    @Field(type = FieldType.Float, name = "longitude")
    @JsonProperty("longitude")
    private Float longitude;

    @Field(type = FieldType.Integer, name = "status")
    @JsonProperty("status")
    private Integer status;

    @Field(type = FieldType.Keyword, name = "region")
    @JsonProperty("region")
    private String region;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", name = "description")
    @JsonProperty("description")
    private String description;

    @Field(type = FieldType.Keyword, name = "img")
    @JsonProperty("img")
    private String img;

    @Field(type = FieldType.Keyword, name = "video")
    @JsonProperty("video")
    private String video;

    @Field(type = FieldType.Integer, name = "elevator")
    @JsonProperty("elevator")
    private Integer elevator;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", name = "detailed_address")
    @JsonProperty("detailed_address")
    private String detailedAddress;

    @Field(type = FieldType.Integer, name = "verify")
    @JsonProperty("verify")
    private Integer verify;

    @Field(type = FieldType.Date, name = "create_time")
    @JsonProperty("create_time")
    private Date createTime;

    @Field(type = FieldType.Date, name = "update_time")
    @JsonProperty("update_time")
    private Date updateTime;

    @Field(type = FieldType.Integer, name = "is_deleted")
    @JsonProperty("is_deleted")
    private Integer is_deleted;

    @GeoPointField
    private GeoPoint location;


    // 忽略 Spring Data 自动生成的 _class 字段
    @JsonIgnore
    @Field(type = FieldType.Keyword, enabled = false)
    private String _class;

    private List<String> titleHighlights;
    private List<String> addressHighlights;

    // 初始化 location 字段（根据 latitude/longitude）
    public void initLocation() {
        if (latitude != null && longitude != null) {
            this.location = new GeoPoint(latitude, longitude);
        }
    }
}
