package nhom22.doctorfinder.data.remote.dto.response;

/**
 * Response DTO cho endpoint POST /api/appointments.
 * trangThaiPhieu enum: CHO_XAC_NHAN | DA_XAC_NHAN | DA_HUY | TU_CHOI
 */

public class AppointmentResponse {
    public int    maPhieuDatLich;
    public String trangThaiPhieu;       // CHO_XAC_NHAN | DA_XAC_NHAN | DA_HUY | TU_CHOI
    public String lyDoTuChoi;
    public String loaiPhieu;
    public String trieuChungGhiChu;

    // Bệnh nhân
    public int    maNguoiDung;
    public String hoTenBenhNhan;
    public String soDienThoaiBenhNhan;  // ⚠️ Backend trả soDienThoaiBenhNhan, không phải soDienThoai
    public String emailBenhNhan;

    // Bác sĩ
    public int    maBacSi;
    public String hoTenBacSi;
    public String chuyenKhoa;
    public String tenCoSoYTe;
    public String diaChiLamViec;

    // Lịch hẹn
    public String ngayCuThe;
    public String thuTrongTuan;
    public String gioBatDau;
    public String gioKetThuc;
    public int    maChiTiet;
    public int    maLichLamViec;
    public int    maKhungGio;
    public int    thoiLuongPhut;
    public String trangThaiLich;
}