package nhom22.doctorfinder.ui.user.search;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import nhom22.doctorfinder.R;
import nhom22.doctorfinder.adapter.DoctorAdapter;
import nhom22.doctorfinder.model.Doctor;

public class SearchResultActivity extends AppCompatActivity {

    private RecyclerView rvDoctors;
    private DoctorAdapter doctorAdapter;
    private List<Doctor> doctorList;
    private LinearLayout emptyState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        ImageButton btnBack = findViewById(R.id.btnBack);
        View btnToggleFilter = findViewById(R.id.btnToggleFilter);
        TextView tvSearchQuery = findViewById(R.id.tvSearchQuery);
        View filterActiveBadge = findViewById(R.id.filterActiveBadge);
        rvDoctors = findViewById(R.id.rvDoctors);
        emptyState = findViewById(R.id.emptyState);
        TextView tvResultCount = findViewById(R.id.tvResultCount);

        String keyword = getIntent().getStringExtra(SearchBoxActivity.EXTRA_KEYWORD);
        String symptom = getIntent().getStringExtra(SearchBoxActivity.EXTRA_SYMPTOM);
        String doctorType = getIntent().getStringExtra(SearchBoxActivity.EXTRA_DOCTOR_TYPE);
        float minRating = getIntent().getFloatExtra(SearchBoxActivity.EXTRA_MIN_RATING, 0f);
        ArrayList<String> imageUris = getIntent().getStringArrayListExtra(SearchBoxActivity.EXTRA_IMAGE_URIS);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (btnToggleFilter != null) {
            btnToggleFilter.setOnClickListener(v -> {
                View filterPanel = findViewById(R.id.filterPanel);
                if (filterPanel == null) {
                    return;
                }
                filterPanel.setVisibility(filterPanel.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            });
        }

        if (tvSearchQuery != null) {
            tvSearchQuery.setText(buildQuerySummary(keyword, symptom, doctorType, minRating, imageUris));
        }

        if (filterActiveBadge != null) {
            boolean hasFilter = minRating > 0f || (doctorType != null && !doctorType.isEmpty() && !"Tất cả".equals(doctorType));
            filterActiveBadge.setVisibility(hasFilter ? View.VISIBLE : View.GONE);
        }

        // Setup RecyclerView
        setupRecyclerView();

        // Load mock doctor data
        doctorList = generateMockDoctorList(keyword, symptom, doctorType, minRating);

        // Filter and display
        if (doctorList.isEmpty()) {
            rvDoctors.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        } else {
            rvDoctors.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
            doctorAdapter.updateList(doctorList);
            if (tvResultCount != null) {
                tvResultCount.setText("Tìm thấy " + doctorList.size() + " bác sĩ");
            }
        }

        // Reset filter button
        View btnResetFromEmpty = findViewById(R.id.btnResetFromEmpty);
        if (btnResetFromEmpty != null) {
            btnResetFromEmpty.setOnClickListener(v -> finish());
        }
    }

    private void setupRecyclerView() {
        rvDoctors.setLayoutManager(new LinearLayoutManager(this));
        doctorAdapter = new DoctorAdapter(this, new ArrayList<>());
        rvDoctors.setAdapter(doctorAdapter);
    }

    private List<Doctor> generateMockDoctorList(String keyword, String symptom, String doctorType, float minRating) {
        List<Doctor> doctors = new ArrayList<>();

        // Add mock doctors - in real app, would fetch from backend
        doctors.add(new Doctor("1", "BS. Nguyễn Thị Lan", "Thần kinh", "Bác sĩ",
                "BV Đà Nẵng", 4.9f, 312, true, 15, "Hospital"));
        doctors.add(new Doctor("2", "BS. Trần Văn Hùng", "Thần kinh", "Thạc sĩ",
                "BV Quân Dân Y", 4.8f, 245, false, 12, "Hospital"));
        doctors.add(new Doctor("3", "BS. Phạm Hoa Lan", "Tim mạch", "Bác sĩ",
                "Phòng khám Gia Phát", 4.7f, 189, true, 10, "Clinic"));
        doctors.add(new Doctor("4", "BS. Hoàng Minh Tuấn", "Tim mạch", "Tiến sĩ",
                "BV Tây Đô", 4.6f, 156, false, 18, "Hospital"));
        doctors.add(new Doctor("5", "BS. Lê Thanh Hương", "Nha khoa", "Bác sĩ",
                "Nha khoa Quốc Tế", 4.5f, 234, true, 8, "Clinic"));

        // Filter by minRating
        if (minRating > 0f) {
            doctors.removeIf(d -> d.getRating() < minRating);
        }

        // Filter by doctorType if specified
        if (doctorType != null && !doctorType.isEmpty() && !"Tất cả".equals(doctorType)) {
            String finalType = doctorType.equals("Bệnh viện") ? "Hospital" :
                    doctorType.equals("Phòng khám") ? "Clinic" :
                            doctorType.equals("Online") ? "Online" : doctorType;
            final String typeFilter = finalType;
            doctors.removeIf(d -> !d.getDoctorType().equals(typeFilter));
        }

        return doctors;
    }

    private String buildQuerySummary(String keyword, String symptom, String doctorType, float minRating, ArrayList<String> imageUris) {
        StringBuilder builder = new StringBuilder();
        if (keyword != null && !keyword.isEmpty()) {
            builder.append(keyword);
        }
        if (symptom != null && !symptom.isEmpty()) {
            if (builder.length() > 0) {
                builder.append(" • ");
            }
            builder.append("Triệu chứng: ").append(symptom);
        }
        if (doctorType != null && !doctorType.isEmpty() && !"Tất cả".equals(doctorType)) {
            if (builder.length() > 0) {
                builder.append(" • ");
            }
            builder.append(doctorType);
        }
        if (minRating > 0f) {
            if (builder.length() > 0) {
                builder.append(" • ");
            }
            builder.append("Từ ").append(minRating).append(" sao");
        }
        if (imageUris != null && !imageUris.isEmpty()) {
            if (builder.length() > 0) {
                builder.append(" • ");
            }
            builder.append(imageUris.size()).append(" ảnh");
        }
        if (builder.length() == 0) {
            builder.append("Kết quả tìm kiếm");
        }
        return builder.toString();
    }
}
