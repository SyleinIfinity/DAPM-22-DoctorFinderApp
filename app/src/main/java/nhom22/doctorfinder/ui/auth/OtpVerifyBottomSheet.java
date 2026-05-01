package nhom22.doctorfinder.ui.auth;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import nhom22.doctorfinder.R;
import nhom22.doctorfinder.databinding.BottomSheetOtpVerifyBinding;

/**
 * BottomSheet nhập mã OTP 6 chữ số từ email.
 * ViewModel được lấy từ Activity cha (scope chung).
 */
public class OtpVerifyBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG = "OtpVerifyBottomSheet";
    private static final String ARG_EMAIL = "email";
    private static final long COUNTDOWN_MS = 120_000L; // 2 phút

    private BottomSheetOtpVerifyBinding binding;
    private RegisterViewModel viewModel;
    private String email;
    private CountDownTimer countDownTimer;

    // 6 ô nhập OTP
    private EditText[] otpBoxes;

    public static OtpVerifyBottomSheet newInstance(String email) {
        OtpVerifyBottomSheet sheet = new OtpVerifyBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_EMAIL, email);
        sheet.setArguments(args);
        return sheet;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) email = getArguments().getString(ARG_EMAIL, "");
        // Dùng chung ViewModel với RegisterActivity
        viewModel = new ViewModelProvider(requireActivity()).get(RegisterViewModel.class);
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = BottomSheetOtpVerifyBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Hiện email bị ẩn một phần: a***@gmail.com
        binding.tvEmailHint.setText("Mã đã gửi đến " + maskEmail(email));

        setupOtpBoxes();
        startCountdown();
        setupButtons();
        observeViewModel();
    }

    // ── 6 ô OTP ──────────────────────────────────────────────────────────────
    private void setupOtpBoxes() {
        otpBoxes = new EditText[]{
                binding.otp1, binding.otp2, binding.otp3,
                binding.otp4, binding.otp5, binding.otp6
        };

        for (int i = 0; i < otpBoxes.length; i++) {
            final int index = i;
            otpBoxes[i].addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int c, int a) {}
                @Override public void onTextChanged(CharSequence s, int start, int b, int count) {}
                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 1 && index < otpBoxes.length - 1) {
                        otpBoxes[index + 1].requestFocus();
                    }
                    updateVerifyButtonState();
                }
            });
            // Xóa lùi
            otpBoxes[i].setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_DEL
                        && event.getAction() == KeyEvent.ACTION_DOWN
                        && otpBoxes[index].getText().toString().isEmpty()
                        && index > 0) {
                    otpBoxes[index - 1].requestFocus();
                    otpBoxes[index - 1].setText("");
                    return true;
                }
                return false;
            });
            // IME Done trên ô cuối
            if (i == otpBoxes.length - 1) {
                otpBoxes[i].setOnEditorActionListener((v, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        attemptVerify();
                        return true;
                    }
                    return false;
                });
            }
        }
        otpBoxes[0].requestFocus();
    }

    private void updateVerifyButtonState() {
        boolean allFilled = getOtpCode().length() == 6;
        binding.btnVerify.setEnabled(allFilled);
        binding.btnVerify.setAlpha(allFilled ? 1f : 0.5f);
    }

    private String getOtpCode() {
        StringBuilder sb = new StringBuilder();
        for (EditText box : otpBoxes) sb.append(box.getText().toString().trim());
        return sb.toString();
    }

    private void clearOtpBoxes() {
        for (EditText box : otpBoxes) box.setText("");
        otpBoxes[0].requestFocus();
    }

    // ── Countdown 2 phút ─────────────────────────────────────────────────────
    private void startCountdown() {
        binding.tvResend.setEnabled(false);
        binding.tvResend.setAlpha(0.4f);

        countDownTimer = new CountDownTimer(COUNTDOWN_MS, 1000) {
            @Override
            public void onTick(long ms) {
                long sec = ms / 1000;
                binding.tvCountdown.setText(String.format("%02d:%02d", sec / 60, sec % 60));
                binding.tvCountdown.setVisibility(View.VISIBLE);
            }
            @Override
            public void onFinish() {
                binding.tvCountdown.setVisibility(View.GONE);
                binding.tvResend.setEnabled(true);
                binding.tvResend.setAlpha(1f);
            }
        }.start();
    }

    // ── Buttons ───────────────────────────────────────────────────────────────
    private void setupButtons() {
        updateVerifyButtonState();

        binding.btnVerify.setOnClickListener(v -> attemptVerify());

        binding.tvResend.setOnClickListener(v -> {
            viewModel.resendOtp(email);
            clearOtpBoxes();
            startCountdown();
        });

        binding.btnClose.setOnClickListener(v -> dismiss());
    }

    private void attemptVerify() {
        String code = getOtpCode();
        if (code.length() < 6) {
            Toast.makeText(requireContext(), "Vui lòng nhập đủ 6 số", Toast.LENGTH_SHORT).show();
            return;
        }
        setLoading(true);
        viewModel.verifyOtp(email, code);
    }

    // ── Observe ───────────────────────────────────────────────────────────────
    private void observeViewModel() {
        viewModel.getOtpVerifyResult().observe(getViewLifecycleOwner(), resource -> {
            if (resource.isLoading()) return;
            setLoading(false);
            if (resource.isSuccess() && resource.data != null && resource.data.isVerified()) {
                showSuccess();
                // ViewModel sẽ tự gọi doRegister(); dismiss sau animation ngắn
                binding.getRoot().postDelayed(this::dismiss, 1200);
            } else {
                showOtpError(resource.message != null ? resource.message : "OTP không đúng, thử lại");
            }
        });
    }

    private void showSuccess() {
        for (EditText box : otpBoxes) {
            box.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), R.color.teal_primary)));
        }
        binding.tvStatus.setText("✓ Xác minh thành công!");
        binding.tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.teal_primary));
        binding.tvStatus.setVisibility(View.VISIBLE);
    }

    private void showOtpError(String msg) {
        clearOtpBoxes();
        for (EditText box : otpBoxes) {
            box.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), R.color.error_red)));
        }
        binding.tvStatus.setText(msg);
        binding.tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.error_red));
        binding.tvStatus.setVisibility(View.VISIBLE);
    }

    private void setLoading(boolean loading) {
        binding.btnVerify.setEnabled(!loading);
        binding.btnVerify.setText(loading ? "Đang xác minh…" : "Xác nhận");
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    // ── Utils ─────────────────────────────────────────────────────────────────
    private String maskEmail(String e) {
        if (e == null || !e.contains("@")) return e;
        String[] parts = e.split("@");
        String local = parts[0];
        String shown = local.length() <= 2 ? local : local.substring(0, 2) + "***";
        return shown + "@" + parts[1];
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (countDownTimer != null) countDownTimer.cancel();
        binding = null;
    }
}