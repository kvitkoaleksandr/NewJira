package newJira.system.mapper;

import newJira.system.dto.CommentDto;
import newJira.system.dto.TaskDto;
import newJira.system.dto.UserDto;
import newJira.system.entity.AppUser;
import newJira.system.entity.Comment;
import newJira.system.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ManagementMapper {
    //UserMapper
    UserDto toUserDto(AppUser appUser);

    AppUser toUser(UserDto userDto);

    //CommentMapper
    CommentDto toCommentDto(Comment comment);

    Comment toComment(CommentDto commentDto);

    //TaskMapper
    TaskDto toTaskDto(Task task);

    Task toTask(TaskDto taskDto);
}