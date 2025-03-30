package newJira.system.dto.task;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TaskStatusUpdateRequestDto {
    @NotNull(message = "Новый статус обязателен")
    private String status;
}