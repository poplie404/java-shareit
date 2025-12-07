package ru.practicum.shareit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.entity.User;


public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
}
