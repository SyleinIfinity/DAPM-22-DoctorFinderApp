package nhom22.doctorfinder.ui.user.search;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import nhom22.doctorfinder.R;
import nhom22.doctorfinder.adapter.DoctorAdapter;
import nhom22.doctorfinder.data.remote.api.DoctorApiService;
import nhom22.doctorfinder.data.remote.client.RetrofitClient;
import nhom22.doctorfinder.data.remote.dto.response.DoctorResponse;
import nhom22.doctorfinder.model.Doctor;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchResultActivity extends AppCompatActivity {

    // ========== VIEWS ==========
    private RecyclerView rvDoctors;
    private DoctorAdapter doctorAdapter;
    private LinearLayout emptyState;
    private TextView tvSearchQuery;
    private FrameLayout btnToggleFilter;
    private View filterActiveBadge;

    // Sort chips
    private TextView chipSortDefault;
    private TextView chipSortRating;
    private TextView chipSortPriceAsc;
    private TextView chipSortPriceDesc;
    private TextView chipSortExp;

    // ========== STATE ==========
    private List<Doctor> fullList = new ArrayList<>(); // danh sách gốc từ API
    private String currentSort = "default";

    // ========== SEARCH PARAMS ==========
    private String keyword;
    private String symptom;
    private String doctorType;
    private float minRating;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        readIntentExtras();
        initViews();
        bindSortChips();
        bindFilterButton();
        fetchData();
    }

    // ================= INTENT =================
    private void readIntentExtras() {
        keyword    = getIntent().getStringExtra(SearchBoxActivity.EXTRA_KEYWORD);
        symptom    = getIntent().getStringExtra(SearchBoxActivity.EXTRA_SYMPTOM);
        doctorType = getIntent().getStringExtra(SearchBoxActivity.EXTRA_DOCTOR_TYPE);
        minRating  = getIntent().getFloatExtra(SearchBoxActivity.EXTRA_MIN_RATING, 0f);

        if (keyword    == null) keyword    = "";
        if (symptom    == null) symptom    = "";
        if (doctorType == null) doctorType = "";
    }

    // ================= INIT VIEWS =================
    private void initViews() {
        ImageButton btnBack = findViewById(R.id.btnBack);
        rvDoctors         = findViewById(R.id.rvDoctors);
        emptyState        = findViewById(R.id.emptyState);
        tvSearchQuery     = findViewById(R.id.tvSearchQuery);
        btnToggleFilter   = findViewById(R.id.btnToggleFilter);
        filterActiveBadge = findViewById(R.id.filterActiveBadge);

        chipSortDefault   = findViewById(R.id.chipSortDefault);
        chipSortRating    = findViewById(R.id.chipSortRating);
        chipSortPriceAsc  = findViewById(R.id.chipSortPriceAsc);
        chipSortPriceDesc = findViewById(R.id.chipSortPriceDesc);
        chipSortExp       = findViewById(R.id.chipSortExp);

        // Back
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Hiển thị tóm tắt từ khóa tìm kiếm
        if (tvSearchQuery != null) {
            tvSearchQuery.setText(buildQuerySummary());
        }

        // Hiển thị badge nếu có filter đang áp dụng
        updateFilterBadge();

        // Setup RecyclerView
        rvDoctors.setLayoutManager(new LinearLayoutManager(this));
        doctorAdapter = new DoctorAdapter(this, new ArrayList<>());
        rvDoctors.setAdapter(doctorAdapter);

        // Reset filter từ empty state
        View btnResetFromEmpty = findViewById(R.id.btnResetFromEmpty);
        if (btnResetFromEmpty != null) {
            btnResetFromEmpty.setOnClickListener(v -> goBackToSearchBox());
        }
    }

    // ================= FILTER BUTTON =================
    private void bindFilterButton() {
        if (btnToggleFilter == null) return;

        // Bấm nút lọc → quay về SearchBoxActivity để chỉnh filter
        btnToggleFilter.setOnClickListener(v -> goBackToSearchBox());
    }

    // Quay lại màn hình tìm kiếm để người dùng chỉnh lại bộ lọc
    private void goBackToSearchBox() {
        finish(); // SearchBoxActivity vẫn còn trong stack
    }

    // Hiển thị badge đỏ khi có filter đang áp dụng (khác mặc định)
    private void updateFilterBadge() {
        if (filterActiveBadge == null) return;

        boolean hasFilter = minRating > 0f
                || ("Tất cả".equals(doctorType) ? false : !doctorType.isEmpty());

        filterActiveBadge.setVisibility(hasFilter ? View.VISIBLE : View.GONE);
    }

    // ================= SORT CHIPS =================
    private void bindSortChips() {
        if (chipSortDefault   != null) chipSortDefault.setOnClickListener(v -> applySort("default"));
        if (chipSortRating    != null) chipSortRating.setOnClickListener(v -> applySort("rating"));
        if (chipSortPriceAsc  != null) chipSortPriceAsc.setOnClickListener(v -> applySort("price_asc"));
        if (chipSortPriceDesc != null) chipSortPriceDesc.setOnClickListener(v -> applySort("price_desc"));
        if (chipSortExp       != null) chipSortExp.setOnClickListener(v -> applySort("experience"));
    }

    private void applySort(String sortKey) {
        currentSort = sortKey;
        refreshSortChipUI();

        List<Doctor> sorted = new ArrayList<>(fullList);

        switch (sortKey) {
            case "rating":
                sorted.sort((a, b) -> Float.compare(b.getRating(), a.getRating()));
                break;
            case "price_asc":
                // TODO: thay getRating() bằng getter giá phù hợp khi Doctor có field giá
                sorted.sort((a, b) -> Float.compare(a.getRating(), b.getRating()));
                break;
            case "price_desc":
                // TODO: thay getRating() bằng getter giá phù hợp khi Doctor có field giá
                sorted.sort((a, b) -> Float.compare(b.getRating(), a.getRating()));
                break;
            case "experience":
                // TODO: thay getRating() bằng getter năm kinh nghiệm khi Doctor có field đó
                sorted.sort((a, b) -> Float.compare(b.getRating(), a.getRating()));
                break;
            case "default":
            default:
                // giữ thứ tự gốc từ API
                break;
        }

        doctorAdapter.updateList(sorted);
    }

    private void refreshSortChipUI() {
        setSortChipActive(chipSortDefault,   "default".equals(currentSort));
        setSortChipActive(chipSortRating,    "rating".equals(currentSort));
        setSortChipActive(chipSortPriceAsc,  "price_asc".equals(currentSort));
        setSortChipActive(chipSortPriceDesc, "price_desc".equals(currentSort));
        setSortChipActive(chipSortExp,       "experience".equals(currentSort));
    }

    private void setSortChipActive(TextView chip, boolean active) {
        if (chip == null) return;
        chip.setBackgroundResource(active
                ? R.drawable.bg_chip_sort_active
                : R.drawable.bg_chip_sort_inactive);
        chip.setTextColor(getColor(active ? android.R.color.white : R.color.text_muted));
    }

    // ================= API CALL =================
    private void fetchDoctors() {
        DoctorApiService api = RetrofitClient.getClient().create(DoctorApiService.class);

        // 🔥 FIX: truyền đúng param cho API
        String keywordParam = (keyword != null && !keyword.isEmpty()) ? keyword : null;

        // Ví dụ fix cứng để test trước
        String chuyenKhoaParam = "Rang ham mat";

        api.searchDoctors(
                keywordParam,
                chuyenKhoaParam,
                null,
                "DA_DUYET",
                50,
                0
        ).enqueue(new Callback<List<DoctorResponse>>() {

            @Override
            public void onResponse(Call<List<DoctorResponse>> call,
                                   Response<List<DoctorResponse>> response) {

                // 🔥 Log debug

                Log.d("API_DEBUG", "Message: " + response.message());
                Log.d("API_DEBUG", "Code: " + response.code());

                if (!response.isSuccessful()) {
                    showError("Lỗi server: " + response.code());
                    return;
                }

                List<DoctorResponse> body = response.body();

                // 🔥 FIX: check rỗng
                if (body == null || body.isEmpty()) {
                    showError("Không tìm thấy bác sĩ");
                    return;
                }

                // 🔥 Map dữ liệu
                List<Doctor> mapped = mapResponse(body);

                fullList = mapped;

                applySort(currentSort);
                updateResultCount(mapped.size());
            }

            @Override
            public void onFailure(Call<List<DoctorResponse>> call, Throwable t) {
                showError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    // ================= MAPPING =================
    private List<Doctor> mapResponse(List<DoctorResponse> body) {
        List<Doctor> list = new ArrayList<>();

        for (DoctorResponse d : body) {
            Doctor doctor = new Doctor(
                    String.valueOf(d.maBacSi),
                    d.hoTenDayDu,
                    d.chuyenKhoa,
                    d.trinhDoChuyenMon != null ? d.trinhDoChuyenMon : "",
                    d.tenCoSoYTe,
                    0f,   // API chưa trả rating → để 0, cập nhật khi backend bổ sung
                    0,
                    false,
                    0,
                    mapDoctorType(d.loaiHinhBacSi)
            );
            list.add(doctor);
        }

        return list;
    }

    // ================= CLIENT-SIDE FILTER =================
    // Fallback filter client-side: lọc loại hình và rating (API chưa hỗ trợ 2 field này)
    private List<Doctor> clientFilter(List<Doctor> source) {
        List<Doctor> result = new ArrayList<>();
        String type = typeParam();

        for (Doctor d : source) {
            // Lọc loại hình bác sĩ
            if (type != null && !type.equalsIgnoreCase(d.getDoctorType())) {
                continue;
            }
            // Lọc rating tối thiểu (chỉ có hiệu lực khi backend bổ sung rating)
            if (minRating > 0f && d.getRating() < minRating) {
                continue;
            }
            result.add(d);
        }

        return result;
    }

    // Null nếu "Tất cả", ngược lại trả về giá trị đã map
    @Nullable
    private String typeParam() {
        if (doctorType == null || doctorType.isEmpty() || "Tất cả".equals(doctorType)) {
            return null;
        }
        return doctorType;
    }

    // ================= UPDATE UI =================
    private void updateResultCount(int count) {
        if (count == 0) {
            rvDoctors.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        } else {
            rvDoctors.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
        }
    }

    private void fetchData() {

        ArrayList<String> imageUris = getIntent().getStringArrayListExtra(
                SearchBoxActivity.EXTRA_IMAGE_URIS
        );

        if (imageUris != null && !imageUris.isEmpty()) {
            fetchDoctorsByImage(Uri.parse(imageUris.get(0)));
        } else {
            fetchDoctors();
        }
    }

    private void fetchDoctorsByImage(Uri uri) {

        DoctorApiService api = RetrofitClient.getClient().create(DoctorApiService.class);

        File file = FileUtils.getFile(this, uri);

        RequestBody requestFile =
                RequestBody.create(file, okhttp3.MediaType.parse("image/*"));

        MultipartBody.Part body =
                MultipartBody.Part.createFormData("image", file.getName(), requestFile);

        RequestBody limit =
                RequestBody.create("10", okhttp3.MediaType.parse("text/plain"));

        api.searchDoctorsByImage(body, limit)
                .enqueue(new Callback<List<DoctorResponse>>() {

                    @Override
                    public void onResponse(Call<List<DoctorResponse>> call, Response<List<DoctorResponse>> response) {

                        Log.d("API", "CODE: " + response.code());

                        if (!response.isSuccessful()) {
                            showError("Lỗi: " + response.code());
                            return;
                        }

                        List<DoctorResponse> body = response.body();

                        if (body == null || body.isEmpty()) {
                            showError("Không tìm thấy");
                            return;
                        }

                        handleData(body);
                    }

                    @Override
                    public void onFailure(Call<List<DoctorResponse>> call, Throwable t) {
                        showError("Lỗi mạng: " + t.getMessage());
                    }
                });
    }
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        rvDoctors.setVisibility(View.GONE);
        emptyState.setVisibility(View.VISIBLE);
    }

    // ================= HELPER =================
    // Sửa: khi không map được thì mặc định vào "Bệnh viện" thay vì trả về apiType
    private String mapDoctorType(String apiType) {
        if (apiType == null) return "Bệnh viện";
        if (apiType.contains("online") || apiType.contains("Online")) return "Online";
        if (apiType.contains("khám") || apiType.contains("Clinic")) return "Phòng khám";
        return "Bệnh viện"; // fallback thay vì trả về apiType gốc
    }

    private String buildQuerySummary() {
        StringBuilder sb = new StringBuilder();

        if (!keyword.isEmpty()) {
            sb.append(keyword);
        }
        if (!symptom.isEmpty()) {
            if (sb.length() > 0) sb.append(" • ");
            sb.append(symptom);
        }
        if (!doctorType.isEmpty() && !"Tất cả".equals(doctorType)) {
            if (sb.length() > 0) sb.append(" • ");
            sb.append(doctorType);
        }
        if (minRating > 0f) {
            if (sb.length() > 0) sb.append(" • ");
            sb.append("≥ ").append(minRating).append("★");
        }
        if (sb.length() == 0) {
            sb.append("Kết quả tìm kiếm");
        }

        return sb.toString();
    }
}