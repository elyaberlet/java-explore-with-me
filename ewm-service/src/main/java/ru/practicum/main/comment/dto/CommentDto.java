package ru.practicum.main.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.main.comment.model.CommentState;
import ru.practicum.main.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;
    private String text;
    private UserShortDto author;
    private Long eventId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private CommentState state;
}