package nhom22.doctorfinder.data.remote.dto.response.auth;


import com.google.gson.annotations.SerializedName;

public class OtpVerifyResponseDto {
    @SerializedName("verified")
    private boolean verified;

    @SerializedName("message")
    private String message;

    public boolean isVerified() { return verified; }
    public String getMessage() { return message; }
}