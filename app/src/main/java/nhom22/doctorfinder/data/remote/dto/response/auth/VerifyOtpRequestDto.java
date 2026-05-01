package nhom22.doctorfinder.data.remote.dto.response.auth;



import com.google.gson.annotations.SerializedName;

public class VerifyOtpRequestDto {
    @SerializedName("email")
    private final String email;

    @SerializedName("purpose")
    private final String purpose;

    @SerializedName("otpCode")
    private final String otpCode;

    public VerifyOtpRequestDto(String email, String purpose, String otpCode) {
        this.email = email;
        this.purpose = purpose;
        this.otpCode = otpCode;
    }
}
