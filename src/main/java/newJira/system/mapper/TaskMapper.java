package newJira.system.mapper;

import newJira.system.dto.TaskDto;
import newJira.system.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper {
    TaskDto toTaskDto(Task task);
    Task toTask(TaskDto taskDto);
}