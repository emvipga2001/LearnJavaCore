# AGENT CONTEXT & PROJECT PROGRESS

> **Mục tiêu:** Đào tạo và làm chủ Java Core chuyên sâu phục vụ thiết kế hệ thống Backend hiệu năng cao, hiểu sâu bản chất JVM, bộ nhớ và concurrency mà không phụ thuộc vào framework bên ngoài.

---

## 1. Nguyên tắc & Quy chuẩn kỹ thuật đã thống nhất
- **Phạm vi:** 100% Pure Java (Java SE / Core APIs), tuyệt đối không dùng Spring, Spring Boot hay thư viện bên ngoài.
- **Tiêu chuẩn bài giảng (3 tầng):**
  1. *Lý thuyết chuyên sâu (Deep Dive):* Bản chất tầng thấp (JVM Memory, Bytecode, CPU Cache, OS Thread, Big-O).
  2. *Giải thích đơn giản (ELI5 / Mental Model):* Ẩn dụ thực tế, trực quan, dễ nhớ.
  3. *Code minh họa:* Snippet mẫu chỉ rõ hành vi runtime.
- **Quy trình thực hành bắt buộc:**
  - AI cung cấp **Skeleton code** với JavaDoc và các điểm `// TODO:`.
  - Nêu rõ **Task, Constraints và Acceptance Criteria**.
  - Cung cấp sẵn bộ **Unit Tests (JUnit 5)** để người học tự nghiệm thu.
  - Review chi tiết: Tính đúng đắn, Hiệu năng/GC, Concurrency Safety và Clean Code.

---

## 2. Tiến độ & Các quyết định kỹ thuật đã đạt được

### 📁 Hạ tầng dự án
- [x] Tạo file quy chuẩn hệ thống: [GEMINI.md](file:///d:/Work/Projects/LearnJavaCore/GEMINI.md) và [.agents/rules/java_core_mentor.md](file:///d:/Work/Projects/LearnJavaCore/.agents/rules/java_core_mentor.md).
- [x] Cấu hình JUnit 5 trong [pom.xml](file:///d:/Work/Projects/LearnJavaCore/pom.xml).

---

### 🧩 Module 01: OOP & JVM Memory Fundamentals
- **Trọng tâm:** Hợp đồng `equals()` và `hashCode()`, Object Memory Layout, Java Records.
- **Mã nguồn:**
  - Skeleton & Implementation: [AccountKey.java](file:///d:/Work/Projects/LearnJavaCore/src/main/java/org/example/module01_oop/AccountKey.java)
  - Unit Test: [AccountKeyTest.java](file:///d:/Work/Projects/LearnJavaCore/src/test/java/org/example/module01_oop/AccountKeyTest.java) *(Passed 7/7)*
- **Quyết định kỹ thuật cốt lõi:**
  1. **Tối ưu so sánh:** Kiểm tra `this == obj` để đạt $O(1)$ ngay lập tức trên CPU register.
  2. **Pattern Matching for `instanceof`:** Viết code ngắn gọn, null-safe, tránh ép kiểu thủ công.
  3. **Tránh GC Pressure:** Không dùng `Objects.hash(...)` trong các service high-throughput vì tạo mảng rác `Object[]` trên Heap (Eden space) mỗi lần gọi. Thay vào đó tự tính với số nguyên tố `31` (được JIT compiler tối ưu thành phép shift bit `(i << 5) - i` và phép trừ ở mức vi xử lý).
  4. **Caching HashCode:** Lưu lại giá trị hash sau lần tính đầu tiên đối với các key bất biến.
  5. **Bản chất `record`:** Dùng làm DTO/Response bất biến; tuy nhiên `record` không cho phép khai báo instance field phụ để cache hashcode như `class` truyền thống.

---

### 🧩 Module 02: JVM Memory Architecture & Reference Types
- **Trọng tâm:** Vòng đời Object, Garbage Collection (GC Roots), 4 cấp độ Reference (Strong, Soft, Weak, Phantom).
- **Mã nguồn:**
  - Skeleton & Implementation: [MemorySensitiveCache.java](file:///d:/Work/Projects/LearnJavaCore/src/main/java/org/example/module02_jvm_memory/MemorySensitiveCache.java)
  - Unit Test: [MemorySensitiveCacheTest.java](file:///d:/Work/Projects/LearnJavaCore/src/test/java/org/example/module02_jvm_memory/MemorySensitiveCacheTest.java) *(Passed 5/5)*
- **Quyết định kỹ thuật cốt lõi:**
  1. **Chống sập Server do OOM:** Dùng `SoftReference<V>` bọc value, cho phép GC tự động giải phóng vùng nhớ khi RAM của JVM sắp cạn kiệt trước khi quăng `OutOfMemoryError`.
  2. **Triển khai LRU Eviction:** Dùng `LinkedHashMap` với constructor `(capacity, 0.75f, true)` (`accessOrder = true`) kết hợp ghi đè `removeEldestEntry()`.
  3. **Tối ưu Double Lookup:** Tránh gọi `map.get()` hai lần; lưu kết quả vào biến cục bộ trên Thread Stack để tiết kiệm CPU và tránh dịch chuyển node 2 lần trong linked list.
  4. **Chống rò rỉ "Vỏ ốc rỗng" (Zombie Entries):** Khi `ref.get() == null` (do GC đã thu hồi value), chủ động gọi `map.remove(key)` để xóa bỏ key rác, tránh lãng phí RAM.
  5. **Mở rộng kiến trúc:** Hiểu cơ chế `ReferenceQueue` được các thư viện cache chuyên nghiệp (Caffeine, Guava) sử dụng để dọn dẹp reference rác ở background.

---

## 3. Kế hoạch & Việc cần làm tiếp theo (Next Action)

### 🎯 Module 03: Collections Framework Under The Hood
- **Bài học tiếp theo:** **Bản chất bên trong `HashMap` (Bit Masking, Hash Collision, Treeification & Resize Mechanism)**.
- **Nội dung lý thuyết chuyên sâu:**
  - Tại sao mảng bucket trong `HashMap` luôn có dung lượng là lũy thừa của 2 ($2^n$)?
  - Phép tính bitwise `(n - 1) & hash` thay thế cho phép chia lấy dư `%`.
  - Phân tích hiện tượng va chạm băm (Hash Collision) và ngưỡng chuyển đổi sang Cây Đỏ - Đen (Red-Black Tree threshold 8/6).
  - Cơ chế Resize và Rehash; Vấn đề Deadlock vòng lặp vô hạn ở Java 7 vs giải pháp chia đôi mảng (High/Low chain) ở Java 8+.
- **Task thực hành dự kiến:**
  - Tự tay xây dựng một **Custom Mini-HashMap** từ mảng thô (`Entry<K, V>[] table`) hỗ trợ `put()`, `get()`, xử lý va chạm bằng Chaining và tự động Resize khi vượt quá `threshold = capacity * loadFactor`.
