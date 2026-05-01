package nhom22.doctorfinder.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import nhom22.doctorfinder.data.remote.api.AuthApiService;

import nhom22.doctorfinder.data.remote.dto.request.auth.RegisterUserRequestDto;
import nhom22.doctorfinder.data.remote.dto.response.auth.RegisterResponseDto;
import nhom22.doctorfinder.utils.Resource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AuthRepository {

    private final AuthApiService api;

    @Inject
    public AuthRepository(AuthApiService api) {
        this.api = api;
    }

    // ── Đăng ký thành viên ────────────────────────────────────────────────────
    public LiveData<Resource<RegisterResponseDto>> registerUser(RegisterUserRequestDto dto) {
        MutableLiveData<Resource<RegisterResponseDto>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());
        api.registerUser(dto).enqueue(new Callback<RegisterResponseDto>() {
            @Override
            public void onResponse(Call<RegisterResponseDto> call, Response<RegisterResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.postValue(Resource.success(response.body()));
                } else {
                    result.postValue(Resource.error("Đăng ký thất bại: " + response.code(), null));
                }
            }
            @Override
            public void onFailure(Call<RegisterResponseDto> call, Throwable t) {
                result.postValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
            }
        });
        return result;
    }

    // ── Đăng ký bác sĩ ────────────────────────────────────────────────────────
    public LiveData<Resource<RegisterResponseDto>> registerDoctor(RegisterDoctorRequestDto dto) {
        MutableLiveData<Resource<RegisterResponseDto>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());
        api.registerDoctor(dto).enqueue(new Callback<RegisterResponseDto>() {
            @Override
            public void onResponse(Call<RegisterResponseDto> call, Response<RegisterResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.postValue(Resource.success(response.body()));
                } else {
                    result.postValue(Resource.error("Đăng ký thất bại: " + response.code(), null));
                }
            }
            @Override
            public void onFailure(Call<RegisterResponseDto> call, Throwable t) {
                result.postValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
            }
        });
        return result;
    }

    // ── Gửi OTP ───────────────────────────────────────────────────────────────
    public LiveData<Resource<OtpSendResponseDto>> sendOtp(String email, String purpose, boolean forceResend) {
        MutableLiveData<Resource<OtpSendResponseDto>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());
        api.sendOtp(new SendOtpRequestDto(email, purpose, forceResend))
                .enqueue(new Callback<OtpSendResponseDto>() {
                    @Override
                    public void onResponse(Call<OtpSendResponseDto> call, Response<OtpSendResponseDto> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            result.postValue(Resource.success(response.body()));
                        } else {
                            result.postValue(Resource.error("Gửi OTP thất bại: " + response.code(), null));
                        }
                    }
                    @Override
                    public void onFailure(Call<OtpSendResponseDto> call, Throwable t) {
                        result.postValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
                    }
                });
        return result;
    }

    // ── Xác minh OTP ──────────────────────────────────────────────────────────
    public LiveData<Resource<OtpVerifyResponseDto>> verifyOtp(String email, String purpose, String otpCode) {
        MutableLiveData<Resource<OtpVerifyResponseDto>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());
        api.verifyOtp(new VerifyOtpRequestDto(email, purpose, otpCode))
                .enqueue(new Callback<OtpVerifyResponseDto>() {
                    @Override
                    public void onResponse(Call<OtpVerifyResponseDto> call, Response<OtpVerifyResponseDto> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            result.postValue(Resource.success(response.body()));
                        } else {
                            result.postValue(Resource.error("OTP không hợp lệ: " + response.code(), null));
                        }
                    }
                    @Override
                    public void onFailure(Call<OtpVerifyResponseDto> call, Throwable t) {
                        result.postValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
                    }
                });
        return result;
    }
}