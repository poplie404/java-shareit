package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.time.LocalDateTime;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findAllByItemIdOrderByStartDesc(Long itemId);

    boolean existsByItemIdAndBookerIdAndEndBefore(Long itemId, Long bookerId, LocalDateTime end);

    @Query("""
        select b from Booking b
        join Item i on b.itemId = i.id
        where i.ownerId = :ownerId
        order by b.start desc
    """)
    List<Booking> findAllByOwnerId(Long ownerId);
}
