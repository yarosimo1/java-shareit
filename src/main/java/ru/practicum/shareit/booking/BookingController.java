package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.enumStatus.State;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Collection;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto addBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @Valid @RequestBody CreateBookingDto bookingDto) {
        return  bookingService.add(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(@PathVariable("bookingId") long bookingId,
                                    @RequestHeader("X-Sharer-User-Id") long ownerId,
                                    @RequestParam(name = "approved", required = false)
                                        boolean approved) {
        return bookingService.update(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable("bookingId") long bookingId,
                                 @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    public Collection<BookingDto> getBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @RequestParam(
                                                        name = "state",
                                                        defaultValue = "ALL"
                                                ) State state
    ) {
        return bookingService.getUserBookings(userId, state);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                    @RequestParam(
                                                            name = "state",
                                                            defaultValue = "ALL"
                                                    ) State state
    ) {
        return bookingService.getOwnerBookings(ownerId, state);
    }
}
