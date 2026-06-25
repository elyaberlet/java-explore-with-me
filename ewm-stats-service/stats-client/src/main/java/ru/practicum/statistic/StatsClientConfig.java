package ru.practicum.statistic;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StatsClientConfig {

    @Value("${stat-service.url:http://localhost:9090}")
    private String serverUrl;

    @Bean
    public StatsClient statsClient() {
        return new StatsClient(serverUrl);
    }
}
