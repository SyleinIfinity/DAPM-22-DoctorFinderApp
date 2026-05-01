package nhom22.doctorfinder.data.remote.api;


import nhom22.doctorfinder.data.remote.dto.request.auth.RegisterDoctorRequestDto;
import nhom22.doctorfinder.data.remote.dto.request.auth.RegisterUserRequestDto;
import nhom22.doctorfinder.data.remote.dto.response.auth.OtpSendResponseDto;
import nhom22.doctorfinder.data.remote.dto.response.auth.OtpVerifyResponseDto;
import nhom22.doctorfinder.data.remote.dto.response.auth.RegisterResponseDto;
import nhom22.doctorfinder.data.remote.dto.response.auth.SendOtpRequestDto;
import nhom22.doctorfinder.data.remote.dto.response.auth.VerifyOtpRequestDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApiService {

    // ── Đăng ký thành viên ────────────────────────────────────────────────────
    @POST("api/auth/register/user")
    Call<RegisterResponseDto> registerUser(@Body RegisterUserRequestDto body);

    // ── Đăng ký bác sĩ ────────────────────────────────────────────────────────
    @POST("api/auth/register/doctor")
    Call<RegisterResponseDto> registerDoctor(@Body RegisterDoctorRequestDto body);

    // ── Gửi OTP ───────────────────────────────────────────────────────────────
    @POST("api/auth/otp/send")
    Call<OtpSendResponseDto> sendOtp(@Body SendOtpRequestDto body);

    // ── Xác minh OTP ──────────────────────────────────────────────────────────
    @POST("api/auth/otp/verify")
    Call<OtpVerifyResponseDto> verifyOtp(@Body VerifyOtpRequestDto body);
}