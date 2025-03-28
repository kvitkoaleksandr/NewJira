package newJira.system.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import newJira.system.entity.Priority;
import newJira.system.entity.Status;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDto {

    private Long id;
    @NotEmpty(message = "Title is can't be required")
    private String title;
    private Status status = Status.TO_DO;
    @NotNull(message = "Priority can't be null")
    private Priority priority;
    @NotNull(message = "Author can't be null")
    private Long authorId;
    private Long executorId;
}