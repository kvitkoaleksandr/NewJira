package newJira.system.mapper;

import newJira.system.dto.task.TaskDto;
import newJira.system.entity.Priority;
import newJira.system.entity.Status;
import newJira.system.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper {

    @Mapping(target = "status", source = "status", qualifiedByName = "mapStatusFromEnum")
    @Mapping(target = "priority", source = "priority", qualifiedByName = "mapPriorityFromEnum")
    TaskDto toTaskDto(Task task);

    @Mapping(target = "status", source = "status", qualifiedByName = "mapStatusFromString")
    @Mapping(target = "priority", source = "priority", qualifiedByName = "mapPriorityFromString")
    Task toTask(TaskDto taskDto);

    @Named("mapStatusFromEnum")
    default String mapStatusFromEnum(Status status) {
        return status.name();
    }

    @Named("mapPriorityFromEnum")
    default String mapPriorityFromEnum(Priority priority) {
        return priority.name();
    }

    @Named("mapStatusFromString")
    default Status mapStatusFromString(String status) {
        return Status.valueOf(status);
    }

    @Named("mapPriorityFromString")
    default Priority mapPriorityFromString(String priority) {
        return Priority.valueOf(priority);
    }
}