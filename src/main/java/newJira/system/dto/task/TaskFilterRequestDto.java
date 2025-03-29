package newJira.system.dto.task;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TaskFilterRequestDto {
    private String author;
    private String executor;
    @NotNull(message = "Page is required")
    private Integer page;

    @NotNull(message = "Size is required")
    private Integer size;
}