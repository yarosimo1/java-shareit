package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.booking.enumStatus.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class BookingRepositoryTest {

    private final LocalDateTime now = LocalDateTime.now();
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private TestEntityManager em;
    private User owner;
    private User booker;
    private Item item;
    private Booking pastBooking;
    private Booking currentBooking;
    private Booking futureBooking;

    @BeforeEach
    public void setUp() {
        owner = em.persist(new User(
                null,
                "owner",
                "owner@mail.com"
        ));

        booker = em.persist(new User(
                null,
                "booker",
                "booker@mail.com"
        ));

        item = em.persist(new Item(
                null,
                owner,
                "item",
                "desc",
                true,
                null,
                List.of()
        ));

        pastBooking = em.persist(new Booking(
                null,
                now.minusDays(3),
                now.minusDays(1),
                item,
                booker,
                Status.APPROVED
        ));

        currentBooking = em.persist(new Booking(
                null,
                now.minusHours(1),
                now.plusHours(1),
                item,
                booker,
                Status.APPROVED
        ));

        futureBooking = em.persist(new Booking(
                null,
                now.plusDays(1),
                now.plusDays(2),
                item,
                booker,
                Status.WAITING
        ));

        em.flush();
        em.clear();
    }

    @Test
    public void findAllByBooker_shouldReturnAllOrdered() {
        List<Booking> bookings = bookingRepository.findAllByBooker(booker.getId());

        assertEquals(3, bookings.size());
        assertEquals(futureBooking.getId(), bookings.get(0).getId());
    }

    @Test
    public void findCurrentByBooker_shouldReturnCurrent() {
        List<Booking> bookings =
                bookingRepository.findCurrentByBooker(booker.getId(), now);

        assertEquals(1, bookings.size());
        assertEquals(currentBooking.getId(), bookings.get(0).getId());
    }

    @Test
    public void findPastByBooker_shouldReturnPast() {
        List<Booking> bookings =
                bookingRepository.findPastByBooker(booker.getId(), now);

        assertEquals(1, bookings.size());
        assertEquals(pastBooking.getId(), bookings.get(0).getId());
    }

    @Test
    public void findFutureByBooker_shouldReturnFuture() {
        List<Booking> bookings =
                bookingRepository.findFutureByBooker(booker.getId(), now);

        assertEquals(1, bookings.size());
        assertEquals(futureBooking.getId(), bookings.get(0).getId());
    }

    @Test
    public void findByBookerAndStatus_shouldReturnWaiting() {
        List<Booking> bookings =
                bookingRepository.findByBookerAndStatus(booker.getId(), Status.WAITING);

        assertEquals(1, bookings.size());
        assertEquals(futureBooking.getId(), bookings.get(0).getId());
    }

    @Test
    public void findAllByOwner_shouldReturnAll() {
        List<Booking> bookings =
                bookingRepository.findAllByOwner(owner.getId());

        assertEquals(3, bookings.size());
    }

    @Test
    public void hasFinishedBooking_shouldReturnTrue() {
        boolean result = bookingRepository.hasFinishedBooking(
                item.getId(),
                booker.getId(),
                now
        );

        assertTrue(result);
    }

    @Test
    public void findLastBooking_shouldReturnLast() {
        List<Booking> bookings =
                bookingRepository.findLastBooking(item.getId(), now);

        assertFalse(bookings.isEmpty());
        assertEquals(currentBooking.getId(), bookings.get(0).getId());
    }

    @Test
    public void findNextBooking_shouldReturnNext() {
        List<Booking> bookings =
                bookingRepository.findNextBooking(item.getId(), now);

        assertFalse(bookings.isEmpty());
    }

}