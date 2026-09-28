package org.example.module01_oop;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Module 01 - Kiểm thử hợp đồng equals() & hashCode() của AccountKey")
class AccountKeyTest {

    @Test
    @DisplayName("1. Tính chất phản xạ (Reflexive): x.equals(x) phải trả về true")
    void testReflexive() {
        AccountKey key = new AccountKey("ACC001", "VND");
        assertEquals(key, key);
    }

    @Test
    @DisplayName("2. Tính chất đối xứng (Symmetric): x.equals(y) == y.equals(x)")
    void testSymmetric() {
        AccountKey k1 = new AccountKey("ACC001", "VND");
        AccountKey k2 = new AccountKey("ACC001", "VND");

        assertTrue(k1.equals(k2));
        assertTrue(k2.equals(k1));
    }

    @Test
    @DisplayName("3. Tính chất bắc cầu (Transitive): k1=k2 và k2=k3 thì k1=k3")
    void testTransitive() {
        AccountKey k1 = new AccountKey("ACC001", "VND");
        AccountKey k2 = new AccountKey("ACC001", "VND");
        AccountKey k3 = new AccountKey("ACC001", "VND");

        assertEquals(k1, k2);
        assertEquals(k2, k3);
        assertEquals(k1, k3);
    }

    @Test
    @DisplayName("4. So sánh với null và đối tượng khác kiểu dữ liệu")
    void testNullAndOtherTypes() {
        AccountKey key = new AccountKey("ACC001", "VND");
        assertNotEquals(null, key);
        assertNotEquals("ACC001_VND", key);
    }

    @Test
    @DisplayName("5. Hợp đồng hashCode: Hai đối tượng equals == true thì hashCode PHẢI bằng nhau")
    void testHashCodeContract() {
        AccountKey k1 = new AccountKey("ACC001", "VND");
        AccountKey k2 = new AccountKey("ACC001", "VND");

        assertEquals(k1, k2, "Hai đối tượng phải bằng nhau theo equals");
        assertEquals(k1.hashCode(), k2.hashCode(), "Hai đối tượng equals phải có hashCode giống hệt nhau");
    }

    @Test
    @DisplayName("6. Ứng dụng trong HashMap Cache: Tìm lại được dữ liệu khi dùng instance mới có cùng giá trị")
    void testHashMapLookup() {
        Map<AccountKey, Double> balanceCache = new HashMap<>();

        AccountKey originalKey = new AccountKey("ACC_VN_888", "VND");
        balanceCache.put(originalKey, 5_000_000.0);

        // Giả lập một request backend mới đến, tạo key mới từ request params:
        AccountKey lookupKey = new AccountKey("ACC_VN_888", "VND");

        assertTrue(balanceCache.containsKey(lookupKey), "HashMap phải tìm thấy key dù là 2 object instance khác nhau trong Heap");
        assertEquals(5_000_000.0, balanceCache.get(lookupKey));
    }

    @Test
    @DisplayName("7. Ứng dụng trong HashSet: Không bị nhân đôi phần tử trùng lặp")
    void testHashSetDeduplication() {
        Set<AccountKey> activeAccounts = new HashSet<>();

        AccountKey k1 = new AccountKey("ACC001", "USD");
        AccountKey k2 = new AccountKey("ACC001", "USD");

        activeAccounts.add(k1);
        activeAccounts.add(k2);

        assertEquals(1, activeAccounts.size(), "HashSet chỉ được chứa duy nhất 1 phần tử khi 2 key trùng nhau");
    }
}
