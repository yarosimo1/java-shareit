package ru.practicum.shareit.exception;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.exception.handler.ErrorResponse;
import ru.practicum.shareit.exception.handler.GlobalExceptionHandler;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleNotFound_shouldReturn404() {
        NotFoundException ex = new NotFoundException("Item not found");

        ResponseEntity<ErrorResponse> response = handler.handleNotFound(ex);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("NotFound Error", response.getBody().getError());
        assertEquals("Item not found", response.getBody().getMessage());
    }

    @Test
    void handleDuplicatedData_shouldReturn409() {
        DuplicatedDataException ex = new DuplicatedDataException("Email exists");

        ResponseEntity<ErrorResponse> response = handler.handleDuplicatedData(ex);

        assertEquals(409, response.getStatusCodeValue());
        assertEquals("DuplicatedData Error", response.getBody().getError());
        assertEquals("Email exists", response.getBody().getMessage());
    }

    @Test
    void handleUnavailableItem_shouldReturn400() {
        UnavailableItemException ex = new UnavailableItemException("Item not available");

        ResponseEntity<ErrorResponse> response = handler.handleUnavailableItem(ex);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("The booked item Error", response.getBody().getError());
        assertEquals("Item not available", response.getBody().getMessage());
    }

    @Test
    void handleNotOwner_shouldReturn403() {
        NotOwnerException ex = new NotOwnerException("Not the owner");

        ResponseEntity<ErrorResponse> response = handler.handleUnavailableItem(ex);

        assertEquals(403, response.getStatusCodeValue());
        assertEquals("Not owner Error", response.getBody().getError());
        assertEquals("Not the owner", response.getBody().getMessage());
    }

    @Test
    void handleValidationException_shouldReturn400() {
        ValidationException ex = new ValidationException("Cannot comment");

        ResponseEntity<ErrorResponse> response = handler.handleUnavailableItem(ex);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("it is impossible to comment", response.getBody().getError());
        assertEquals("Cannot comment", response.getBody().getMessage());
    }

    @Test
    void handleMethodArgumentNotValid_shouldReturn400() {
        BindingResult bindingResult = org.mockito.Mockito.mock(BindingResult.class);
        FieldError fieldError = new FieldError("object", "field", "must not be null");
        org.mockito.Mockito.when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ErrorResponse> response = handler.handleValidationErrors(ex);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("MethodArgumentNotValid Error", response.getBody().getError());
        // Проверяем, что сообщение содержит имя поля и текст ошибки
        assertEquals("field: must not be null", response.getBody().getMessage());
    }

    @Test
    void handleUnexpected_shouldReturn500() {
        Exception ex = new Exception("Unexpected");

        ResponseEntity<ErrorResponse> response = handler.handleUnexpected(ex);

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Exception", response.getBody().getError());
        assertEquals("Произошла внутренняя ошибка сервера", response.getBody().getMessage());
    }
}
