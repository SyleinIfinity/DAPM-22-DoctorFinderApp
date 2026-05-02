package nhom22.doctorfinder.data.remote.dto.request;

public class OtpSendRequest {
    public String email;
    public String purpose = "REGISTER";
    public boolean forceResend = true;
}