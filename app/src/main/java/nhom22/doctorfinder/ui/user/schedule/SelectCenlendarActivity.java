package nhom22.doctorfinder.ui.user.schedule;

import android.content.Intent;
import android.graphics.Paint;
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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    private RecyclerView  rvWeekStrip;
    private FlexboxLayout flexSlotsSang;
    private FlexboxLayout flexSlotsChieu;
    private TextView      tvSelectedSlot;
    private TextView      tvDuration;

    // ── State ─────────────────────────────────────────────────────────────────
    private WorkingSlot    selectedSlot   = null;
    private MaterialButton selectedButton = null;

    /** Ngày đang được chọn trên week strip (yyyy-MM-dd) */
    private String selectedDate = null;

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
        buildWeekStrip();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Re-fetch khi quay lại app để cập nhật slot đã qua giờ
        if (selectedDate != null && maBacSi >= 0) {
            fetchWorkingSlots(maBacSi, selectedDate);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Đọc Intent extras
    // ─────────────────────────────────────────────────────────────────────────

    private void readIntentExtras() {
        Intent intent = getIntent();
        String idStr  = intent.getStringExtra("doctor_id");
        doctorName    = intent.getStringExtra("doctor_name");

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
            tvDoctorNameView.setText("BS. "+doctorName);

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

    private void buildWeekStrip() {
        List<DayItem> days = new ArrayList<>();
        SimpleDateFormat sdfKey  = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat sdfDay  = new SimpleDateFormat("dd", Locale.getDefault());
        SimpleDateFormat sdfDow  = new SimpleDateFormat("EEE", new Locale("vi", "VN"));

        Calendar cal = Calendar.getInstance();
        for (int i = 0; i < 14; i++) {
            DayItem item  = new DayItem();
            item.dateKey  = sdfKey.format(cal.getTime());
            item.dayNum   = sdfDay.format(cal.getTime());
            item.dayLabel = sdfDow.format(cal.getTime())
                    .replace("thứ", "T")
                    .replace("Thứ", "T");

            if (i == 0) item.dayLabel = "Hôm\nnay";

            days.add(item);
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }

        dayStripAdapter = new DayStripAdapter(days, position -> {
            DayItem selected = days.get(position);
            selectedDate   = selected.dateKey;
            selectedSlot   = null;
            selectedButton = null;
            clearSelectedSlotInfo();
            fetchWorkingSlots(maBacSi, selectedDate);
        });

        rvWeekStrip.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvWeekStrip.setAdapter(dayStripAdapter);

        // Tự động chọn hôm nay
        dayStripAdapter.selectPosition(0);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Kiểm tra slot đã qua giờ chưa (chỉ áp dụng cho ngày hôm nay)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Trả về true nếu slot đã qua giờ hiện tại (chỉ check khi đang xem hôm nay).
     * So sánh theo phút: slotHH*60+mm < nowHH*60+mm
     */
    private boolean isSlotPassed(@NonNull WorkingSlot slot) {
        // Chỉ kiểm tra khi xem ngày hôm nay
        SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today          = sdf.format(new Date());
        if (!today.equals(selectedDate)) return false;

        try {
            String startTime = slot.getDisplayTime(); // "08:00"
            String[] parts   = startTime.split(":");
            int slotHour     = Integer.parseInt(parts[0]);
            int slotMinute   = Integer.parseInt(parts[1]);

            Calendar now     = Calendar.getInstance();
            int nowHour      = now.get(Calendar.HOUR_OF_DAY);
            int nowMinute    = now.get(Calendar.MINUTE);

            return (slotHour * 60 + slotMinute) < (nowHour * 60 + nowMinute);
        } catch (Exception e) {
            Log.e(TAG, "isSlotPassed parse error", e);
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // API: lấy danh sách slot theo ngày
    // ─────────────────────────────────────────────────────────────────────────

    private void fetchWorkingSlots(int maBacSi, String date) {
        if (maBacSi < 0) {
            Toast.makeText(this, "Không xác định được bác sĩ", Toast.LENGTH_SHORT).show();
            return;
        }

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

        boolean hasSang   = false;
        boolean hasChieu  = false;

        for (WorkingSlot slot : slots) {
            MaterialButton btn = createSlotButton(slot);
            if (slot.isMorning()) {
                if (flexSlotsSang != null) {
                    flexSlotsSang.addView(btn);
                    hasSang = true;
                }
            } else {
                if (flexSlotsChieu != null) {
                    flexSlotsChieu.addView(btn);
                    hasChieu = true;
                }
            }
        }

        // Ẩn label "Buổi sáng" / "Buổi chiều" nếu không có slot
        View tvLabelSang  = findViewById(R.id.tvLabelSang);
        View tvLabelChieu = findViewById(R.id.tvLabelChieu);
        if (tvLabelSang  != null) tvLabelSang.setVisibility(hasSang  ? View.VISIBLE : View.GONE);
        if (tvLabelChieu != null) tvLabelChieu.setVisibility(hasChieu ? View.VISIBLE : View.GONE);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Tạo slot button với logic passed / full / available
    // ─────────────────────────────────────────────────────────────────────────

    private MaterialButton createSlotButton(@NonNull WorkingSlot slot) {
        boolean passed    = isSlotPassed(slot);
        boolean available = slot.isAvailable() && !passed;

        int styleRes = available
                ? R.style.Widget_Slot_Available
                : R.style.Widget_Slot_Full;

        MaterialButton btn = new MaterialButton(
                new android.view.ContextThemeWrapper(this, styleRes), null, 0);

        btn.setText(slot.getDisplayTime());
        btn.setTextSize(12f);

        int dp1  = (int)(1  * getResources().getDisplayMetrics().density);
        int dp12 = (int)(12 * getResources().getDisplayMetrics().density);

        if (available) {
            // ── Còn trống, chưa qua giờ ──────────────────────────────────────
            btn.setTextColor(ContextCompat.getColor(this, R.color.teal_600));
            btn.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.teal_50));
            btn.setStrokeColor(ContextCompat.getColorStateList(this, R.color.teal_600));
            btn.setStrokeWidth(dp1);
            btn.setCornerRadius(dp12);
            btn.setOnClickListener(v -> onSlotSelected(btn, slot));

        } else if (passed) {
            // ── Đã qua giờ → xám + gạch ngang ───────────────────────────────
            btn.setTextColor(ContextCompat.getColor(this, R.color.text_muted));
            btn.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.gray_200));
            btn.setCornerRadius(dp12);
            btn.setEnabled(false);
            // Gạch ngang để phân biệt với "hết chỗ"
            btn.setPaintFlags(btn.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        } else {
            // ── Hết chỗ (full) ────────────────────────────────────────────────
            btn.setTextColor(ContextCompat.getColor(this, R.color.text_muted));
            btn.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.gray_200));
            btn.setCornerRadius(dp12);
            btn.setEnabled(false);
        }

        // Layout params
        FlexboxLayout.LayoutParams lp = new FlexboxLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        int margin = (int)(3 * getResources().getDisplayMetrics().density);
        lp.setMargins(margin, margin, margin, margin);
        btn.setLayoutParams(lp);

        return btn;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Xử lý chọn slot
    // ─────────────────────────────────────────────────────────────────────────

    private void onSlotSelected(@NonNull MaterialButton clickedBtn, @NonNull WorkingSlot slot) {
        // Bỏ chọn button cũ
        if (selectedButton != null && selectedButton != clickedBtn) {
            applySlotStyle(selectedButton, false /* restore to available */);
        }
        // Chọn button mới
        applySlotStyle(clickedBtn, true /* selected */);
        selectedButton = clickedBtn;
        selectedSlot   = slot;
        updateSelectedSlotInfo(slot);
    }

    /**
     * @param selected true = apply style "đang chọn", false = restore "còn trống"
     */
    private void applySlotStyle(@NonNull MaterialButton btn, boolean selected) {
        int dp1  = (int)(1 * getResources().getDisplayMetrics().density);
        int dp12 = (int)(12 * getResources().getDisplayMetrics().density);

        if (selected) {
            btn.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.teal_600));
            btn.setTextColor(ContextCompat.getColor(this, R.color.white));
            btn.setStrokeWidth(0);
            btn.setCornerRadius(dp12);
        } else {
            btn.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.teal_50));
            btn.setTextColor(ContextCompat.getColor(this, R.color.teal_600));
            btn.setStrokeWidth(dp1);
            btn.setStrokeColor(ContextCompat.getColorStateList(this, R.color.teal_600));
            btn.setCornerRadius(dp12);
        }
    }

    private void updateSelectedSlotInfo(@NonNull WorkingSlot slot) {
        if (tvSelectedSlot != null)
            tvSelectedSlot.setText("Đã chọn: " + slot.getDisplayTime()
                    + " - " + slot.getDisplayEndTime());
        if (tvDuration != null)
            tvDuration.setText(" · Thời lượng " + slot.thoiLuongPhut + " phút");
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
        intent.putExtra("doctor_id",     String.valueOf(maBacSi));
        intent.putExtra("doctor_name",   doctorName);
        intent.putExtra("slot_id",       selectedSlot.maChiTiet);
        intent.putExtra("slot_date",     selectedSlot.ngayCuThe);
        intent.putExtra("slot_start",    selectedSlot.getDisplayTime());
        intent.putExtra("slot_end",      selectedSlot.getDisplayEndTime());
        intent.putExtra("slot_duration", selectedSlot.thoiLuongPhut);

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

        private final List<DayItem> items;
        private final OnDaySelected callback;
        private int                 selectedPos = -1;

        DayStripAdapter(List<DayItem> items, OnDaySelected callback) {
            this.items    = items;
            this.callback = callback;
        }

        void selectPosition(int pos) {
            int prev    = selectedPos;
            selectedPos = pos;
            if (prev >= 0) notifyItemChanged(prev);
            notifyItemChanged(pos);
            callback.onSelected(pos);
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_day_pill, parent, false);
            return new VH(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            DayItem item       = items.get(position);
            boolean isSelected = (position == selectedPos);

            holder.tvDayNum.setText(item.dayNum);
            holder.tvDayLabel.setText(item.dayLabel);

            if (isSelected) {
                holder.itemView.setBackgroundResource(R.drawable.bg_day_selected);
                holder.tvDayNum.setTextColor(
                        ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
                holder.tvDayLabel.setTextColor(
                        ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
            } else {
                holder.itemView.setBackgroundResource(R.drawable.bg_day_normal);
                holder.tvDayNum.setTextColor(
                        ContextCompat.getColor(holder.itemView.getContext(), R.color.text_primary));
                holder.tvDayLabel.setTextColor(
                        ContextCompat.getColor(holder.itemView.getContext(), R.color.text_muted));
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