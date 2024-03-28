package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotRightOwnerException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repo;

    private final UserService userService;

    private final BookingRepository bookingRepo;

    @Override
    public ItemDto create(ItemDto itemDto, long userId) {
        checkIfUserExists(userId);

        User owner = UserMapper.mapToUser(userService.get(userId));
        Item item = ItemMapper.mapToItem(itemDto, owner);

        return ItemMapper.mapToItemDto(repo.save(item));
    }

    @Override
    public ItemResponseDto get(long itemId, long userId) {
        Item item = repo.findById(itemId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Вещь с идентификатором %s не найдена", itemId)));

        ItemResponseDto itemResponseDto = ItemMapper.mapToItemResponseDto(item);
        List<Booking> itemBookings = bookingRepo.findAllByItemId(itemId);

        if (userId == item.getOwner().getId()) {
            itemResponseDto.setLastBooking(getLastBooking(itemBookings));
            itemResponseDto.setNextBooking(getNextBooking(itemBookings));
        }

        return itemResponseDto;
    }

    @Override
    public List<ItemResponseDto> getAll(long ownerId) {
        List<Item> items = repo.findAllByOwnerIdOrderByIdAsc(ownerId);
        List<ItemResponseDto> itemResponseDtos = items.stream()
                .map(ItemMapper::mapToItemResponseDto)
                .collect(Collectors.toList());

        itemResponseDtos.forEach(item -> {
            long itemId = item.getId();
            item.setLastBooking(getLastBooking(bookingRepo.findAllByItemId(itemId)));
            item.setNextBooking(getNextBooking(bookingRepo.findAllByItemId(itemId)));
        });
        return itemResponseDtos;
    }

    @Override
    public ItemDto update(ItemDto itemDto, long userId) {
        Item oldItem = repo.findById(itemDto.getId())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Вещь для обновления с идентификатором %s не найдена", itemDto.getId())));

        User user = UserMapper.mapToUser(userService.get(userId));
        if (!oldItem.getOwner().equals(user)) {
            throw new NotRightOwnerException("Вещь может редактировать только владелец");
        }

        Item item = ItemMapper.mapToItem(itemDto);
        Item updatedItem = fillItem(item, oldItem);

        return ItemMapper.mapToItemDto(repo.save(updatedItem));
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        List<Item> searchedItems = repo.findByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(text, text);
        return searchedItems.stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    private void checkIfUserExists(long userId) {
        userService.get(userId);
    }

    private Item fillItem(Item newItem, Item oldItem) {
        if (newItem.getName() != null) {
            oldItem.setName(newItem.getName());
        }
        if (newItem.getDescription() != null) {
            oldItem.setDescription(newItem.getDescription());
        }
        if (newItem.getAvailable() != null) {
            oldItem.setAvailable(newItem.getAvailable());
        }
        if (newItem.getOwner() != null) {
            oldItem.setOwner(newItem.getOwner());
        }
        return oldItem;
    }

    private BookingItemDto getLastBooking(List<Booking> bookings) {
        Optional<Booking> lastBooking = bookings
                .stream()
                .filter(booking -> booking.getStatus().equals(BookingState.APPROVED)
                        && booking.getStartDate().isBefore(LocalDateTime.now()))
                .max(Comparator.comparing(Booking::getEndDate));


        return lastBooking.map(BookingMapper::mapToBookingItemDto).orElse(null);
    }

    private BookingItemDto getNextBooking(List<Booking> bookings) {
        Optional<Booking> nextBooking = bookings
                .stream()
                .filter(booking -> booking.getStatus().equals(BookingState.APPROVED)
                        && booking.getStartDate().isAfter(LocalDateTime.now())).
                min(Comparator.comparing(Booking::getStartDate));

        return nextBooking.map(BookingMapper::mapToBookingItemDto).orElse(null);
    }
}
