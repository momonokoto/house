package com.zpark.deepseek.util;

import com.zpark.dto.HouseQueryConditionsDto;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryParser {

    private static final String[] SUPPORTED_CITIES = {
            "北京", "上海", "天津", "重庆", "广州", "深圳", "南京",
            "杭州", "成都", "武汉", "西安", "长沙", "青岛", "厦门", "苏州"
    };

    public static HouseQueryConditionsDto parse(String question) {
        HouseQueryConditionsDto dto = new HouseQueryConditionsDto();

        // 匹配城市
        for (String city : SUPPORTED_CITIES) {
            if (question.contains(city)) {
                dto.setRegion(city);
                break;
            }
        }

        // 匹配价格区间：如“2000到3000元”
        //匹配模式
        Pattern pricePattern = Pattern.compile("(\\d+)[到至](\\d+)元");
        Matcher priceMatcher = pricePattern.matcher(question);
        if (priceMatcher.find()) {
            dto.setMinPrice(new BigDecimal(priceMatcher.group(1)));
            dto.setMaxPrice(new BigDecimal(priceMatcher.group(2)));
        } else {
            // 单边匹配
            Pattern minPricePattern = Pattern.compile("高于(\\d+)元|大于(\\d+)元");
            Matcher minPriceMatcher = minPricePattern.matcher(question);
            if (minPriceMatcher.find()) {
                String price = minPriceMatcher.group(1) != null ? minPriceMatcher.group(1) : minPriceMatcher.group(2);
                dto.setMinPrice(new BigDecimal(price));
            }

            Pattern maxPricePattern = Pattern.compile("低于(\\d+)元|小于(\\d+)元");
            Matcher maxPriceMatcher = maxPricePattern.matcher(question);
            if (maxPriceMatcher.find()) {
                String price = maxPriceMatcher.group(1) != null ? maxPriceMatcher.group(1) : maxPriceMatcher.group(2);
                dto.setMaxPrice(new BigDecimal(price));
            }
        }

        // 匹配面积
        Pattern areaPattern = Pattern.compile("面积(\\d+)[到至](\\d+)");
        Matcher areaMatcher = areaPattern.matcher(question);
        if (areaMatcher.find()) {
            dto.setMinArea(Float.parseFloat(areaMatcher.group(1)));
            dto.setMaxArea(Float.parseFloat(areaMatcher.group(2)));
        }

        // 户型识别
        if (question.contains("一室")) {
            dto.setRoomType("1");
        } else if (question.contains("二室")) {
            dto.setRoomType("2");
        } else if (question.contains("三室")) {
            dto.setRoomType("3");
        } else if (question.contains("三室以上") || question.contains("多室")) {
            dto.setRoomType("4");
        }

        // 租赁方式
        if (question.contains("合租")) {
            dto.setRentType("1");
        } else if (question.contains("整租")) {
            dto.setRentType("2");
        } else if (question.contains("公寓")) {
            dto.setRentType("3");
        }

        return dto;
    }
}