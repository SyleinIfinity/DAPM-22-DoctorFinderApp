package nhom22.doctorfinder.data.remote.dto.request;

/**
 * Request body cho endpoint POST /api/appointments.
 * loaiPhieu nhận một trong: "KHAM_MOI" | "TAI_KHAM" | "YEU_CAU"
 */

public class AppointmentRequest {
    public int    maNguoiDung;
    public int    maChiTiet;
    public String loaiPhieu;        // Backend nhận: "PHONGKHAM"
    public String trieuChungGhiChu;

    public AppointmentRequest(int maNguoiDung, int maChiTiet,
                              String loaiPhieu, String trieuChungGhiChu) {
        this.maNguoiDung      = maNguoiDung;
        this.maChiTiet        = maChiTiet;
        this.loaiPhieu        = loaiPhieu;
        this.trieuChungGhiChu = trieuChungGhiChu;
    }
}
