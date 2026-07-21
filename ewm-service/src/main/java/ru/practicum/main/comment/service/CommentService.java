package ru.practicum.main.comment.service;

import ru.practicum.main.comment.dto.CommentDto;
import ru.practicum.main.comment.dto.NewCommentDto;
import ru.practicum.main.comment.dto.UpdateCommentDto;

import java.util.List;

public interface CommentService {

    List<CommentDto> getEventComments(Long eventId, int from, int size);

    List<CommentDto> getUserComments(Long userId, int from, int size);

    CommentDto addComment(Long userId, Long eventId, NewCommentDto newCommentDto);

    CommentDto updateComment(Long userId, Long commentId, UpdateCommentDto updateDto);

    void deleteComment(Long userId, Long commentId);

    CommentDto moderateComment(Long commentId, String stateAction);

    void deleteCommentAdmin(Long commentId);

    List<CommentDto> getAllCommentsForAdmin(String text, String state, int from, int size);
}