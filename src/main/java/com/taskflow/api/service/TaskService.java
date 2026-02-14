package com.taskflow.api.service;

import com.taskflow.api.dto.TaskRequest;
import com.taskflow.api.dto.TaskResponse;
import com.taskflow.api.dto.UserResponse;
import com.taskflow.api.entity.Task;
import com.taskflow.api.entity.TaskPriority;
import com.taskflow.api.entity.TaskStatus;
import com.taskflow.api.entity.User;
import com.taskflow.api.exception.NotFoundException;
import com.taskflow.api.repository.TaskRepository;
import com.taskflow.api.spec.TaskSpecifications;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;

    public TaskService(TaskRepository taskRepository, UserService userService) {
        this.taskRepository = taskRepository;
        this.userService = userService;
    }

    public List<TaskResponse> listTasks(TaskStatus status, TaskPriority priority, Long assignedToId) {
        Specification<Task> spec = Specification.where(TaskSpecifications.hasStatus(status))
            .and(TaskSpecifications.hasPriority(priority))
            .and(TaskSpecifications.hasAssignedTo(assignedToId));

        return taskRepository.findAll(spec).stream()
            .map(this::toResponse)
            .toList();
    }

    public TaskResponse getTask(Long id) {
        return toResponse(findTask(id));
    }

    public TaskResponse createTask(TaskRequest request) {
        Task task = new Task();
        applyRequest(task, request);
        return toResponse(taskRepository.save(task));
    }

    public TaskResponse updateTask(Long id, TaskRequest request) {
        Task task = findTask(id);
        applyRequest(task, request);
        return toResponse(taskRepository.save(task));
    }

    public void deleteTask(Long id) {
        Task task = findTask(id);
        taskRepository.delete(task);
    }

    private void applyRequest(Task task, TaskRequest request) {
        User createdBy = userService.getUserOrThrow(request.createdById());
        User assignedTo = request.assignedToId() == null ? null : userService.getUserOrThrow(request.assignedToId());

        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setStatus(request.status());
        task.setPriority(request.priority());
        task.setDueDate(request.dueDate());
        task.setCreatedBy(createdBy);
        task.setAssignedTo(assignedTo);
    }

    private Task findTask(Long id) {
        return taskRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Task not found: " + id));
    }

    private TaskResponse toResponse(Task task) {
        return new TaskResponse(
            task.getId(),
            task.getTitle(),
            task.getDescription(),
            task.getStatus(),
            task.getPriority(),
            task.getDueDate(),
            task.getCreatedAt(),
            task.getUpdatedAt(),
            toUserResponse(task.getAssignedTo()),
            toUserResponse(task.getCreatedBy())
        );
    }

    private UserResponse toUserResponse(User user) {
        if (user == null) {
            return null;
        }
        return new UserResponse(user.getId(), user.getName(), user.getEmail());
    }
}
