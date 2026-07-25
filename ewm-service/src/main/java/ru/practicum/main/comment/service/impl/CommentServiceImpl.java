package ru.practicum.main.comment.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.comment.dto.CommentDto;
import ru.practicum.main.comment.dto.NewCommentDto;
import ru.practicum.main.comment.dto.UpdateCommentDto;
import ru.practicum.main.comment.mapper.CommentMapper;
import ru.practicum.main.comment.model.Comment;
import ru.practicum.main.comment.model.CommentState;
import ru.practicum.main.comment.repository.CommentRepository;
import ru.practicum.main.comment.service.CommentService;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.EventState;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exception.BadRequestException;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public List<CommentDto> getEventComments(Long eventId, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return commentRepository.findPublishedByEventId(eventId, pageable)
                .stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> getUserComments(Long userId, int from, int size) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }

        Pageable pageable = PageRequest.of(from / size, size);
        return commentRepository.findAllByAuthorId(userId, pageable)
                .stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Cannot comment on unpublished event");
        }

        Comment comment = commentMapper.toEntity(newCommentDto, event, author);
        comment = commentRepository.save(comment);

        log.info("User {} added comment to event {}", userId, eventId);

        return commentMapper.toDto(comment);
    }

    @Override
    @Transactional
    public CommentDto updateComment(Long userId, Long commentId, UpdateCommentDto updateDto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment with id=" + commentId + " was not found"));

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new NotFoundException("Comment with id=" + commentId + " was not found for user " + userId);
        }

        if (comment.getState() == CommentState.PUBLISHED) {
            throw new ConflictException("Cannot edit published comment");
        }

        comment.setText(updateDto.getText());
        comment.setUpdatedAt(LocalDateTime.now());

        log.info("User {} updated comment {}", userId, commentId);

        return commentMapper.toDto(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment with id=" + commentId + " was not found"));

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new NotFoundException("Comment with id=" + commentId + " was not found for user " + userId);
        }

        if (comment.getState() == CommentState.PUBLISHED) {
            comment.setState(CommentState.CANCELED);
            comment.setUpdatedAt(LocalDateTime.now());
        } else {
            commentRepository.delete(comment);
        }

        log.info("User {} deleted comment {}", userId, commentId);
    }

    @Override
    @Transactional
    public CommentDto moderateComment(Long commentId, String stateAction) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment with id=" + commentId + " was not found"));

        if ("PUBLISH".equals(stateAction)) {
            comment.setState(CommentState.PUBLISHED);
        } else if ("REJECT".equals(stateAction)) {
            comment.setState(CommentState.REJECTED);
        } else {
            throw new BadRequestException("Invalid state action: " + stateAction);
        }

        return commentMapper.toDto(comment);
    }

    @Override
    @Transactional
    public void deleteCommentAdmin(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new NotFoundException("Comment with id=" + commentId + " was not found");
        }
        commentRepository.deleteById(commentId);
        log.info("Comment {} deleted by admin", commentId);
    }

    @Override
    public List<CommentDto> getAllCommentsForAdmin(String text, String state, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        Page<Comment> comments;

        if (text != null && !text.isEmpty()) {
            comments = commentRepository.findByTextContaining(text, pageable);
        } else if (state != null) {
            try {
                CommentState commentState = CommentState.valueOf(state);
                comments = commentRepository.findAllByState(commentState, pageable);
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid state: " + state);
            }
        } else {
            comments = commentRepository.findAll(pageable);
        }

        return comments.stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }
}