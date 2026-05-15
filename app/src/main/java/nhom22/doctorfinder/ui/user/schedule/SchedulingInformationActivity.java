package nhom22.doctorfinder.ui.user.schedule;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.util.Locale;

import nhom22.doctorfinder.R;
import nhom22.doctorfinder.data.remote.api.UserApiService;
import nhom22.doctorfinder.data.remote.client.RetrofitClient;
import nhom22.doctorfinder.data.remote.dto.request.AppointmentRequest;
import nhom22.doctorfinder.data.remote.dto.response.AppointmentResponse;
import nhom22.doctorfinder.data.remote.dto.response.UserProfileResponse;
import nhom22.doctorfinder.utils.SharedPrefManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SchedulingInformationActivity extends AppCompatActivity {

    private static final String TAG        = "SchedulingInfo";
    private static final String LOAI_PHIEU = "DAT_MOI";

    // ── Intent extras ────────────────────────────────────────────────────────
    private String doctorId;
    private String doctorName;
    private int    slotId;
    private String slotDate;
    private String slotStart;
    private String slotEnd;
    private int    slotDuration;

    // ── User ─────────────────────────────────────────────────────────────────
    private int maNguoiDung;

    // ── Views ────────────────────────────────────────────────────────────────
    private TextView          tvSlotSummary;
    private TextView          tvLocationSummary;
    private TextView          tvCountdown;
    private TextInputEditText etHoTen;
    private TextInputEditText etSdt;
    private TextInputEditText etNgaySinh;
    private TextInputEditText etTrieuChung;
    private MaterialButton    btnGuiPhieu;

    // ── Timer ────────────────────────────────────────────────────────────────
    private CountDownTimer        countDownTimer;
    private static final long COUNTDOWN_MS = 10 * 60 * 1000L;

    // ── API ──────────────────────────────────────────────────────────────────
    private UserApiService userApiService;
    private final Gson     gson = new Gson();

    // =========================================================================

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduling_information);

        readIntent();
        bindViews();
        setupToolbar();
        displaySlotSummary();
        loadUserProfile();
        startCountdown();
        setupButtons();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) countDownTimer.cancel();
    }

    // ── 1. Đọc Intent ────────────────────────────────────────────────────────

    private void readIntent() {
        Intent i     = getIntent();
        doctorId     = i.getStringExtra("doctor_id");
        doctorName   = i.getStringExtra("doctor_name");
        slotId       = i.getIntExtra("slot_id", -1);
        slotDate     = i.getStringExtra("slot_date");
        slotStart    = i.getStringExtra("slot_start");
        slotEnd      = i.getStringExtra("slot_end");
        slotDuration = i.getIntExtra("slot_duration", 0);

        maNguoiDung  = SharedPrefManager.getInstance(this).getUserId();
        Log.d(TAG, "maNguoiDung=" + maNguoiDung + " slotId=" + slotId);

        userApiService = RetrofitClient.getClient().create(UserApiService.class);
    }

    // ── 2. Bind Views ────────────────────────────────────────────────────────

    private void bindViews() {
        tvSlotSummary     = findViewById(R.id.tvSlotSummary);
        tvLocationSummary = findViewById(R.id.tvLocationSummary);
        tvCountdown       = findViewById(R.id.tvCountdown);
        etHoTen           = findViewById(R.id.etHoTen);
        etSdt             = findViewById(R.id.etSdt);
        etNgaySinh        = findViewById(R.id.etNgaySinh);
        etTrieuChung      = findViewById(R.id.etTrieuChung);
        btnGuiPhieu       = findViewById(R.id.btnGuiPhieu);
    }

    // ── 3. Toolbar ───────────────────────────────────────────────────────────

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) toolbar.setNavigationOnClickListener(v -> finish());
    }

    // ── 4. Hiển thị tóm tắt lịch ─────────────────────────────────────────────

    private void displaySlotSummary() {
        if (tvSlotSummary != null) {
            tvSlotSummary.setText(String.format("%s – %s · %s",
                    slotStart != null ? slotStart : "",
                    slotEnd   != null ? slotEnd   : "",
                    slotDate  != null ? slotDate  : ""));
        }
        if (tvLocationSummary != null) {
            tvLocationSummary.setText("Tại phòng khám · BS. " +
                    (doctorName != null ? doctorName : ""));
        }
    }

    // ── 5. Gọi API lấy thông tin người dùng ──────────────────────────────────

    private void loadUserProfile() {
        if (maNguoiDung <= 0) {
            Log.w(TAG, "maNguoiDung không hợp lệ, bỏ qua loadUserProfile");
            return;
        }

        userApiService.getUserProfile(maNguoiDung)
                .enqueue(new Callback<UserProfileResponse>() {
                    @Override
                    public void onResponse(Call<UserProfileResponse> call,
                                           Response<UserProfileResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            fillUserForm(response.body());
                        } else {
                            Log.w(TAG, "getUserProfile failed: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                        Log.e(TAG, "getUserProfile network error", t);
                    }
                });
    }

    private void fillUserForm(UserProfileResponse p) {
        if (etHoTen != null) {
            if (p.hoTenDayDu != null && !p.hoTenDayDu.isEmpty()) {
                etHoTen.setText(p.hoTenDayDu);
            } else {
                String full = (p.hoLot != null ? p.hoLot + " " : "") +
                        (p.ten   != null ? p.ten         : "");
                etHoTen.setText(full.trim());
            }
        }

        if (etSdt != null && p.soDienThoai != null)
            etSdt.setText(p.soDienThoai);

        if (etNgaySinh != null && p.ngaySinh != null && !p.ngaySinh.isEmpty()) {
            try {
                String[] parts = p.ngaySinh.split("-");
                etNgaySinh.setText(parts.length == 3
                        ? parts[2] + "/" + parts[1] + "/" + parts[0]
                        : p.ngaySinh);
            } catch (Exception e) {
                etNgaySinh.setText(p.ngaySinh);
            }
        }
    }

    // ── 6. Countdown 10 phút ─────────────────────────────────────────────────

    private void startCountdown() {
        countDownTimer = new CountDownTimer(COUNTDOWN_MS, 1000) {
            @Override
            public void onTick(long ms) {
                if (tvCountdown != null)
                    tvCountdown.setText(String.format(Locale.getDefault(),
                            "%02d:%02d", ms / 60000, (ms % 60000) / 1000));
            }

            @Override
            public void onFinish() {
                if (tvCountdown != null) tvCountdown.setText("00:00");
                Toast.makeText(SchedulingInformationActivity.this,
                        "Đã hết thời gian giữ chỗ", Toast.LENGTH_LONG).show();
                finish();
            }
        }.start();
    }

    // ── 7. Buttons ───────────────────────────────────────────────────────────

    private void setupButtons() {
        MaterialButton btnQuayLai = findViewById(R.id.btnQuayLai);
        if (btnQuayLai != null) btnQuayLai.setOnClickListener(v -> finish());

        MaterialButton btnEditSlot = findViewById(R.id.btnEditSlot);
        if (btnEditSlot != null) btnEditSlot.setOnClickListener(v -> finish());

        if (btnGuiPhieu != null) btnGuiPhieu.setOnClickListener(v -> submitAppointment());
    }

    // ── 8. Gửi phiếu đặt lịch ───────────────────────────────────────────────

    private void submitAppointment() {
        if (slotId <= 0) {
            Toast.makeText(this, "Thông tin lịch không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }
        if (maNguoiDung <= 0) {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            return;
        }

        String trieuChung = null;
        if (etTrieuChung != null && etTrieuChung.getText() != null) {
            String text = etTrieuChung.getText().toString().trim();
            if (!text.isEmpty()) trieuChung = text;
        }

        btnGuiPhieu.setEnabled(false);

        AppointmentRequest request = new AppointmentRequest(
                maNguoiDung,
                slotId,
                LOAI_PHIEU,   // luôn là "DAT_MOI"
                trieuChung
        );

        Log.d(TAG, "POST /api/appointments → " + gson.toJson(request));

        userApiService.createAppointment(request).enqueue(new Callback<AppointmentResponse>() {
            @Override
            public void onResponse(Call<AppointmentResponse> call,
                                   Response<AppointmentResponse> response) {
                btnGuiPhieu.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    if (countDownTimer != null) countDownTimer.cancel();

                    Log.d(TAG, "Đặt lịch thành công: maPhieu=" +
                            response.body().maPhieuDatLich);

                    Intent intent = new Intent(
                            SchedulingInformationActivity.this,
                            ConfirmAppointmentActivity.class);

                    intent.putExtra("appointment_result", gson.toJson(response.body()));
                    intent.putExtra("doctor_name",   doctorName);
                    intent.putExtra("slot_date",     slotDate);
                    intent.putExtra("slot_start",    slotStart);
                    intent.putExtra("slot_end",      slotEnd);
                    intent.putExtra("slot_duration", slotDuration);

                    if (etTrieuChung != null && etTrieuChung.getText() != null)
                        intent.putExtra("trieu_chung", etTrieuChung.getText().toString());
                    if (etHoTen != null && etHoTen.getText() != null)
                        intent.putExtra("patient_name", etHoTen.getText().toString());
                    if (etSdt != null && etSdt.getText() != null)
                        intent.putExtra("patient_phone", etSdt.getText().toString());

                    startActivity(intent);
                    finish();

                } else {
                    try {
                        String errBody = response.errorBody() != null
                                ? response.errorBody().string() : "null";
                        Log.e(TAG, "Đặt lịch thất bại HTTP " + response.code() + ": " + errBody);
                    } catch (Exception ignored) {}

                    Toast.makeText(SchedulingInformationActivity.this,
                            "Đặt lịch thất bại (HTTP " + response.code() + ")",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AppointmentResponse> call, Throwable t) {
                btnGuiPhieu.setEnabled(true);
                Log.e(TAG, "Lỗi mạng khi đặt lịch", t);
                Toast.makeText(SchedulingInformationActivity.this,
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}