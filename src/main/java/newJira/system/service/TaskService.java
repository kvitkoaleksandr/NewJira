package newJira.system.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserRepository userRepository;
    private final RoleChecker roleChecker;

    @Transactional
    public TaskDto createTask(TaskDto taskDto) {
        AppUser author = userRepository.findById(taskDto.getAuthorId())
                .orElseThrow(() -> {
                    log.warn("Автор с ID {} не найден", taskDto.getAuthorId());
                    return new NotFoundException("Автор с ID " + taskDto.getAuthorId() + " не найден");
                });
        Task task = taskMapper.toTask(taskDto);
        task.setAuthor(author);

        if (taskDto.getExecutorId() != null) {
            AppUser executor = userRepository.findById(taskDto.getExecutorId()).orElseThrow(() -> {
                log.warn("Исполнитель с ID {} не найден", taskDto.getExecutorId());
                return new NotFoundException("Исполнитель с ID " + taskDto.getExecutorId() + " не найден");
            });
            task.setExecutor(executor);
        }

        Task savedTask = taskRepository.save(task);
        return taskMapper.toTaskDto(savedTask);
    }

    @CacheEvict(value = {"tasksByAuthor", "tasksByExecutor"}, allEntries = true)
    @Transactional
    public TaskDto updateTask(Long taskId, TaskDto taskDto) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> {
            log.warn("Задача с ID {} не найдена", taskId);
            return new NotFoundException("Задача с ID " + taskId + " не найдена");
        });
        task.setTitle(taskDto.getTitle());
        task.setStatus(Status.valueOf(taskDto.getStatus()));
        task.setPriority(Priority.valueOf(taskDto.getPriority()));

        AppUser executor = userRepository.findById(taskDto.getExecutorId()).orElseThrow(() -> {
            log.warn("Исполнитель с ID {} не найден", taskDto.getExecutorId());
            return new NotFoundException("Пользователь с ID " + taskDto.getExecutorId() + " не найден");
        });
        task.setExecutor(executor);

        Task updatedTask = taskRepository.save(task);
        return taskMapper.toTaskDto(updatedTask);
    }

    @CacheEvict(value = {"tasksByAuthor", "tasksByExecutor"}, allEntries = true)
    @Transactional
    public void deleteTask(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            log.warn("Задача с ID {} не найдена", taskId);
            throw new NotFoundException("Задача с ID " + taskId + " не найдена");
        }
        taskRepository.deleteById(taskId);
    }

    @Cacheable(value = "tasksByAuthor", key = "#authorId")
    public List<TaskDto> getTasksByAuthor(Long authorId) {
        List<Task> tasks = taskRepository.findByAuthorId(authorId);
        return tasks.stream()
                .map(taskMapper::toTaskDto)
                .toList();
    }

    @Cacheable(value = "tasksByExecutor", key = "#executorId")
    public List<TaskDto> getTasksByExecutors(Long executorId) {
        List<Task> tasks = taskRepository.findByExecutorId(executorId);
        return tasks.stream()
                .map(taskMapper::toTaskDto)
                .toList();
    }

    public Page<TaskDto> getTasks(TaskFilterRequestDto request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());

        Page<Task> tasks;

        if (request.getAuthor() != null && request.getExecutor() != null) {
            tasks = taskRepository.findByAuthorAndExecutor(request.getAuthor(), request.getExecutor(), pageable);
        } else if (request.getAuthor() != null) {
            tasks = taskRepository.findByAuthor(request.getAuthor(), pageable);
        } else if (request.getExecutor() != null) {
            tasks = taskRepository.findByExecutor(request.getExecutor(), pageable);
        } else {
            tasks = taskRepository.findAll(pageable);
        }

        return tasks.map(taskMapper::toTaskDto);
    }

    @Transactional
    @CacheEvict(value = {"tasksByExecutor", "tasksByAuthor"}, allEntries = true)
    public TaskDto updateStatusByExecutor(Long taskId,
                                          TaskStatusUpdateRequestDto requestDto,
                                          HttpServletRequest request) {
        String userEmail = roleChecker.getCurrentEmail(request);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Задача с ID " + taskId + " не найдена"));

        if (task.getExecutor() == null || !task.getExecutor().getEmail().equals(userEmail)) {
            throw new ForbiddenException("Вы не являетесь исполнителем этой задачи");
        }

        Status newStatus;
        try {
            newStatus = Status.valueOf(requestDto.getStatus());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Недопустимый статус: " + requestDto.getStatus());
        }

        task.setStatus(newStatus);
        Task updated = taskRepository.save(task);
        return taskMapper.toTaskDto(updated);
    }
}