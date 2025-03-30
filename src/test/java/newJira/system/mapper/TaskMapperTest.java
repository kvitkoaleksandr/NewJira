package newJira.system.mapper;

import newJira.system.dto.task.TaskDto;
import newJira.system.entity.Priority;
import newJira.system.entity.Status;
import newJira.system.entity.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TaskMapperTest {
    private TaskMapper taskMapper;

    @BeforeEach
    void setUp() {
        taskMapper = new TaskMapperImpl();
    }

    @Test
    @DisplayName("Маппинг TaskDto → Task")
    void toTaskSuccessTest() {
        TaskDto dto = new TaskDto();
        dto.setId(2L);
        dto.setTitle("Новая задача");
        dto.setStatus("IN_PROGRESS");
        dto.setPriority("LOW");

        Task task = taskMapper.toTask(dto);

        assertNotNull(task);
        assertEquals("Новая задача", task.getTitle());
        assertEquals(Status.IN_PROGRESS, task.getStatus());
        assertEquals(Priority.LOW, task.getPriority());
    }

    @Test
    @DisplayName("Enum → String → Enum — работает корректно")
    void enumStringCycleMappingTest() {
        Status originalStatus = Status.TO_DO;
        Priority originalPriority = Priority.MEDIUM;

        String statusStr = taskMapper.mapStatusFromEnum(originalStatus);
        String priorityStr = taskMapper.mapPriorityFromEnum(originalPriority);

        Status mappedStatus = taskMapper.mapStatusFromString(statusStr);
        Priority mappedPriority = taskMapper.mapPriorityFromString(priorityStr);

        assertEquals(originalStatus, mappedStatus);
        assertEquals(originalPriority, mappedPriority);
    }
}