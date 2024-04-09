package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.exception.NotEndedBookingException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotRightOwnerException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemDto create(ItemCreateDto itemCreateDto, long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Пользователь с идентификатором %s не найден", userId)));

        ItemRequest request = itemCreateDto.getRequestId() != null ?
                itemRequestRepository.findById(itemCreateDto.getRequestId()).orElse(null) : null;

        Item item = ItemMapper.mapToItem(itemCreateDto, user, request);

        return ItemMapper.mapToItemDto(repository.save(item));
    }

    @Override
    public ItemDto get(long itemId, long userId) {
        Item item = repository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Вещь с идентификатором %s не найдена", itemId)));

        ItemDto itemDto = ItemMapper.mapToItemDto(item);

        if (userId == item.getOwner().getId()) {
            itemDto.setLastBooking(getLastBooking(itemId));
            itemDto.setNextBooking(getNextBooking(itemId));
        }

        itemDto.setComments(getComments(itemId));

        return itemDto;
    }

    @Override
    public List<ItemDto> getAll(long ownerId, Pageable pageable) {
        List<Item> items = repository.findAllByOwnerId(ownerId, pageable);

        List<Long> itemsIds = items
                .stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        List<Booking> bookings = bookingRepository.findAllByItemIdIn(itemsIds);
        if (bookings.isEmpty()) {
            return items
                    .stream()
                    .map(ItemMapper::mapToItemDto)
                    .collect(Collectors.toList());
        }
        List<Comment> comments = commentRepository.findAllByItemIdIn(itemsIds);

        Map<Long, List<Booking>> itemBookings = bookings.stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId(), Collectors.toList()));
        Map<Long, List<Comment>> itemComments = comments.stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId(), Collectors.toList()));

        return items.stream()
                .map(item -> {
                    BookingItemDto lastBooking = getLastBooking(itemBookings.getOrDefault(item.getId(), new ArrayList<>()));
                    BookingItemDto nextBooking = getNextBooking(itemBookings.getOrDefault(item.getId(), new ArrayList<>()));
                    List<CommentDto> commentsItem = itemComments.containsKey(item.getId()) ?
                            itemComments.get(item.getId())
                                    .stream()
                                    .map(CommentMapper::mapToCommentDto)
                                    .collect(Collectors.toList())
                            : new ArrayList<>();

                    return ItemMapper.mapToItemDto(item, lastBooking,
                            nextBooking, commentsItem);
                })
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto update(ItemCreateDto itemCreateDto, long userId) {
        Item oldItem = repository.findById(itemCreateDto.getId())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Вещь для обновления с идентификатором %s не найдена", itemCreateDto.getId())));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Пользователь с идентификатором %s не найден", userId)));

        if (!oldItem.getOwner().equals(user)) {
            throw new NotRightOwnerException("Вещь может редактировать только владелец");
        }

        Item item = ItemMapper.mapToItem(itemCreateDto);
        Item updatedItem = fillItem(item, oldItem);

        return ItemMapper.mapToItemDto(repository.save(updatedItem));
    }

    @Override
    public List<ItemDto> search(String text, Pageable pageable) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        List<Item> searchedItems = repository.findByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(text, text,
                pageable);
        return searchedItems.stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto createComment(Long itemId, CommentDto commentDto, long userId) {
        Booking booking = bookingRepository.findFirstByItemIdAndBookerIdAndStatusEqualsAndEndDateIsBefore(itemId,
                        userId, BookingStatus.APPROVED, LocalDateTime.now(), Sort.by("endDate").descending())
                .orElseThrow(() -> new NotEndedBookingException("Срок брони должен быть окончен и статус 'APPROVED'"));

        if (booking.getBooker().getId() != userId) {
            throw new NotFoundException("Оставлять комментарии может только человек, который бронировал");
        }

        if (booking.getEndDate().isAfter(LocalDateTime.now())) {
            throw new NotEndedBookingException("Комментировать можно только брони у которых срок закончен");
        }

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Пользователь с идентификатором %s не найден", userId)));
        Item item = booking.getItem();

        Comment comment = CommentMapper.mapToComment(commentDto, author, item);

        return CommentMapper.mapToCommentDto(commentRepository.save(comment));
    }

    private BookingItemDto getLastBooking(long itemId) {
        return bookingRepository.findFirstByItemIdAndStatusEqualsAndStartDateIsBefore(itemId,
                        BookingStatus.APPROVED, LocalDateTime.now(), Sort.by("endDate").descending())
                .map(BookingMapper::mapToBookingItemDto)
                .orElse(null);
    }

    private BookingItemDto getNextBooking(long itemId) {
        return bookingRepository.findFirstByItemIdAndStatusEqualsAndStartDateIsAfter(itemId,
                        BookingStatus.APPROVED, LocalDateTime.now(), Sort.by("startDate").ascending())
                .map(BookingMapper::mapToBookingItemDto)
                .orElse(null);
    }

    private BookingItemDto getLastBooking(List<Booking> bookings) {
        Optional<Booking> lastBooking = bookings
                .stream()
                .filter(booking -> booking.getStatus().equals(BookingStatus.APPROVED)
                        && booking.getStartDate().isBefore(LocalDateTime.now()))
                .max(Comparator.comparing(Booking::getEndDate));

        return lastBooking.map(BookingMapper::mapToBookingItemDto).orElse(null);
    }

    private BookingItemDto getNextBooking(List<Booking> bookings) {
        Optional<Booking> nextBooking = bookings
                .stream()
                .filter(booking -> booking.getStatus().equals(BookingStatus.APPROVED)
                        && booking.getStartDate().isAfter(LocalDateTime.now()))
                .min(Comparator.comparing(Booking::getStartDate));
        return nextBooking.map(BookingMapper::mapToBookingItemDto).orElse(null);
    }

    private List<CommentDto> getComments(long itemId) {
        return commentRepository.findAllByItemId(itemId)
                .stream()
                .map(CommentMapper::mapToCommentDto)
                .collect(Collectors.toList());
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
}
