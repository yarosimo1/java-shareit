package ru.practicum.shareit.booking.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.enumStatus.State;
import ru.practicum.shareit.booking.enumStatus.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class BookingServiceImplIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private User owner;
    private User booker;
    private Item item;

    @BeforeEach
    void setUp() {
        owner = userRepository.save(new User(
                null,
                "owner",
                "owner@mail.com"
        ));

        booker = userRepository.save(new User(
                null,
                "booker",
                "booker@mail.com"
        ));

        item = itemRepository.save(new Item(
                null,
                owner,
                "item",
                "desc",
                true,
                null,
                List.of()
        ));
    }

    @Test
    void addBooking_shouldPersistAndReturnDto() {
        CreateBookingDto dto = CreateBookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        BookingDto result = bookingService.add(booker.getId(), dto);

        assertNotNull(result.getId());

        Booking booking = bookingRepository.findById(result.getId()).orElseThrow();
        assertEquals(Status.WAITING, booking.getStatus());
        assertEquals(booker.getId(), booking.getBooker().getId());
    }

    @Test
    void update_shouldApproveBooking() {
        Booking booking = bookingRepository.save(new Booking(
                null,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item,
                booker,
                Status.WAITING
        ));

        BookingDto result =
                bookingService.update(owner.getId(), booking.getId(), true);

        assertEquals(Status.APPROVED, bookingRepository
                .findById(booking.getId())
                .orElseThrow()
                .getStatus());
    }

    @Test
    void getUserBookings_shouldReturnCurrent() {
        bookingRepository.save(new Booking(
                null,
                LocalDateTime.now().minusMinutes(10),
                LocalDateTime.now().plusMinutes(10),
                item,
                booker,
                Status.APPROVED
        ));

        Collection<BookingDto> bookings =
                bookingService.getUserBookings(booker.getId(), State.CURRENT);

        assertEquals(1, bookings.size());
    }

    @Test
    void getBooking_shouldReturnForOwner() {
        Booking booking = bookingRepository.save(new Booking(
                null,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item,
                booker,
                Status.WAITING
        ));

        BookingDto result =
                bookingService.getBooking(booking.getId(), owner.getId());

        assertEquals(booking.getId(), result.getId());
    }

}