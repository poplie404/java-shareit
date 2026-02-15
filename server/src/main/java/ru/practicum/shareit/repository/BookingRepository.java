package ru.practicum.shareit.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.entity.Booking;
import ru.practicum.shareit.entity.BookingStatus;


import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            Long bookerId, LocalDateTime now1, LocalDateTime now2);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(
            Long bookerId, LocalDateTime now);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(
            Long bookerId, LocalDateTime now);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(
            Long bookerId, BookingStatus status);

    boolean existsByItemIdAndBookerIdAndEndBefore(Long itemId, Long bookerId, LocalDateTime end);

    // ============ OWNER BOOKINGS ============

    @Query("""
        SELECT b FROM Booking b
        WHERE b.item.owner.id = :ownerId
        ORDER BY b.start DESC
    """)
    List<Booking> findAllByOwnerId(Long ownerId);

    @Query("""
        SELECT b FROM Booking b
        WHERE b.item.owner.id = :ownerId
          AND b.start < :now
          AND b.end > :now
        ORDER BY b.start DESC
    """)
    List<Booking> findCurrentByOwner(Long ownerId, LocalDateTime now);

    @Query("""
        SELECT b FROM Booking b
        WHERE b.item.owner.id = :ownerId
          AND b.end < :now
        ORDER BY b.start DESC
    """)
    List<Booking> findPastByOwner(Long ownerId, LocalDateTime now);

    @Query("""
        SELECT b FROM Booking b
        WHERE b.item.owner.id = :ownerId
          AND b.start > :now
        ORDER BY b.start DESC
    """)
    List<Booking> findFutureByOwner(Long ownerId, LocalDateTime now);

    @Query("""
        SELECT b FROM Booking b
        WHERE b.item.owner.id = :ownerId
          AND b.status = :status
        ORDER BY b.start DESC
    """)
    List<Booking> findByOwnerAndStatus(Long ownerId, BookingStatus status);


    @Query("""
        SELECT b FROM Booking b
        WHERE b.item.id = :itemId
          AND b.start < CURRENT_TIMESTAMP
          AND b.status = 'APPROVED'
        ORDER BY b.start DESC
    """)
    List<Booking> findLastBookingRaw(Long itemId, Pageable pageable);

    @Query("""
        SELECT b FROM Booking b
        WHERE b.item.id = :itemId
          AND b.start > CURRENT_TIMESTAMP
          AND b.status = 'APPROVED'
        ORDER BY b.start ASC
    """)
    List<Booking> findNextBookingRaw(Long itemId, Pageable pageable);
}
