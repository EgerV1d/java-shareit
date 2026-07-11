package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class CommentMapperTest {

    private final CommentMapper mapper = new CommentMapper();

    @Test
    void toEntity_shouldMapCorrectly() {
        String text = "Great item!";
        Item item = new Item();
        item.setId(1L);
        User author = new User();
        author.setId(1L);
        author.setName("Author");

        Comment comment = mapper.toEntity(text, item, author);

        assertThat(comment.getText()).isEqualTo(text);
        assertThat(comment.getItem()).isEqualTo(item);
        assertThat(comment.getAuthor()).isEqualTo(author);
        assertThat(comment.getCreated()).isNotNull();
    }

    @Test
    void toDto_shouldMapCorrectly() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Great item!");

        User author = new User();
        author.setId(1L);
        author.setName("Author");
        comment.setAuthor(author);

        LocalDateTime created = LocalDateTime.now();
        comment.setCreated(created);

        CommentDto dto = mapper.toDto(comment);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getText()).isEqualTo("Great item!");
        assertThat(dto.getAuthorName()).isEqualTo("Author");
        assertThat(dto.getCreated()).isEqualTo(created);
    }

    @Test
    void toDto_shouldReturnNullWhenCommentIsNull() {
        assertThat(mapper.toDto(null)).isNull();
    }

}
