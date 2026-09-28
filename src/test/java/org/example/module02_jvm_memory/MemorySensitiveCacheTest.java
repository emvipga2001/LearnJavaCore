package org.example.module02_jvm_memory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Module 02 - Kiểm thử MemorySensitiveCache (LRU & SoftReference)")
class MemorySensitiveCacheTest {

    @Test
    @DisplayName("1. Kiểm tra thao tác put và get thông thường (Happy Path)")
    void testBasicPutAndGet() {
        MemorySensitiveCache<String, String> cache = new MemorySensitiveCache<>(5);

        cache.put("user:101", "Quan Nguyen");
        cache.put("user:102", "Alex");

        Optional<String> user1 = cache.get("user:101");
        Optional<String> user2 = cache.get("user:102");
        Optional<String> notFound = cache.get("user:999");

        assertTrue(user1.isPresent());
        assertEquals("Quan Nguyen", user1.get());
        assertTrue(user2.isPresent());
        assertEquals("Alex", user2.get());
        assertTrue(notFound.isEmpty());
        assertEquals(2, cache.size());
    }

    @Test
    @DisplayName("2. Kiểm tra tính năng loại bỏ phần tử cũ nhất theo LRU khi vượt quá capacity")
    void testLruEvictionWhenCapacityExceeded() {
        // Cache sức chứa tối đa là 3 phần tử
        MemorySensitiveCache<String, Integer> cache = new MemorySensitiveCache<>(3);

        cache.put("A", 1);
        cache.put("B", 2);
        cache.put("C", 3);

        // Lúc này thứ tự truy cập: A (cũ nhất), B, C (mới nhất)
        // Khi ta thêm phần tử D, phần tử A cũ nhất phải bị loại bỏ (evicted)
        cache.put("D", 4);

        assertEquals(3, cache.size());
        assertTrue(cache.get("A").isEmpty(), "Key 'A' phải bị loại bỏ vì là phần tử cũ nhất");
        assertTrue(cache.get("B").isPresent());
        assertTrue(cache.get("C").isPresent());
        assertTrue(cache.get("D").isPresent());
    }

    @Test
    @DisplayName("3. Kiểm tra cơ chế cập nhật thứ tự LRU khi gọi get() (Access-Order)")
    void testLruOrderUpdateOnAccess() {
        MemorySensitiveCache<String, String> cache = new MemorySensitiveCache<>(3);

        cache.put("A", "Apple");
        cache.put("B", "Banana");
        cache.put("C", "Cherry");

        // Gọi get("A") -> 'A' được làm mới, trở thành phần tử được truy cập gần đây nhất!
        // Thứ tự lúc này từ cũ đến mới: B -> C -> A
        cache.get("A");

        // Thêm phần tử 'D' -> Kích hoạt LRU eviction. Phần tử bị đá ra phải là 'B', chứ KHÔNG PHẢI 'A'!
        cache.put("D", "Durian");

        assertEquals(3, cache.size());
        assertTrue(cache.get("B").isEmpty(), "Key 'B' phải bị đá ra vì là phần tử lâu không dùng nhất");
        assertTrue(cache.get("A").isPresent(), "Key 'A' phải còn sống vì vừa được truy cập lại");
        assertTrue(cache.get("C").isPresent());
        assertTrue(cache.get("D").isPresent());
    }

    @Test
    @DisplayName("4. Kiểm tra validate tham số đầu vào (Edge Cases)")
    void testEdgeCases() {
        assertThrows(IllegalArgumentException.class, () -> new MemorySensitiveCache<>(0));
        assertThrows(IllegalArgumentException.class, () -> new MemorySensitiveCache<>(-1));

        MemorySensitiveCache<String, String> cache = new MemorySensitiveCache<>(2);
        assertThrows(IllegalArgumentException.class, () -> cache.put(null, "Value"));
        assertThrows(IllegalArgumentException.class, () -> cache.put("Key", null));

        assertTrue(cache.get(null).isEmpty());
    }

    @Test
    @DisplayName("5. Kiểm tra clear và containsKey")
    void testClearAndContainsKey() {
        MemorySensitiveCache<String, String> cache = new MemorySensitiveCache<>(2);
        cache.put("K1", "V1");
        cache.put("K2", "V2");

        assertTrue(cache.containsKey("K1"));
        cache.clear();

        assertEquals(0, cache.size());
        assertFalse(cache.containsKey("K1"));
    }
}
