package nhom22.doctorfinder.data.remote.dto.response;

import com.google.gson.annotations.SerializedName;

/**
 * Response model cho POST /api/follows/{maBacSi}
 * và DELETE /api/follows/{maBacSi}
 */
public class FollowResponse {

    @SerializedName("success")
    public boolean success;

    @SerializedName("message")
    public String message;

    @SerializedName("maNguoiDung")
    public int maNguoiDung;

    @SerializedName("maBacSi")
    public int maBacSi;

    @SerializedName("followed")
    public boolean followed;

    @SerializedName("ngayTheoDoi")
    public String ngayTheoDoi;
}
