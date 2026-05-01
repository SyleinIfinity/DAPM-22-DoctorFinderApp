package nhom22.doctorfinder.ui.auth;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import nhom22.doctorfinder.data.remote.dto.request.auth.RegisterDoctorRequestDto;
import nhom22.doctorfinder.data.remote.dto.request.auth.RegisterUserRequestDto;
import nhom22.doctorfinder.data.remote.dto.response.auth.OtpSendResponseDto;
import nhom22.doctorfinder.data.remote.dto.response.auth.OtpVerifyResponseDto;
import nhom22.doctorfinder.data.remote.dto.response.auth.RegisterResponseDto;
import nhom22.doctorfinder.data.repository.AuthRepository;
import nhom22.doctorfinder.utils.Resource;

import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class RegisterViewModel extends ViewModel {

    private final AuthRepository repository;

    // Lưu tạm thông tin form để dùng sau khi OTP xác nhận thành công
    private RegisterUserRequestDto pendingUserDto;
    private RegisterDoctorRequestDto pendingDoctorDto;
    private boolean isDoctor = false;

    // Trạng thái gửi OTP
    private final MutableLiveData<Resource<OtpSendResponseDto>> otpSendResult = new MutableLiveData<>();
    // Trạng thái xác minh OTP
    private final MutableLiveData<Resource<OtpVerifyResponseDto>> otpVerifyResult = new MutableLiveData<>();
    // Trạng thái đăng ký cuối cùng
    private final MutableLiveData<Resource<RegisterResponseDto>> registerResult = new MutableLiveData<>();

    @Inject
    public RegisterViewModel(AuthRepository repository) {
        this.repository = repository;
    }

    // ── Bước 1: Lưu form và gửi OTP ──────────────────────────────────────────
    public void requestOtpForUser(RegisterUserRequestDto dto) {
        this.pendingUserDto = dto;
        this.isDoctor = false;
        sendOtp(dto.getEmail());
    }

    public void requestOtpForDoctor(RegisterDoctorRequestDto dto) {
        this.pendingDoctorDto = dto;
        this.isDoctor = true;
        sendOtp(dto.getEmail());
    }

    private void sendOtp(String email) {
        repository.sendOtp(email, "REGISTER", false)
                .observeForever(resource -> otpSendResult.postValue(resource));
    }

    public void resendOtp(String email) {
        repository.sendOtp(email, "REGISTER", true)
                .observeForever(resource -> otpSendResult.postValue(resource));
    }

    // ── Bước 2: Xác minh OTP ─────────────────────────────────────────────────
    public void verifyOtp(String email, String otpCode) {
        repository.verifyOtp(email, "REGISTER", otpCode)
                .observeForever(resource -> {
                    otpVerifyResult.postValue(resource);
                    // Nếu OTP hợp lệ -> tự động gọi API đăng ký
                    if (resource.isSuccess() && resource.data != null && resource.data.isVerified()) {
                        doRegister();
                    }
                });
    }

    // ── Bước 3: Gọi API đăng ký ──────────────────────────────────────────────
    private void doRegister() {
        if (isDoctor && pendingDoctorDto != null) {
            repository.registerDoctor(pendingDoctorDto)
                    .observeForever(resource -> registerResult.postValue(resource));
        } else if (!isDoctor && pendingUserDto != null) {
            repository.registerUser(pendingUserDto)
                    .observeForever(resource -> registerResult.postValue(resource));
        }
    }

    // ── Expose LiveData ───────────────────────────────────────────────────────
    public LiveData<Resource<OtpSendResponseDto>> getOtpSendResult() { return otpSendResult; }
    public LiveData<Resource<OtpVerifyResponseDto>> getOtpVerifyResult() { return otpVerifyResult; }
    public LiveData<Resource<RegisterResponseDto>> getRegisterResult() { return registerResult; }

    // Helpers
    public String getPendingEmail() {
        if (isDoctor && pendingDoctorDto != null) return pendingDoctorDto.getEmail();
        if (!isDoctor && pendingUserDto != null) return pendingUserDto.getEmail();
        return "";
    }

    // NOTE: thêm getter getEmail() vào các DTO nếu chưa có
}