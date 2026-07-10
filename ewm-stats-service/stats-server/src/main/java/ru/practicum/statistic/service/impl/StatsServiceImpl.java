package ru.practicum.statistic.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.statistic.dto.EndpointHitDto;
import ru.practicum.statistic.dto.ViewStatsDto;
import ru.practicum.statistic.model.Hit;
import ru.practicum.statistic.repository.HitRepository;
import ru.practicum.statistic.service.StatsService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final HitRepository hitRepository;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void saveHit(EndpointHitDto dto) {
        Hit hit = new Hit();
        hit.setApp(dto.getApp());
        hit.setUri(dto.getUri());
        hit.setIp(dto.getIp());
        hit.setTimestamp(dto.getTimestamp());
        hitRepository.save(hit);
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        LocalDateTime startDateTime = LocalDateTime.parse(start, FORMATTER);
        LocalDateTime endDateTime = LocalDateTime.parse(end, FORMATTER);

        if (startDateTime.isAfter(endDateTime)) {
            throw new IllegalArgumentException(
                    "Дата начала (" + start + ") не может быть позже даты конца (" + end + ")"
            );
        }

        boolean hasUris = uris != null && !uris.isEmpty();
        boolean isUnique = Boolean.TRUE.equals(unique);

        if (isUnique) {
            return hasUris
                    ? hitRepository.findUniqueStatsWithUris(startDateTime, endDateTime, uris)
                    : hitRepository.findUniqueStats(startDateTime, endDateTime);
        } else {
            return hasUris
                    ? hitRepository.findAllStatsWithUris(startDateTime, endDateTime, uris)
                    : hitRepository.findAllStats(startDateTime, endDateTime);
        }
    }
}