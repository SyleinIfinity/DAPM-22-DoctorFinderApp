package nhom22.doctorfinder.data.remote.dto.response;

import com.google.gson.annotations.SerializedName;

/**
 * Model cho một khung giờ khám từ API:
 * GET /api/doctors/{maBacSi}/working-slots?date=yyyy-MM-dd
 *
 * Ví dụ response:
 * {
 *   "maChiTiet": 7,
 *   "maBacSi": 1,
 *   "ngayCuThe": "2026-05-04",
 *   "gioBatDau": "08:00:00",
 *   "gioKetThuc": "08:30:00",
 *   "trangThai": "TRONG",
 *   "thoiLuongPhut": 30,
 *   ...
 * }
 *
 * Giá trị trangThai:
 *   TRONG      → còn trống   (Available)
 *   DA_DAT     → đã đặt      (Full)
 *   DANG_GIU   → đang giữ    (Full)
 */
public class WorkingSlot {

    @SerializedName("maChiTiet")
    public int maChiTiet;

    @SerializedName("maLichLamViec")
    public int maLichLamViec;

    @SerializedName("maBacSi")
    public int maBacSi;

    @SerializedName("thuTrongTuan")
    public String thuTrongTuan;

    @SerializedName("ngayCuThe")
    public String ngayCuThe;

    @SerializedName("gioBatDau")
    public String gioBatDau;      // e.g. "08:00:00"

    @SerializedName("gioKetThuc")
    public String gioKetThuc;     // e.g. "08:30:00"

    @SerializedName("trangThai")
    public String trangThai;      // "TRONG" | "DA_DAT" | "DANG_GIU"

    @SerializedName("khoaDen")
    public String khoaDen;

    @SerializedName("maPhieuDatLichHienTai")
    public Object maPhieuDatLichHienTai;

    @SerializedName("maKhungGio")
    public int maKhungGio;

    @SerializedName("thoiLuongPhut")
    public int thoiLuongPhut;     // e.g. 30

    @SerializedName("trangThaiLich")
    public String trangThaiLich;  // e.g. "SAP_DIEN_RA"

    // ── Helpers ──────────────────────────────────────────────────────────────

    /**
     * Trả về giờ hiển thị trên button: chỉ lấy HH:mm (bỏ giây).
     * Ví dụ: "08:00:00" → "08:00"
     */
    public String getDisplayTime() {
        if (gioBatDau == null || gioBatDau.length() < 5) return "";
        return gioBatDau.substring(0, 5);
    }

    /**
     * Trả về giờ kết thúc hiển thị: HH:mm.
     * Ví dụ: "08:30:00" → "08:30"
     */
    public String getDisplayEndTime() {
        if (gioKetThuc == null || gioKetThuc.length() < 5) return "";
        return gioKetThuc.substring(0, 5);
    }

    /**
     * Kiểm tra xem slot này có thuộc buổi sáng không (giờ bắt đầu < 12:00).
     */
    public boolean isMorning() {
        if (gioBatDau == null || gioBatDau.length() < 2) return true;
        try {
            int hour = Integer.parseInt(gioBatDau.substring(0, 2));
            return hour < 12;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    /**
     * Kiểm tra slot còn trống.
     */
    public boolean isAvailable() {
        return "TRONG".equals(trangThai);
    }

    /**
     * Kiểm tra slot đã đặt / đang giữ (không thể chọn).
     */
    public boolean isFull() {
        return "DA_DAT".equals(trangThai) || "DANG_GIU".equals(trangThai);
    }
}
