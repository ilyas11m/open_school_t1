package com.project.app.open_school_t1.service;

import com.project.app.open_school_t1.consts.TaskStatus;
import com.project.app.open_school_t1.dto.TaskDTO;
import com.project.app.open_school_t1.entity.Task;
import com.project.app.open_school_t1.exception.TaskNotFoundException;
import com.project.app.open_school_t1.repository.TaskRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplementationTest {

    @Mock private TaskRepository taskRepository;

    @Mock private NewTopic taskStatusTopic;

    @Mock private KafkaTemplate<String, TaskDTO> taskStatusKafkaTemplate;

    @InjectMocks private TaskServiceImplementation taskService;

    private Task task;
    private List<Task> taskList;

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setUserId(1L);
        task.setStatus(TaskStatus.TO_DO);

        Task task2 = new Task();
        task2.setId(2L);
        task2.setTitle("Test Task 2");
        task2.setDescription("Test Description 2");
        task2.setUserId(1L);
        task2.setStatus(TaskStatus.PROCESSING);

        taskList = List.of(task, task2);
    }

    @Test
    @DisplayName("Создание задачи должно возвращать созданную задачу")
    void createTask_ShouldReturnCreatedTask() {
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task savedTask = taskService.addTask(task);

        assertNotNull(savedTask);
        assertEquals(task.getId(), savedTask.getId());
        assertEquals(task.getTitle(), savedTask.getTitle());
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    @DisplayName("Получение задачи по ID должно возвращать задачу, если она существует")
    void getTaskById_ShouldReturnTask_WhenTaskExists() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        Task foundTask = taskService.getById(1L);

        assertNotNull(foundTask);
        assertEquals(1L, foundTask.getId());
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Получение задачи по несуществующему ID должно вызывать исключение")
    void getTaskById_ShouldThrowException_WhenTaskDoesNotExist() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        TaskNotFoundException exception =
                assertThrows(
                        TaskNotFoundException.class,
                        () -> {
                            taskService.getById(999L);
                        });

        assertEquals("Task с id 999 не найден.", exception.getMessage());
        verify(taskRepository, times(1)).findById(999L);
    }

    @ParameterizedTest
    @EnumSource(TaskStatus.class)
    @DisplayName("Обновление задачи должно отправлять сообщение в Kafka при изменении статуса")
    void updateTask_ShouldSendMessageToKafka_WhenStatusChanged(TaskStatus newStatus) {
        if (newStatus == TaskStatus.TO_DO) {
            return;
        }

        Task updatedTask = new Task();
        updatedTask.setTitle("Updated Title");
        updatedTask.setDescription("Updated Description");
        updatedTask.setUserId(1L);
        updatedTask.setStatus(newStatus);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(taskStatusTopic.name()).thenReturn("task-status-topic");

        Task result = taskService.update(1L, updatedTask);

        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Description", result.getDescription());
        assertEquals(newStatus, result.getStatus());

        ArgumentCaptor<TaskDTO> messageCaptor =
                ArgumentCaptor.forClass(TaskDTO.class);
        verify(taskStatusKafkaTemplate).send(eq("task-status-topic"), messageCaptor.capture());

        TaskDTO capturedMessage = messageCaptor.getValue();
        assertEquals(1L, capturedMessage.getId());
        assertEquals(newStatus.name(), capturedMessage.getStatus());
    }

    @Test
    @DisplayName("Обновление задачи не должно отправлять сообщение в Kafka, если статус не изменился")
    void updateTask_ShouldNotSendKafkaMessage_WhenStatusNotChanged() {
        // Arrange - подготавливаем данные и настраиваем поведение мок-объектов
        Task sameStatusTask = new Task();
        sameStatusTask.setTitle("Updated Title");
        sameStatusTask.setDescription("Updated Description");
        sameStatusTask.setUserId(1L);
        sameStatusTask.setStatus(TaskStatus.TO_DO);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task result = taskService.update(1L, sameStatusTask);

        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        assertEquals(TaskStatus.TO_DO, result.getStatus());
        verify(taskRepository).findById(1L);
        verify(taskRepository).save(any(Task.class));
        verify(taskStatusKafkaTemplate, never()).send(anyString(), any(TaskDTO.class));
    }

    @Test
    @DisplayName("Обновление несуществующей задачи должно вызывать исключение")
    void updateTask_ShouldThrowException_WhenTaskDoesNotExist() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        TaskNotFoundException exception =
                assertThrows(
                        TaskNotFoundException.class,
                        () -> {
                            taskService.update(999L, task);
                        });

        assertEquals("Task с id 999 не найден.", exception.getMessage());
        verify(taskRepository).findById(999L);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("Удаление задачи должно вызывать соответствующий метод репозитория")
    void deleteTask_ShouldCallRepositoryDeleteById() {
        doNothing().when(taskRepository).deleteById(1L);
        taskService.deleteTask(1L);
        verify(taskRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Получение всех задач должно возвращать список задач")
    void getAllTasks_ShouldReturnTaskList() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> page = new PageImpl<>(taskList);
        when(taskRepository.findAll(pageable)).thenReturn(page);

        List<Task> result = taskService.getAllTasks();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(taskRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Получение всех задач должно возвращать пустой список, если задач нет")
    void getAllTasks_ShouldReturnEmptyList_WhenNoTasksExist() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> emptyPage = new PageImpl<>(Collections.emptyList());
        when(taskRepository.findAll(pageable)).thenReturn(emptyPage);

        List<Task> result = taskService.getAllTasks();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(taskRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Обновление задачи должно корректно обрабатывать ошибки отправки в Kafka")
    void updateTask_ShouldHandleKafkaException() {
        Task updatedTask = new Task();
        updatedTask.setTitle("Updated Title");
        updatedTask.setDescription("Updated Description");
        updatedTask.setUserId(1L);
        updatedTask.setStatus(TaskStatus.PROCESSING);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(taskStatusTopic.name()).thenReturn("task-status-topic");
        doThrow(new RuntimeException("Kafka connection error"))
                .when(taskStatusKafkaTemplate)
                .send(anyString(), any(TaskDTO.class));

        Task result = taskService.update(1L, updatedTask);

        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        assertEquals(TaskStatus.PROCESSING, result.getStatus());
        verify(taskRepository).findById(1L);
        verify(taskRepository).save(any(Task.class));
        verify(taskStatusKafkaTemplate).send(anyString(), any(TaskDTO.class));
    }
}