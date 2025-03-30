package newJira.system.service;

import newJira.system.dto.CommentDto;
import newJira.system.entity.Comment;
import newJira.system.mapper.CommentMapper;
import newJira.system.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    private static final Long TASK_ID = 100L;
    private static final Long COMMENT_ID = 1L;
    private static final String TEXT = "Test comment";

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentService commentService;

    private Comment comment;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        comment = new Comment();
        comment.setId(COMMENT_ID);
        comment.setText(TEXT);

        commentDto = new CommentDto();
        commentDto.setText(TEXT);
        commentDto.setTaskId(TASK_ID);
    }

    @Test
    @DisplayName("Успешное создание комментария")
    void createCommentSuccessTest() {
        when(commentMapper.toComment(commentDto)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.toCommentDto(comment)).thenReturn(commentDto);

        CommentDto result = commentService.createComment(commentDto);

        assertEquals(TEXT, result.getText());
        verify(commentRepository).save(comment);
    }

    @Test
    @DisplayName("Получение комментариев по ID задачи")
    void getCommentsByTaskIdSuccessTest() {
        when(commentRepository.findByTaskId(TASK_ID)).thenReturn(List.of(comment));
        when(commentMapper.toCommentDto(comment)).thenReturn(commentDto);

        List<CommentDto> result = commentService.getCommentsByTaskId(TASK_ID);

        assertEquals(1, result.size());
        assertEquals(TEXT, result.get(0).getText());
        verify(commentRepository).findByTaskId(TASK_ID);
    }
}