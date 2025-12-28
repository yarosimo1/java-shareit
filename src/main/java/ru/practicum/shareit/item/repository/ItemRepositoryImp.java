package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Slf4j
@Repository
public class ItemRepositoryImp implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Collection<Item> findAll() {
        return items.values();
    }

    @Override
    public Item save(Item item) {
        log.info("Сохранение предмета {}", item);
        item.setId(getNextId());
        items.put(item.getId(), item);
        log.info("Предмет сохранен {}", item);
        return item;
    }

    @Override
    public Item update(Item item) {
        log.info("Обновление предмета {}", item);
        return items.put(item.getId(), item);
    }

    @Override
    public Optional<Item> findById(long itemId) {
        log.info("Поиск предмета {}", itemId);
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public Collection<Item> searchItems(String query) {
        log.info("Поиск по запросу {}", query);
        if (query == null || query.isBlank()) {
            return List.of();
        }

        String lowerQuery = query.toLowerCase();

        return items.values().stream()
                .filter(item -> Boolean.TRUE.equals(item.getAvailable()))
                .filter(item -> item.getName().toLowerCase().contains(lowerQuery)
                        || item.getDescription().toLowerCase().contains(lowerQuery))
                .toList();
    }

    private long getNextId() {
        long currentMaxId = items.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
