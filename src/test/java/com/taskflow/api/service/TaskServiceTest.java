package com.taskflow.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.taskflow.api.dto.TaskRequest;
import com.taskflow.api.dto.TaskResponse;
import com.taskflow.api.entity.Task;
import com.taskflow.api.entity.TaskPriority;
import com.taskflow.api.entity.TaskStatus;
import com.taskflow.api.entity.User;
import com.taskflow.api.exception.NotFoundException;
import com.taskflow.api.repository.TaskRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TaskService taskService;

    @Test
    void createTask_setsFieldsAndSaves() {
        User creator = user(1L, "Ana", "ana@local");
        when(userService.getUserOrThrow(1L)).thenReturn(creator);
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
            Task saved = invocation.getArgument(0, Task.class);
            saved.setId(10L);
            return saved;
        });

        TaskRequest request = new TaskRequest(
            "New task",
            "Details",
            TaskStatus.TODO,
            TaskPriority.MEDIUM,
            LocalDate.of(2026, 2, 20),
            null,
            1L
        );

        TaskResponse response = taskService.createTask(request);

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(captor.capture());
        Task saved = captor.getValue();

        assertThat(saved.getTitle()).isEqualTo("New task");
        assertThat(saved.getAssignedTo()).isNull();
        assertThat(saved.getCreatedBy()).isEqualTo(creator);
        assertThat(response.id()).isEqualTo(10L);
    }

    @Test
    void updateTask_updatesExisting() {
        User creator = user(1L, "Ana", "ana@local");
        User assignee = user(2L, "Bruno", "bruno@local");
        Task existing = new Task();
        existing.setId(5L);

        when(taskRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(userService.getUserOrThrow(1L)).thenReturn(creator);
        when(userService.getUserOrThrow(2L)).thenReturn(assignee);
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0, Task.class));

        TaskRequest request = new TaskRequest(
            "Updated",
            null,
            TaskStatus.IN_PROGRESS,
            TaskPriority.HIGH,
            null,
            2L,
            1L
        );

        TaskResponse response = taskService.updateTask(5L, request);

        assertThat(response.title()).isEqualTo("Updated");
        assertThat(response.assignedTo().id()).isEqualTo(2L);
        assertThat(response.createdBy().id()).isEqualTo(1L);
    }

    @Test
    void getTask_throwsWhenMissing() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getTask(99L))
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining("Task not found");
    }

    @Test
    void listTasks_mapsResponses() {
        User creator = user(1L, "Ana", "ana@local");
        Task task = new Task();
        task.setId(3L);
        task.setTitle("List me");
        task.setStatus(TaskStatus.DONE);
        task.setPriority(TaskPriority.LOW);
        task.setCreatedBy(creator);

        when(taskRepository.findAll(any(Specification.class))).thenReturn(List.of(task));

        List<TaskResponse> result = taskService.listTasks(TaskStatus.DONE, TaskPriority.LOW, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(3L);
        assertThat(result.get(0).createdBy().email()).isEqualTo("ana@local");
    }

    private User user(Long id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        return user;
    }
}
