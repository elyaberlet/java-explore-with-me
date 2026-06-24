package ru.practicum.statistic.service;

import ru.practicum.statistic.dto.EndpointHitDto;
import ru.practicum.statistic.dto.ViewStatsDto;

import java.util.List;

public interface StatsService {
    void saveHit(EndpointHitDto dto);
    List<ViewStatsDto> getStats(String start, String end, List<String> uris, Boolean unique);
}
