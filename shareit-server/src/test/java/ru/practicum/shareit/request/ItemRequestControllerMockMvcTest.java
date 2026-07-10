package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService requestService;

    @Test
    void shouldCreateRequest() throws Exception {
        CreateItemRequestDto request = new CreateItemRequestDto();
        request.setDescription("Need a drill");

        ItemRequestDto response = ItemRequestDto.builder()
                .id(1L)
                .description("Need a drill")
                .requesterId(1L)
                .created(LocalDateTime.now())
                .build();

        when(requestService.create(anyLong(), any(CreateItemRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Need a drill"));
    }

    @Test
    void shouldGetUserRequests() throws Exception {
        ItemRequestDto request = ItemRequestDto.builder()
                .id(1L)
                .description("Need a drill")
                .requesterId(1L)
                .created(LocalDateTime.now())
                .items(List.of())
                .build();

        when(requestService.getUserRequests(anyLong())).thenReturn(List.of(request));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void shouldGetAllRequests() throws Exception {
        ItemRequestDto request = ItemRequestDto.builder()
                .id(1L)
                .description("Need a drill")
                .requesterId(2L)
                .created(LocalDateTime.now())
                .items(List.of())
                .build();

        when(requestService.getAllRequests(anyLong())).thenReturn(List.of(request));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void shouldGetRequestById() throws Exception {
        ItemResponseDto item = ItemResponseDto.builder()
                .id(1L)
                .name("Drill")
                .ownerId(2L)
                .build();

        ItemRequestDto response = ItemRequestDto.builder()
                .id(1L)
                .description("Need a drill")
                .requesterId(1L)
                .created(LocalDateTime.now())
                .items(List.of(item))
                .build();

        when(requestService.getRequestById(anyLong(), anyLong())).thenReturn(response);

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.items[0].id").value(1L));
    }
}
