package ru.practicum.statistic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EndpointHitDto {
    private Long id;

    @NotBlank(message = "Название сервиса не может быть пустым")
    private String app;

    @NotBlank(message = "URI не может быть пустым")
    private String uri;

    @NotBlank(message = "IP-адрес не может быть пустым")
    private String ip;

    @NotNull(message = "Время запроса не может быть пустым")
    @PastOrPresent(message = "Время не может быть в будущем")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}