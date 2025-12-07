package ru.practicum.shareit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.dto.comment.CommentDto;
import ru.practicum.shareit.entity.Comment;
import ru.practicum.shareit.entity.Item;
import ru.practicum.shareit.entity.User;
import ru.practicum.shareit.mapper.CommentMapper;
import ru.practicum.shareit.repository.CommentRepository;
import ru.practicum.shareit.repository.ItemRepository;
import ru.practicum.shareit.repository.UserRepository;
import ru.practicum.shareit.repository.BookingRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь не найден"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Вещь не найдена"));

        // Проверяем, что пользователь действительно брал эту вещь
        boolean hasFinishedBooking = bookingRepository
                .existsByItemIdAndBookerIdAndEndBefore(itemId, userId, LocalDateTime.now());

        if (!hasFinishedBooking) {
            throw new IllegalArgumentException("Оставить комментарий может только тот, кто брал вещь");
        }

        Comment comment = CommentMapper.toComment(commentDto, item, user);
        Comment saved = commentRepository.save(comment);

        return CommentMapper.toCommentDto(saved);
    }

    @Override
    public List<CommentDto> getCommentsByItem(Long itemId) {
        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        return comments.stream()
                .map(CommentMapper::toCommentDto)
                .toList();
    }
}