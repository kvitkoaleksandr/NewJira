package newJira.system.mapper;

import newJira.system.dto.CommentDto;
import newJira.system.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {
    CommentDto toCommentDto(Comment comment);
    Comment toComment(CommentDto commentDto);
}