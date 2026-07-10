package ru.practicum.main.compilation.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.main.event.model.Event;

import java.util.List;

@Entity
@Table(name = "compilations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    @JoinTable(name = "compilation_events")
    private List<Event> events;

    @Column(nullable = false)
    private Boolean pinned = false;

    @Column(nullable = false, length = 50)
    private String title;
}
