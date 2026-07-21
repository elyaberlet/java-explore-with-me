package ru.practicum.main.comment.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.comment.dto.CommentDto;
import ru.practicum.main.comment.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
public class AdminCommentController {

    private final CommentService commentService;

    @GetMapping
    public List<CommentDto> getComments(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) String state,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        return commentService.getAllCommentsForAdmin(text, state, from, size);
    }

    @PatchMapping("/{commentId}")
    public CommentDto moderateComment(
            @PathVariable Long commentId,
            @RequestParam String stateAction) {
        return commentService.moderateComment(commentId, stateAction);
    }

    @DeleteMapping("/{commentId}")
    public String deleteComment(@PathVariable Long commentId) {
        commentService.deleteCommentAdmin(commentId);
        return "Comment deleted by admin";
    }
}