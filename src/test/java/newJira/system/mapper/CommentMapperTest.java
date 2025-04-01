package newJira.system.mapper;

import newJira.system.dto.CommentDto;
import newJira.system.entity.AppUser;
import newJira.system.entity.Comment;
import newJira.system.entity.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommentMapperTest {
    private CommentMapper commentMapper;

    @BeforeEach
    void setUp() {
        commentMapper = new CommentMapperImpl();
    }

    @Test
    @DisplayName("Маппинг Comment → CommentDto")
    void toCommentDtoSuccessTest() {
        Task task = new Task();
        task.setId(100L);

        AppUser author = new AppUser();
        author.setId(200L);

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Комментарий");
        comment.setTask(task);
        comment.setAuthor(author);

        CommentDto dto = commentMapper.toCommentDto(comment);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Комментарий", dto.getText());
        assertEquals(100L, dto.getTaskId());
        assertEquals(200L, dto.getAuthorId());
    }

    @Test
    @DisplayName("Маппинг CommentDto → Comment")
    void toCommentSuccessTest() {
        CommentDto dto = new CommentDto();
        dto.setId(1L);
        dto.setText("Комментарий");
        dto.setTaskId(100L);
        dto.setAuthorId(200L);

        Comment comment = commentMapper.toComment(dto);

        assertNotNull(comment);
        assertEquals("Комментарий", comment.getText());
        assertEquals(100L, comment.getTask().getId());
        assertEquals(200L, comment.getAuthor().getId());
    }

    @Test
    @DisplayName("Маппинг с null-значениями — ничего не ломается")
    void toCommentNullFieldsTest() {
        CommentDto dto = new CommentDto();
        dto.setText("Без автора и задачи");

        Comment comment = commentMapper.toComment(dto);

        assertNotNull(comment);
        assertEquals("Без автора и задачи", comment.getText());
        assertNull(comment.getTask());
        assertNull(comment.getAuthor());
    }
}