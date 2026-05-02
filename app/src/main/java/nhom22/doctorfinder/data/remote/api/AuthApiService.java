package nhom22.doctorfinder.data.remote.api;

import nhom22.doctorfinder.data.remote.dto.request.LoginRequest;
import nhom22.doctorfinder.data.remote.dto.request.OtpSendRequest;
import nhom22.doctorfinder.data.remote.dto.request.OtpVerifyRequest;
import nhom22.doctorfinder.data.remote.dto.request.RegisterDoctorRequest;
import nhom22.doctorfinder.data.remote.dto.request.RegisterRequest;
import nhom22.doctorfinder.data.remote.dto.response.LoginResponse;
import nhom22.doctorfinder.data.remote.dto.response.OtpVerifyResponse;
import nhom22.doctorfinder.data.remote.dto.response.RegisterResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApiService {

    @POST("api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);
    @POST("api/auth/otp/send")
    Call<Object> sendOtp(@Body OtpSendRequest request);

    @POST("api/auth/otp/verify")
    Call<OtpVerifyResponse> verifyOtp(@Body OtpVerifyRequest request);

    @POST("api/auth/register/user")
    Call<RegisterResponse> registerUser(@Body RegisterRequest request);

    @POST("api/auth/register/doctor")
    Call<RegisterResponse> registerDoctor(@Body RegisterDoctorRequest request);
}
