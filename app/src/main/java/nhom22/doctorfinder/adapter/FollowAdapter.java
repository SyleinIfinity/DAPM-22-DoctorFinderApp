package nhom22.doctorfinder.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import nhom22.doctorfinder.R;
import nhom22.doctorfinder.data.remote.dto.response.FollowDoctorItem;
import nhom22.doctorfinder.ui.user.profile.DoctorProfileActivity;

/**
 * Adapter cho RecyclerView danh sách bác sĩ đã theo dõi (FollowFragment).
 */
public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.FollowViewHolder> {

    private final Context context;
    private List<FollowDoctorItem> items;

    public FollowAdapter(Context context) {
        this.context = context;
        this.items = new ArrayList<>();
    }

    /** Cập nhật toàn bộ danh sách và refresh UI */
    public void submitList(@NonNull List<FollowDoctorItem> newList) {
        this.items = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FollowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_follow_doctor, parent, false);
        return new FollowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    // ──────────────────────────────────────────────────────────────────────────

    class FollowViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivAvatar;
        private final TextView tvInitials;
        private final TextView tvName;
        private final TextView tvSpecialty;
        private final TextView tvHospital;

        FollowViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar    = itemView.findViewById(R.id.ivDoctorAvatar);
            tvInitials  = itemView.findViewById(R.id.tvAvatarInitials);
            tvName      = itemView.findViewById(R.id.tvDoctorName);
            tvSpecialty = itemView.findViewById(R.id.tvSpecialty);
            tvHospital  = itemView.findViewById(R.id.tvHospital);
        }

        void bind(@NonNull FollowDoctorItem item) {
            // ─── Tên bác sĩ ───
            tvName.setText(item.hoTenBacSi != null ? "BS. "+ item.hoTenBacSi : "");

            // ─── Chuyên khoa ───
            tvSpecialty.setText(item.chuyenKhoa != null ? item.chuyenKhoa : "");

            // ─── Bệnh viện / địa chỉ ───
            String hospital = buildHospitalText(item);
            tvHospital.setText(hospital);

            // ─── Avatar ───
            loadAvatar(item);

            // ─── Click → mở DoctorProfileActivity ───
            itemView.setOnClickListener(v -> openDoctorProfile(item));
        }

        /** Ưu tiên tenCoSoYTe, fallback sang diaChiLamViec */
        private String buildHospitalText(FollowDoctorItem item) {
            if (!TextUtils.isEmpty(item.tenCoSoYTe)) return item.tenCoSoYTe;
            if (!TextUtils.isEmpty(item.diaChiLamViec)) return item.diaChiLamViec;
            return "";
        }

        private void loadAvatar(FollowDoctorItem item) {
            if (!TextUtils.isEmpty(item.anhDaiDienBacSi)) {
                // Có URL → dùng Glide, ẩn initials
                ivAvatar.setVisibility(View.VISIBLE);
                tvInitials.setVisibility(View.GONE);
                Glide.with(context)
                        .load(item.anhDaiDienBacSi)
                        .placeholder(R.drawable.bg_avatar_circle)
                        .circleCrop()
                        .into(ivAvatar);
            } else {
                // Không có ảnh → hiển thị chữ cái đầu tên
                ivAvatar.setVisibility(View.GONE);
                tvInitials.setVisibility(View.VISIBLE);
                String initials = getInitials(item.hoTenBacSi);
                tvInitials.setText(initials);
            }
        }

        /** Lấy chữ cái đầu của tên (an toàn với null/empty) */
        @NonNull
        private String getInitials(String fullName) {
            if (TextUtils.isEmpty(fullName)) return "?";
            String[] parts = fullName.trim().split("\\s+");
            return parts[parts.length - 1].substring(0, 1).toUpperCase();
        }

        private void openDoctorProfile(FollowDoctorItem item) {
            Intent intent = new Intent(context, DoctorProfileActivity.class);
            intent.putExtra("doctor_id", String.valueOf(item.maBacSi));
            intent.putExtra("doctor_name", item.hoTenBacSi);
            intent.putExtra("doctor_specialty", item.chuyenKhoa);
            intent.putExtra("doctor_hospital", item.tenCoSoYTe);
            context.startActivity(intent);
        }
    }
}
