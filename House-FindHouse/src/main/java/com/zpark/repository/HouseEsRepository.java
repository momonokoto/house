package com.zpark.repository;

import com.zpark.es.HouseEs;
import com.zpark.vo.PageResponse;
import org.springframework.data.elasticsearch.annotations.Highlight;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface HouseEsRepository extends ElasticsearchRepository<HouseEs, Integer>{
}
