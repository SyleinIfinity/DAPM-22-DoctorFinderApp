package nhom22.doctorfinder.data.remote.dto.response;

/**
 * Response POST /api/conversations
 */
public class ConversationResponse {

    public int maCuocHoiThoai;
    /** Tên hiển thị bác sĩ (ưu tiên nếu server trả về) */
    public String hoTenBacSi;
    public String tenBacSi;
    public String anhDaiDienBacSi;
}
