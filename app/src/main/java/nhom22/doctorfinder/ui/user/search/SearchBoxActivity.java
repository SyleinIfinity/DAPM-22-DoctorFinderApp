package nhom22.doctorfinder.ui.user.search;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import nhom22.doctorfinder.R;

public class SearchBoxActivity extends AppCompatActivity {

    public static final String EXTRA_KEYWORD     = "extra_keyword";
    public static final String EXTRA_SYMPTOM     = "extra_symptom";
    public static final String EXTRA_DOCTOR_TYPE = "extra_doctor_type";
    public static final String EXTRA_MIN_RATING  = "extra_min_rating";
    public static final String EXTRA_IMAGE_URIS  = "extra_image_uris";

    // ========== VIEWS ==========
    private TextInputEditText etKeyword;
    private TextInputEditText etSymptom;
    private TextView chipTypeAll, chipTypeHospital, chipTypeClinic, chipTypeOnline;
    private TextView chipRatingAll, chipRating45, chipRating40, chipRating35;
    private TextView tvImageCount;
    private ImageView ivImage1, ivImage2;
    private View btnRemoveImage1, btnRemoveImage2;
    private FrameLayout btnFilter;
    private View filterActiveBadge;

    // ========== STATE ==========
    private final List<Uri> selectedImages = new ArrayList<>();
    private String selectedDoctorType = "Tất cả";
    private float selectedMinRating   = 0f;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_box);

        initLauncher();
        initViews();
        bindEvents();
        refreshTypeChips();
        refreshRatingChips();
        refreshImagePreview();
        refreshFilterBadge();
    }

    // ================= LAUNCHER =================
    private void initLauncher() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() != RESULT_OK || result.getData() == null) return;
                    handlePickedImages(result.getData());
                }
        );
    }

    // ================= INIT VIEWS =================
    private void initViews() {
        ImageButton btnBack = findViewById(R.id.btnBack);
        etKeyword        = findViewById(R.id.etKeyword);
        etSymptom        = findViewById(R.id.etSymptom);
        chipTypeAll      = findViewById(R.id.chipTypeAll);
        chipTypeHospital = findViewById(R.id.chipTypeHospital);
        chipTypeClinic   = findViewById(R.id.chipTypeClinic);
        chipTypeOnline   = findViewById(R.id.chipTypeOnline);
        chipRatingAll    = findViewById(R.id.chipRatingAll);
        chipRating45     = findViewById(R.id.chipRating45);
        chipRating40     = findViewById(R.id.chipRating40);
        chipRating35     = findViewById(R.id.chipRating35);
        tvImageCount     = findViewById(R.id.tvImageCount);
        ivImage1         = findViewById(R.id.ivImage1);
        ivImage2         = findViewById(R.id.ivImage2);
        btnRemoveImage1  = findViewById(R.id.btnRemoveImage1);
        btnRemoveImage2  = findViewById(R.id.btnRemoveImage2);
        btnFilter        = findViewById(R.id.btnFilter);
        filterActiveBadge = findViewById(R.id.filterActiveBadge);

        MaterialButton btnAddImages = findViewById(R.id.btnAddImages);
        MaterialButton btnSearch    = findViewById(R.id.btnSearch);

        if (btnBack     != null) btnBack.setOnClickListener(v -> finish());
        if (btnAddImages != null) btnAddImages.setOnClickListener(v -> openImagePicker());
        if (btnSearch   != null) btnSearch.setOnClickListener(v -> openSearchResult());

        // Nút bộ lọc: scroll xuống card filter (hoặc có thể mở bottom sheet nếu cần)
        if (btnFilter != null) {
            btnFilter.setOnClickListener(v -> scrollToFilterSection());
        }
    }

    // Cuộn đến khu vực chip loại hình (card filter đầu tiên trong scroll)
    private void scrollToFilterSection() {
        if (chipTypeAll != null) {
            chipTypeAll.requestFocus();
            chipTypeAll.getParent().requestChildFocus(chipTypeAll, chipTypeAll);
        }
    }

    // ================= BIND EVENTS =================
    private void bindEvents() {
        bindTypeChip(chipTypeAll,      "Tất cả");
        bindTypeChip(chipTypeHospital, "Bệnh viện");
        bindTypeChip(chipTypeClinic,   "Phòng khám");
        bindTypeChip(chipTypeOnline,   "Online");

        bindRatingChip(chipRatingAll, 0f);
        bindRatingChip(chipRating45,  4.5f);
        bindRatingChip(chipRating40,  4.0f);
        bindRatingChip(chipRating35,  3.5f);

        if (ivImage1 != null) ivImage1.setOnClickListener(v -> openImagePicker());
        if (ivImage2 != null) ivImage2.setOnClickListener(v -> openImagePicker());
        if (btnRemoveImage1 != null) btnRemoveImage1.setOnClickListener(v -> removeImageAt(0));
        if (btnRemoveImage2 != null) btnRemoveImage2.setOnClickListener(v -> removeImageAt(1));
    }

    private void bindTypeChip(TextView chip, String value) {
        if (chip == null) return;
        chip.setOnClickListener(v -> {
            selectedDoctorType = value;
            refreshTypeChips();
            refreshFilterBadge();
        });
    }

    private void bindRatingChip(TextView chip, float value) {
        if (chip == null) return;
        chip.setOnClickListener(v -> {
            selectedMinRating = value;
            refreshRatingChips();
            refreshFilterBadge();
        });
    }

    // ================= CHIP UI =================
    private void refreshTypeChips() {
        applyChipState(chipTypeAll,      "Tất cả".equals(selectedDoctorType));
        applyChipState(chipTypeHospital, "Bệnh viện".equals(selectedDoctorType));
        applyChipState(chipTypeClinic,   "Phòng khám".equals(selectedDoctorType));
        applyChipState(chipTypeOnline,   "Online".equals(selectedDoctorType));
    }

    private void refreshRatingChips() {
        applyChipState(chipRatingAll, selectedMinRating == 0f);
        applyChipState(chipRating45,  selectedMinRating == 4.5f);
        applyChipState(chipRating40,  selectedMinRating == 4.0f);
        applyChipState(chipRating35,  selectedMinRating == 3.5f);
    }

    private void applyChipState(TextView chip, boolean selected) {
        if (chip == null) return;
        chip.setBackgroundResource(selected ? R.drawable.bg_chip_active : R.drawable.bg_chip_inactive);
        chip.setTextColor(ContextCompat.getColor(this,
                selected ? R.color.white : R.color.text_muted));
    }

    // Badge đỏ trên nút bộ lọc khi filter khác mặc định
    private void refreshFilterBadge() {
        if (filterActiveBadge == null) return;
        boolean hasFilter = minRating() > 0f || !"Tất cả".equals(selectedDoctorType);
        filterActiveBadge.setVisibility(hasFilter ? View.VISIBLE : View.GONE);
    }

    private float minRating() {
        return selectedMinRating;
    }

    // ================= IMAGE PICKER =================
    private void openImagePicker() {
        if (selectedImages.size() >= 2) {
            Toast.makeText(this, "Chỉ được tải tối đa 2 ảnh", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        imagePickerLauncher.launch(Intent.createChooser(intent, "Chọn ảnh"));
    }

    private void handlePickedImages(Intent data) {
        List<Uri> incoming = new ArrayList<>();
        if (data.getClipData() != null) {
            int count = data.getClipData().getItemCount();
            for (int i = 0; i < count; i++) {
                Uri uri = data.getClipData().getItemAt(i).getUri();
                if (uri != null) incoming.add(uri);
            }
        } else if (data.getData() != null) {
            incoming.add(data.getData());
        }

        for (Uri uri : incoming) {
            if (selectedImages.size() >= 2) break;
            selectedImages.add(uri);
        }
        refreshImagePreview();
    }

    private void removeImageAt(int index) {
        if (index < selectedImages.size()) {
            selectedImages.remove(index);
            refreshImagePreview();
        }
    }

    private void refreshImagePreview() {
        if (selectedImages.size() > 0) {
            ivImage1.setImageURI(selectedImages.get(0));
            ivImage1.setScaleType(ImageView.ScaleType.CENTER_CROP);
            btnRemoveImage1.setVisibility(View.VISIBLE);
        } else {
            ivImage1.setImageResource(R.drawable.ic_upload);
            ivImage1.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            btnRemoveImage1.setVisibility(View.GONE);
        }

        if (selectedImages.size() > 1) {
            ivImage2.setImageURI(selectedImages.get(1));
            ivImage2.setScaleType(ImageView.ScaleType.CENTER_CROP);
            btnRemoveImage2.setVisibility(View.VISIBLE);
        } else {
            ivImage2.setImageResource(R.drawable.ic_upload);
            ivImage2.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            btnRemoveImage2.setVisibility(View.GONE);
        }

        if (tvImageCount != null) {
            tvImageCount.setText(selectedImages.size() + "/2 ảnh");
        }
    }

    // ================= OPEN RESULT =================
    private void openSearchResult() {
        String keyword = getTextValue(etKeyword);
        String symptom = getTextValue(etSymptom);

//        if (keyword.isEmpty() && symptom.isEmpty() && selectedImages.isEmpty()) {
//            Toast.makeText(this,
//                    "Vui lòng nhập từ khóa, triệu chứng hoặc tải ảnh",
//                    Toast.LENGTH_SHORT).show();
//            return;
//        }

        Intent intent = new Intent(this, SearchResultActivity.class);
        intent.putExtra(EXTRA_KEYWORD,     keyword);
        intent.putExtra(EXTRA_SYMPTOM,     symptom);
        intent.putExtra(EXTRA_DOCTOR_TYPE, selectedDoctorType);
        intent.putExtra(EXTRA_MIN_RATING,  selectedMinRating);

        ArrayList<String> uriStrings = new ArrayList<>();
        for (Uri uri : selectedImages) uriStrings.add(uri.toString());
        intent.putStringArrayListExtra(EXTRA_IMAGE_URIS, uriStrings);

        startActivity(intent);
    }

    // ================= HELPER =================
    private String getTextValue(TextInputEditText editText) {
        if (editText == null || editText.getText() == null) return "";
        return editText.getText().toString().trim();
    }
}