package nhom22.doctorfinder.ui.user.search;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import nhom22.doctorfinder.R;
import nhom22.doctorfinder.adapter.DoctorAdapter;
import nhom22.doctorfinder.data.remote.api.DoctorApiService;
import nhom22.doctorfinder.data.remote.client.RetrofitClient;
import nhom22.doctorfinder.data.remote.dto.response.DoctorResponse;
import nhom22.doctorfinder.model.Doctor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchResultActivity extends AppCompatActivity {

    private RecyclerView rvDoctors;
    private DoctorAdapter doctorAdapter;
    private LinearLayout emptyState;
    private TextView tvResultCount;
    private TextView tvSearchQuery;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        // ===== INIT VIEW =====
        ImageButton btnBack = findViewById(R.id.btnBack);
        rvDoctors = findViewById(R.id.rvDoctors);
        emptyState = findViewById(R.id.emptyState);
        tvResultCount = findViewById(R.id.tvResultCount);
        tvSearchQuery = findViewById(R.id.tvSearchQuery);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // ===== GET DATA FROM INTENT =====
        String keyword = getIntent().getStringExtra(SearchBoxActivity.EXTRA_KEYWORD);
        String symptom = getIntent().getStringExtra(SearchBoxActivity.EXTRA_SYMPTOM);
        String doctorType = getIntent().getStringExtra(SearchBoxActivity.EXTRA_DOCTOR_TYPE);
        float minRating = getIntent().getFloatExtra(SearchBoxActivity.EXTRA_MIN_RATING, 0f);

        // ===== SET QUERY TEXT =====
        if (tvSearchQuery != null) {
            tvSearchQuery.setText(buildQuerySummary(keyword, symptom, doctorType, minRating));
        }

        // ===== SETUP RECYCLER =====
        rvDoctors.setLayoutManager(new LinearLayoutManager(this));
        doctorAdapter = new DoctorAdapter(this, new ArrayList<>());
        rvDoctors.setAdapter(doctorAdapter);

        // ===== CALL API =====
        fetchDoctors(keyword);
    }

    // ================= API CALL =================
    private void fetchDoctors(String keyword) {
        DoctorApiService api = RetrofitClient.getClient().create(DoctorApiService.class);

        api.searchDoctors(keyword, 20, 0).enqueue(new Callback<List<DoctorResponse>>() {
            @Override
            public void onResponse(Call<List<DoctorResponse>> call, Response<List<DoctorResponse>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    List<Doctor> list = new ArrayList<>();

                    for (DoctorResponse d : response.body()) {

                        Doctor doctor = new Doctor(
                                String.valueOf(d.maBacSi),
                                d.hoTenDayDu,
                                d.chuyenKhoa,
                                d.trinhDoChuyenMon != null ? d.trinhDoChuyenMon : "",
                                d.tenCoSoYTe,
                                4.5f, // ⚠️ backend chưa có rating -> tạm
                                0,
                                false,
                                0,
                                mapDoctorType(d.loaiHinhBacSi)
                        );

                        list.add(doctor);
                    }

                    updateUI(list);

                } else {
                    showError("Không có dữ liệu");
                }
            }

            @Override
            public void onFailure(Call<List<DoctorResponse>> call, Throwable t) {
                showError("Lỗi kết nối API");
            }
        });
    }

    // ================= UPDATE UI =================
    private void updateUI(List<Doctor> list) {
        if (list == null || list.isEmpty()) {
            rvDoctors.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
            tvResultCount.setText("Không tìm thấy kết quả");
        } else {
            rvDoctors.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
            doctorAdapter.updateList(list);
            tvResultCount.setText("Tìm thấy " + list.size() + " bác sĩ");
        }
    }

    // ================= HELPER =================
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        rvDoctors.setVisibility(View.GONE);
        emptyState.setVisibility(View.VISIBLE);
    }

    private String mapDoctorType(String apiType) {
        if (apiType == null) return "";

        switch (apiType) {
            case "Hospital":
                return "Bệnh viện";
            case "Clinic":
                return "Phòng khám";
            case "Online":
                return "Online";
            default:
                return apiType;
        }
    }

    private String buildQuerySummary(String keyword, String symptom, String doctorType, float minRating) {
        StringBuilder builder = new StringBuilder();

        if (keyword != null && !keyword.isEmpty()) {
            builder.append(keyword);
        }

        if (symptom != null && !symptom.isEmpty()) {
            if (builder.length() > 0) builder.append(" • ");
            builder.append("Triệu chứng: ").append(symptom);
        }

        if (doctorType != null && !doctorType.isEmpty() && !"Tất cả".equals(doctorType)) {
            if (builder.length() > 0) builder.append(" • ");
            builder.append(doctorType);
        }

        if (minRating > 0f) {
            if (builder.length() > 0) builder.append(" • ");
            builder.append("Từ ").append(minRating).append(" sao");
        }

        if (builder.length() == 0) {
            builder.append("Kết quả tìm kiếm");
        }

        return builder.toString();
    }
}