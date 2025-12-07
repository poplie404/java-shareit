package ru.practicum.shareit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.dto.request.ItemRequestCreateDto;
import ru.practicum.shareit.dto.request.ItemRequestDto;
import ru.practicum.shareit.entity.Item;
import ru.practicum.shareit.entity.ItemRequest;
import ru.practicum.shareit.entity.User;
import ru.practicum.shareit.mapper.ItemRequestMapper;
import ru.practicum.shareit.repository.ItemRepository;
import ru.practicum.shareit.repository.ItemRequestRepository;
import ru.practicum.shareit.repository.UserRepository;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository repository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestMapper mapper;

    @Override
    public ItemRequestDto create(Long userId, ItemRequestCreateDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        ItemRequest request = mapper.toEntity(dto, user);
        ItemRequest saved = repository.save(request);

        return mapper.toResponseDto(saved, List.of());
    }

    @Override
    public List<ItemRequestDto> getOwn(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        List<ItemRequest> requests = repository.findAllByRequesterIdOrderByCreatedDesc(userId);

        return requests.stream()
                .map(r -> {
                    List<Item> items = itemRepository.findAllByRequestId(r.getId());
                    return mapper.toResponseDto(r, items);
                })
                .toList();
    }

    @Override
    public List<ItemRequestDto> getAll(Long userId, int from, int size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("created").descending());

        Page<ItemRequest> page =
                repository.findAllByRequesterIdNot(userId, pageable);

        return page.stream()
                .map(r -> {
                    List<Item> items = itemRepository.findAllByRequestId(r.getId());
                    return mapper.toResponseDto(r, items);
                })
                .toList();
    }



    @Override
    public ItemRequestDto getById(Long userId, Long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        ItemRequest request = repository.findById(requestId)
                .orElseThrow(() -> new NoSuchElementException("Request not found"));

        List<Item> items = itemRepository.findAllByRequestId(requestId);

        return mapper.toResponseDto(request, items);
    }
}