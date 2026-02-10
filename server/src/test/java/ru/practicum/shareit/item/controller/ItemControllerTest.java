package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    private CreateItemDto createItemDto;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        createItemDto = new CreateItemDto();
        createItemDto.setName("Test Item");
        createItemDto.setDescription("Test Description");
        createItemDto.setAvailable(true);

        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName(createItemDto.getName());
        itemDto.setDescription(createItemDto.getDescription());
        itemDto.setAvailable(createItemDto.getAvailable());
        itemDto.setComments(Collections.emptyList());
    }

    @Test
    void addItem() throws Exception {
        when(itemService.add(eq(1L), any(CreateItemDto.class))).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createItemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()));
    }

    @Test
    void updateItem() throws Exception {
        UpdateItemDto updateDto = new UpdateItemDto();
        updateDto.setName("Updated Name");
        updateDto.setDescription("Updated Description");
        updateDto.setAvailable(false);

        itemDto.setName(updateDto.getName());
        itemDto.setDescription(updateDto.getDescription());
        itemDto.setAvailable(updateDto.getAvailable());

        when(itemService.update(eq(1L), eq(1L), any(UpdateItemDto.class))).thenReturn(itemDto);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    void getItem() throws Exception {
        when(itemService.getItem(1L)).thenReturn(itemDto);

        mockMvc.perform(get("/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value(itemDto.getName()));
    }

    @Test
    void getItems() throws Exception {
        when(itemService.getItems(1L)).thenReturn(Collections.singletonList(itemDto));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemDto.getId()));
    }

    @Test
    void searchItems() throws Exception {
        when(itemService.searchItems("test")).thenReturn(Collections.singletonList(itemDto));

        mockMvc.perform(get("/items/search")
                        .param("text", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(itemDto.getName()));
    }

    @Test
    void addComment() throws Exception {
        CreateCommentDto commentDto = new CreateCommentDto();
        commentDto.setText("Nice item");

        CommentDto responseDto = new CommentDto();
        responseDto.setId(1L);
        responseDto.setText(commentDto.getText());
        responseDto.setAuthorName("Test User");
        responseDto.setCreated(LocalDateTime.now());

        when(itemService.addComment(eq(1L), eq(1L), any(CreateCommentDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Nice item"));
    }
}
