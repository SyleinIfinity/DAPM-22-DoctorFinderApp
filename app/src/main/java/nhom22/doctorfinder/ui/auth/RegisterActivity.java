package nhom22.doctorfinder.ui.auth;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;

import dagger.hilt.android.AndroidEntryPoint;
import nhom22.doctorfinder.R;
import nhom22.doctorfinder.base.BaseActivity;
import nhom22.doctorfinder.data.remote.dto.auth.request.RegisterDoctorRequestDto;
import nhom22.doctorfinder.data.remote.dto.auth.request.RegisterUserRequestDto;
import nhom22.doctorfinder.databinding.ActivityRegisterBinding;

@AndroidEntryPoint
public class RegisterActivity extends BaseActivity {

    private ActivityRegisterBinding binding;
    private RegisterViewModel viewModel;

    private boolean isDoctorTab = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        setupTabToggle();
        setupLoginLink();
        setupRegisterButton();
        observeViewModel();

        binding.btnBack.setOnClickListener(v -> finish());
    }

    // ── Tab Toggle ────────────────────────────────────────────────────────────
    private void setupTabToggle() {
        binding.tabMember.setOnClickListener(v -> switchTab(false));
        binding.tabDoctor.setOnClickListener(v -> switchTab(true));
    }

    private void switchTab(boolean doctor) {
        isDoctorTab = doctor;
        binding.layoutDoctorInfo.setVisibility(doctor ? View.VISIBLE : View.GONE);

        binding.tabMember.setBackgroundResource(doctor ? android.R.color.transparent : R.drawable.bg_tab_active);
        binding.tabDoctor.setBackgroundResource(doctor ? R.drawable.bg_tab_active : android.R.color.transparent);

        binding.tabMember.setTextColor(ContextCompat.getColor(this, doctor ? R.color.text_muted : R.color.primary));
        binding.tabDoctor.setTextColor(ContextCompat.getColor(this, doctor ? R.color.primary : R.color.text_muted));
    }

    // ── Login link ────────────────────────────────────────────────────────────
    private void setupLoginLink() {
        String full = "Đã có tài khoản? Đăng nhập";
        SpannableString ss = new SpannableString(full);
        int start = full.indexOf("Đăng nhập");
        ss.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.primary)),
                start, full.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.tvLogin.setText(ss);
        binding.tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    // ── Register button ───────────────────────────────────────────────────────
    private void setupRegisterButton() {
        binding.btnRegister.setOnClickListener(v -> {
            if (!validateForm()) return;

            setLoading(true);

            String fullName = getText(binding.etFullName);
            String[] nameParts = splitName(fullName);
            String hoLot = nameParts[0];
            String ten = nameParts[1];
            String phone = getText(binding.etPhone);
            String email = getText(binding.etEmail);
            String cccd = getText(binding.etCccd);
            String username = email; // dùng email làm username
            String password = getText(binding.etPassword);

            if (isDoctorTab) {
                RegisterDoctorRequestDto dto = new RegisterDoctorRequestDto(
                        username, password, hoLot, ten, phone, email, cccd,
                        getText(binding.etSpecialty),
                        getText(binding.etQualification),
                        getText(binding.etCertificate),
                        getText(binding.etWorkplace)
                );
                viewModel.requestOtpForDoctor(dto);
            } else {
                RegisterUserRequestDto dto = new RegisterUserRequestDto(
                        username, password, hoLot, ten, phone, email, cccd
                );
                viewModel.requestOtpForUser(dto);
            }
        });
    }

    // ── Observe ViewModel ─────────────────────────────────────────────────────
    private void observeViewModel() {
        // Bước 1: OTP đã gửi -> mở màn hình nhập OTP
        viewModel.getOtpSendResult().observe(this, resource -> {
            if (resource.isLoading()) return;
            setLoading(false);
            if (resource.isSuccess() && resource.data != null && resource.data.isSent()) {
                openOtpDialog();
            } else {
                showError(resource.message != null ? resource.message : "Không thể gửi OTP");
            }
        });

        // Bước 3: Đăng ký hoàn tất
        viewModel.getRegisterResult().observe(this, resource -> {
            if (resource.isLoading()) return;
            setLoading(false);
            if (resource.isSuccess()) {
                Toast.makeText(this, "Đăng ký thành công! Vui lòng đăng nhập.", Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            } else {
                showError(resource.message != null ? resource.message : "Đăng ký thất bại");
            }
        });
    }

    // ── Open OTP bottom sheet / dialog ───────────────────────────────────────
    private void openOtpDialog() {
        String email = viewModel.getPendingEmail();
        OtpVerifyBottomSheet sheet = OtpVerifyBottomSheet.newInstance(email);
        sheet.show(getSupportFragmentManager(), OtpVerifyBottomSheet.TAG);
    }

    // ── Validation ────────────────────────────────────────────────────────────
    private boolean validateForm() {
        boolean ok = true;
        if (getText(binding.etFullName).isEmpty()) {
            binding.etFullName.setError("Vui lòng nhập họ tên");
            ok = false;
        }
        if (getText(binding.etPhone).length() != 10) {
            binding.etPhone.setError("Số điện thoại phải có 10 số");
            ok = false;
        }
        if (!getText(binding.etEmail).contains("@")) {
            binding.etEmail.setError("Email không hợp lệ");
            ok = false;
        }
        if (getText(binding.etCccd).length() != 12) {
            binding.etCccd.setError("CCCD phải có 12 số");
            ok = false;
        }
        if (getText(binding.etPassword).length() < 6) {
            binding.etPassword.setError("Mật khẩu tối thiểu 6 ký tự");
            ok = false;
        }
        if (isDoctorTab) {
            if (getText(binding.etSpecialty).isEmpty()) {
                binding.etSpecialty.setError("Nhập chuyên khoa");
                ok = false;
            }
            if (getText(binding.etCertificate).isEmpty()) {
                binding.etCertificate.setError("Nhập mã chứng chỉ");
                ok = false;
            }
            if (getText(binding.etWorkplace).isEmpty()) {
                binding.etWorkplace.setError("Nhập nơi làm việc");
                ok = false;
            }
            if (getText(binding.etQualification).isEmpty()) {
                binding.etQualification.setError("Nhập trình độ");
                ok = false;
            }
        }
        return ok;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private String getText(TextInputEditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }

    /** Tách "Nguyễn Văn A" -> hoLot="Nguyễn Văn", ten="A" */
    private String[] splitName(String fullName) {
        int lastSpace = fullName.lastIndexOf(' ');
        if (lastSpace < 0) return new String[]{"", fullName};
        return new String[]{fullName.substring(0, lastSpace), fullName.substring(lastSpace + 1)};
    }

    private void setLoading(boolean loading) {
        binding.btnRegister.setEnabled(!loading);
        binding.btnRegister.setText(loading ? "Đang xử lý…" : "Đăng ký ngay →");
    }

    private void showError(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}