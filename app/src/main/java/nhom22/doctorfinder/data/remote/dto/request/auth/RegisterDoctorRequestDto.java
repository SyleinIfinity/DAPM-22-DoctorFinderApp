package nhom22.doctorfinder.data.remote.dto.request.auth;


import com.google.gson.annotations.SerializedName;

public class RegisterDoctorRequestDto {
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

    @SerializedName("chuyenKhoa")
    private final String chuyenKhoa;

    @SerializedName("trinhDoChuyenMon")
    private final String trinhDoChuyenMon;

    @SerializedName("maChungChiHanhNghe")
    private final String maChungChiHanhNghe;

    @SerializedName("tenCoSoYTe")
    private final String tenCoSoYTe;

    public RegisterDoctorRequestDto(String tenDangNhap, String matKhau,
                                    String hoLot, String ten,
                                    String soDienThoai, String email, String cccd,
                                    String chuyenKhoa, String trinhDoChuyenMon,
                                    String maChungChiHanhNghe, String tenCoSoYTe) {
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.hoLot = hoLot;
        this.ten = ten;
        this.soDienThoai = soDienThoai;
        this.email = email;
        this.cccd = cccd;
        this.chuyenKhoa = chuyenKhoa;
        this.trinhDoChuyenMon = trinhDoChuyenMon;
        this.maChungChiHanhNghe = maChungChiHanhNghe;
        this.tenCoSoYTe = tenCoSoYTe;
    }
}