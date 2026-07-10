package ru.practicum.statistic.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.statistic.dto.EndpointHitDto;
import ru.practicum.statistic.dto.ViewStatsDto;
import ru.practicum.statistic.model.Hit;
import ru.practicum.statistic.repository.HitRepository;
import ru.practicum.statistic.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final HitRepository hitRepository;

    @Override
    public void saveHit(EndpointHitDto hitDto) {
        Hit hit = new Hit();
        hit.setApp(hitDto.getApp());
        hit.setUri(hitDto.getUri());
        hit.setIp(hitDto.getIp());
        hit.setTimestamp(hitDto.getTimestamp());
        hitRepository.save(hit);
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (unique) {
            if (uris != null && !uris.isEmpty()) {
                return hitRepository.findUniqueStatsWithUris(start, end, uris);
            }
            return hitRepository.findUniqueStats(start, end);
        } else {
            if (uris != null && !uris.isEmpty()) {
                return hitRepository.findAllStatsWithUris(start, end, uris);
            }
            return hitRepository.findAllStats(start, end);
        }
    }
}