package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.enumStatus.State;
import ru.practicum.shareit.booking.enumStatus.Status;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void addBooking_shouldReturnBookingDto() throws Exception {
        long userId = 1L;

        CreateBookingDto createDto = CreateBookingDto.builder()
                .itemId(2L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        BookingDto bookingDto = BookingDto.builder()
                .id(10L)
                .status(Status.WAITING)
                .build();

        when(bookingService.add(eq(userId), any(CreateBookingDto.class)))
                .thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L));
    }

    @Test
    void updateBooking_shouldApprove() throws Exception {
        long ownerId = 1L;
        long bookingId = 10L;

        BookingDto bookingDto = BookingDto.builder()
                .id(bookingId)
                .status(Status.APPROVED)
                .build();

        when(bookingService.update(ownerId, bookingId, true))
                .thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", ownerId)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void getBooking_shouldReturnBooking() throws Exception {
        long userId = 2L;
        long bookingId = 10L;

        BookingDto bookingDto = BookingDto.builder()
                .id(bookingId)
                .build();

        when(bookingService.getBooking(bookingId, userId))
                .thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId));
    }

    @Test
    void getUserBookings_shouldReturnList() throws Exception {
        long userId = 1L;

        when(bookingService.getUserBookings(userId, State.ALL))
                .thenReturn(List.of(BookingDto.builder().id(1L).build()));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getOwnerBookings_shouldReturnList() throws Exception {
        long ownerId = 1L;

        when(bookingService.getOwnerBookings(ownerId, State.ALL))
                .thenReturn(List.of(BookingDto.builder().id(2L).build()));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2L));
    }

}