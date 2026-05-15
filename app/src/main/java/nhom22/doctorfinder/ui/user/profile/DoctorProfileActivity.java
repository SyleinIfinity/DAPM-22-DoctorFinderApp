package nhom22.doctorfinder.ui.user.profile;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;

import nhom22.doctorfinder.R;
import nhom22.doctorfinder.data.remote.api.DoctorApiService;
import nhom22.doctorfinder.data.remote.api.FollowApiService;
import nhom22.doctorfinder.data.remote.client.RetrofitClient;
import nhom22.doctorfinder.data.remote.dto.response.DoctorResponse;
import nhom22.doctorfinder.data.remote.dto.response.FollowDoctorItem;
import nhom22.doctorfinder.data.remote.dto.response.FollowResponse;
import nhom22.doctorfinder.data.remote.dto.response.RatingSummaryResponse;
import nhom22.doctorfinder.data.remote.dto.response.ReviewItem;
import nhom22.doctorfinder.utils.SharedPrefManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

public class DoctorProfileActivity extends AppCompatActivity {

    // ───── Intent data ─────
    private String doctorId;
    private String doctorName;
    private String doctorSpecialty;
    private String doctorDegree;
    private String doctorHospital;
    private float  doctorRating;
    private int    doctorReviewCount;
    private int    doctorExperience;
    private boolean isOnline;
    private String doctorType;

    // ───── Views ─────
    private TextView tvDoctorName, tvSpecialty, tvDegreeChip, tvHospital, tvStatus;
    private TextView tvBio, tvLicenseNo;
    private TextView tvRowSpecialtyValue, tvRowDegreeValue, tvRowTypeValue, tvRowWorkplaceValue;
    private TextView tvExperience, tvRating, tvRatingCount;

    // Follow button
    private LinearLayout btnFollow;
    private TextView     tvFollowText;
    private ProgressBar  pbFollow;        // loading nhỏ trên nút follow (tuỳ chọn)

    // Reviews
    private LinearLayout llReviewsContainer;
    private TextView     tvNoReviews;

    // ───── State ─────
    private boolean isFollowed   = false;
    private boolean isFollowBusy = false; // tránh double-tap

    // ───── API ─────
    private FollowApiService followApi;
    private DoctorApiService doctorApi;
    private ImageView ivDoctorAvatar;
    private String doctorAvatarUrl;

