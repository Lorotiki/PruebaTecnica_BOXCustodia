package com.taskflow.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskflow.api.dto.TaskRequest;
import com.taskflow.api.dto.TaskResponse;
import com.taskflow.api.dto.UserResponse;
import com.taskflow.api.entity.TaskPriority;
import com.taskflow.api.entity.TaskStatus;
import com.taskflow.api.service.TaskService;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    @Test
    @WithMockUser
    void listTasks_returnsResults() throws Exception {
        UserResponse creator = new UserResponse(1L, "Ana", "ana@local");
        TaskResponse response = new TaskResponse(
            5L,
            "Sample",
            null,
            TaskStatus.TODO,
            TaskPriority.MEDIUM,
            LocalDate.of(2026, 2, 20),
            Instant.now(),
            Instant.now(),
            null,
            creator
        );

        when(taskService.listTasks(null, null, null)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/tasks"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(5))
            .andExpect(jsonPath("$[0].createdBy.email").value("ana@local"));
    }

    @Test
    @WithMockUser
    void createTask_returnsCreated() throws Exception {
        UserResponse creator = new UserResponse(1L, "Ana", "ana@local");
        TaskResponse response = new TaskResponse(
            9L,
            "New",
            "Desc",
            TaskStatus.TODO,
            TaskPriority.LOW,
            null,
            Instant.now(),
            Instant.now(),
            null,
            creator
        );

        when(taskService.createTask(any(TaskRequest.class))).thenReturn(response);

        TaskRequest request = new TaskRequest(
            "New",
            "Desc",
            TaskStatus.TODO,
            TaskPriority.LOW,
            null,
            null,
            1L
        );

        mockMvc.perform(post("/api/tasks")
            .with(csrf())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(9));
    }

    @Test
    @WithMockUser
    void createTask_validatesBody() throws Exception {
        TaskRequest request = new TaskRequest(
            "",
            null,
            null,
            null,
            null,
            null,
            null
        );

        mockMvc.perform(post("/api/tasks")
            .with(csrf())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }
}
