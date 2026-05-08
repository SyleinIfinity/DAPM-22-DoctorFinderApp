package nhom22.doctorfinder.data.remote.dto.response;

/**
 * Response DTO cho endpoint GET /api/users/{maNguoiDung}.
 * Ánh xạ trực tiếp với JSON trả về từ server.
 */
public class UserProfileResponse {
    public int    maNguoiDung;
    public String hoLot;
    public String ten;
    public String hoTenDayDu;
    public String soDienThoai;
    public String email;
    public String cccd;
    /** Ngày sinh, có thể null nếu chưa cập nhật */
    public String ngaySinh;
}
