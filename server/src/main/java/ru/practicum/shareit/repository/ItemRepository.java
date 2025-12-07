package ru.practicum.shareit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.entity.Item;


import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerId(Long ownerId);

    Item findItemByOwnerId(Long ownerId);

    @Query("""
        select i from Item i
        where (lower(i.name) like lower(concat('%', :text, '%'))
            or lower(i.description) like lower(concat('%', :text, '%')))
        and i.available = true
    """)
    List<Item> search(String text);

    List<Item> findAllByRequestId(Long requestId);

}
