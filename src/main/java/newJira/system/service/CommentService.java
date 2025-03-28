package newJira.system.service;

import lombok.RequiredArgsConstructor;
import newJira.system.dto.CommentDto;
import newJira.system.entity.Comment;
import newJira.system.mapper.ManagementMapper;
import newJira.system.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final ManagementMapper managementMapper;

    public CommentDto createComment(CommentDto commentDto) {
        Comment comment = managementMapper.toComment(commentDto);
        Comment saved = commentRepository.save(comment);
        return managementMapper.toCommentDto(saved);
    }

    public List<CommentDto> getCommentsByTaskId(Long taskId) {
        return commentRepository.findByTaskId(taskId).stream()
                .map(managementMapper::toCommentDto)
                .toList();
    }
}