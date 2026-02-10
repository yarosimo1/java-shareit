package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.enumStatus.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class BookingRepositoryTest {

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
    void setUp() {
        owner = em.persist(new User(null, "Owner", "owner@mail.ru"));
        booker = em.persist(new User(null, "Booker", "booker@mail.ru"));

        item = em.persist(Item.builder()
                .name("Item")
                .description("Desc")
                .available(true)
                .owner(owner)
                .build());

        pastBooking = em.persist(Booking.builder()
                .item(item)
                .booker(booker)
                .status(Status.APPROVED)
                .start(LocalDateTime.now().minusDays(3))
                .end(LocalDateTime.now().minusDays(2))
                .build());

        currentBooking = em.persist(Booking.builder()
                .item(item)
                .booker(booker)
                .status(Status.APPROVED)
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now().plusHours(1))
                .build());

        futureBooking = em.persist(Booking.builder()
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build());
    }

    @Test
    void findCurrentByBooker() {
        List<Booking> bookings =
                bookingRepository.findCurrentByBooker(booker.getId(), LocalDateTime.now());

        assertThat(bookings).containsExactly(currentBooking);
    }

    @Test
    void findPastByOwner() {
        List<Booking> bookings =
                bookingRepository.findPastByOwner(owner.getId(), LocalDateTime.now());

        assertThat(bookings).containsExactly(pastBooking);
    }

    @Test
    void hasFinishedBooking_true() {
        boolean result = bookingRepository.hasFinishedBooking(
                item.getId(), booker.getId(), LocalDateTime.now());

        assertThat(result).isTrue();
    }
}

