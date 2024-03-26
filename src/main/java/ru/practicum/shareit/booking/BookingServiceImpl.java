package ru.practicum.shareit.booking;

import com.google.common.base.Enums;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingUpdateDto;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repo;
    private final UserService userService;
    private final ItemRepository itemRepo;
    private final ModelMapper mapper;


    @Override
    public BookingUpdateDto create(BookingDto bookingDto, long userId) {
        checkIfUserExists(userId);

        Booking booking = mapper.map(bookingDto, Booking.class);

        User booker = mapper.map(userService.get(userId), User.class);
        Item item = itemRepo.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Вещь с идентификатором %s не найдена", bookingDto.getItemId())));

        booking.setBooker(booker);
        booking.setItem(item);

        if (!booking.getItem().getAvailable()) {
            throw new NotAvailableItemException("Запрещено бронировать недоступную вещь");
        }

        long ownerId = item.getOwner().getId();
        if (ownerId == userId) {
            throw new NotRightBookerOrOwnerException("Владелец не может забронировать собственную вещь");
        }

        return mapper.map(repo.save(booking), BookingUpdateDto.class);
    }

    @Override
    public BookingUpdateDto updateStatus(long bookingId, long userId, Boolean approved) {
        Booking booking = repo.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Бронь с идентификатором %s не найдена", bookingId)));

        if (booking.getStatus().equals(BookingState.APPROVED)) {
            throw new AlreadyApprovedException("Бронь уже подтверждена");
        }

        long ownerId = booking.getItem().getOwner().getId();

        if (ownerId != userId) {
            throw new NotRightBookerOrOwnerException("Подтвердить запрос на бронирование может только владелец вещи");
        }

        booking.setStatus(approved ? BookingState.APPROVED : BookingState.REJECTED);

        return mapper.map(repo.save(booking), BookingUpdateDto.class);
    }

    @Override
    public BookingUpdateDto get(long bookingId, long userId) {
        Booking booking = repo.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Бронь с идентификатором %s не найдена", bookingId)));

        long bookerId = booking.getBooker().getId();
        long ownerId = booking.getItem().getOwner().getId();

        if (userId != bookerId && userId != ownerId) {
            throw new NotRightBookerOrOwnerException("Получить бронь можно либо владельцу вещи либо автором бронирования");
        }

        return mapper.map(booking, BookingUpdateDto.class);
    }

    @Override
    public List<BookingUpdateDto> getAllByUser(long userId, String state) {
        checkIfStateSupports(state);
        checkIfUserExists(userId);

        return repo.findAllByBookerIdOrderByStartDateDesc(userId)
                .stream()
                .map(booking -> mapper.map(booking, BookingUpdateDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingUpdateDto> getAllByOwnerItems(long userId, String state) {
        checkIfStateSupports(state);
        checkIfUserExists(userId);

        return repo.findAllByItemOwnerIdOrderByStartDateDesc(userId)
                .stream()
                .map(booking -> mapper.map(booking, BookingUpdateDto.class))
                .collect(Collectors.toList());
    }

    private void checkIfUserExists(long userId) {
        userService.get(userId);
    }

    private void checkIfStateSupports(String state) {
        if (!Enums.getIfPresent(ParamState.class, state).isPresent()) {
            throw new NotSupportedStateException("Unknown state: UNSUPPORTED_STATUS");
        }
    }
}
