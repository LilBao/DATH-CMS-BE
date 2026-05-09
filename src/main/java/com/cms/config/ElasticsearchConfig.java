package com.cms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.cms.repository.search")
@EnableJpaRepositories(basePackages = {
    "com.cms.repository.booking",
    "com.cms.repository.cinema",
    "com.cms.repository.customer",
    "com.cms.repository.movie",
    "com.cms.repository.products",
    "com.cms.repository.screening",
    "com.cms.repository.staff"
})
public class ElasticsearchConfig {
}
