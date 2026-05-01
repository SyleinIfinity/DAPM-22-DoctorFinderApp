package nhom22.doctorfinder.data.remote.dto.response.auth;

// ─── SendOtpRequestDto.java ───────────────────────────────────────────────────

import com.google.gson.annotations.SerializedName;

public class SendOtpRequestDto {
    @SerializedName("email")
    private final String email;

    @SerializedName("purpose")
    private final String purpose;   // e.g. "REGISTER"

    @SerializedName("forceResend")
    private final boolean forceResend;

    public SendOtpRequestDto(String email, String purpose, boolean forceResend) {
        this.email = email;
        this.purpose = purpose;
        this.forceResend = forceResend;
    }
}
}