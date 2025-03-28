package newJira.system.service;

import lombok.RequiredArgsConstructor;
import newJira.system.dto.CommentDto;
import newJira.system.entity.Comment;
import newJira.system.mapper.CommentMapper;
import newJira.system.mapper.ManagementMapper;
import newJira.system.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    public CommentDto createComment(CommentDto commentDto) {
        Comment comment = commentMapper.toComment(commentDto);
        Comment saved = commentRepository.save(comment);
        return commentMapper.toCommentDto(saved);
    }

    public List<CommentDto> getCommentsByTaskId(Long taskId) {
        return commentRepository.findByTaskId(taskId).stream()
                .map(commentMapper::toCommentDto)
                .toList();
    }
}