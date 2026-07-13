package ru.practicum.main.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.EventState;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query(value = "SELECT * FROM events e " +
            "WHERE e.state = 'PUBLISHED' " +
            "AND (CAST(:text AS text) IS NULL OR e.annotation ILIKE CONCAT('%', CAST(:text AS text), '%') " +
            "OR e.description ILIKE CONCAT('%', CAST(:text AS text), '%')) " +
            "AND (CAST(:categories AS text) IS NULL OR e.category_id IN (:categories)) " +
            "AND (CAST(:paid AS text) IS NULL OR e.paid = :paid) " +
            "AND (e.event_date BETWEEN CAST(:rangeStart AS timestamp) AND CAST(:rangeEnd AS timestamp)) " +
            "AND (:onlyAvailable = false OR e.participant_limit = 0 OR e.confirmed_requests < e.participant_limit)",
            nativeQuery = true)
    Page<Event> findPublishedEvents(
            @Param("text") String text,
            @Param("categories") List<Long> categories,
            @Param("paid") Boolean paid,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            @Param("onlyAvailable") Boolean onlyAvailable,
            Pageable pageable
    );

    Page<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    @Query("SELECT e FROM Event e " +
            "WHERE (:users IS NULL OR e.initiator.id IN :users) " +
            "AND (:states IS NULL OR e.state IN :states) " +
            "AND (:categories IS NULL OR e.category.id IN :categories) " +
            "AND (COALESCE(:rangeStart, e.eventDate) <= e.eventDate) " +
            "AND (COALESCE(:rangeEnd, e.eventDate) >= e.eventDate)")
    Page<Event> findEventsForAdmin(
            @Param("users") List<Long> users,
            @Param("states") List<EventState> states,
            @Param("categories") List<Long> categories,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            Pageable pageable
    );

    boolean existsByCategoryId(Long categoryId);
}