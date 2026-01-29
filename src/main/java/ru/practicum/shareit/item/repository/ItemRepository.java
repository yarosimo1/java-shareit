package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("""
    SELECT i
    FROM Item i
    WHERE i.available = true
      AND (:query IS NOT NULL AND TRIM(:query) <> ''
           AND (LOWER(i.name) LIKE LOWER(CONCAT('%', :query, '%'))
             OR LOWER(i.description) LIKE LOWER(CONCAT('%', :query, '%')))
          )
    """)
    Collection<Item> searchItems(@Param("query") String query);

    @Query("""
            SELECT i
            FROM Item i
            LEFT JOIN FETCH i.comments c
            LEFT JOIN FETCH c.author
            WHERE i.owner.id = :ownerId
    """)
    List<Item> findAllWithCommentsByOwnerId(@Param("ownerId") long ownerId);

    @Query("""
            SELECT i
            FROM Item i
            LEFT JOIN FETCH i.comments c
            LEFT JOIN FETCH c.author
            WHERE i.id = :itemId
    """)
    Optional<Item> findByIdWithComments(@Param("itemId") long itemId);
}