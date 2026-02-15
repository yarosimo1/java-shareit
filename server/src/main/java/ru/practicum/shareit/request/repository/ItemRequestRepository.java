package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    @Query("""
            select ir
            from ItemRequest ir
            left join fetch ir.items
            where ir.requestor.id = :userId
            order by ir.created desc
            """)
    List<ItemRequest> getAllByUserId(@Param("userId") long userId);

    @Query("""
            select distinct ir
            from ItemRequest ir
            left join fetch ir.items
            where ir.requestor.id <> :userId
            order by ir.created desc
            """)
    List<ItemRequest> findAllOtherUsersRequests(@Param("userId") long userId);

    @Query("""
            select ir
            from ItemRequest ir
            left join fetch ir.items
            where ir.id = :itemRequestId
            order by ir.created desc
            """)
    ItemRequest findItemRequestById(@Param("itemRequestId") long itemRequestId);
}