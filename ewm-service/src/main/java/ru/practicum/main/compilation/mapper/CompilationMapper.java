package ru.practicum.main.compilation.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.main.compilation.dto.CompilationDto;
import ru.practicum.main.compilation.dto.NewCompilationDto;
import ru.practicum.main.compilation.model.Compilation;
import ru.practicum.main.event.mapper.EventMapper;
import ru.practicum.main.event.model.Event;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CompilationMapper {

    private final EventMapper eventMapper;

    public Compilation toEntity(NewCompilationDto dto, List<Event> events) {
        Compilation compilation = new Compilation();
        compilation.setEvents(events);
        compilation.setPinned(dto.getPinned() != null ? dto.getPinned() : false);
        compilation.setTitle(dto.getTitle());
        return compilation;
    }

    public CompilationDto toDto(Compilation compilation) {
        CompilationDto dto = new CompilationDto();
        dto.setId(compilation.getId());
        dto.setPinned(compilation.getPinned());
        dto.setTitle(compilation.getTitle());

        if (compilation.getEvents() != null) {
            dto.setEvents(compilation.getEvents().stream()
                    .map(eventMapper::toShortDto)
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}