package nhom22.doctorfinder.data.remote.dto.request;


import nhom22.doctorfinder.data.remote.dto.DoctorInfo;
import nhom22.doctorfinder.data.remote.dto.UserInfo;

public class RegisterDoctorRequest {
    public UserInfo thongTinNguoiDung;
    public DoctorInfo thongTinBacSi;
    public String otpProofToken;
}