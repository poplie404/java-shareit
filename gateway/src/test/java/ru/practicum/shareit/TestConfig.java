package ru.practicum.shareit;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.comment.CommentService;


@TestConfiguration
public class TestConfig {
    @Bean
    @Primary
    public ItemService itemService() {
        return Mockito.mock(ItemService.class);
    }

    @Bean
    @Primary
    public CommentService commentService() {
        return Mockito.mock(CommentService.class);
    }
}