package nhom22.doctorfinder.ui.auth;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import nhom22.doctorfinder.data.remote.api.AuthApiService;
import nhom22.doctorfinder.data.remote.client.RetrofitClient;
import nhom22.doctorfinder.data.remote.dto.DoctorInfo;
import nhom22.doctorfinder.data.remote.dto.UserInfo;
import nhom22.doctorfinder.data.remote.dto.request.OtpSendRequest;
import nhom22.doctorfinder.data.remote.dto.request.OtpVerifyRequest;
import nhom22.doctorfinder.data.remote.dto.request.RegisterRequest;
import nhom22.doctorfinder.data.remote.dto.request.RegisterDoctorRequest;
import nhom22.doctorfinder.data.remote.dto.response.OtpVerifyResponse;
import nhom22.doctorfinder.data.remote.dto.response.RegisterResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterViewModel extends ViewModel {

    public MutableLiveData<Boolean> otpSent = new MutableLiveData<>();
    public MutableLiveData<String> otpToken = new MutableLiveData<>();
    public MutableLiveData<Boolean> registerSuccess = new MutableLiveData<>();
    public MutableLiveData<String> error = new MutableLiveData<>();

    private final AuthApiService api =
            RetrofitClient.getClient().create(AuthApiService.class);

    // ── Send OTP ──────────────────────────────────────────────────────────────
    public void sendOtp(String email) {
        OtpSendRequest req = new OtpSendRequest();
        req.email = email;

        api.sendOtp(req).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) otpSent.postValue(true);
                else error.postValue("Gửi OTP thất bại: " + response.code());
            }
            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                error.postValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    // ── Verify OTP ────────────────────────────────────────────────────────────
    public void verifyOtp(String email, String otp) {
        OtpVerifyRequest req = new OtpVerifyRequest();
        req.email = email;
        req.otpCode = otp;

        api.verifyOtp(req).enqueue(new Callback<OtpVerifyResponse>() {
            @Override
            public void onResponse(Call<OtpVerifyResponse> call, Response<OtpVerifyResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().verified) {
                    otpToken.postValue(response.body().otpProofToken);
                } else {
                    error.postValue("OTP không đúng hoặc đã hết hạn");
                }
            }
            @Override
            public void onFailure(Call<OtpVerifyResponse> call, Throwable t) {
                error.postValue("Lỗi xác thực OTP: " + t.getMessage());
            }
        });
    }

    // ── Register User ─────────────────────────────────────────────────────────
    public void registerUser(UserInfo userInfo, String otpProofToken) {
        RegisterRequest req = new RegisterRequest();
        req.thongTinNguoiDung = userInfo;
        req.otpProofToken = otpProofToken;

        api.registerUser(req).enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().registered) {
                    registerSuccess.postValue(true);
                } else {
                    String msg = (response.body() != null) ? response.body().message : "Đăng ký thất bại";
                    error.postValue(msg);
                }
            }
            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                error.postValue("Lỗi đăng ký: " + t.getMessage());
            }
        });
    }

    // ── Register Doctor ───────────────────────────────────────────────────────
    public void registerDoctor(UserInfo userInfo, DoctorInfo doctorInfo, String otpProofToken) {
        RegisterDoctorRequest req = new RegisterDoctorRequest();
        req.thongTinNguoiDung = userInfo;
        req.thongTinBacSi = doctorInfo;
        req.otpProofToken = otpProofToken;

        api.registerDoctor(req).enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().registered) {
                    registerSuccess.postValue(true);
                } else {
                    String msg = (response.body() != null) ? response.body().message : "Đăng ký bác sĩ thất bại";
                    error.postValue(msg);
                }
            }
            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                error.postValue("Lỗi đăng ký: " + t.getMessage());
            }
        });
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    public void clearState() {
        otpSent.setValue(null);
        error.setValue(null);
    }
}