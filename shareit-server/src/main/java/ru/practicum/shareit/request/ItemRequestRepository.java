package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequestorId(long requestorId, Sort sort);

    List<ItemRequest> findAllByRequestorIdNot(long requestId, Pageable pageable);
}
