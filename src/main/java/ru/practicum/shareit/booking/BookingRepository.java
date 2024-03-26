package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDateDesc(long bookerId);

    List<Booking> findAllByItemOwnerIdOrderByStartDateDesc(long ownerId);
}
