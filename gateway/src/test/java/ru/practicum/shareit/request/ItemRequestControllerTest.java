package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestClient itemRequestClient;

    private CreateItemRequestDto validRequestDto;

    @BeforeEach
    public void setUp() {
        validRequestDto = new CreateItemRequestDto();
        validRequestDto.setDescription("Нужна дрель");
    }

    @Test
    public void addRequest_whenValid_thenReturns200() throws Exception {
        when(itemRequestClient.addRequest(anyLong(), any(CreateItemRequestDto.class)))
                .thenReturn(ResponseEntity.ok().body("RequestAdded"));

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("RequestAdded"));
    }

    @Test
    public void addRequest_whenInvalid_thenReturns400() throws Exception {
        CreateItemRequestDto invalidRequest = new CreateItemRequestDto(); // description пустой

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getItemRequestDto_whenCalled_thenReturns200() throws Exception {
        when(itemRequestClient.getUsersItemRequests(anyLong()))
                .thenReturn(ResponseEntity.ok().body("UserRequests"));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("UserRequests"));
    }

    @Test
    public void getAllItemRequest_whenCalled_thenReturns200() throws Exception {
        when(itemRequestClient.getAllItemRequests(anyLong()))
                .thenReturn(ResponseEntity.ok().body("AllRequests"));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("AllRequests"));
    }

    @Test
    public void getItemRequestById_whenCalled_thenReturns200() throws Exception {
        when(itemRequestClient.getItemRequest(anyLong()))
                .thenReturn(ResponseEntity.ok().body("RequestById"));

        mockMvc.perform(get("/requests/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("RequestById"));
    }
}