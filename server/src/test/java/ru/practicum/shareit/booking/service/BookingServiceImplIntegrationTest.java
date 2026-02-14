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
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.exception.UnavailableItemException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
    public void setUp() {
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
    public void addBooking_shouldPersistAndReturnDto() {
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
    public void update_shouldApproveBooking() {
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
    public void getUserBookings_shouldReturnCurrent() {
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
    public void getBooking_shouldReturnForOwner() {
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

    @Test
    public void addBooking_shouldThrowIfItemNotAvailable() {
        item.setAvailable(false);
        itemRepository.save(item);

        CreateBookingDto dto = CreateBookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        assertThrows(UnavailableItemException.class, () ->
                bookingService.add(booker.getId(), dto));
    }

    @Test
    public void update_shouldRejectBooking() {
        Booking booking = bookingRepository.save(new Booking(
                null,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item,
                booker,
                Status.WAITING
        ));

        BookingDto result =
                bookingService.update(owner.getId(), booking.getId(), false);

        assertEquals(Status.REJECTED, bookingRepository
                .findById(booking.getId())
                .orElseThrow()
                .getStatus());
    }

    @Test
    public void update_shouldThrowIfNotOwner() {
        Booking booking = bookingRepository.save(new Booking(
                null,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item,
                booker,
                Status.WAITING
        ));

        assertThrows(NotOwnerException.class, () ->
                bookingService.update(booker.getId(), booking.getId(), true));
    }

    @Test
    public void getBooking_shouldThrowIfNoAccess() {
        Booking booking = bookingRepository.save(new Booking(
                null,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item,
                booker,
                Status.WAITING
        ));

        User anotherUser = userRepository.save(new User(null, "other", "other@mail.com"));

        assertThrows(NotFoundException.class, () ->
                bookingService.getBooking(booking.getId(), anotherUser.getId()));
    }

    @Test
    public void getUserBookings_shouldReturnPastFutureWaitingRejected() {
        LocalDateTime now = LocalDateTime.now();

        Booking past = bookingRepository.save(new Booking(
                null, now.minusDays(2), now.minusDays(1), item, booker, Status.APPROVED
        ));

        Booking future = bookingRepository.save(new Booking(
                null, now.plusDays(1), now.plusDays(2), item, booker, Status.APPROVED
        ));

        Booking waiting = bookingRepository.save(new Booking(
                null, now.plusHours(1), now.plusHours(2), item, booker, Status.WAITING
        ));

        Booking rejected = bookingRepository.save(new Booking(
                null, now.plusHours(3), now.plusHours(4), item, booker, Status.REJECTED
        ));

        assertEquals(1, bookingService.getUserBookings(booker.getId(), State.PAST).size());
        assertEquals(3, bookingService.getUserBookings(booker.getId(), State.FUTURE).size());
        assertEquals(1, bookingService.getUserBookings(booker.getId(), State.WAITING).size());
        assertEquals(1, bookingService.getUserBookings(booker.getId(), State.REJECTED).size());
    }

    @Test
    public void getOwnerBookings_shouldReturnAllStates() {
        LocalDateTime now = LocalDateTime.now();

        bookingRepository.save(new Booking(
                null, now.minusDays(2), now.minusDays(1), item, booker, Status.APPROVED
        ));

        bookingRepository.save(new Booking(
                null, now.plusDays(1), now.plusDays(2), item, booker, Status.APPROVED
        ));

        bookingRepository.save(new Booking(
                null, now.plusHours(1), now.plusHours(2), item, booker, Status.WAITING
        ));

        bookingRepository.save(new Booking(
                null, now.plusHours(3), now.plusHours(4), item, booker, Status.REJECTED
        ));

        assertEquals(1, bookingService.getOwnerBookings(owner.getId(), State.PAST).size());
        assertEquals(3, bookingService.getOwnerBookings(owner.getId(), State.FUTURE).size());
        assertEquals(1, bookingService.getOwnerBookings(owner.getId(), State.WAITING).size());
        assertEquals(1, bookingService.getOwnerBookings(owner.getId(), State.REJECTED).size());
        assertEquals(4, bookingService.getOwnerBookings(owner.getId(), State.ALL).size());
    }

    @Test
    public void update_shouldThrowIfBookingNotFound() {
        assertThrows(NotFoundException.class, () ->
                bookingService.update(owner.getId(), 9999L, true));
    }

    @Test
    public void getBooking_shouldThrowIfBookingNotFound() {
        assertThrows(NotFoundException.class, () ->
                bookingService.getBooking(9999L, owner.getId()));
    }

    @Test
    public void getUserBookings_shouldReturnAll() {
        bookingRepository.save(new Booking(
                null,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item,
                booker,
                Status.WAITING
        ));

        bookingRepository.save(new Booking(
                null,
                LocalDateTime.now().plusHours(3),
                LocalDateTime.now().plusHours(4),
                item,
                booker,
                Status.APPROVED
        ));

        Collection<BookingDto> bookings =
                bookingService.getUserBookings(booker.getId(), State.ALL);

        assertEquals(2, bookings.size());
    }

    @Test
    public void getOwnerBookings_shouldReturnCurrent() {
        bookingRepository.save(new Booking(
                null,
                LocalDateTime.now().minusMinutes(5),
                LocalDateTime.now().plusMinutes(5),
                item,
                booker,
                Status.APPROVED
        ));

        Collection<BookingDto> bookings =
                bookingService.getOwnerBookings(owner.getId(), State.CURRENT);

        assertEquals(1, bookings.size());
    }

    @Test
    public void getUserBookings_shouldReturnCurrentBooking() {
        bookingRepository.save(new Booking(
                null,
                LocalDateTime.now().minusMinutes(1),
                LocalDateTime.now().plusMinutes(1),
                item,
                booker,
                Status.APPROVED
        ));

        Collection<BookingDto> bookings =
                bookingService.getUserBookings(booker.getId(), State.CURRENT);

        assertEquals(1, bookings.size());
    }

}