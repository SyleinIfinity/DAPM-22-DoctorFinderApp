package nhom22.doctorfinder.data.remote.dto.response.auth;

import com.google.gson.annotations.SerializedName;

public class RegisterResponseDto {
    @SerializedName("maTaiKhoan")
    private int maTaiKhoan;

    @SerializedName("tenDangNhap")
    private String tenDangNhap;

    @SerializedName("vaiTro")
    private String vaiTro;

    @SerializedName("message")
    private String message;

    public int getMaTaiKhoan() { return maTaiKhoan; }
    public String getTenDangNhap() { return tenDangNhap; }
    public String getVaiTro() { return vaiTro; }
    public String getMessage() { return message; }
}