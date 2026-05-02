package nhom22.doctorfinder.data.remote.dto.request;

public class LoginRequest {
    private String tenDangNhap;
    private String matKhau;

    public LoginRequest(String tenDangNhap, String matKhau) {
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
    }
}