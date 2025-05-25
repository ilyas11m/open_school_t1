package com.project.app.open_school_t1.controller;

import com.project.app.open_school_t1.consts.TaskStatus;
import com.project.app.open_school_t1.dto.TaskDTO;
import com.project.app.open_school_t1.repository.TaskRepository;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest
@AutoConfigureMockMvc
class TaskControllerTest {

    @Autowired MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @Autowired private TaskRepository taskRepository;

    @MockitoBean private NewTopic taskStatusTopic;

    @MockitoBean private KafkaTemplate<String, TaskDTO> taskStatusKafkaTemplate;

    private TaskDTO taskDTO;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();

        taskDTO =
                TaskDTO.builder()
                        .title("Integration Test Task")
                        .description("Integration Test Description")
                        .userId(1L)
                        .status(TaskStatus.TO_DO)
                        .build();
    }

    @Test
    @DisplayName("Создание задачи должно сохранять задачу в БД и возвращать её со статусом 201")
    void createTask_ShouldCreateAndReturnTask() throws Exception {
        mockMvc
                .perform(
                        post("/tasks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(taskDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.title", is("Integration Test Task")))
                .andExpect(jsonPath("$.description", is("Integration Test Description")))
                .andExpect(jsonPath("$.userId", is(1)))
                .andExpect(jsonPath("$.status", is("TO_DO")));

        assertEquals(1, taskRepository.count(), "Количество задач в БД должно быть 1");
    }

    @Test
    @DisplayName("Получение задачи по ID должно возвращать задачу, если она существует")
    void getTask_ShouldReturnTask_WhenTaskExists() throws Exception {
        MvcResult createResult =
                mockMvc
                        .perform(
                                post("/tasks")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(taskDTO)))
                        .andExpect(status().isCreated())
                        .andReturn();

        TaskDTO createdTask = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                TaskDTO.class
        );

        mockMvc
                .perform(get("/tasks/" + createdTask.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(createdTask.getId().intValue())))
                .andExpect(jsonPath("$.title", is("Integration Test Task")))
                .andExpect(jsonPath("$.description", is("Integration Test Description")))
                .andExpect(jsonPath("$.userId", is(1)))
                .andExpect(jsonPath("$.status", is("TO_DO")));
    }

    @Test
    @DisplayName("Получение несуществующей задачи должно возвращать статус 404")
    void getTask_ShouldReturnNotFound_WhenTaskDoesNotExist() throws Exception {
        mockMvc
                .perform(get("/tasks/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Task с id 999 не найден.")))
                .andExpect(jsonPath("$.status", is(404)));
    }

    @Test
    @DisplayName("Обновление задачи должно изменять задачу в БД и возвращать обновленные данные")
    void updateTask_ShouldUpdateAndReturnTask_WhenTaskExists() throws Exception {
        MvcResult createResult =
                mockMvc
                        .perform(
                                post("/tasks")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(taskDTO)))
                        .andExpect(status().isCreated())
                        .andReturn();

        TaskDTO createdTask = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                TaskDTO.class
        );

        TaskDTO updatedTaskDTO =
                TaskDTO.builder()
                        .title("Updated Task")
                        .description("Updated Description")
                        .userId(2L)
                        .status(TaskStatus.TO_DO)
                        .build();

        mockMvc
                .perform(
                        put("/tasks/" + createdTask.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedTaskDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(createdTask.getId().intValue())))
                .andExpect(jsonPath("$.title", is("Updated Task")))
                .andExpect(jsonPath("$.description", is("Updated Description")))
                .andExpect(jsonPath("$.userId", is(2)))
                .andExpect(jsonPath("$.status", is("TO_DO")));

        assertEquals(1, taskRepository.count(), "Количество задач в БД должно оставаться 1");
        assertTrue(taskRepository.findById(createdTask.getId()).isPresent(), "Задача должна существовать в БД");
        assertEquals("Updated Task", taskRepository.findById(createdTask.getId()).get().getTitle(),
                "Название задачи должно быть обновлено в БД");
    }

    @Test
    @DisplayName("Обновление несуществующей задачи должно возвращать статус 404")
    void updateTask_ShouldReturnNotFound_WhenTaskDoesNotExist() throws Exception {
        mockMvc
                .perform(
                        put("/tasks/999")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(taskDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Task с id 999 не найден.")))
                .andExpect(jsonPath("$.status", is(404)));
    }

    @Test
    @DisplayName("Удаление задачи должно удалять задачу из БД")
    void deleteTask_ShouldDeleteTask() throws Exception {
        MvcResult createResult =
                mockMvc
                        .perform(
                                post("/tasks")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(taskDTO)))
                        .andExpect(status().isCreated())
                        .andReturn();

        TaskDTO createdTask = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                TaskDTO.class
        );

        assertEquals(1, taskRepository.count(), "Задача должна быть создана в БД");

        mockMvc.perform(delete("/tasks/" + createdTask.getId()))
                .andExpect(status().isNoContent());

        assertEquals(0, taskRepository.count(), "Задача должна быть удалена из БД");
        assertTrue(taskRepository.findById(createdTask.getId()).isEmpty(),
                "Задача не должна существовать в БД после удаления");
    }

    @Test
    @DisplayName("Получение списка задач должно возвращать все задачи с учетом пагинации")
    void getAllTasks_ShouldReturnTasks() throws Exception {
        mockMvc
                .perform(
                        post("/tasks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(taskDTO)))
                .andExpect(status().isCreated());

        TaskDTO task2DTO =
                TaskDTO.builder()
                        .title("Second Task")
                        .description("Second Description")
                        .userId(2L)
                        .status(TaskStatus.TO_DO)
                        .build();

        mockMvc
                .perform(
                        post("/tasks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(task2DTO)))
                .andExpect(status().isCreated());

        mockMvc
                .perform(get("/tasks?page=0&limit=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("Integration Test Task")))
                .andExpect(jsonPath("$[1].title", is("Second Task")));

        mockMvc
                .perform(get("/tasks?page=0&limit=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Integration Test Task")));

        mockMvc
                .perform(get("/tasks?page=1&limit=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Second Task")));
    }
}