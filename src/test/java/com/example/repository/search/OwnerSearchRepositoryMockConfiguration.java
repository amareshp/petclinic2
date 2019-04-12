package com.example.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of OwnerSearchRepository to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class OwnerSearchRepositoryMockConfiguration {

    @MockBean
    private OwnerSearchRepository mockOwnerSearchRepository;

}
