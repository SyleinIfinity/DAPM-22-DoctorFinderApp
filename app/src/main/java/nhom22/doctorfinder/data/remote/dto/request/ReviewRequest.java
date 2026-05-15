package nhom22.doctorfinder.data.remote.dto.request;

import com.google.gson.annotations.SerializedName;

/**
 * Body JSON cho endpoint POST /api/reviews.
 * Không cần token – gửi thẳng không có Authorization header.
 */
public class ReviewRequest {

    @SerializedName("maNguoiDung")
    public int maNguoiDung;

    @SerializedName("maBacSi")
    public int maBacSi;

    @SerializedName("soSao")
    public int soSao;

    @SerializedName("noiDung")
    public String noiDung;

    public ReviewRequest(int maNguoiDung, int maBacSi, int soSao, String noiDung) {
        this.maNguoiDung = maNguoiDung;
        this.maBacSi     = maBacSi;
        this.soSao       = soSao;
        this.noiDung     = noiDung;
    }
}
