package ru.practicum.main.event.service;

import ru.practicum.main.exception.BadRequestException;
import ru.practicum.main.event.dto.*;
import ru.practicum.main.event.model.EventState;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    List<EventShortDto> getPublicEvents(
            String text,
            List<Long> categories,
            Boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Boolean onlyAvailable,
            String sort,
            int from,
            int size,
            HttpServletRequest request
    );

    EventFullDto getPublicEvent(Long id, HttpServletRequest request);

    List<EventShortDto> getUserEvents(Long userId, int from, int size);

    EventFullDto addEvent(Long userId, NewEventDto newEventDto) throws BadRequestException;

    EventFullDto getUserEvent(Long userId, Long eventId);

    EventFullDto updateUserEvent(Long userId, Long eventId, UpdateEventUserRequest updateRequest) throws BadRequestException;

    List<EventFullDto> getAdminEvents(
            List<Long> users,
            List<EventState> states,
            List<Long> categories,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            int from,
            int size
    );

    EventFullDto updateAdminEvent(Long eventId, UpdateEventAdminRequest updateRequest) throws BadRequestException;
}