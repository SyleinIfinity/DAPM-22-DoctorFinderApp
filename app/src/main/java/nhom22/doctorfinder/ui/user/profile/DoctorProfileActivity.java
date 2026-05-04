package nhom22.doctorfinder.ui.user.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import nhom22.doctorfinder.R;
import nhom22.doctorfinder.data.remote.api.DoctorApiService;
import nhom22.doctorfinder.data.remote.client.RetrofitClient;
import nhom22.doctorfinder.data.remote.dto.response.DoctorResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DoctorProfileActivity extends AppCompatActivity {

    // ───── Intent data ─────
    private String doctorId;
    private String doctorName;
    private String doctorSpecialty;
    private String doctorDegree;
    private String doctorHospital;
    private float doctorRating;
    private int doctorReviewCount;
    private int doctorExperience;
    private boolean isOnline;
    private String doctorType;

    // ───── Views ─────
    private TextView tvDoctorName, tvSpecialty, tvDegreeChip, tvHospital, tvStatus;
    private TextView tvBio, tvLicenseNo;
    private TextView tvRowSpecialtyValue, tvRowDegreeValue, tvRowTypeValue, tvRowWorkplaceValue;

    // Stats
    private TextView tvExperience, tvRating, tvRatingCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ✅ FIX đúng layout
        setContentView(R.layout.activity_doctor_profile);

        readIntentExtras();
        bindViews();
        populateFromExtras();
        fetchFullProfile();
    }

    private void readIntentExtras() {
        Intent intent = getIntent();
        doctorId = intent.getStringExtra("doctor_id");
        doctorName = intent.getStringExtra("doctor_name");
        doctorSpecialty = intent.getStringExtra("doctor_specialty");
        doctorDegree = intent.getStringExtra("doctor_degree");
        doctorHospital = intent.getStringExtra("doctor_hospital");
        doctorRating = intent.getFloatExtra("doctor_rating", 0f);
        doctorReviewCount = intent.getIntExtra("doctor_review_count", 0);
        doctorExperience = intent.getIntExtra("doctor_experience", 0);
        isOnline = intent.getBooleanExtra("doctor_is_online", false);
        doctorType = intent.getStringExtra("doctor_type");
    }

    private void bindViews() {
        ImageButton btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        tvDoctorName = findViewById(R.id.tvDoctorName);
        tvSpecialty = findViewById(R.id.tvSpecialty);
        tvDegreeChip = findViewById(R.id.tvDegreeChip);
        tvHospital = findViewById(R.id.tvHospital);
        tvStatus = findViewById(R.id.tvStatus);

        tvBio = findViewById(R.id.tvBio);
        tvLicenseNo = findViewById(R.id.tvLicenseNo);

        // Stats
        tvExperience = findViewById(R.id.tvExperience);
        tvRating = findViewById(R.id.tvBigRating);
        tvRatingCount = findViewById(R.id.tvRatingCount);

        // Info rows
        View rowSpecialty = findViewById(R.id.rowSpecialty);
        View rowDegree = findViewById(R.id.rowDegree);
        View rowType = findViewById(R.id.rowType);
        View rowWorkplace = findViewById(R.id.rowWorkplace);

        if (rowSpecialty != null) tvRowSpecialtyValue = rowSpecialty.findViewById(R.id.tvInfoValue);
        if (rowDegree != null) tvRowDegreeValue = rowDegree.findViewById(R.id.tvInfoValue);
        if (rowType != null) tvRowTypeValue = rowType.findViewById(R.id.tvInfoValue);
        if (rowWorkplace != null) tvRowWorkplaceValue = rowWorkplace.findViewById(R.id.tvInfoValue);

        // Avatar
        TextView tvAvatar = findViewById(R.id.tvAvatar);
        if (tvAvatar != null && doctorName != null && !doctorName.isEmpty()) {
            tvAvatar.setText(doctorName.substring(0, 1).toUpperCase());
        }

        // Buttons
        findViewById(R.id.btnFollow).setOnClickListener(v ->
                Toast.makeText(this, "Đã theo dõi", Toast.LENGTH_SHORT).show());

        findViewById(R.id.btnMessage).setOnClickListener(v -> {
            Intent i = new Intent(this,
                    nhom22.doctorfinder.ui.user.chat.ChatBoxActivity.class);
            i.putExtra("doctor_id", doctorId);
            i.putExtra("doctor_name", doctorName);
            startActivity(i);
        });

        findViewById(R.id.btnViewSchedule).setOnClickListener(v -> openSchedule());
        findViewById(R.id.btnBook).setOnClickListener(v -> openSchedule());

        findViewById(R.id.tvReadMore).setOnClickListener(v -> {
            tvBio.setMaxLines(Integer.MAX_VALUE);
            v.setVisibility(View.GONE);
        });
    }

    private void populateFromExtras() {
        setText(tvDoctorName, doctorName);
        setText(tvSpecialty, doctorSpecialty);
        setText(tvDegreeChip, doctorDegree);
        setText(tvHospital, doctorHospital);

        tvStatus.setText(isOnline ? "Trực tuyến" : "Xác minh");

        setText(tvRowSpecialtyValue, doctorSpecialty);
        setText(tvRowDegreeValue, doctorDegree);
        setText(tvRowTypeValue, doctorType);
        setText(tvRowWorkplaceValue, doctorHospital);

        // Stats
        if (tvExperience != null)
            tvExperience.setText(String.valueOf(doctorExperience));

        if (tvRating != null)
            tvRating.setText(String.valueOf(doctorRating));

        if (tvRatingCount != null)
            tvRatingCount.setText("(" + doctorReviewCount + ")");
    }

    private void fetchFullProfile() {
        if (doctorId == null) return;

        int id;
        try {
            id = Integer.parseInt(doctorId);
        } catch (Exception e) {
            return;
        }

        DoctorApiService api = RetrofitClient.getClient().create(DoctorApiService.class);

        api.getDoctorById(id).enqueue(new Callback<DoctorResponse>() {
            @Override
            public void onResponse(Call<DoctorResponse> call, Response<DoctorResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    populateFromResponse(response.body());
                }
            }

            @Override
            public void onFailure(Call<DoctorResponse> call, Throwable t) {
                Toast.makeText(DoctorProfileActivity.this,
                        "Không tải được dữ liệu",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateFromResponse(DoctorResponse d) {
        setText(tvDoctorName, d.hoTenDayDu);
        setText(tvSpecialty, d.chuyenKhoa);
        setText(tvDegreeChip, d.trinhDoChuyenMon);
        setText(tvHospital, d.tenCoSoYTe);

        boolean online = "HOAT_DONG".equals(d.trangThaiTaiKhoan);
        tvStatus.setText(online ? "Trực tuyến" : "Xác minh");

        setText(tvBio, d.moTaBanThan);
        setText(tvLicenseNo, d.maChungChiHanhNghe);

        setText(tvRowSpecialtyValue, d.chuyenKhoa);
        setText(tvRowDegreeValue, d.trinhDoChuyenMon);
        setText(tvRowTypeValue, d.loaiHinhBacSi);
        setText(tvRowWorkplaceValue, buildWorkplace(d));

        doctorName = d.hoTenDayDu;
    }

    private String buildWorkplace(DoctorResponse d) {
        if (d.tenCoSoYTe == null) return d.diaChiLamViec;
        if (d.diaChiLamViec == null) return d.tenCoSoYTe;
        return d.tenCoSoYTe + " - " + d.diaChiLamViec;
    }

    private void setText(TextView tv, String value) {
        if (tv != null) tv.setText(value != null ? value : "");
    }

    private void openSchedule() {
        Intent i = new Intent(this,
                nhom22.doctorfinder.ui.user.schedule.SelectCenlendarActivity.class);
        i.putExtra("doctor_id", doctorId);
        i.putExtra("doctor_name", doctorName);
        startActivity(i);
    }
}