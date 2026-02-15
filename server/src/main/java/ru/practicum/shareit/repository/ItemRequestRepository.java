package ru.practicum.shareit.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.entity.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    // получить запросы определённого пользователя
    List<ItemRequest> findAllByRequesterIdOrderByCreatedDesc(Long userId);

    // все запросы других пользователей
    @Query("SELECT ir FROM ItemRequest ir WHERE ir.requester.id <> :userId ORDER BY ir.created DESC")
    Page<ItemRequest> findAllByRequesterIdNot(@Param("userId") Long userId, Pageable pageable);
}
