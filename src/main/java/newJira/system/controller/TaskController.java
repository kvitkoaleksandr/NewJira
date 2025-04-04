package newJira.system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import newJira.system.dto.task.TaskDto;
import newJira.system.dto.task.TaskFilterRequestDto;
import newJira.system.dto.task.TaskStatusUpdateRequestDto;
import newJira.system.exception.custom.BadRequestException;
import newJira.system.exception.custom.ForbiddenException;
import newJira.system.security.RoleChecker;
import newJira.system.service.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tasks")
@Slf4j
public class TaskController {
    private final TaskService taskService;
    private final RoleChecker roleChecker;


    @Operation(summary = "Создание новой задачи")
    @SecurityRequirement(name = "BearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Задача успешно создана"),
            @ApiResponse(responseCode = "400", description = "Неверные входные данные")
    })
    @PostMapping
    public TaskDto createTask(@Valid @RequestBody TaskDto taskDto, HttpServletRequest request) {
        checkAdminAccess(request);
        return taskService.createTask(taskDto);
    }

    @Operation(summary = "Обновление существующей задачи")
    @SecurityRequirement(name = "BearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Задача успешно обновлена"),
            @ApiResponse(responseCode = "404", description = "Задача не найдена"),
            @ApiResponse(responseCode = "400", description = "Неверные входные данные")
    })
    @PutMapping("/{taskId}")
    public TaskDto updateTask(@PathVariable Long taskId,
                              @Valid @RequestBody TaskDto taskDto,
                              HttpServletRequest request) {
        checkAdminAccess(request);
        return taskService.updateTask(taskId, taskDto);
    }

    @Operation(summary = "Получение задач по ID автора")
    @SecurityRequirement(name = "BearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Задачи успешно получены"),
            @ApiResponse(responseCode = "404", description = "Автор не найден")
    })
    @GetMapping("/author/{authorId}")
    public List<TaskDto> getTasksByAuthor(@PathVariable Long authorId, HttpServletRequest request) {
        checkAdminAccess(request);
        return taskService.getTasksByAuthor(authorId);
    }

    @Operation(summary = "Получение задач по ID исполнителя")
    @SecurityRequirement(name = "BearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Задачи успешно получены"),
            @ApiResponse(responseCode = "404", description = "Исполнитель не найден")
    })
    @GetMapping("/executor/{executorId}")
    public List<TaskDto> getTasksByExecutors(@PathVariable Long executorId) {
        if (executorId == null) {
            throw new BadRequestException("ID исполнителя не может быть пустым");
        }
        return taskService.getTasksByExecutors(executorId);
    }

    @Operation(summary = "Удаление задачи по ID")
    @SecurityRequirement(name = "BearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Задача успешно удалена"),
            @ApiResponse(responseCode = "404", description = "Задача не найдена")
    })
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId, HttpServletRequest request) {
        checkAdminAccess(request);
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Получить список задач с пагинацией")
    @SecurityRequirement(name = "BearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Задачи успешно получены"),
            @ApiResponse(responseCode = "400", description = "Некорректные входные данные")
    })
    @PostMapping("/filter")
    public Page<TaskDto> filterTasks(@Valid @RequestBody TaskFilterRequestDto request,
                                     HttpServletRequest servletRequest) {
        checkAdminAccess(servletRequest);
        return taskService.getTasks(request);
    }

    @Operation(summary = "Изменить статус задачи (для исполнителя)")
    @SecurityRequirement(name = "BearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Статус успешно обновлён"),
            @ApiResponse(responseCode = "403", description = "Вы не исполнитель задачи"),
            @ApiResponse(responseCode = "404", description = "Задача не найдена")
    })
    @PatchMapping("/{taskId}/status")
    public TaskDto updateTaskStatusByExecutor(@PathVariable Long taskId,
                                              @Valid @RequestBody TaskStatusUpdateRequestDto requestDto,
                                              HttpServletRequest request) {
        return taskService.updateStatusByExecutor(taskId, requestDto, request);
    }

    private void checkAdminAccess(HttpServletRequest request) {
        if (!roleChecker.isAdmin(request)) {
            log.warn("Доступ запрещён: пользователь не является администратором. Запрос: {}", request.getRequestURI());
            throw new ForbiddenException("Только ADMIN имеет доступ к этой операции");
        }
    }
}