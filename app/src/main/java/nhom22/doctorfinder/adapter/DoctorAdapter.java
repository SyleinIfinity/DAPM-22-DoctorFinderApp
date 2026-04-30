package nhom22.doctorfinder.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import nhom22.doctorfinder.R;
import nhom22.doctorfinder.model.Doctor;
import nhom22.doctorfinder.ui.user.profile.DoctorProfileActivity;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder> {

    private List<Doctor> doctorList;
    private Context context;

    public DoctorAdapter(Context context, List<Doctor> doctorList) {
        this.context = context;
        this.doctorList = doctorList;
    }

    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_doctor_search_result, parent, false);
        return new DoctorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
        Doctor doctor = doctorList.get(position);
        holder.bind(doctor);
    }

    @Override
    public int getItemCount() {
        return doctorList != null ? doctorList.size() : 0;
    }

    public void updateList(List<Doctor> newList) {
        this.doctorList = newList;
        notifyDataSetChanged();
    }

    class DoctorViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivDoctorAvatar;
        private TextView tvDoctorName, tvSpecialty, tvHospital;
        private TextView tvStars, tvRating, tvReviewCount;
        private TextView badgeTop, badgeAvailable, badgeOnline;
        private View onlineDot;

        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);
            initViews();
            setupClickListener();
        }

        private void initViews() {
            ivDoctorAvatar = itemView.findViewById(R.id.ivDoctorAvatar);
            tvDoctorName = itemView.findViewById(R.id.tvDoctorName);
            tvSpecialty = itemView.findViewById(R.id.tvSpecialty);
            tvHospital = itemView.findViewById(R.id.tvHospital);
            tvStars = itemView.findViewById(R.id.tvStars);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvReviewCount = itemView.findViewById(R.id.tvReviewCount);
            badgeTop = itemView.findViewById(R.id.badgeTop);
            badgeAvailable = itemView.findViewById(R.id.badgeAvailable);
            badgeOnline = itemView.findViewById(R.id.badgeOnline);
            onlineDot = itemView.findViewById(R.id.onlineDot);
        }

        private void setupClickListener() {
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Doctor doctor = doctorList.get(position);
                    Intent intent = new Intent(context, DoctorProfileActivity.class);
                    intent.putExtra("doctor_id", doctor.getId());
                    intent.putExtra("doctor_name", doctor.getName());
                    intent.putExtra("doctor_specialty", doctor.getSpecialty());
                    intent.putExtra("doctor_degree", doctor.getDegree());
                    intent.putExtra("doctor_hospital", doctor.getHospital());
                    intent.putExtra("doctor_rating", doctor.getRating());
                    intent.putExtra("doctor_review_count", doctor.getReviewCount());
                    intent.putExtra("doctor_experience", doctor.getExperienceYears());
                    intent.putExtra("doctor_is_online", doctor.isOnline());
                    intent.putExtra("doctor_type", doctor.getDoctorType());
                    context.startActivity(intent);
                }
            });
        }

        public void bind(Doctor doctor) {
            tvDoctorName.setText(doctor.getName());
            tvSpecialty.setText(doctor.getFormattedInfo());
            tvHospital.setText("🏥 " + doctor.getHospital());
            tvStars.setText(doctor.getStarString());
            tvRating.setText(String.format(" %.1f", doctor.getRating()));
            tvReviewCount.setText(String.format(" (%d đánh giá)", doctor.getReviewCount()));

            // Show top doctor badge if applicable
            if (doctor.isTopDoctor()) {
                badgeTop.setVisibility(View.VISIBLE);
            } else {
                badgeTop.setVisibility(View.GONE);
            }

            // Show online dot if online
            if (doctor.isOnline()) {
                onlineDot.setVisibility(View.VISIBLE);
                badgeOnline.setVisibility(View.VISIBLE);
            } else {
                onlineDot.setVisibility(View.GONE);
                badgeOnline.setVisibility(View.GONE);
            }

            // Show available slot badge if has slot
            if (doctor.hasAvailableSlot()) {
                badgeAvailable.setVisibility(View.VISIBLE);
            } else {
                badgeAvailable.setVisibility(View.GONE);
            }
        }
    }
}
