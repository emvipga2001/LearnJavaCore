package org.example.module02_jvm_memory;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Bài tập 02: Xây dựng In-Memory LRU Cache an toàn với bộ nhớ (Memory-Sensitive LRU Cache)
 *
 * <p>BỐI CẢNH BACKEND:
 * Trong các microservice xử lý dữ liệu tải cao, việc cache dữ liệu vào RAM giúp giảm tải cho Database.
 * Tuy nhiên, 2 nguy cơ chí mạng thường gặp:
 * 1. Cache phình to vô hạn -> Dẫn đến java.lang.OutOfMemoryError (OOM) làm sập service.
 * 2. Cache không tự loại bỏ dữ liệu cũ (stale/infrequently accessed data).
 *
 * <p>MỤC TIÊU KỸ THUẬT:
 * 1. Triển khai thuật toán LRU (Least Recently Used) để giới hạn kích thước tối đa (capacity).
 * 2. Kết hợp với `SoftReference<V>` của JVM: Nếu Heap bị đầy và GC sắp kích hoạt OOM,
 *    JVM sẽ tự động giải phóng vùng nhớ của các đối tượng được bọc trong SoftReference.
 *
 * @param <K> Kiểu dữ liệu của khóa (Key)
 * @param <V> Kiểu dữ liệu của giá trị (Value)
 */
public class MemorySensitiveCache<K, V> {

    private final int maxCapacity;
    private final Map<K, SoftReference<V>> internalMap;

    /**
     * Khởi tạo Cache với sức chứa tối đa.
     *
     * @param maxCapacity Số lượng phần tử tối đa được lưu trữ trong cache trước khi đẩy phần tử cũ nhất ra ngoài (LRU)
     */
    public MemorySensitiveCache(int maxCapacity) {
        if (maxCapacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than 0");
        }
        this.maxCapacity = maxCapacity;

        // TODO 1: Khởi tạo internalMap bằng LinkedHashMap với cấu hình LRU.
        // GỢI Ý JVM:
        // LinkedHashMap có một constructor đặc biệt:
        //   new LinkedHashMap<>(initialCapacity, loadFactor, accessOrder)
        // - accessOrder = true: Thứ tự duyệt mảng được sắp xếp theo lần TRUY CẬP gần nhất (access-order),
        //                       phục vụ hoàn hảo cho thuật toán LRU!
        // - Đồng thời ghi đè phương thức protected boolean removeEldestEntry(Map.Entry<K, SoftReference<V>> eldest)
        //   để tự động xóa phần tử lâu nhất khi size() > maxCapacity.
        this.internalMap = new LinkedHashMap<K, SoftReference<V>>(maxCapacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, SoftReference<V>> eldest) {
                // Khi size vượt quá maxCapacity, hàm này trả về true
                // -> LinkedHashMap sẽ tự động trảm (evict) phần tử lâu nhất chưa dùng
                return size() > maxCapacity;
            }
        };
    }

    /**
     * Lưu một cặp key-value vào cache.
     *
     * @param key   Khóa định danh (không được null)
     * @param value Giá trị cần cache (không được null)
     */
    public void put(K key, V value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("Key and Value must not be null");
        }

        // TODO 2: Bọc giá trị `value` vào một SoftReference<V> trước khi lưu vào internalMap.
        // ĐIỀU NÀY CÓ Ý NGHĨA GÌ VỚI JVM?
        // - Nếu lưu trực tiếp `value`, nó là một Strong Reference -> GC không bao giờ được thu hồi.
        // - Khi bọc trong SoftReference, GC sẽ thu hồi nếu bộ nhớ Heap rơi vào trạng thái thiếu hụt nghiêm trọng.
        ReferenceQueue<V> queue = new ReferenceQueue<>();
        SoftReference<V> ref = new SoftReference<>(value, queue);
        this.internalMap.put(key, ref);
    }

    /**
     * Lấy giá trị từ cache dựa trên key.
     *
     * @param key Khóa định danh
     * @return Optional chứa giá trị nếu còn tồn tại trong cache và chưa bị GC thu hồi; Optional.empty() nếu ngược lại.
     */
    public Optional<V> get(K key) {
        if (key == null) {
            return Optional.empty();
        }

        // TODO 3:
        // Bước 1: Lấy SoftReference tương ứng từ internalMap.
        // Bước 2: Kiểm tra nếu SoftReference tồn tại, gọi phương thức .get() của SoftReference để lấy đối tượng thực tế.
        // Bước 3:
        //   - Nếu đối tượng thực tế != null: Trả về Optional.of(value).
        //   - Nếu đối tượng thực tế == null (nghĩa là GC đã dọn dẹp nó vì thiếu RAM):
        //     Xóa bỏ luôn "xác rỗng" (key đó) khỏi internalMap để tránh rò rỉ rác bộ nhớ,
        //     sau đó trả về Optional.empty().
        SoftReference<V> ref = this.internalMap.get(key);
        if(ref != null){
            V value = ref.get();
            if (value == null) {
                this.internalMap.remove(key);
                return Optional.empty();
            }
            return Optional.of(value);
        }else{
            return Optional.empty();
        }
    }

    /**
     * Kiểm tra xem key có tồn tại trong cache và value thực tế chưa bị GC dọn hay không.
     */
    public boolean containsKey(K key) {
        return get(key).isPresent();
    }

    /**
     * Trả về số lượng phần tử hiện tại trong cache (chỉ tính các phần tử chưa bị dọn sạch).
     */
    public int size() {
        // TODO 4: Trả về số lượng entry hiện có trong internalMap.
        // Lưu ý: Có thể dọn dẹp các dead references nếu cần, hoặc đơn giản trả về internalMap.size().
        return this.internalMap.size();
    }

    /**
     * Xóa sạch toàn bộ cache.
     */
    public void clear() {
        // TODO 5: Xóa sạch internalMap
        this.internalMap.clear();
    }
}
