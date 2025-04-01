package newJira.system.service;

import jakarta.servlet.http.HttpServletRequest;
import newJira.system.dto.task.TaskDto;
import newJira.system.dto.task.TaskFilterRequestDto;
import newJira.system.dto.task.TaskStatusUpdateRequestDto;
import newJira.system.entity.AppUser;
import newJira.system.entity.Priority;
import newJira.system.entity.Status;
import newJira.system.entity.Task;
import newJira.system.exception.custom.BadRequestException;
import newJira.system.exception.custom.ForbiddenException;
import newJira.system.exception.custom.NotFoundException;
import newJira.system.mapper.TaskMapper;
import newJira.system.repository.TaskRepository;
import newJira.system.repository.UserRepository;
import newJira.system.security.RoleChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    private static final Long TASK_ID = 1L;
    private static final Long AUTHOR_ID = 10L;
    private static final Long EXECUTOR_ID = 20L;
    private static final String TITLE = "Test Task";
    private static final String STATUS = "IN_PROGRESS";
    private static final String PRIORITY = "HIGH";
    private static final String EMAIL = "executor@example.com";

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TaskMapper taskMapper;
    @Mock
    private RoleChecker roleChecker;
    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private TaskService taskService;

    private Task task;
    private TaskDto taskDto;
    private AppUser author;
    private AppUser executor;

    @BeforeEach
    void setUp() {
        author = new AppUser();
        author.setId(AUTHOR_ID);

        executor = new AppUser();
        executor.setId(EXECUTOR_ID);
        executor.setEmail(EMAIL);

        task = new Task();
        task.setId(TASK_ID);
        task.setTitle(TITLE);
        task.setAuthor(author);
        task.setExecutor(executor);
        task.setStatus(Status.IN_PROGRESS);
        task.setPriority(Priority.HIGH);

        taskDto = new TaskDto();
        taskDto.setId(TASK_ID);
        taskDto.setTitle(TITLE);
        taskDto.setAuthorId(AUTHOR_ID);
        taskDto.setExecutorId(EXECUTOR_ID);
        taskDto.setStatus(STATUS);
        taskDto.setPriority(PRIORITY);
    }

    @Test
    @DisplayName("Успешное создание задачи")
    void createTaskSuccessTest() {
        when(userRepository.findById(AUTHOR_ID)).thenReturn(Optional.of(author));
        when(userRepository.findById(EXECUTOR_ID)).thenReturn(Optional.of(executor));
        when(taskMapper.toTask(taskDto)).thenReturn(task);
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toTaskDto(task)).thenReturn(taskDto);

        TaskDto result = taskService.createTask(taskDto);

        assertEquals(TITLE, result.getTitle());
        verify(taskRepository).save(task);
    }

    @Test
    @DisplayName("Ошибка при создании задачи — автор не найден")
    void createTaskAuthorNotFoundTest() {
        when(userRepository.findById(AUTHOR_ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> taskService.createTask(taskDto));
    }

    @Test
    @DisplayName("Успешное обновление задачи")
    void updateTaskSuccessTest() {
        when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(task));
        when(userRepository.findById(EXECUTOR_ID)).thenReturn(Optional.of(executor));
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toTaskDto(task)).thenReturn(taskDto);

        TaskDto result = taskService.updateTask(TASK_ID, taskDto);

        assertEquals(TITLE, result.getTitle());
        verify(taskRepository).save(task);
    }

    @Test
    @DisplayName("Ошибка при обновлении задачи — задача не найдена")
    void updateTaskNotFoundTest() {
        when(taskRepository.findById(TASK_ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> taskService.updateTask(TASK_ID, taskDto));
    }

    @Test
    @DisplayName("Ошибка при обновлении задачи — исполнитель не найден")
    void updateTaskExecutorNotFoundTest() {
        when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(task));
        when(userRepository.findById(EXECUTOR_ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> taskService.updateTask(TASK_ID, taskDto));
    }

    @Test
    @DisplayName("Успешное удаление задачи")
    void deleteTaskSuccessTest() {
        when(taskRepository.existsById(TASK_ID)).thenReturn(true);

        assertDoesNotThrow(() -> taskService.deleteTask(TASK_ID));
        verify(taskRepository).deleteById(TASK_ID);
    }

    @Test
    @DisplayName("Ошибка при удалении задачи — задача не найдена")
    void deleteTaskNotFoundTest() {
        when(taskRepository.existsById(TASK_ID)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> taskService.deleteTask(TASK_ID));
    }

    @Test
    @DisplayName("Получение задач по автору")
    void getTasksByAuthorSuccessTest() {
        when(taskRepository.findByAuthorId(AUTHOR_ID)).thenReturn(List.of(task));
        when(taskMapper.toTaskDto(task)).thenReturn(taskDto);

        List<TaskDto> result = taskService.getTasksByAuthor(AUTHOR_ID);

        assertEquals(1, result.size());
        assertEquals(TITLE, result.get(0).getTitle());
    }

    @Test
    @DisplayName("Получение задач по исполнителю")
    void getTasksByExecutorSuccessTest() {
        when(taskRepository.findByExecutorId(EXECUTOR_ID)).thenReturn(List.of(task));
        when(taskMapper.toTaskDto(task)).thenReturn(taskDto);

        List<TaskDto> result = taskService.getTasksByExecutors(EXECUTOR_ID);

        assertEquals(1, result.size());
        assertEquals(TITLE, result.get(0).getTitle());
    }

    @Test
    @DisplayName("Фильтрация задач по автору и исполнителю")
    void getTasksByAuthorAndExecutorSuccessTest() {
        TaskFilterRequestDto filter = new TaskFilterRequestDto();
        filter.setAuthor("author@mail.com");
        filter.setExecutor("exec@mail.com");
        filter.setPage(0);
        filter.setSize(10);

        Page<Task> page = new PageImpl<>(List.of(task));
        when(taskRepository.findByAuthorAndExecutor(any(), any(), any())).thenReturn(page);
        when(taskMapper.toTaskDto(task)).thenReturn(taskDto);

        Page<TaskDto> result = taskService.getTasks(filter);

        assertEquals(1, result.getContent().size());
        assertEquals(TITLE, result.getContent().get(0).getTitle());
    }

    @Test
    @DisplayName("Ошибка — задача не найдена при обновлении статуса")
    void updateStatusTaskNotFoundTest() {
        TaskStatusUpdateRequestDto statusDto = new TaskStatusUpdateRequestDto();
        statusDto.setStatus("DONE");

        when(taskRepository.findById(TASK_ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                taskService.updateStatusByExecutor(TASK_ID, statusDto, httpServletRequest));
    }

    @Test
    @DisplayName("Ошибка — текущий пользователь не является исполнителем задачи")
    void updateStatusForbiddenTest() {
        TaskStatusUpdateRequestDto statusDto = new TaskStatusUpdateRequestDto();
        statusDto.setStatus("DONE");

        when(roleChecker.getCurrentEmail(httpServletRequest)).thenReturn("not-executor@mail.com");
        when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(task));

        assertThrows(ForbiddenException.class, () ->
                taskService.updateStatusByExecutor(TASK_ID, statusDto, httpServletRequest));
    }

    @Test
    @DisplayName("Ошибка — недопустимый статус задачи")
    void updateStatusInvalidStatusTest() {
        TaskStatusUpdateRequestDto statusDto = new TaskStatusUpdateRequestDto();
        statusDto.setStatus("INVALID_STATUS");

        when(roleChecker.getCurrentEmail(httpServletRequest)).thenReturn(EMAIL);
        when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(task));

        assertThrows(BadRequestException.class, () ->
                taskService.updateStatusByExecutor(TASK_ID, statusDto, httpServletRequest));
    }
}