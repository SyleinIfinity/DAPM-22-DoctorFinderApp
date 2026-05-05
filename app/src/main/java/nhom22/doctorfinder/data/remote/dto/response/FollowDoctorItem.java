package nhom22.doctorfinder.data.remote.dto.response;

import com.google.gson.annotations.SerializedName;

/**
 * Một phần tử trong danh sách bác sĩ đã theo dõi.
 * Response của GET /api/follows
 */
public class FollowDoctorItem {

    @SerializedName("maNguoiDung")
    public int maNguoiDung;

    @SerializedName("maBacSi")
    public int maBacSi;

    @SerializedName("hoTenBacSi")
    public String hoTenBacSi;

    @SerializedName("chuyenKhoa")
    public String chuyenKhoa;

    @SerializedName("tenCoSoYTe")
    public String tenCoSoYTe;

    @SerializedName("diaChiLamViec")
    public String diaChiLamViec;

    @SerializedName("anhDaiDienBacSi")
    public String anhDaiDienBacSi;

    @SerializedName("ngayTheoDoi")
    public String ngayTheoDoi;
}
