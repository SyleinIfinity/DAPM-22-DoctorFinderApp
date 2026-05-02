package nhom22.doctorfinder.ui.auth;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.textfield.TextInputEditText;

import nhom22.doctorfinder.R;

public class RegisterFragment extends AuthFragment {

    private RegisterViewModel mViewModel;

    // Basic fields
    private TextInputEditText etFullName, etUserName, etPhone,
            etEmail, etCccd, etPassword, etConfirmPassword;

    // Doctor fields
    private TextInputEditText etSpecialty, etCertificate, etWorkplace, etQualification;
    private LinearLayout layoutDoctorInfo;

    // Tab state
    private boolean isDoctor = false;
    private TextView tabMember, tabDoctor;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        bindViews(view);
        setupTabs();
        setupLoginLink(view);
        setupBackButton(view);

        view.findViewById(R.id.btnRegister).setOnClickListener(v -> onRegisterClicked());

        // OTP sent → navigate to OTP screen
        mViewModel.otpSent.observe(getViewLifecycleOwner(), sent -> {
            if (Boolean.TRUE.equals(sent)) {
                navigateToOtp();
                mViewModel.clearState();
            }
        });

        mViewModel.error.observe(getViewLifecycleOwner(), err -> {
            if (err != null) Toast.makeText(requireContext(), err, Toast.LENGTH_SHORT).show();
        });
    }

    // ── Bind all views ────────────────────────────────────────────────────────
    private void bindViews(View view) {
        etFullName        = view.findViewById(R.id.etFullName);
        etUserName        = view.findViewById(R.id.etUserName);
        etPhone           = view.findViewById(R.id.etPhone);
        etEmail           = view.findViewById(R.id.etEmail);
        etCccd            = view.findViewById(R.id.etCccd);
        etPassword        = view.findViewById(R.id.etPassword);
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword);

        etSpecialty       = view.findViewById(R.id.etSpecialty);
        etCertificate     = view.findViewById(R.id.etCertificate);
        etWorkplace       = view.findViewById(R.id.etWorkplace);
        etQualification   = view.findViewById(R.id.etQualification);
        layoutDoctorInfo  = view.findViewById(R.id.layoutDoctorInfo);

        tabMember = view.findViewById(R.id.tabMember);
        tabDoctor = view.findViewById(R.id.tabDoctor);
    }

    // ── Tab toggle ────────────────────────────────────────────────────────────
    private void setupTabs() {
        tabMember.setOnClickListener(v -> setTab(false));
        tabDoctor.setOnClickListener(v -> setTab(true));
        setTab(false); // default
    }

    private void setTab(boolean doctor) {
        isDoctor = doctor;
        if (doctor) {
            tabDoctor.setBackgroundResource(R.drawable.bg_tab_active);
            tabDoctor.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary));
            tabMember.setBackgroundColor(android.graphics.Color.TRANSPARENT);
            tabMember.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_muted));
            layoutDoctorInfo.setVisibility(View.VISIBLE);
        } else {
            tabMember.setBackgroundResource(R.drawable.bg_tab_active);
            tabMember.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary));
            tabDoctor.setBackgroundColor(android.graphics.Color.TRANSPARENT);
            tabDoctor.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_muted));
            layoutDoctorInfo.setVisibility(View.GONE);
        }
    }

    // ── Back button ───────────────────────────────────────────────────────────
    private void setupBackButton(View view) {
        view.findViewById(R.id.btnBack).setOnClickListener(v ->
                NavHostFragment.findNavController(this).navigateUp());
    }

    // ── "Đã có tài khoản" link ────────────────────────────────────────────────
    private void setupLoginLink(View view) {
        TextView tvLogin = view.findViewById(R.id.tvLogin);
        String full = "Đã có tài khoản? Đăng nhập";
        SpannableString ss = new SpannableString(full);
        int start = full.indexOf("Đăng nhập");
        ss.setSpan(
                new ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.primary)),
                start, full.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvLogin.setText(ss);
        tvLogin.setOnClickListener(v ->
                NavHostFragment.findNavController(this).navigateUp());
    }

    // ── Validate & kick off OTP ───────────────────────────────────────────────
    private void onRegisterClicked() {
        String fullName  = text(etFullName);
        String userName  = text(etUserName);
        String phone     = text(etPhone);
        String email     = text(etEmail);
        String cccd      = text(etCccd);
        String password  = text(etPassword);
        String confirm   = text(etConfirmPassword);

        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(userName)
                || TextUtils.isEmpty(phone) || TextUtils.isEmpty(email)
                || TextUtils.isEmpty(cccd)  || TextUtils.isEmpty(password)) {
            Toast.makeText(requireContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirm)) {
            Toast.makeText(requireContext(), "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isDoctor) {
            if (TextUtils.isEmpty(text(etSpecialty)) || TextUtils.isEmpty(text(etCertificate))
                    || TextUtils.isEmpty(text(etWorkplace)) || TextUtils.isEmpty(text(etQualification))) {
                Toast.makeText(requireContext(), "Vui lòng điền đầy đủ thông tin bác sĩ", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Store all entered data in the args bundle we'll pass to OTP
        mViewModel.sendOtp(email);
    }

    // ── Navigate to OTP, carrying all form data ────────────────────────────────
    private void navigateToOtp() {
        Bundle bundle = new Bundle();
        bundle.putString("email",           text(etEmail));
        bundle.putString("tenDangNhap",     text(etUserName));
        bundle.putString("matKhau",         text(etPassword));
        bundle.putString("xacNhanMatKhau",  text(etConfirmPassword));
        bundle.putString("hoLot",           getHoLot(text(etFullName)));
        bundle.putString("ten",             getTen(text(etFullName)));
        bundle.putString("soDienThoai",     text(etPhone));
        bundle.putString("cccd",            text(etCccd));
        bundle.putBoolean("isDoctor",       isDoctor);

        if (isDoctor) {
            bundle.putString("chuyenKhoa",          text(etSpecialty));
            bundle.putString("maChungChiHanhNghe",  text(etCertificate));
            bundle.putString("tenCoSoYTe",          text(etWorkplace));
            bundle.putString("trinhDoChuyenMon",    text(etQualification));
        }

        NavHostFragment.findNavController(this)
                .navigate(R.id.action_registerFragment_to_otpFragment, bundle);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private String text(TextInputEditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }

    /** "Nguyễn Văn"  from "Nguyễn Văn An" */
    private String getHoLot(String fullName) {
        int last = fullName.lastIndexOf(' ');
        return last > 0 ? fullName.substring(0, last).trim() : "";
    }

    /** "An" from "Nguyễn Văn An" */
    private String getTen(String fullName) {
        int last = fullName.lastIndexOf(' ');
        return last > 0 ? fullName.substring(last + 1).trim() : fullName;
    }
}