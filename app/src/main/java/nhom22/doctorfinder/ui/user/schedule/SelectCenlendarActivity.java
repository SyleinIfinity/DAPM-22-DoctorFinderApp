package nhom22.doctorfinder.ui.user.schedule;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import nhom22.doctorfinder.R;
import nhom22.doctorfinder.data.remote.api.DoctorApiService;
import nhom22.doctorfinder.data.remote.client.RetrofitClient;
import nhom22.doctorfinder.data.remote.dto.response.WorkingSlot;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectCenlendarActivity extends AppCompatActivity {

    private static final String TAG = "SelectCenlendarActivity";

    // ── Intent data ───────────────────────────────────────────────────────────
    private int    maBacSi = -1;
    private String doctorName;

    // ── Views ─────────────────────────────────────────────────────────────────
    private RecyclerView      rvWeekStrip;
    private FlexboxLayout     flexSlotsSang;
    private FlexboxLayout     flexSlotsChieu;
    private TextView          tvSelectedSlot;
    private TextView          tvDuration;

    // ── State ─────────────────────────────────────────────────────────────────
    private WorkingSlot    selectedSlot   = null;
    private MaterialButton selectedButton = null;

    /** Ngày đang được chọn trên week strip (yyyy-MM-dd) */
    private String         selectedDate   = null;

    // ── Adapters ──────────────────────────────────────────────────────────────
    private DayStripAdapter dayStripAdapter;

    // ── API ───────────────────────────────────────────────────────────────────
    private DoctorApiService doctorApiService;

    // ─────────────────────────────────────────────────────────────────────────

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_cenlendar);

        doctorApiService = RetrofitClient.getClient().create(DoctorApiService.class);

        readIntentExtras();
        bindViews();
        buildWeekStrip(); // Xây dựng 14 ngày từ hôm nay
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Đọc Intent extras
    // ─────────────────────────────────────────────────────────────────────────

    private void readIntentExtras() {
        Intent intent  = getIntent();
        String idStr   = intent.getStringExtra("doctor_id");
        doctorName     = intent.getStringExtra("doctor_name");

        if (idStr != null) {
            try { maBacSi = Integer.parseInt(idStr); }
            catch (NumberFormatException e) { Log.e(TAG, "parse doctor_id failed: " + idStr); }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Bind views
    // ─────────────────────────────────────────────────────────────────────────

    private void bindViews() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) toolbar.setNavigationOnClickListener(v -> finish());

        TextView tvDoctorNameView = findViewById(R.id.tvDoctorName);
        if (tvDoctorNameView != null && doctorName != null)
            tvDoctorNameView.setText(doctorName);

        rvWeekStrip    = findViewById(R.id.rvWeekStrip);
        flexSlotsSang  = findViewById(R.id.flexSlotsSang);
        flexSlotsChieu = findViewById(R.id.flexSlotsChieu);
        tvSelectedSlot = findViewById(R.id.tvSelectedSlot);
        tvDuration     = findViewById(R.id.tvDuration);

        View btnTiepTuc = findViewById(R.id.btnTiepTuc);
        if (btnTiepTuc != null) btnTiepTuc.setOnClickListener(v -> onContinueClicked());

        View btnHuy = findViewById(R.id.btnHuy);
        if (btnHuy != null) btnHuy.setOnClickListener(v -> finish());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Xây dựng week strip 14 ngày
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Tạo danh sách 14 ngày bắt đầu từ hôm nay, hiển thị trong RecyclerView ngang.
     * Mặc định chọn ngày đầu tiên (hôm nay) và tải slot của ngày đó.
     */
    private void buildWeekStrip() {
        List<DayItem> days = new ArrayList<>();
        SimpleDateFormat sdfKey  = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat sdfDay  = new SimpleDateFormat("dd", Locale.getDefault());
        SimpleDateFormat sdfDow  = new SimpleDateFormat("EEE", new Locale("vi", "VN"));

        Calendar cal = Calendar.getInstance();
        for (int i = 0; i < 14; i++) {
            DayItem item = new DayItem();
            item.dateKey  = sdfKey.format(cal.getTime());   // "2026-05-06"
            item.dayNum   = sdfDay.format(cal.getTime());   // "06"
            item.dayLabel = sdfDow.format(cal.getTime())    // "Th 3"
                    .replace("thứ", "T")
                    .replace("Thứ", "T");

            // Hôm nay
            if (i == 0) item.dayLabel = "Hôm\nnay";

            days.add(item);
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }

        dayStripAdapter = new DayStripAdapter(days, position -> {
            // Callback khi người dùng chọn một ngày
            DayItem selected = days.get(position);
            selectedDate = selected.dateKey;

            // Reset slot đã chọn trước đó
            selectedSlot   = null;
            selectedButton = null;
            clearSelectedSlotInfo();

            fetchWorkingSlots(maBacSi, selectedDate);
        });

        rvWeekStrip.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvWeekStrip.setAdapter(dayStripAdapter);

        // Tự động chọn ngày hôm nay
        dayStripAdapter.selectPosition(0);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // API: lấy danh sách slot theo ngày
    // ─────────────────────────────────────────────────────────────────────────

    private void fetchWorkingSlots(int maBacSi, String date) {
        if (maBacSi < 0) {
            Toast.makeText(this, "Không xác định được bác sĩ", Toast.LENGTH_SHORT).show();
            return;
        }

        // Xóa slot cũ trong khi chờ
        if (flexSlotsSang  != null) flexSlotsSang.removeAllViews();
        if (flexSlotsChieu != null) flexSlotsChieu.removeAllViews();

        doctorApiService.getWorkingSlots(maBacSi, date)
                .enqueue(new Callback<List<WorkingSlot>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<WorkingSlot>> call,
                                           @NonNull Response<List<WorkingSlot>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            renderSlots(response.body());
                        } else {
                            Log.w(TAG, "getWorkingSlots: code=" + response.code());
                            Toast.makeText(SelectCenlendarActivity.this,
                                    "Không tải được lịch khám (lỗi " + response.code() + ")",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<WorkingSlot>> call,
                                          @NonNull Throwable t) {
                        Log.e(TAG, "getWorkingSlots: network error", t);
                        Toast.makeText(SelectCenlendarActivity.this,
                                "Lỗi kết nối khi tải lịch khám",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Render slots vào FlexboxLayout
    // ─────────────────────────────────────────────────────────────────────────

    private void renderSlots(@NonNull List<WorkingSlot> slots) {
        if (flexSlotsSang  != null) flexSlotsSang.removeAllViews();
        if (flexSlotsChieu != null) flexSlotsChieu.removeAllViews();

        if (slots.isEmpty()) {
            Toast.makeText(this, "Không có khung giờ khám cho ngày này",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        for (WorkingSlot slot : slots) {
            MaterialButton btn = createSlotButton(slot);
            if (slot.isMorning()) {
                if (flexSlotsSang  != null) flexSlotsSang.addView(btn);
            } else {
                if (flexSlotsChieu != null) flexSlotsChieu.addView(btn);
            }
        }
    }

    private MaterialButton createSlotButton(@NonNull WorkingSlot slot) {
        int styleRes = slot.isAvailable()
                ? R.style.Widget_Slot_Available
                : R.style.Widget_Slot_Full;

        MaterialButton btn = new MaterialButton(
                new android.view.ContextThemeWrapper(this, styleRes), null, 0);

        btn.setText(slot.getDisplayTime());
        btn.setTextSize(12f);

        FlexboxLayout.LayoutParams lp = new FlexboxLayout.LayoutParams(
                FlexboxLayout.LayoutParams.WRAP_CONTENT,
                (int) (32 * getResources().getDisplayMetrics().density));
        int margin = (int) (3 * getResources().getDisplayMetrics().density);
        lp.setMargins(margin, margin, margin, margin);
        btn.setLayoutParams(lp);

        if (slot.isAvailable()) {
            btn.setOnClickListener(v -> onSlotSelected(btn, slot));
        } else {
            btn.setEnabled(false);
        }

        return btn;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Xử lý chọn slot
    // ─────────────────────────────────────────────────────────────────────────

    private void onSlotSelected(@NonNull MaterialButton clickedBtn, @NonNull WorkingSlot slot) {
        if (selectedButton != null && selectedButton != clickedBtn) {
            applySlotStyle(selectedButton, R.style.Widget_Slot_Available);
        }
        applySlotStyle(clickedBtn, R.style.Widget_Slot_Selected);
        selectedButton = clickedBtn;
        selectedSlot   = slot;
        updateSelectedSlotInfo(slot);
    }

    private void applySlotStyle(@NonNull MaterialButton btn, int styleRes) {
        if (styleRes == R.style.Widget_Slot_Selected) {
            btn.setBackgroundTintList(
                    androidx.core.content.ContextCompat.getColorStateList(this, R.color.slot_selected_bg));
            btn.setTextColor(
                    androidx.core.content.ContextCompat.getColor(this, R.color.slot_selected_text));
            btn.setStrokeWidth(0);
        } else {
            btn.setBackgroundTintList(null);
            btn.setTextColor(
                    androidx.core.content.ContextCompat.getColor(this, R.color.slot_available_text));
            btn.setStrokeWidth(1);
            btn.setStrokeColor(
                    androidx.core.content.ContextCompat.getColorStateList(this, R.color.slot_available_text));
        }
    }

    private void updateSelectedSlotInfo(@NonNull WorkingSlot slot) {
        if (tvSelectedSlot != null)
            tvSelectedSlot.setText("Đã chọn: " + slot.getDisplayTime() + " - " + slot.getDisplayEndTime());
        if (tvDuration != null)
            tvDuration.setText("· Thời lượng " + slot.thoiLuongPhut + " phút");
    }

    private void clearSelectedSlotInfo() {
        if (tvSelectedSlot != null) tvSelectedSlot.setText("Chưa chọn khung giờ");
        if (tvDuration     != null) tvDuration.setText("");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Tiếp tục → SchedulingInformationActivity
    // ─────────────────────────────────────────────────────────────────────────

    private void onContinueClicked() {
        if (selectedSlot == null) {
            Toast.makeText(this, "Vui lòng chọn khung giờ khám", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, SchedulingInformationActivity.class);
        intent.putExtra("doctor_id",    String.valueOf(maBacSi));
        intent.putExtra("doctor_name",  doctorName);
        intent.putExtra("slot_id",      selectedSlot.maChiTiet);
        intent.putExtra("slot_date",    selectedSlot.ngayCuThe);
        intent.putExtra("slot_start",   selectedSlot.getDisplayTime());
        intent.putExtra("slot_end",     selectedSlot.getDisplayEndTime());
        intent.putExtra("slot_duration",selectedSlot.thoiLuongPhut);

        startActivity(intent);
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Inner: DayItem model
    // ═════════════════════════════════════════════════════════════════════════

    static class DayItem {
        String dateKey;   // "2026-05-06"
        String dayNum;    // "06"
        String dayLabel;  // "T 3" hoặc "Hôm\nnay"
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Inner: DayStripAdapter
    // ═════════════════════════════════════════════════════════════════════════

    static class DayStripAdapter extends RecyclerView.Adapter<DayStripAdapter.VH> {

        interface OnDaySelected { void onSelected(int position); }

        private final List<DayItem>   items;
        private final OnDaySelected   callback;
        private int                   selectedPos = -1;

        DayStripAdapter(List<DayItem> items, OnDaySelected callback) {
            this.items    = items;
            this.callback = callback;
        }

        void selectPosition(int pos) {
            int prev = selectedPos;
            selectedPos = pos;
            if (prev >= 0) notifyItemChanged(prev);
            notifyItemChanged(pos);
            callback.onSelected(pos);
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Inflate item layout, hoặc tạo View code-only để không phụ thuộc XML
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_day_pill, parent, false);
            return new VH(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            DayItem item = items.get(position);
            holder.tvDayNum.setText(item.dayNum);
            holder.tvDayLabel.setText(item.dayLabel);

            boolean isSelected = (position == selectedPos);

            // Thay đổi background & màu chữ theo trạng thái
            if (isSelected) {
                holder.itemView.setBackgroundResource(R.drawable.bg_day_selected);
                holder.tvDayNum.setTextColor(
                        androidx.core.content.ContextCompat.getColor(
                                holder.itemView.getContext(), R.color.white));
                holder.tvDayLabel.setTextColor(
                        androidx.core.content.ContextCompat.getColor(
                                holder.itemView.getContext(), R.color.white));
            } else {
                holder.itemView.setBackgroundResource(R.drawable.bg_day_normal);
                holder.tvDayNum.setTextColor(
                        androidx.core.content.ContextCompat.getColor(
                                holder.itemView.getContext(), R.color.text_primary));
                holder.tvDayLabel.setTextColor(
                        androidx.core.content.ContextCompat.getColor(
                                holder.itemView.getContext(), R.color.text_muted));
            }

            holder.itemView.setOnClickListener(v -> selectPosition(holder.getAdapterPosition()));
        }

        @Override
        public int getItemCount() { return items.size(); }

        static class VH extends RecyclerView.ViewHolder {
            TextView tvDayNum;
            TextView tvDayLabel;

            VH(@NonNull View itemView) {
                super(itemView);
                tvDayNum   = itemView.findViewById(R.id.tvDayNum);
                tvDayLabel = itemView.findViewById(R.id.tvDayLabel);
            }
        }
    }
}