package nhom22.doctorfinder.data.remote.dto.request.auth;

import com.google.gson.annotations.SerializedName;


public class RegisterUserRequestDto {
    @SerializedName("tenDangNhap")
    private final String tenDangNhap;

    @SerializedName("matKhau")
    private final String matKhau;

    @SerializedName("hoLot")
    private final String hoLot;

    @SerializedName("ten")
    private final String ten;

    @SerializedName("soDienThoai")
    private final String soDienThoai;

    @SerializedName("email")
    private final String email;

    @SerializedName("cccd")
    private final String cccd;



    public RegisterUserRequestDto(String tenDangNhap, String matKhau,
                                  String hoLot, String ten,
                                  String soDienThoai, String email, String cccd) {
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.hoLot = hoLot;
        this.ten = ten;
        this.soDienThoai = soDienThoai;
        this.email = email;
        this.cccd = cccd;
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public String getHoLot() {
        return hoLot;
    }

    public String getTen() {
        return ten;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public String getEmail() {
        return email;
    }

    public String getCccd() {
        return cccd;
    }

    @Override
    public String toString() {
        return "RegisterUserRequestDto{" +
                "tenDangNhap='" + tenDangNhap + '\'' +
                ", matKhau='" + matKhau + '\'' +
                ", hoLot='" + hoLot + '\'' +
                ", ten='" + ten + '\'' +
                ", soDienThoai='" + soDienThoai + '\'' +
                ", email='" + email + '\'' +
                ", cccd='" + cccd + '\'' +
                '}';
    }
}