package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerId(long bookerId, Sort sort);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "and b.endDate < :time ")
    List<Booking> findAllByBookerIdWithPastState(@Param("bookerId") long bookerId, @Param("time") LocalDateTime time, Sort sort);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "and b.startDate > :time and b.endDate > :time")
    List<Booking> findAllByBookerIdWithFutureState(@Param("bookerId") long bookerId, @Param("time") LocalDateTime time, Sort sort);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "and b.startDate < :time and b.endDate > :time")
    List<Booking> findAllByBookerIdWithCurrentState(@Param("bookerId") long bookerId, @Param("time") LocalDateTime time, Sort sort);

    List<Booking> findAllByBookerIdAndStatusEquals(long bookerId, BookingStatus status, Sort sort);

    List<Booking> findAllByItemOwnerId(long ownerId, Sort sort);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "and b.endDate < :time ")
    List<Booking> findAllByItemOwnerIdWithPastState(@Param("ownerId") long ownerId, @Param("time") LocalDateTime time, Sort sort);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "and b.startDate > :time and b.endDate > :time")
    List<Booking> findAllByItemOwnerIdWithFutureState(@Param("ownerId") long ownerId, @Param("time") LocalDateTime time, Sort sort);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "and b.startDate < :time and b.endDate > :time")
    List<Booking> findAllByItemOwnerIdWithCurrentState(@Param("ownerId") long ownerId, @Param("time") LocalDateTime time, Sort sort);

    List<Booking> findAllByItemOwnerIdAndStatusEquals(long ownerId, BookingStatus status, Sort sort);

    Optional<Booking> findFirstByItemIdAndStatusEqualsAndStartDateIsBefore(long itemId, BookingStatus status,
                                                                           LocalDateTime time, Sort sort);

    Optional<Booking> findFirstByItemIdAndStatusEqualsAndStartDateIsAfter(long itemId, BookingStatus status,
                                                                          LocalDateTime time, Sort sort);

    Optional<Booking> findFirstByItemIdAndBookerIdAndStatusEqualsAndEndDateIsBefore(long itemId, long bookerId,
                                                                                    BookingStatus status, LocalDateTime time,
                                                                                    Sort sort);
}
