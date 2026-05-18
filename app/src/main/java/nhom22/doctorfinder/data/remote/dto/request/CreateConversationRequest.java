package nhom22.doctorfinder.data.remote.dto.request;

/**
 * Body POST /api/conversations
 */
public class CreateConversationRequest {

    public int maNguoiDung;
    public int maBacSi;

    public CreateConversationRequest(int maNguoiDung, int maBacSi) {
        this.maNguoiDung = maNguoiDung;
        this.maBacSi = maBacSi;
    }
}
