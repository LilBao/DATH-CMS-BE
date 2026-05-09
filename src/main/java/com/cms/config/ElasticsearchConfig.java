package com.cms.config;

import org.apache.hc.client5.http.impl.async.HttpAsyncClientBuilder;
import org.apache.hc.core5.util.TimeValue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.ClientConfiguration.ClientConfigurationCallback;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.elasticsearch.support.HttpHeaders;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.lang.NonNull;

import java.time.Duration;

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
public class ElasticsearchConfig extends ElasticsearchConfiguration {
    @Value("${spring.data.elasticsearch.uris:http://localhost:9200}")
    private String elasticsearchUri;

    @Value("${spring.data.elasticsearch.socket-timeout:30s}")
    private Duration socketTimeout;

    @Value("${spring.data.elasticsearch.connection-timeout:10s}")
    private Duration connectionTimeout;

    @Override
    @NonNull
    public ClientConfiguration clientConfiguration() {
        String hostAndPort = elasticsearchUri.replace("http://", "").replace("https://", "");

        HttpHeaders headers = new HttpHeaders();
        // Compatibility headers for ES 8/9
        headers.add("Accept", "application/vnd.elasticsearch+json;compatible-with=8");
        headers.add("Content-Type", "application/vnd.elasticsearch+json;compatible-with=8");

        // Use a direct lambda for the ClientConfigurationCallback
        // Return a TimeValue object as required by Apache HttpClient 5
        ClientConfigurationCallback<HttpAsyncClientBuilder> keepAliveCallback = clientBuilder ->
                clientBuilder.setKeepAliveStrategy((response, context) -> TimeValue.ofSeconds(15));

        // Chain the configuration safely based on whether SSL is required
        if (elasticsearchUri.startsWith("https://")) {
            return ClientConfiguration.builder()
                    .connectedTo(hostAndPort)
                    .usingSsl()
                    .withConnectTimeout(connectionTimeout)
                    .withSocketTimeout(socketTimeout)
                    .withDefaultHeaders(headers)
                    .withClientConfigurer(keepAliveCallback)
                    .build();
        } else {
            return ClientConfiguration.builder()
                    .connectedTo(hostAndPort)
                    .withConnectTimeout(connectionTimeout)
                    .withSocketTimeout(socketTimeout)
                    .withDefaultHeaders(headers)
                    .withClientConfigurer(keepAliveCallback)
                    .build();
        }
    }
}