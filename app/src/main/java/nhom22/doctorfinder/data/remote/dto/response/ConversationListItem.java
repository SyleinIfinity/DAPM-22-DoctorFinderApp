package nhom22.doctorfinder.data.remote.dto.response;

/**
 * Một dòng trong danh sách cuộc hội thoại (GET /api/conversations?maNguoiDung=...).
 * Gson bỏ qua field không có trong JSON; hỗ trợ thêm một số tên field thường gặp cho tin cuối / thời gian.
 */
public class ConversationListItem {

    public int maCuocHoiThoai;
    public int maBacSi;

    public String hoTenBacSi;
    public String tenBacSi;
    public String anhDaiDienBacSi;

    public String tinNhanCuoi;
    public String noiDungTinNhanCuoi;

    public String thoiGianTinCuoi;
    public String thoiGianCapNhat;

    public Integer soTinChuaDoc;
}
