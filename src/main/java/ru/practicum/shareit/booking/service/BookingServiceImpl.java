package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.enumStatus.State;
import ru.practicum.shareit.booking.enumStatus.Status;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.exception.UnavailableItemException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserService userService;
    private final UserMapper userMapper;
    private final ItemService itemService;
    private final ItemMapper itemMapper;

    @Transactional
    @Override
    public BookingDto add(long userId, CreateBookingDto bookingDto) {
        log.info("Добавление аренды {}", bookingDto);

        ItemDto itemDto = itemService.getItem(bookingDto.getItemId());

        //для проверки на существующего пользователя
        userService.getUser(userId);

        if (!itemDto.getAvailable()) {
            throw new UnavailableItemException("The item is booked");
        }

        Booking booking = bookingMapper.toBooking(bookingDto);
        booking.setBooker(userMapper.toUser(userService.getUser(userId)));
        booking.setStatus(Status.WAITING);
        booking.setItem(itemMapper.toItem(itemDto));

        return bookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Transactional
    @Override
    public BookingDto update(long ownerId, long bookingId, boolean approved) {
        log.info("Изменение доступности бронирования {} ", approved);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException("Booking not found"));

        if (!booking.getItem().getOwner().getId().equals(ownerId)) throw new NotOwnerException("You are not owner");

        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }

        Booking savedBooking = bookingRepository.save(booking);

        log.info("Обновленный предмет  {}", booking);
        return bookingMapper.toBookingDto(savedBooking);
    }

    @Override
    public Collection<BookingDto> getUserBookings(long userId, State state) {
        //для проверки пользователя на существование
        userService.getUser(userId);
        
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findAllByBooker(userId);
            case CURRENT -> bookingRepository.findCurrentByBooker(userId, now);
            case PAST -> bookingRepository.findPastByBooker(userId, now);
            case FUTURE -> bookingRepository.findFutureByBooker(userId, now);
            case WAITING ->
                    bookingRepository.findByBookerAndStatus(userId, Status.WAITING);
            case REJECTED ->
                    bookingRepository.findByBookerAndStatus(userId, Status.REJECTED);
        };

        return bookings.stream()
                .map(bookingMapper::toBookingDto)
                .toList();
    }

    @Override
    public Collection<BookingDto> getOwnerBookings(long ownerId, State state) {
        //для проверки пользователя на существование
        userService.getUser(ownerId);

        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findAllByOwner(ownerId);
            case CURRENT -> bookingRepository.findCurrentByOwner(ownerId, now);
            case PAST -> bookingRepository.findPastByOwner(ownerId, now);
            case FUTURE -> bookingRepository.findFutureByOwner(ownerId, now);
            case WAITING ->
                    bookingRepository.findByOwnerAndStatus(ownerId, Status.WAITING);
            case REJECTED ->
                    bookingRepository.findByOwnerAndStatus(ownerId, Status.REJECTED);
        };

        return bookings.stream()
                .map(bookingMapper::toBookingDto)
                .toList();
    }

    @Override
    public BookingDto getBooking(long bookingId, long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException("Booking not found"));

        Long bookerId = booking.getBooker().getId();
        Long ownerId = booking.getItem().getOwner().getId();

        if (bookerId.equals(userId) || ownerId.equals(userId)) {
            return bookingMapper.toBookingDto(booking);
        }

        throw new NotFoundException("Access denied");
    }
}
