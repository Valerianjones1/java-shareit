package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class BookingRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;


    private User user;
    private Booking booking;
    private Booking futureBooking;
    private Booking pastBooking;
    private Booking currentBooking;
    private Item item;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("test@email.ru");
        user.setName("test name");

        item = new Item();
        item.setName("test name");
        item.setDescription("test desc");
        item.setAvailable(true);
        item.setOwner(user);

        booking = new Booking();
        booking.setStartDate(LocalDateTime.now().minusDays(2));
        booking.setEndDate(LocalDateTime.now().minusDays(1));
        booking.setStatus(BookingStatus.APPROVED);
        booking.setBooker(user);
        booking.setItem(item);

        currentBooking = new Booking();
        currentBooking.setStatus(BookingStatus.APPROVED);
        currentBooking.setStartDate(LocalDateTime.now().minusDays(1));
        currentBooking.setEndDate(LocalDateTime.now().plusHours(2));
        currentBooking.setBooker(user);
        currentBooking.setItem(item);


        pastBooking = new Booking();
        pastBooking.setStatus(BookingStatus.APPROVED);
        pastBooking.setStartDate(LocalDateTime.now().minusDays(3));
        pastBooking.setEndDate(LocalDateTime.now().minusDays(1));
        pastBooking.setBooker(user);
        pastBooking.setItem(item);

        futureBooking = new Booking();
        futureBooking.setStatus(BookingStatus.APPROVED);
        futureBooking.setStartDate(LocalDateTime.now().plusMinutes(15));
        futureBooking.setEndDate(LocalDateTime.now().plusHours(1));
        futureBooking.setBooker(user);
        futureBooking.setItem(item);

        userRepository.save(user);
        itemRepository.save(item);

        booking = bookingRepository.save(booking);
        pastBooking = bookingRepository.save(pastBooking);
        currentBooking = bookingRepository.save(currentBooking);
        futureBooking = bookingRepository.save(futureBooking);
    }

    @Test
    void shouldReturnAllBookingByBookerId() {
        List<Booking> bookings = bookingRepository.findAllByBookerId(user.getId(), Pageable.unpaged());

        assertEquals(4, bookings.size());
    }

    @Test
    void shouldFindAllByBookerIdWithPastState() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdWithPastState(user.getId(), LocalDateTime.now(), Pageable.unpaged());

        assertEquals(2, bookings.size());
        assertThat(booking)
                .usingRecursiveComparison()
                .isEqualTo(bookings.get(0));
        assertThat(pastBooking)
                .usingRecursiveComparison()
                .isEqualTo(bookings.get(1));
    }

    @Test
    void shouldFindAllByBookerIdWithFutureState() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdWithFutureState(user.getId(), LocalDateTime.now(), Pageable.unpaged());

        assertEquals(1, bookings.size());
        assertThat(futureBooking)
                .usingRecursiveComparison()
                .isEqualTo(bookings.get(0));
    }

    @Test
    void shouldFindAllByBookerIdWithCurrentState() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdWithCurrentState(user.getId(), LocalDateTime.now(), Pageable.unpaged());

        assertEquals(1, bookings.size());
        assertThat(currentBooking)
                .usingRecursiveComparison()
                .isEqualTo(bookings.get(0));
    }

    @Test
    void shouldFindAllByBookerIdWithRejectedState() {
        booking.setStatus(BookingStatus.REJECTED);
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndStatusEquals(user.getId(),
                BookingStatus.REJECTED, Pageable.unpaged());

        assertEquals(1, bookings.size());
        assertThat(booking)
                .usingRecursiveComparison()
                .isEqualTo(bookings.get(0));
    }

    @Test
    void shouldFindAllByBookerIdWithWaitingState() {
        booking.setStatus(BookingStatus.WAITING);
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndStatusEquals(user.getId(),
                BookingStatus.WAITING, Pageable.unpaged());

        assertEquals(1, bookings.size());
        assertThat(booking)
                .usingRecursiveComparison()
                .isEqualTo(bookings.get(0));
    }

    @Test
    void shouldFindAllByItemOwnerIdWithPastState() {
        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdWithPastState(user.getId(),
                LocalDateTime.now(), Pageable.unpaged());

        assertEquals(2, bookings.size());
        assertThat(booking)
                .usingRecursiveComparison()
                .isEqualTo(bookings.get(0));
        assertThat(pastBooking)
                .usingRecursiveComparison()
                .isEqualTo(bookings.get(1));
    }

    @Test
    void shouldFindAllByItemOwnerIdWithFutureState() {
        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdWithFutureState(user.getId(),
                LocalDateTime.now(), Pageable.unpaged());

        assertEquals(1, bookings.size());
        assertThat(futureBooking)
                .usingRecursiveComparison()
                .isEqualTo(bookings.get(0));
    }


    @Test
    void shouldFindAllByItemOwnerIdWithCurrentState() {
        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdWithCurrentState(user.getId(),
                LocalDateTime.now(), Pageable.unpaged());

        assertEquals(1, bookings.size());
        assertThat(currentBooking)
                .usingRecursiveComparison()
                .isEqualTo(bookings.get(0));
    }

    @Test
    void shouldFindAllByItemOwnerIdAndStatusRejected() {
        booking.setStatus(BookingStatus.REJECTED);
        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndStatusEquals(user.getId(),
                BookingStatus.REJECTED, Pageable.unpaged());

        assertEquals(1, bookings.size());
        assertThat(booking)
                .usingRecursiveComparison()
                .isEqualTo(bookings.get(0));
    }

    @Test
    void shouldFindAllByItemOwnerIdAndStatusWaiting() {
        booking.setStatus(BookingStatus.WAITING);
        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndStatusEquals(user.getId(),
                BookingStatus.WAITING, Pageable.unpaged());

        assertEquals(1, bookings.size());
        assertThat(booking)
                .usingRecursiveComparison()
                .isEqualTo(bookings.get(0));
    }

    @Test
    void shouldFindAllByItemOwnerId() {
        List<Booking> bookings = bookingRepository.findAllByItemOwnerId(user.getId(), Pageable.unpaged());

        assertEquals(4, bookings.size());
        assertThat(booking)
                .usingRecursiveComparison()
                .isEqualTo(bookings.get(0));
    }

    @Test
    void shouldFindFirstByItemIdAndStatusEqualsAndStartDateIsBefore() {
        Booking lastBooking = bookingRepository.findFirstByItemIdAndStatusEqualsAndStartDateIsBefore(item.getId(),
                BookingStatus.APPROVED, LocalDateTime.now(), Sort.by("endDate").descending()).orElse(null);

        assertNotNull(lastBooking);
        assertTrue(lastBooking.getStartDate().isBefore(LocalDateTime.now()));
        assertThat(lastBooking)
                .usingRecursiveComparison()
                .isEqualTo(currentBooking);
    }

    @Test
    void shouldFindFirstByItemIdAndStatusEqualsAndStartDateIsAfter() {
        Booking nextBooking = bookingRepository.findFirstByItemIdAndStatusEqualsAndStartDateIsAfter(item.getId(),
                BookingStatus.APPROVED, LocalDateTime.now(), Sort.by("startDate").ascending()).orElse(null);

        assertNotNull(nextBooking);
        assertTrue(nextBooking.getStartDate().isAfter(LocalDateTime.now()));
        assertThat(nextBooking)
                .usingRecursiveComparison()
                .isEqualTo(futureBooking);
    }

    @Test
    void shouldFindFirstByItemIdAndBookerIdAndStatusEqualsAndEndDateIsBefore() {
        Booking booking = bookingRepository.findFirstByItemIdAndBookerIdAndStatusEqualsAndEndDateIsBefore(item.getId(),
                user.getId(), BookingStatus.APPROVED, LocalDateTime.now(), Sort.by("endDate").descending()).orElse(null);


        assertNotNull(booking);
        assertTrue(booking.getEndDate().isBefore(LocalDateTime.now()));
        assertThat(booking)
                .usingRecursiveComparison()
                .isEqualTo(booking);
    }
}
