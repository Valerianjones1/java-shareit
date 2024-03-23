package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerId(Long ownerId);

    List<Item> findByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(String name, String description);
}
