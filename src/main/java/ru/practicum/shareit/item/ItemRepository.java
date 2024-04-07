package ru.practicum.shareit.item;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerId(Long ownerId, Sort sort);

    List<Item> findByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(String name, String description);
}
