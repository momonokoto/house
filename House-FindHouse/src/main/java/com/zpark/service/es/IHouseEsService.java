package com.zpark.service.es;


import com.zpark.es.HouseEs;
import com.zpark.vo.PageResponse;
import org.springframework.data.elasticsearch.annotations.Highlight;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.io.IOException;
import java.util.List;

public interface IHouseEsService{
    PageResponse<HouseEs> searchByKeyword(String keyword, int pageNum, int pageSize) throws IOException;

}
