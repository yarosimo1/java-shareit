package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class BookingMapperTest {

    private final BookingMapper mapper = BookingMapper.INSTANCE;

    @Test
    public void toBookingDto_shouldMapAllFields() {
        User booker = new User(1L, "John", "john@mail.com");
        Item item = new Item(2L, booker, "Drill", "Power drill", true, null, null);

        Booking booking = new Booking(
                10L,
                LocalDateTime.of(2026, 2, 11, 10, 0),
                LocalDateTime.of(2026, 2, 11, 12, 0),
                item,
                booker,
                null // статус можно оставить null для теста
        );

        BookingDto dto = mapper.toBookingDto(booking);

        assertEquals(booking.getId(), dto.getId());
        assertEquals(booking.getStart(), dto.getStart());
        assertEquals(booking.getEnd(), dto.getEnd());
        assertEquals(booking.getItem().getId(), dto.getItem().getId());
        assertEquals(booking.getItem().getName(), dto.getItem().getName());
        assertEquals(booking.getBooker(), dto.getBooker());
    }

    @Test
    public void toBooking_shouldMapItemId() {
        CreateBookingDto dto = CreateBookingDto.builder()
                .itemId(5L)
                .start(LocalDateTime.of(2026, 2, 11, 14, 0))
                .end(LocalDateTime.of(2026, 2, 11, 16, 0))
                .build();

        Booking booking = mapper.toBooking(dto);

        assertEquals(dto.getItemId(), booking.getItem().getId());
        assertEquals(dto.getStart(), booking.getStart());
        assertEquals(dto.getEnd(), booking.getEnd());
    }

    @Test
    public void toBookingDto_shouldReturnNull_whenBookingIsNull() {
        BookingDto dto = mapper.toBookingDto(null);

        assertNull(dto);
    }

    @Test
    public void toBooking_shouldReturnNull_whenCreateBookingDtoIsNull() {
        Booking booking = mapper.toBooking(null);

        assertNull(booking);
    }

    @Test
    public void toBookingDto_shouldSetItemNull_whenItemIsNull() {
        Booking booking = new Booking(
                1L,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1),
                null,          // ← ВАЖНО
                null,
                null
        );

        BookingDto dto = mapper.toBookingDto(booking);

        assertNull(dto.getItem());
    }

    @Test
    public void toBooking_shouldSetItemNull_whenCreateBookingDtoHasNullItemId() {
        CreateBookingDto dto = CreateBookingDto.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .build();

        Booking booking = mapper.toBooking(dto);

        assertNull(booking.getItem().getId());
    }

}
