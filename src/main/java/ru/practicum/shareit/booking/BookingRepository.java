package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerId(long bookerId, Sort sort);

    List<Booking> findAllByItemOwnerId(long ownerId, Sort sort);

    List<Booking> findAllByItemId(long itemId);

    List<Booking> findAllByItemIdAndBookerId(long itemId, long bookerId);
}
