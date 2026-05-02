package nhom22.doctorfinder.data.remote.dto.request;

public class OtpVerifyRequest {
    public String email;
    public String purpose = "REGISTER";
    public String otpCode;
}