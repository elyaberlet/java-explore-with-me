package ru.practicum.main.request.service;

import ru.practicum.main.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.main.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.main.request.dto.ParticipationRequestDto;
import ru.practicum.main.request.model.Request;

import java.util.List;

public interface RequestService {
    List<ParticipationRequestDto> getUserRequests(Long userId);

    ParticipationRequestDto addUserRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);

    List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateRequestStatus(
            Long userId,
            Long eventId,
            EventRequestStatusUpdateRequest updateRequest
    );
}
