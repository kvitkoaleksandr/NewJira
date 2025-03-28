package newJira.system.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import newJira.system.dto.TaskDto;
import newJira.system.dto.TaskFilterRequestDto;
import newJira.system.entity.AppUser;
import newJira.system.entity.Task;
import newJira.system.mapper.ManagementMapper;
import newJira.system.repository.TaskRepository;
import newJira.system.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final ManagementMapper managementMapper;
    private final UserRepository userRepository;

    public TaskDto createTask(TaskDto taskDto) {
        Task task = managementMapper.toTask(taskDto);
        Task savedTask = taskRepository.save(task);
        return managementMapper.toTaskDto(savedTask);
    }

    public TaskDto updateTask(Long taskId, TaskDto taskDto) {
        Task task = taskRepository.findById(taskId).orElseThrow(() ->
                new EntityNotFoundException("Задача с ID " + taskId + " не найдена"));
        task.setTitle(taskDto.getTitle());
        task.setStatus(taskDto.getStatus());
        task.setPriority(taskDto.getPriority());
        AppUser executor = userRepository.findById(taskDto.getExecutorId()).orElseThrow(
                () -> new EntityNotFoundException("Пользователь с ID " + taskDto.getExecutorId() + " не найден")
        );

        task.setExecutor(executor);

        Task updatedTask = taskRepository.save(task);
        return managementMapper.toTaskDto(updatedTask);
    }

    public void deleteTask(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new EntityNotFoundException("Задача с ID " + taskId + " не найдена");
        }
        taskRepository.deleteById(taskId);
    }

    public List<TaskDto> getTasksByAuthor(Long authorId) {
        List<Task> tasks = taskRepository.findByAuthorId(authorId);
        return tasks.stream()
                .map(managementMapper::toTaskDto)
                .toList();
    }

    public List<TaskDto> getTasksByExecutors(Long executorId) {
        List<Task> tasks = taskRepository.findByExecutorId(executorId);
        return tasks.stream()
                .map(managementMapper::toTaskDto)
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

        return tasks.map(managementMapper::toTaskDto);
    }
}