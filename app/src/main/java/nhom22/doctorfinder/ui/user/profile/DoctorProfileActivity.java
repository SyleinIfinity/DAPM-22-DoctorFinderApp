package nhom22.doctorfinder.ui.user.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import nhom22.doctorfinder.R;

public class DoctorProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profile);

        Intent intent = getIntent();
        String doctorId = intent.getStringExtra("doctor_id");
        String doctorName = intent.getStringExtra("doctor_name");
        String doctorSpecialty = intent.getStringExtra("doctor_specialty");
        String doctorDegree = intent.getStringExtra("doctor_degree");
        String doctorHospital = intent.getStringExtra("doctor_hospital");
        float doctorRating = intent.getFloatExtra("doctor_rating", 0);
        int doctorReviewCount = intent.getIntExtra("doctor_review_count", 0);
        int doctorExperience = intent.getIntExtra("doctor_experience", 0);
        boolean isOnline = intent.getBooleanExtra("doctor_is_online", false);
        String doctorType = intent.getStringExtra("doctor_type");

        // Setup back button
        ImageButton btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Populate doctor info
        TextView tvDoctorName = findViewById(R.id.tvDoctorName);
        if (tvDoctorName != null) {
            tvDoctorName.setText(doctorName);
        }

        TextView tvSpecialty = findViewById(R.id.tvSpecialty);
        if (tvSpecialty != null) {
            tvSpecialty.setText(doctorSpecialty);
        }

        TextView tvDegreeChip = findViewById(R.id.tvDegreeChip);
        if (tvDegreeChip != null) {
            tvDegreeChip.setText(doctorDegree);
        }

        TextView tvHospital = findViewById(R.id.tvHospital);
        if (tvHospital != null) {
            tvHospital.setText(doctorHospital);
        }

        TextView tvStatus = findViewById(R.id.tvStatus);
        if (tvStatus != null) {
            if (isOnline) {
                tvStatus.setText("Trực tuyến");
            } else {
                tvStatus.setText("Xác minh hồ sơ");
            }
        }

        // Setup follow button
        android.view.View btnFollow = findViewById(R.id.btnFollow);
        if (btnFollow != null) {
            btnFollow.setOnClickListener(v -> {
                // Handle follow action
            });
        }

        // Open chat when tapping message icon
        android.view.View btnMessage = findViewById(R.id.btnMessage);
        if (btnMessage != null) {
            btnMessage.setOnClickListener(v -> {
                Intent chatIntent = new Intent(DoctorProfileActivity.this, nhom22.doctorfinder.ui.user.chat.ChatBoxActivity.class);
                chatIntent.putExtra("doctor_id", doctorId);
                chatIntent.putExtra("doctor_name", doctorName);
                startActivity(chatIntent);
            });
        }

        // View schedule or start booking
        android.view.View btnViewSchedule = findViewById(R.id.btnViewSchedule);
        if (btnViewSchedule != null) {
            btnViewSchedule.setOnClickListener(v -> {
                Intent intentSchedule = new Intent(DoctorProfileActivity.this, nhom22.doctorfinder.ui.user.schedule.SelectCenlendarActivity.class);
                intentSchedule.putExtra("doctor_id", doctorId);
                intentSchedule.putExtra("doctor_name", doctorName);
                startActivity(intentSchedule);
            });
        }

        android.view.View btnBook = findViewById(R.id.btnBook);
        if (btnBook != null) {
            btnBook.setOnClickListener(v -> {
                Intent bookIntent = new Intent(DoctorProfileActivity.this, nhom22.doctorfinder.ui.user.schedule.SelectCenlendarActivity.class);
                bookIntent.putExtra("doctor_id", doctorId);
                bookIntent.putExtra("doctor_name", doctorName);
                startActivity(bookIntent);
            });
        }

        // Note: Additional fields like rating, reviews, experience can be displayed
        // in separate TextViews if they exist in the layout
    }
}
