package ru.practicum.main.compilation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class NewCompilationDto {
    private Boolean pinned = false;

    @NotBlank
    @Size(min = 1, max = 50)
    private String title;

    private List<Long> events;
}