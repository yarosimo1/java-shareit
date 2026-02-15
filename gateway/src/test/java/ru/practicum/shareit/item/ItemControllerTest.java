package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.comment.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemClient itemClient;

    private CreateItemDto validItemDto;
    private CreateCommentDto validCommentDto;
    private UpdateItemDto updateItemDto;

    @BeforeEach
    public void setUp() {
        validItemDto = new CreateItemDto();
        validItemDto.setName("Дрель");
        validItemDto.setDescription("Электрическая дрель");
        validItemDto.setAvailable(true);

        validCommentDto = new CreateCommentDto();
        validCommentDto.setText("Отличный инструмент!");

        updateItemDto = new UpdateItemDto();
        updateItemDto.setName("Новая дрель");
        updateItemDto.setDescription("Описание обновлено");
        updateItemDto.setAvailable(false);
    }

    @Test
    public void addItem_whenValid_thenReturns200() throws Exception {
        when(itemClient.addItem(anyLong(), any(CreateItemDto.class)))
                .thenReturn(ResponseEntity.ok().body("OK"));

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validItemDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));
    }

    @Test
    public void addItem_whenInvalid_thenReturns400() throws Exception {
        CreateItemDto invalidItem = new CreateItemDto(); // все поля null

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidItem)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateItem_whenValid_thenReturns200() throws Exception {
        when(itemClient.updateItem(anyLong(), anyLong(), any(UpdateItemDto.class)))
                .thenReturn(ResponseEntity.ok().body("Updated"));

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateItemDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Updated"));
    }

    @Test
    public void getItems_whenCalled_thenReturns200() throws Exception {
        when(itemClient.getItems(anyLong()))
                .thenReturn(ResponseEntity.ok().body("Items"));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("Items"));
    }

    @Test
    public void getItem_whenCalled_thenReturns200() throws Exception {
        when(itemClient.getItem(anyLong()))
                .thenReturn(ResponseEntity.ok().body("Item"));

        mockMvc.perform(get("/items/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Item"));
    }

    @Test
    public void searchItems_whenCalled_thenReturns200() throws Exception {
        when(itemClient.searchItems(anyLong(), any()))
                .thenReturn(ResponseEntity.ok().body("SearchResult"));

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", "дрель"))
                .andExpect(status().isOk())
                .andExpect(content().string("SearchResult"));
    }

    @Test
    public void addComment_whenValid_thenReturns200() throws Exception {
        when(itemClient.addComment(anyLong(), anyLong(), any(CreateCommentDto.class)))
                .thenReturn(ResponseEntity.ok().body("CommentAdded"));

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCommentDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("CommentAdded"));
    }

    @Test
    public void addComment_whenInvalid_thenReturns400() throws Exception {
        CreateCommentDto invalidComment = new CreateCommentDto(); // текст null

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidComment)))
                .andExpect(status().isBadRequest());
    }
}
