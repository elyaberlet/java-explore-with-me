package ru.practicum.main.comment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.main.comment.model.Comment;
import ru.practicum.main.comment.model.CommentState;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.event.id = :eventId AND c.state = 'PUBLISHED'")
    Page<Comment> findPublishedByEventId(@Param("eventId") Long eventId, Pageable pageable);

    Page<Comment> findAllByAuthorId(Long authorId, Pageable pageable);

    Page<Comment> findAllByState(CommentState state, Pageable pageable);

    @Query("SELECT c FROM Comment c WHERE LOWER(c.text) LIKE LOWER(CONCAT('%', :text, '%'))")
    Page<Comment> findByTextContaining(@Param("text") String text, Pageable pageable);

}
