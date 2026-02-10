package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.enumStatus.State;

import java.util.Collection;

public interface BookingService {
    BookingDto add(long userId, CreateBookingDto booking);

    BookingDto update(long ownerId, long bookingId, boolean approved);

    Collection<BookingDto> getUserBookings(long userId, State state);

    Collection<BookingDto> getOwnerBookings(long ownerId, State state);

    BookingDto getBooking(long bookingid, long userId);
}
