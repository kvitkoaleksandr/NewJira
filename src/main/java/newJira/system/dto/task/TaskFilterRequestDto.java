package newJira.system.dto.task;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TaskFilterRequestDto {
    private String author;
    private String executor;
    @NotNull(message = "Страница обязательна")
    private Integer page;

    @NotNull(message = "Размер страницы обязателен")
    private Integer size;
}