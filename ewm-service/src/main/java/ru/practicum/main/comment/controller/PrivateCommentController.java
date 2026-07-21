package ru.practicum.main.comment.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.comment.dto.CommentDto;
import ru.practicum.main.comment.dto.NewCommentDto;
import ru.practicum.main.comment.dto.UpdateCommentDto;
import ru.practicum.main.comment.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}")
@RequiredArgsConstructor
public class PrivateCommentController {

    private final CommentService commentService;

    @GetMapping("/comments")
    public List<CommentDto> getUserComments(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        return commentService.getUserComments(userId, from, size);
    }

    @PostMapping("/events/{eventId}/comments")
    public CommentDto addComment(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @Valid @RequestBody NewCommentDto newCommentDto) {
        return commentService.addComment(userId, eventId, newCommentDto);
    }

    @PatchMapping("/comments/{commentId}")
    public CommentDto updateComment(
            @PathVariable Long userId,
            @PathVariable Long commentId,
            @Valid @RequestBody UpdateCommentDto updateDto) {
        return commentService.updateComment(userId, commentId, updateDto);
    }

    @DeleteMapping("/comments/{commentId}")
    public void deleteComment(
            @PathVariable Long userId,
            @PathVariable Long commentId) {
        commentService.deleteComment(userId, commentId);
    }
}