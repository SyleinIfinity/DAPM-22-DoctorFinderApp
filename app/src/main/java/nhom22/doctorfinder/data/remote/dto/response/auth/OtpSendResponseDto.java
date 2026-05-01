package nhom22.doctorfinder.data.remote.dto.response.auth;


import com.google.gson.annotations.SerializedName;

public class OtpSendResponseDto {
    @SerializedName("sent")
    private boolean sent;

    @SerializedName("message")
    private String message;

    @SerializedName("expiresInSeconds")
    private int expiresInSeconds;

    public boolean isSent() { return sent; }
    public String getMessage() { return message; }
    public int getExpiresInSeconds() { return expiresInSeconds; }
}