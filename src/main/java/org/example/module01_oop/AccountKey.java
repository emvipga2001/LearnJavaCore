package org.example.module01_oop;

import java.util.Objects;

/**
 * Bài tập 01: Thiết kế Khóa tài khoản (AccountKey) làm key trong Map / In-memory Cache.
 *
 * <p>BỐI CẢNH BACKEND:
 * Trong hệ thống backend tài chính/ngân hàng, các transaction được nhóm theo AccountKey
 * gồm `accountId` (mã tài khoản) và `currency` (loại tiền tệ, ví dụ: "VND", "USD").
 * Đối tượng này được dùng làm key trong HashMap/ConcurrentHashMap làm cache tầng memory.
 *
 * <p>NHIỆM VỤ:
 * Triển khai đầy đủ và chính xác contract của equals() và hashCode() để:
 * 1. Đảm bảo tính bất biến (Immutability).
 * 2. Phân bố hash đồng đều, hạn chế tối đa va chạm (Hash Collision).
 * 3. Ngăn ngừa thất lạc dữ liệu hoặc rò rỉ bộ nhớ (Memory Leak) trong Map/Set.
 */
public final class AccountKey {

    private final String accountId;
    private final String currency;

    public AccountKey(String accountId, String currency) {
        if (accountId == null || currency == null) {
            throw new IllegalArgumentException("accountId and currency must not be null");
        }
        this.accountId = accountId;
        this.currency = currency.toUpperCase().trim();
    }

    public String getAccountId() {
        return accountId;
    }

    public String getCurrency() {
        return currency;
    }

    /**
     * TODO 1: Triển khai equals() tuân thủ đầy đủ 5 tính chất của Java Specification:
     * - Phản xạ (Reflexive): x.equals(x) == true
     * - Đối xứng (Symmetric): x.equals(y) == y.equals(x)
     * - Bắc cầu (Transitive): x.equals(y) && y.equals(z) => x.equals(z)
     * - Nhất quán (Consistent): nhiều lần gọi cho kết quả không đổi nếu đối tượng không đổi
     * - So sánh với null: x.equals(null) == false
     *
     * Gợi ý tối ưu hiệu năng:
     * - Kiểm tra tham chiếu bằng toán tử `==` trước tiên.
     * - Dùng `getClass()` hoặc `instanceof` (lưu ý tính đối xứng khi kế thừa).
     */
    @Override
    public boolean equals(Object obj) {
        // TODO: Xóa dòng ném exception dưới đây và tự triển khai logic
//        // Bước 1: Kiểm tra phản xạ (Reflexive) & tối ưu hiệu năng
//        // Nếu cả 2 cùng trỏ vào 1 ô nhớ trên RAM thì chắc chắn bằng nhau
//        if (this == obj) {
//            return true;
//        }
//
//        // Bước 2: Kiểm tra null và kiểu dữ liệu (Class)
//        // Nếu obj là null hoặc không cùng Class -> lập tức trả về false
//        if (obj == null || getClass() != obj.getClass()) {
//            return false;
//        }
//
//        // Bước 3: Ép kiểu (Type Casting)
//        // Lúc này chắc chắn obj cùng kiểu với class hiện tại
//        AccountKey other = (AccountKey) obj;
//
//        // Bước 4: So sánh từng thuộc tính (field) quan trọng làm nên danh tính của object
//        // Dùng Objects.equals() cho Object và toán tử == cho kiểu nguyên thủy (primitive)
//        return Objects.equals(this.accountId, other.accountId) &&
//                Objects.equals(this.currency, other.currency);

        if (this == obj) return true;
        if (!(obj instanceof AccountKey other)) return false;

        // Vì constructor đã chặn null, ta có thể so sánh trực tiếp .equals() không cần qua Objects.equals
        return this.accountId.equals(other.accountId) &&
                this.currency.equals(other.currency);
    }

    /**
     * TODO 2: Triển khai hashCode() tuân thủ hợp đồng với equals():
     * - Nếu 2 object equals() == true thì hashCode() BẮT BUỘC phải bằng nhau.
     * - Nếu 2 object equals() == false, hashCode KHÔNG bắt buộc khác nhau nhưng
     *   nên khác nhau để tối ưu tốc độ tra cứu O(1) của HashMap.
     *
     * Gợi ý:
     * - Sử dụng số nguyên tố (thường là 31) để nhân tích lũy, hoặc Objects.hash().
     * - Hãy tự tay viết thuật toán tính toán thay vì chỉ gọi Objects.hash() để hiểu rõ cách JVM tính.
     */
    @Override
    public int hashCode() {
        // TODO: Xóa dòng ném exception dưới đây và tự triển khai logic
        //return Objects.hash(accountId, currency);
        int result = accountId.hashCode();
        result = 31 * result + currency.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "AccountKey{" +
                "accountId='" + accountId + '\'' +
                ", currency='" + currency + '\'' +
                '}';
    }
}
