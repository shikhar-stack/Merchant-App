package com.merchant.app.repository;

import com.merchant.app.document.CropDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import java.util.List;

public interface CropSearchRepository extends ElasticsearchRepository<CropDocument, String> {
    List<CropDocument> findByNameContaining(String name);
}
