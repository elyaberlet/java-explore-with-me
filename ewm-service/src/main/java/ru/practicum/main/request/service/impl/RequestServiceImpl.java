package ru.practicum.main.request.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.EventState;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exception.BadRequestException;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.main.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.main.request.dto.ParticipationRequestDto;
import ru.practicum.main.request.mapper.RequestMapper;
import ru.practicum.main.request.model.Request;
import ru.practicum.main.request.model.RequestStatus;
import ru.practicum.main.request.repository.RequestRepository;
import ru.practicum.main.request.service.RequestService;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestMapper requestMapper;

    @Override
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }

        return requestRepository.findAllByRequesterId(userId)
                .stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto addUserRequest(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Initiator cannot add request to his own event");
        }

        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Event is not published");
        }

        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ConflictException("Request already exists");
        }

        long confirmedRequests = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        if (event.getParticipantLimit() > 0 && confirmedRequests >= event.getParticipantLimit()) {
            throw new ConflictException("Participant limit has been reached");
        }

        Request request = new Request();
        request.setCreated(LocalDateTime.now());
        request.setEvent(event);
        request.setRequester(user);

        if ((event.getRequestModeration() != null && !event.getRequestModeration())
                || event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        } else {
            request.setStatus(RequestStatus.PENDING);
        }

        request = requestRepository.save(request);
        log.info("User {} created request for event {}", userId, eventId);

        return requestMapper.toDto(request);
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request with id=" + requestId + " was not found"));

        if (!request.getRequester().getId().equals(userId)) {
            throw new NotFoundException("Request with id=" + requestId + " was not found for user " + userId);
        }

        if (request.getStatus() == RequestStatus.CONFIRMED) {
            Event event = request.getEvent();
            event.setConfirmedRequests(event.getConfirmedRequests() - 1);
            eventRepository.save(event);
        }

        request.setStatus(RequestStatus.CANCELED);
        request = requestRepository.save(request);

        log.info("User {} canceled request {}", userId, requestId);

        return requestMapper.toDto(request);
    }

    @Override
    public List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Event with id=" + eventId + " was not found for user " + userId);
        }

        return requestRepository.findAllByEventId(eventId)
                .stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequestStatus(
            Long userId,
            Long eventId,
            EventRequestStatusUpdateRequest updateRequest) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Event with id=" + eventId + " was not found for user " + userId);
        }

        if (updateRequest.getStatus() != RequestStatus.CONFIRMED &&
                updateRequest.getStatus() != RequestStatus.REJECTED) {
            throw new BadRequestException("Status must be CONFIRMED or REJECTED");
        }

        List<Request> requests = requestRepository.findAllByIdIn(updateRequest.getRequestIds());

        for (Request request : requests) {
            if (request.getStatus() != RequestStatus.PENDING) {
                throw new ConflictException("Request is not in PENDING state");
            }
        }

        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        if (updateRequest.getStatus() == RequestStatus.CONFIRMED) {
            long currentConfirmed = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
            long participantLimit = event.getParticipantLimit();

            if (participantLimit > 0 && currentConfirmed >= participantLimit) {
                throw new ConflictException("The participant limit has been reached");
            }

            for (Request request : requests) {
                if (participantLimit > 0 && currentConfirmed >= participantLimit) {
                    request.setStatus(RequestStatus.REJECTED);
                    rejectedRequests.add(requestMapper.toDto(request));
                } else {
                    request.setStatus(RequestStatus.CONFIRMED);
                    confirmedRequests.add(requestMapper.toDto(request));
                    currentConfirmed++;
                    event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                }
                requestRepository.save(request);
            }
            eventRepository.save(event);
        } else {
            for (Request request : requests) {
                request.setStatus(RequestStatus.REJECTED);
                rejectedRequests.add(requestMapper.toDto(request));
                requestRepository.save(request);
            }
        }

        log.info("User {} updated {} requests for event {}", userId, requests.size(), eventId);

        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }
}