    // ──────────────────────────────────────────────────────────────────────────

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profile);

        followApi = RetrofitClient.getClient().create(FollowApiService.class);
        doctorApi = RetrofitClient.getClient().create(DoctorApiService.class);

        readIntentExtras();
        bindViews();
        populateFromExtras();
        fetchFullProfile();
        fetchRatingSummary();
        fetchReviews();
        checkFollowStatus();   // Kiểm tra xem đã follow chưa ngay khi mở màn hình
    }

    // ─── Đọc Intent extras ────────────────────────────────────────────────────

    private void readIntentExtras() {
        Intent intent  = getIntent();
        doctorId       = intent.getStringExtra("doctor_id");
        doctorName     = intent.getStringExtra("doctor_name");
        doctorSpecialty= intent.getStringExtra("doctor_specialty");
        doctorDegree   = intent.getStringExtra("doctor_degree");
        doctorHospital = intent.getStringExtra("doctor_hospital");
        doctorRating   = intent.getFloatExtra("doctor_rating", 0f);
        doctorReviewCount = intent.getIntExtra("doctor_review_count", 0);
        doctorExperience  = intent.getIntExtra("doctor_experience", 0);
        isOnline       = intent.getBooleanExtra("doctor_is_online", false);
        doctorType     = intent.getStringExtra("doctor_type");
        doctorAvatarUrl = intent.getStringExtra("doctor_avatar_url");
    }

    // ─── Bind views ───────────────────────────────────────────────────────────

    private void bindViews() {
        // Back
        ImageButton btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        // Text views
        tvDoctorName  = findViewById(R.id.tvDoctorName);
        tvSpecialty   = findViewById(R.id.tvSpecialty);
        tvDegreeChip  = findViewById(R.id.tvDegreeChip);
        tvHospital    = findViewById(R.id.tvHospital);
        tvStatus      = findViewById(R.id.tvStatus);
        tvBio         = findViewById(R.id.tvBio);
        tvLicenseNo   = findViewById(R.id.tvLicenseNo);
        tvExperience  = findViewById(R.id.tvExperience);
        tvRating      = findViewById(R.id.tvBigRating);
        tvRatingCount = findViewById(R.id.tvRatingCount);

        // Info rows
        bindInfoRow(R.id.rowSpecialty, "Chuyên khoa", v -> tvRowSpecialtyValue = v);
        bindInfoRow(R.id.rowDegree,    "Trình độ chuyên môn", v -> tvRowDegreeValue = v);
        bindInfoRow(R.id.rowType,      "Loại hình", v -> tvRowTypeValue = v);
        bindInfoRow(R.id.rowWorkplace, "Nơi làm việc", v -> tvRowWorkplaceValue = v);

        // Avatar initials
//        TextView tvAvatar = findViewById(R.id.tvAvatar);
//        if (tvAvatar != null && doctorName != null && !doctorName.isEmpty()) {
//            tvAvatar.setText(doctorName.substring(0, 1).toUpperCase());

        ivDoctorAvatar = findViewById(R.id.ivDoctorAvatar);
        loadAvatar(doctorAvatarUrl);

        // ─── Reviews container ────────────────────────────────────────────────
        llReviewsContainer = findViewById(R.id.llReviewsContainer);
        tvNoReviews        = findViewById(R.id.tvNoReviews);

        // ─── Follow button ────────────────────────────────────────────────────
        btnFollow   = findViewById(R.id.btnFollow);
        tvFollowText = findViewById(R.id.tvFollowText);

        if (btnFollow != null) {
            btnFollow.setOnClickListener(v -> onFollowButtonClicked());
        }

        // ─── Các nút khác ─────────────────────────────────────────────────────
        View btnMessage = findViewById(R.id.btnMessage);
        if (btnMessage != null) {
            btnMessage.setOnClickListener(v -> {
                Intent i = new Intent(this,
                        nhom22.doctorfinder.ui.user.chat.ChatBoxActivity.class);
                i.putExtra("doctor_id", doctorId);
                i.putExtra("doctor_name", doctorName);
                startActivity(i);
            });
        }

        View btnViewSchedule = findViewById(R.id.btnViewSchedule);
        if (btnViewSchedule != null) btnViewSchedule.setOnClickListener(v -> openSchedule());

        View btnBook = findViewById(R.id.btnBook);
        if (btnBook != null) btnBook.setOnClickListener(v -> openSchedule());

        TextView tvReadMore = findViewById(R.id.tvReadMore);
        if (tvReadMore != null) {
            tvReadMore.setOnClickListener(v -> {
                if (tvBio != null) tvBio.setMaxLines(Integer.MAX_VALUE);
                v.setVisibility(View.GONE);
            });
        }
    }

    private void loadAvatar(String avatarUrl) {
        if (ivDoctorAvatar == null) return;

        if (avatarUrl != null && avatarUrl.startsWith("data:image")) {
            String base64Data = avatarUrl.substring(avatarUrl.indexOf(",") + 1);
            byte[] bytes = android.util.Base64.decode(base64Data, android.util.Base64.DEFAULT);
            Glide.with(this)
                    .load(bytes)
                    .circleCrop()
                    .placeholder(R.drawable.ic_doctor_placeholder)
                    .into(ivDoctorAvatar);
        } else if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Glide.with(this)
                    .load(avatarUrl)
                    .circleCrop()
                    .placeholder(R.drawable.ic_doctor_placeholder)
                    .into(ivDoctorAvatar);
        } else {
            ivDoctorAvatar.setImageResource(R.drawable.ic_doctor_placeholder);
        }
    }
    /** Helper: bind một info-row (label + value) */
    private void bindInfoRow(int rowId, String label, java.util.function.Consumer<TextView> valueSetter) {
        View row = findViewById(rowId);
        if (row == null) return;
        TextView tvLabel = row.findViewById(R.id.tvInfoLabel);
        TextView tvValue = row.findViewById(R.id.tvInfoValue);
        if (tvLabel != null) tvLabel.setText(label);
        if (tvValue != null) valueSetter.accept(tvValue);
    }

    // ─── Populate từ Intent extras ────────────────────────────────────────────

    private void populateFromExtras() {
        setText(tvDoctorName, doctorName != null ? "BS. " + doctorName : "");
        setText(tvSpecialty, doctorSpecialty);
        setText(tvDegreeChip, doctorDegree);
        setText(tvHospital, doctorHospital);
        tvStatus.setText(isOnline ? "Trực tuyến" : "Xác minh");
        setText(tvRowSpecialtyValue, doctorSpecialty);
        setText(tvRowDegreeValue, doctorDegree);
        setText(tvRowTypeValue, doctorType);
        setText(tvRowWorkplaceValue, doctorHospital);
        if (tvExperience != null) tvExperience.setText(String.valueOf(doctorExperience));
        if (tvRating != null)     tvRating.setText(String.valueOf(doctorRating));
        if (tvRatingCount != null) tvRatingCount.setText("(" + doctorReviewCount + ")");
    }

    // ─── Fetch thông tin chi tiết từ server ───────────────────────────────────

    private void fetchFullProfile() {
        int id = parseDoctorId();
        if (id < 0) return;

        doctorApi.getDoctorById(id).enqueue(new Callback<DoctorResponse>() {
            @Override
            public void onResponse(Call<DoctorResponse> call, Response<DoctorResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    populateFromResponse(response.body());
                }
            }

            @Override
            public void onFailure(Call<DoctorResponse> call, Throwable t) {
                Toast.makeText(DoctorProfileActivity.this,
                        "Không tải được dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ─── Kiểm tra trạng thái follow khi vào màn hình ─────────────────────────

    /**
     * Gọi GET /api/follows?maNguoiDung=... rồi tìm xem bác sĩ này có trong
     * danh sách không để thiết lập trạng thái ban đầu của nút Follow.
     */
    private void checkFollowStatus() {
        int doctorIdInt = parseDoctorId();
        if (doctorIdInt < 0) return;

        SharedPrefManager prefs = SharedPrefManager.getInstance(this);
        int userId = prefs.getUserId();

        if (userId < 0) {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }


        followApi.getFollowedDoctors( userId)
                .enqueue(new Callback<List<FollowDoctorItem>>() {
                    @Override
                    public void onResponse(Call<List<FollowDoctorItem>> call,
                                           Response<List<FollowDoctorItem>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            boolean alreadyFollowed = false;
                            for (FollowDoctorItem item : response.body()) {
                                if (item.maBacSi == doctorIdInt) {
                                    alreadyFollowed = true;
                                    break;
                                }
                            }
                            updateFollowUI(alreadyFollowed);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<FollowDoctorItem>> call, Throwable t) {
                        // Không ảnh hưởng UI chính – giữ trạng thái mặc định "Theo dõi"
                    }
                });
    }

    // ─── Xử lý bấm nút Follow ─────────────────────────────────────────────────

    private void onFollowButtonClicked() {
        if (isFollowBusy) return; // Tránh double-tap

        int doctorIdInt = parseDoctorId();
        if (doctorIdInt < 0) {
            Toast.makeText(this, "Không xác định được bác sĩ", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPrefManager prefs = SharedPrefManager.getInstance(this);
        int userId = prefs.getUserId();
        if (userId < 0) {
            Toast.makeText(this, "Vui lòng đăng nhập để theo dõi bác sĩ", Toast.LENGTH_SHORT).show();
            return;
        }

        isFollowBusy = true;

        if (isFollowed) {
            unfollowDoctor( doctorIdInt, userId);
        } else {
            followDoctor(doctorIdInt, userId);
        }
    }

    /** POST /api/follows/{maBacSi} */
    private void followDoctor( int maBacSi, int maNguoiDung) {
        followApi.followDoctor( maBacSi, maNguoiDung)
                .enqueue(new Callback<FollowResponse>() {
                    @Override
                    public void onResponse(Call<FollowResponse> call,
                                           Response<FollowResponse> response) {
                        isFollowBusy = false;
                        if (response.isSuccessful() && response.body() != null
                                && response.body().success) {
                            updateFollowUI(true);
                            Toast.makeText(DoctorProfileActivity.this,
                                    "Đã theo dõi bác sĩ", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(DoctorProfileActivity.this,
                                    "Không thể theo dõi. Vui lòng thử lại.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<FollowResponse> call, Throwable t) {
                        isFollowBusy = false;
                        Toast.makeText(DoctorProfileActivity.this,
                                "Lỗi kết nối: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /** DELETE /api/follows/{maBacSi} */
    private void unfollowDoctor( int maBacSi, int maNguoiDung) {
        followApi.unfollowDoctor( maBacSi, maNguoiDung)
                .enqueue(new Callback<FollowResponse>() {
                    @Override
                    public void onResponse(Call<FollowResponse> call,
                                           Response<FollowResponse> response) {
                        isFollowBusy = false;
                        if (response.isSuccessful()) {
                            updateFollowUI(false);
                            Toast.makeText(DoctorProfileActivity.this,
                                    "Đã hủy theo dõi", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(DoctorProfileActivity.this,
                                    "Không thể hủy theo dõi. Vui lòng thử lại.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<FollowResponse> call, Throwable t) {
                        isFollowBusy = false;
                        Toast.makeText(DoctorProfileActivity.this,
                                "Lỗi kết nối: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Cập nhật trạng thái nút Follow + màu sắc theo trạng thái.
     *
     * @param followed true  → "Đã theo dõi" (màu nhạt / outline)
     *                 false → "Theo dõi"    (màu primary)
     */
    private void updateFollowUI(boolean followed) {
        isFollowed = followed;
        if (tvFollowText == null || btnFollow == null) return;

        if (followed) {
            tvFollowText.setText("Đã theo dõi");
            tvFollowText.setTextColor(ContextCompat.getColor(this, R.color.colorTextMuted));
            btnFollow.setBackgroundTintList(
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorBorder)));
        } else {
            tvFollowText.setText("Theo dõi");
            tvFollowText.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
            btnFollow.setBackgroundTintList(null); // Reset về drawable gốc bg_follow_btn
        }
    }

    // ─── Populate từ response API ──────────────────────────────────────────────

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

        // Cập nhật avatar initials nếu tên thay đổi
        if (d.anhDaiDien != null && !d.anhDaiDien.isEmpty()) {
            loadAvatar(d.anhDaiDien);
        }
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private int parseDoctorId() {
        if (doctorId == null) return -1;
        try {
            return Integer.parseInt(doctorId);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private String buildWorkplace(DoctorResponse d) {
        if (d.tenCoSoYTe == null)   return d.diaChiLamViec != null ? d.diaChiLamViec : "";
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

    // ─── API: Rating Summary ───────────────────────────────────────────────────

    private void fetchRatingSummary() {
        int id = parseDoctorId();
        if (id < 0) return;

        doctorApi.getRatingSummary(id).enqueue(new Callback<RatingSummaryResponse>() {
            @Override
            public void onResponse(Call<RatingSummaryResponse> call,
                                   Response<RatingSummaryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RatingSummaryResponse summary = response.body();
                    if (tvRating != null) {
                        tvRating.setText(summary.soSaoTrungBinh != null
                                ? String.format("%.1f", summary.soSaoTrungBinh) : "0");
                    }
                    if (tvRatingCount != null) {
                        tvRatingCount.setText("(" + summary.tongDanhGia + " đánh giá)");
                    }
                }
            }

            @Override
            public void onFailure(Call<RatingSummaryResponse> call, Throwable t) {
                // Giữ nguyên giá trị từ Intent extras
            }
        });
    }

    // ─── API: Reviews ─────────────────────────────────────────────────────────

    private void fetchReviews() {
        int id = parseDoctorId();
        if (id < 0) return;

        doctorApi.getDoctorReviews(id).enqueue(new Callback<List<ReviewItem>>() {
            @Override
            public void onResponse(Call<List<ReviewItem>> call,
                                   Response<List<ReviewItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ReviewItem> reviews = response.body();
                    if (reviews.isEmpty()) {
                        if (tvNoReviews != null) tvNoReviews.setVisibility(View.VISIBLE);
                    } else {
                        renderReviews(reviews);
                    }
                } else {
                    if (tvNoReviews != null) tvNoReviews.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<ReviewItem>> call, Throwable t) {
                if (tvNoReviews != null) tvNoReviews.setVisibility(View.VISIBLE);
            }
        });
    }

    private void renderReviews(List<ReviewItem> reviews) {
        if (llReviewsContainer == null) return;
        llReviewsContainer.removeAllViews();

        for (ReviewItem review : reviews) {
            View itemView = LayoutInflater.from(this)
                    .inflate(R.layout.item_review_card, llReviewsContainer, false);

            TextView tvAvatar  = itemView.findViewById(R.id.tvRevAvatar1);
            TextView tvRevName = itemView.findViewById(R.id.tvRevName1);
            TextView tvRevDate = itemView.findViewById(R.id.tvRevDate1);
            TextView tvRevText = itemView.findViewById(R.id.tvRevText1);

            // Initials avatar
            if (tvAvatar != null && review.hoTenNguoiDung != null
                    && !review.hoTenNguoiDung.isEmpty()) {
                tvAvatar.setText(review.hoTenNguoiDung
                        .substring(0, Math.min(2, review.hoTenNguoiDung.length()))
                        .toUpperCase());
            }

            if (tvRevName != null)
                tvRevName.setText(review.hoTenNguoiDung != null ? review.hoTenNguoiDung : "");

            if (tvRevDate != null && review.thoiGian != null) {
                try {
                    // "2026-05-13T13:02:47.896Z" → "13/05/2026"
                    String datePart = review.thoiGian.substring(0, 10);
                    String[] parts = datePart.split("-");
                    tvRevDate.setText(parts[2] + "/" + parts[1] + "/" + parts[0]);
                } catch (Exception e) {
                    tvRevDate.setText(review.thoiGian);
                }
            }

            if (tvRevText != null)
                tvRevText.setText(review.noiDung != null ? review.noiDung : "");

            llReviewsContainer.addView(itemView);
        }
    }
}