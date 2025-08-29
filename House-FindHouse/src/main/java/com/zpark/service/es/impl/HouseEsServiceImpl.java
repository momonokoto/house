package com.zpark.service.es.impl;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Highlight;
import com.zpark.es.HouseEs;
import com.zpark.service.es.IHouseEsService;
import com.zpark.vo.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.suggest.response.SortBy;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;


@Service
public class HouseEsServiceImpl implements IHouseEsService {
//    @Autowired
//    private ElasticsearchOperations elasticsearchOperations; // 替代 elasticsearchTemplate

    @Autowired
    private ElasticsearchClient elasticsearchClient;


    /**
     * 使用ElasticsearchOperations实现更复杂的分词查询
     */

    public PageResponse<HouseEs> searchByKeyword(String keyword, int pageNum, int pageSize) {

//         构建主查询（match 查询 title 和 address 字段）
//         构建主查询（multi_match 查询 title 和 address 字段）
        Query query = Query.of(b -> b
                .multiMatch(m -> m
                        .fields("title", "address", "description")
                        .query(keyword)
                        .analyzer("ik_max_word")
                )
        );

        // 构建过滤条件：is_deleted=0
        Query filterQuery = Query.of(b -> b
                .term(t -> t
                        .field("is_deleted")
                        .value(0)
                        .field("verify")
                        .value(1)
                )
        );

        Query filterQuery2 = Query.of(b -> b
                .term(t -> t
                        .field("status")
                        .value(2)
                )
        );

        // 使用 bool 查询组合主查询和过滤条件
        Query combinedQuery = Query.of(b -> b
                .bool(bool -> bool
                        .must(query)
                        .mustNot(filterQuery2)// 过滤条件（status!=2）
                        .filter(filterQuery) // 过滤条件（is_deleted=0）
                )
        );


        Highlight highlight = Highlight.of(h -> h
                .fields("title", f -> f
                        .preTags("<em>")
                        .postTags("</em>")
                )
                .fields("address", f -> f
                        .preTags("<em>")
                        .postTags("</em>")
                )
        );


        // 将 pageNum 转换为从 0 开始的页码，避免前端传入错误导致查询异常
        int from = pageNum > 0 ? (pageNum - 1) * pageSize : 0;
        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index("house_index")
                .query(combinedQuery)
                .highlight(highlight)
                .size(pageSize)
                .from(from)
        );
        try {
            SearchResponse<HouseEs> searchResponse = elasticsearchClient.search(searchRequest, HouseEs.class);

            // 提取结果并处理高亮字段
            List<HouseEs> result = searchResponse.hits().hits().stream()
                    .map(hit -> {
                        HouseEs houseEs = hit.source();

                        // 获取高亮字段
                        Map<String, List<String>> highlightFields = hit.highlight();

                        // 设置高亮内容
                        if (highlightFields != null) {
                            if (highlightFields.containsKey("title")) {
                                assert houseEs != null;
                                houseEs.setTitleHighlights(highlightFields.get("title"));
                            }
                            if (highlightFields.containsKey("address")) {
                                assert houseEs != null;
                                houseEs.setAddressHighlights(highlightFields.get("address"));
                            }
                        }

                        return houseEs;
                    })
                    .toList();
            // 获取总记录数
            assert searchResponse.hits().total() != null;
            long totalElements = searchResponse.hits().total().value();

            // 返回分页响应
            return PageResponse.of(result, totalElements, pageNum, pageSize);
        }catch (IOException e){
            System.out.println("❌ 错误信息：" + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("查询失败");
        }
    }
}


