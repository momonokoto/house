package com.zpark.vo;

import lombok.Data;

import java.util.List;

@Data
public class PageResponse<T> {
    private List<T> content;
    private long totalElements; // 总记录数
    private int pageNumber;     // 当前页码
    private int pageSize;       // 每页数量
    private long totalPages;    // 总页数

    public static <T> PageResponse<T> of(List<T> content, long totalElements, int pageNumber, int pageSize) {
        PageResponse<T> response = new PageResponse<>();
        response.setContent(content);
        response.setTotalElements(totalElements);
        response.setPageNumber(pageNumber);
        response.setPageSize(pageSize);
        response.setTotalPages((totalElements + pageSize - 1) / pageSize); // 计算总页数
        return response;
    }

    // Getters and Setters
}
