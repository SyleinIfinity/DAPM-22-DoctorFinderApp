package nhom22.doctorfinder.ui.user.schedule;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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

    private static final String TAG = "SchedulingInfo";

    // ── Enum loại phiếu – khớp với backend ──────────────────────────────────
    // Dựa theo Swagger response: loaiPhieu = "PHONGKHAM"
    // Bạn cần xác nhận backend chấp nhận đúng những giá trị nào,
    // tạm dùng "PHONGKHAM" là giá trị mặc định duy nhất theo Swagger.
    private static final String[] LOAI_PHIEU_LABELS = {
            "Khám tại phòng khám", "Tái khám", "Yêu cầu khác"
    };
    private static final String[] LOAI_PHIEU_ENUMS = {
            "PHONGKHAM", "TAI_KHAM", "YEU_CAU"
    };

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
    private TextView             tvSlotSummary;
    private TextView             tvLocationSummary;
    private TextView             tvCountdown;
    private TextInputEditText    etHoTen;
    private TextInputEditText    etSdt;
    private TextInputEditText    etNgaySinh;
    private AutoCompleteTextView actvLoaiPhieu;
    private TextInputEditText    etTrieuChung;
    private MaterialButton       btnGuiPhieu;

    // ── Timer ────────────────────────────────────────────────────────────────
    private CountDownTimer countDownTimer;
    private static final long COUNTDOWN_MS = 10 * 60 * 1000L;

    // ── API ──────────────────────────────────────────────────────────────────
    private UserApiService userApiService;
    private final Gson gson = new Gson();

    // ── State ────────────────────────────────────────────────────────────────
    private String selectedLoaiPhieuEnum = LOAI_PHIEU_ENUMS[0];

    // =========================================================================

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduling_information);

        readIntent();
        bindViews();
        setupToolbar();
        setupLoaiPhieuDropdown();
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
        Intent i  = getIntent();
        doctorId  = i.getStringExtra("doctor_id");
        doctorName = i.getStringExtra("doctor_name");
        slotId    = i.getIntExtra("slot_id", -1);
        slotDate  = i.getStringExtra("slot_date");
        slotStart = i.getStringExtra("slot_start");
        slotEnd   = i.getStringExtra("slot_end");
        slotDuration = i.getIntExtra("slot_duration", 0);

        maNguoiDung = SharedPrefManager.getInstance(this).getUserId();
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
        actvLoaiPhieu     = findViewById(R.id.actvLoaiPhieu);
        etTrieuChung      = findViewById(R.id.etTrieuChung);
        btnGuiPhieu       = findViewById(R.id.btnGuiPhieu);
    }

    // ── 3. Toolbar ───────────────────────────────────────────────────────────

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) toolbar.setNavigationOnClickListener(v -> finish());
    }

    // ── 4. Dropdown loại phiếu ───────────────────────────────────────────────

    private void setupLoaiPhieuDropdown() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, LOAI_PHIEU_LABELS);
        actvLoaiPhieu.setAdapter(adapter);
        actvLoaiPhieu.setText(LOAI_PHIEU_LABELS[0], false);
        actvLoaiPhieu.setOnItemClickListener((parent, view, pos, id) ->
                selectedLoaiPhieuEnum = LOAI_PHIEU_ENUMS[pos]);
    }

    // ── 5. Hiển thị tóm tắt lịch ─────────────────────────────────────────────

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

    // ── 6. Gọi API lấy thông tin người dùng ──────────────────────────────────

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
        // Họ tên
        if (etHoTen != null) {
            if (p.hoTenDayDu != null && !p.hoTenDayDu.isEmpty()) {
                etHoTen.setText(p.hoTenDayDu);
            } else {
                String full = (p.hoLot != null ? p.hoLot + " " : "") +
                        (p.ten   != null ? p.ten         : "");
                etHoTen.setText(full.trim());
            }
        }

        // SĐT
        if (etSdt != null && p.soDienThoai != null)
            etSdt.setText(p.soDienThoai);

        // Ngày sinh: chuyển yyyy-MM-dd → dd/MM/yyyy
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

    // ── 7. Countdown 10 phút ─────────────────────────────────────────────────

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

    // ── 8. Buttons ───────────────────────────────────────────────────────────

    private void setupButtons() {
        MaterialButton btnQuayLai = findViewById(R.id.btnQuayLai);
        if (btnQuayLai != null) btnQuayLai.setOnClickListener(v -> finish());

        MaterialButton btnEditSlot = findViewById(R.id.btnEditSlot);
        if (btnEditSlot != null) btnEditSlot.setOnClickListener(v -> finish());

        if (btnGuiPhieu != null) btnGuiPhieu.setOnClickListener(v -> submitAppointment());
    }

    // ── 9. Gửi phiếu đặt lịch ───────────────────────────────────────────────

    private void submitAppointment() {
        // Validate
        if (slotId <= 0) {
            Toast.makeText(this, "Thông tin lịch không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }
        if (maNguoiDung <= 0) {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy triệu chứng (tuỳ chọn)
        String trieuChung = null;
        if (etTrieuChung != null && etTrieuChung.getText() != null) {
            String text = etTrieuChung.getText().toString().trim();
            if (!text.isEmpty()) trieuChung = text;
        }

        // Disable button chống double-submit
        btnGuiPhieu.setEnabled(false);

        // Build request — loaiPhieu dùng enum đúng với backend
        AppointmentRequest request = new AppointmentRequest(
                maNguoiDung,
                slotId,
                selectedLoaiPhieuEnum,  // "PHONGKHAM" | "TAI_KHAM" | "YEU_CAU"
                trieuChung
        );

        Log.d(TAG, "POST /api/appointments → " + gson.toJson(request));

        userApiService.createAppointment(request).enqueue(new Callback<AppointmentResponse>() {
            @Override
            public void onResponse(Call<AppointmentResponse> call,
                                   Response<AppointmentResponse> response) {
                btnGuiPhieu.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    // Dừng đếm ngược
                    if (countDownTimer != null) countDownTimer.cancel();

                    Log.d(TAG, "Đặt lịch thành công: maPhieu=" +
                            response.body().maPhieuDatLich);

                    // Truyền kết quả sang màn hình xác nhận
                    Intent intent = new Intent(
                            SchedulingInformationActivity.this,
                            ConfirmAppointmentActivity.class
                    );

// API response
                    intent.putExtra("appointment_result", gson.toJson(response.body()));

// 🔥 THÊM dữ liệu UI (quan trọng)
                    intent.putExtra("doctor_name", doctorName);
                    intent.putExtra("slot_date", slotDate);
                    intent.putExtra("slot_start", slotStart);
                    intent.putExtra("slot_end", slotEnd);
                    intent.putExtra("slot_duration", slotDuration);
                    intent.putExtra("loai_phieu_label", actvLoaiPhieu.getText().toString());

// triệu chứng
                    if (etTrieuChung.getText() != null) {
                        intent.putExtra("trieu_chung", etTrieuChung.getText().toString());
                    }

// bệnh nhân
                    intent.putExtra("patient_name", etHoTen.getText().toString());
                    intent.putExtra("patient_phone", etSdt.getText().toString());

                    startActivity(intent);
                    finish();

                } else {
                    // Log body lỗi để debug
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