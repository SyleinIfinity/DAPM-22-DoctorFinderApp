package nhom22.doctorfinder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import nhom22.doctorfinder.R;
import nhom22.doctorfinder.data.remote.dto.response.AppointmentResponse;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {

    private final Context context;
    private List<AppointmentResponse> items;
    private final OnAppointmentClickListener listener;

    public interface OnAppointmentClickListener {
        void onPrimaryClick(AppointmentResponse appointment);
        void onSecondaryClick(AppointmentResponse appointment);
    }

    public AppointmentAdapter(Context context, OnAppointmentClickListener listener) {
        this.context = context;
        this.items = new ArrayList<>();
        this.listener = listener;
    }

    public void submitList(List<AppointmentResponse> newList) {
        this.items = newList != null ? new ArrayList<>(newList) : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_appointment_card, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        AppointmentResponse currentItem = items.get(position);
        
        // Month header logic
        boolean showMonthHeader = false;
        if (position == 0) {
            showMonthHeader = true;
        } else {
            AppointmentResponse prevItem = items.get(position - 1);
            String currentMonth = getMonth(currentItem.ngayCuThe);
            String prevMonth = getMonth(prevItem.ngayCuThe);
            if (!currentMonth.equals(prevMonth)) {
                showMonthHeader = true;
            }
        }
        
        holder.bind(currentItem, showMonthHeader);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class AppointmentViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvMonthHeader;
        private final TextView tvAvatar;
        private final TextView tvDoctorName;
        private final TextView tvSpecialty;
        private final TextView tvStatus;
        private final TextView tvDateTime;
        private final TextView tvLocationType;
        private final TextView tvNote;
        private final Button btnPrimary;
        private final Button btnSecondary;

        AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMonthHeader = itemView.findViewById(R.id.tvMonthHeader);
            tvAvatar = itemView.findViewById(R.id.tvAvatar);
            tvDoctorName = itemView.findViewById(R.id.tvDoctorName);
            tvSpecialty = itemView.findViewById(R.id.tvSpecialty);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvLocationType = itemView.findViewById(R.id.tvLocationType);
            tvNote = itemView.findViewById(R.id.tvNote);
            btnPrimary = itemView.findViewById(R.id.btnPrimary);
            btnSecondary = itemView.findViewById(R.id.btnSecondary);
        }

        void bind(AppointmentResponse item, boolean showMonthHeader) {
            if (showMonthHeader) {
                tvMonthHeader.setVisibility(View.VISIBLE);
                tvMonthHeader.setText(getMonthHeader(item.ngayCuThe));
            } else {
                tvMonthHeader.setVisibility(View.GONE);
            }

            // Doctor Info
            tvDoctorName.setText("BS. " + safeString(item.hoTenBacSi));
            tvSpecialty.setText(safeString(item.chuyenKhoa));
            
            // Avatar Initials
            tvAvatar.setText(getInitials(item.hoTenBacSi));

            // Status
            tvStatus.setText(mapStatus(item.trangThaiPhieu));
            
            // Update styling based on status if needed (optional based on your design system)
            if ("DA_HUY".equals(item.trangThaiPhieu) || "TU_CHOI".equals(item.trangThaiPhieu)) {
                tvStatus.setBackgroundResource(R.drawable.bg_badge_pending); // Change if there is specific bg
                tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_pending_text));
            } else if ("DA_XAC_NHAN".equals(item.trangThaiPhieu)) {
                 // Adjust colors
            }

            // Date Time
            tvDateTime.setText(formatDateTime(item.ngayCuThe, item.gioBatDau, item.gioKetThuc));

            // Location Type
            tvLocationType.setText(mapLocationType(item.loaiPhieu));

            // Note
            if (item.trieuChungGhiChu != null && !item.trieuChungGhiChu.trim().isEmpty()) {
                tvNote.setVisibility(View.VISIBLE);
                tvNote.setText(mapLocationType(item.loaiPhieu) + " · " + item.trieuChungGhiChu);
            } else {
                tvNote.setVisibility(View.GONE);
            }

            btnPrimary.setOnClickListener(v -> {
                if (listener != null) listener.onPrimaryClick(item);
            });

            btnSecondary.setOnClickListener(v -> {
                if (listener != null) listener.onSecondaryClick(item);
            });
        }
    }

    // --- Helper Functions ---

    private String safeString(String text) {
        return text != null ? text : "";
    }

    private String getInitials(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) return "?";
        String[] parts = fullName.trim().split("\\s+");
        return parts[parts.length - 1].substring(0, 1).toUpperCase();
    }

    private String mapStatus(String status) {
        if (status == null) return "Chờ xác nhận";
        switch (status) {
            case "CHO_XAC_NHAN": return "Chờ xác nhận";
            case "DA_XAC_NHAN": return "Đã xác nhận";
            case "DA_HUY": return "Đã huỷ";
            case "TU_CHOI": return "Bị từ chối";
            default: return status;
        }
    }

    private String mapLocationType(String type) {
        if (type == null) return "Khám";
        switch (type) {
            case "PHONGKHAM": return "Tại phòng khám";
            case "TAI_KHAM": return "Tái khám";
            case "YEU_CAU": return "Yêu cầu khác";
            default: return type;
        }
    }

    // Input expected: dd/MM/yyyy or yyyy-MM-dd depending on backend. Assuming backend gives dd/MM/yyyy based on UI.
    // Also time format: HH:mm:ss -> HH:mm
    private String formatDateTime(String date, String start, String end) {
        String fStart = removeSeconds(start);
        String fEnd = removeSeconds(end);
        return safeString(date) + " · " + fStart + " - " + fEnd;
    }

    private String removeSeconds(String time) {
        if (time == null) return "";
        // If format is HH:mm:ss, return HH:mm
        if (time.length() >= 5) {
            return time.substring(0, 5);
        }
        return time;
    }

    private String getMonth(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return "";
        try {
            // Check if format is dd/MM/yyyy
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = sdf.parse(dateStr);
            if (date != null) {
                SimpleDateFormat monthSdf = new SimpleDateFormat("MM", Locale.getDefault());
                return monthSdf.format(date);
            }
        } catch (ParseException e) {
            // Maybe it is yyyy-MM-dd
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date date = sdf.parse(dateStr);
                if (date != null) {
                    SimpleDateFormat monthSdf = new SimpleDateFormat("MM", Locale.getDefault());
                    return monthSdf.format(date);
                }
            } catch (ParseException ex) {
                // Ignore
            }
        }
        return "";
    }

    private String getMonthHeader(String dateStr) {
        String month = getMonth(dateStr);
        if (!month.isEmpty()) {
            return "THÁNG " + Integer.parseInt(month);
        }
        return "THÁNG ?";
    }
}
