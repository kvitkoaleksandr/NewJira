package newJira.system.dto.task;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;

    @NotEmpty(message = "Название задачи обязательно")
    private String title;
    private String status = "TO_DO";

    @NotNull(message = "Приоритет не может быть пустым")
    private String priority;

    @NotNull(message = "ID автора обязателен")
    private Long authorId;
    private Long executorId;
}