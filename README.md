# LearnJavaCore

Dự án tự học và làm chủ **Java Core chuyên sâu** phục vụ thiết kế hệ thống Backend hiệu năng cao (100% Pure Java SE / Core APIs).

## 📌 Lộ trình học tập
- **Module 01: OOP & JVM Memory Fundamentals**
  - Hợp đồng `equals()` và `hashCode()` ([AccountKey.java](src/main/java/org/example/module01_oop/AccountKey.java)).
  - Zero-allocation hashcode calculation, caching hash code, `record` vs `class`.
- **Module 02: JVM Memory Architecture & Reference Types**
  - GC Roots & Vòng đời Object.
  - 4 cấp độ tham chiếu: Strong, Soft, Weak, Phantom Reference.
  - In-Memory LRU Cache an toàn bộ nhớ ([MemorySensitiveCache.java](src/main/java/org/example/module02_jvm_memory/MemorySensitiveCache.java)).
- **Module 03: Collections Framework Under The Hood** (Đang triển khai)
  - Bit masking `(n - 1) & hash`, collision resolution, red-black treeification, resize mechanism.

## 📖 Tài liệu liên quan
- `GEMINI.md`: Bộ quy chuẩn workspace và phong cách học.
- `AGENT_CONTEXT.md`: Nhật ký tiến độ và các quyết định kỹ thuật đã thống nhất.
