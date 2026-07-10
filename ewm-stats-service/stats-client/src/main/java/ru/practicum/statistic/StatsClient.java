package ru.practicum.statistic;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import org.springframework.web.client.RestTemplate;
import ru.practicum.statistic.dto.EndpointHitDto;

public class StatsClient {

    private final RestTemplate restTemplate;
    private final String serverUrl;

    public StatsClient(String serverUrl) {
        this.serverUrl = serverUrl;
        this.restTemplate = new RestTemplate();
    }

    public void hit(EndpointHitDto dto) {
        restTemplate.exchange(
                serverUrl + "/hit",
                HttpMethod.POST,
                new HttpEntity<>(dto),
                Void.class
        );
    }
}
