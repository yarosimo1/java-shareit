package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.enumStatus.Status;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    // ALL
    @Query("""
        select b
        from Booking b
        join fetch b.item
        where b.booker.id = :userId
        order by b.start desc
    """)
    List<Booking> findAllByBooker(@Param("userId") long userId);

    // CURRENT
    @Query("""
        select b
        from Booking b
        join fetch b.item
        where b.booker.id = :userId
          and b.start <= :now
          and b.end >= :now
        order by b.start desc
    """)
    List<Booking> findCurrentByBooker(
            @Param("userId") long userId,
            @Param("now") LocalDateTime now
    );

    // PAST
    @Query("""
        select b
        from Booking b
        join fetch b.item
        where b.booker.id = :userId
          and b.end < :now
        order by b.start desc
    """)
    List<Booking> findPastByBooker(
            @Param("userId") long userId,
            @Param("now") LocalDateTime now
    );

    // FUTURE
    @Query("""
        select b
        from Booking b
        join fetch b.item
        where b.booker.id = :userId
          and b.start > :now
        order by b.start desc
    """)
    List<Booking> findFutureByBooker(
            @Param("userId") long userId,
            @Param("now") LocalDateTime now
    );

    // WAITING / REJECTED
    @Query("""
        select b
        from Booking b
        join fetch b.item
        where b.booker.id = :userId
          and b.status = :status
        order by b.start desc
    """)
    List<Booking> findByBookerAndStatus(
            @Param("userId") long userId,
            @Param("status") Status status
    );

    // ALL (owner)
    @Query("""
        select b
        from Booking b
        join fetch b.item i
        join fetch i.owner
        where i.owner.id = :userId
        order by b.start desc
    """)
    List<Booking> findAllByOwner(@Param("userId") long userId);

    // CURRENT
    @Query("""
        select b
        from Booking b
        join fetch b.item i
        join fetch i.owner
        where i.owner.id = :userId
          and b.start <= :now
          and b.end >= :now
        order by b.start desc
    """)
    List<Booking> findCurrentByOwner(
            @Param("userId") long userId,
            @Param("now") LocalDateTime now
    );

    // PAST
    @Query("""
        select b
        from Booking b
        join fetch b.item i
        join fetch i.owner
        where i.owner.id = :userId
          and b.end < :now
        order by b.start desc
    """)
    List<Booking> findPastByOwner(
            @Param("userId") long userId,
            @Param("now") LocalDateTime now
    );

    // FUTURE
    @Query("""
        select b
        from Booking b
        join fetch b.item i
        join fetch i.owner
        where i.owner.id = :userId
          and b.start > :now
        order by b.start desc
    """)
    List<Booking> findFutureByOwner(
            @Param("userId") long userId,
            @Param("now") LocalDateTime now
    );

    // WAITING / REJECTED
    @Query("""
        select b
        from Booking b
        join fetch b.item i
        join fetch i.owner
        where i.owner.id = :userId
          and b.status = :status
        order by b.start desc
    """)
    List<Booking> findByOwnerAndStatus(
            @Param("userId") long userId,
            @Param("status") Status status
    );

    @Query("""
        select count(b) > 0
        from Booking b
        where b.item.id = :itemId
          and b.booker.id = :userId
          and b.status = 'APPROVED'
          and b.end < :now
    """)
    boolean hasFinishedBooking(
            @Param("itemId") long itemId,
            @Param("userId") long userId,
            @Param("now") LocalDateTime now
    );

    @Query("""
        select b
        from Booking b
        where b.item.id = :itemId
          and b.status = 'APPROVED'
          and b.start <= :now
        order by b.start desc
    """)
    List<Booking> findLastBooking(
            @Param("itemId") long itemId,
            @Param("now") LocalDateTime now
    );

    @Query("""
        select b
        from Booking b
        where b.item.id = :itemId
          and b.status = 'APPROVED'
          and b.end >= :now
        order by b.start asc
    """)
    List<Booking> findNextBooking(
            @Param("itemId") long itemId,
            @Param("now") LocalDateTime now
    );
}