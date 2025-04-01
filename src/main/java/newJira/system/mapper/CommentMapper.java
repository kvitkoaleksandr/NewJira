package newJira.system.mapper;

import newJira.system.dto.CommentDto;
import newJira.system.entity.AppUser;
import newJira.system.entity.Comment;
import newJira.system.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    @Mapping(target = "taskId", source = "task.id")
    @Mapping(target = "authorId", source = "author.id")
    CommentDto toCommentDto(Comment comment);

    @Mapping(target = "task", source = "taskId", qualifiedByName = "mapTaskFromId")
    @Mapping(target = "author", source = "authorId", qualifiedByName = "mapUserFromId")
    Comment toComment(CommentDto dto);

    @Named("mapTaskFromId")
    default Task mapTaskFromId(Long taskId) {
        if (taskId == null) return null;
        Task task = new Task();
        task.setId(taskId);
        return task;
    }

    @Named("mapUserFromId")
    default AppUser mapUserFromId(Long userId) {
        if (userId == null) return null;
        AppUser user = new AppUser();
        user.setId(userId);
        return user;
    }
}