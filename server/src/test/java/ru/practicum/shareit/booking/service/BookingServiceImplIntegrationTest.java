package ru.practicum.shareit.booking.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.enumStatus.State;
import ru.practicum.shareit.booking.enumStatus.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
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
        owner = userRepository.save(
                new User(null, "Owner", "owner@mail.ru")
        );

        booker = userRepository.save(
                new User(null, "Booker", "booker@mail.ru")
        );

        item = itemRepository.save(
                Item.builder()
                        .name("Drill")
                        .description("Power drill")
                        .available(true)
                        .owner(owner)
                        .build()
        );
    }

    @Test
    void addBooking_shouldCreateBooking() {
        CreateBookingDto dto = new CreateBookingDto();
        dto.setItemId(item.getId());
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto booking = bookingService.add(booker.getId(), dto);

        assertThat(booking.getId()).isNotNull();
        assertThat(booking.getStatus()).isEqualTo(Status.WAITING);
    }

    @Test
    void getUserBookings_shouldReturnOne() {
        bookingService.add(booker.getId(), createDto());

        Collection<BookingDto> bookings =
                bookingService.getUserBookings(booker.getId(), State.ALL);

        assertThat(bookings).hasSize(1);
    }

    private CreateBookingDto createDto() {
        CreateBookingDto dto = new CreateBookingDto();
        dto.setItemId(item.getId());
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));
        return dto;
    }
}

