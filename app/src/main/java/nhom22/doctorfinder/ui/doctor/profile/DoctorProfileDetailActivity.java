//package nhom22.doctorfinder.ui.doctor.profile;
//
//
//import android.os.Bundle;
//import android.view.View;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import nhom22.doctorfinder.R;
//
//public class DoctorProfileDetailActivity extends AppCompatActivity {
//
//    private TextView tvDoctorName, tvSpecialty, tvHospital, tvBio;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_doctor_profile);
//
//        initViews();
//        setData();
//        setEvents();
//    }
//
//    // ================= INIT VIEW =================
//    private void initViews() {
//        tvDoctorName = findViewById(R.id.tvDoctorName);
//        tvSpecialty = findViewById(R.id.tvSpecialty);
//        tvHospital = findViewById(R.id.tvHospital);
//        tvBio = findViewById(R.id.tvBio);
//    }
//
//    // ================= SET DATA =================
//    private void setData() {
//
//        // ====== THÔNG TIN HEADER ======
//        tvDoctorName.setText("BS. Nguyễn Thanh Tùng");
//        tvSpecialty.setText("Thần kinh học");
//        tvHospital.setText("BV Bạch Mai");
//
//        // ====== BIO ======
//        tvBio.setText("Bác sĩ có hơn 10 năm kinh nghiệm trong lĩnh vực thần kinh. "
//                + "Chuyên điều trị các bệnh liên quan đến não bộ, mất ngủ, đau đầu kéo dài...");
//
//        // ====== CARD: THÔNG TIN CHUYÊN MÔN ======
//        setRowText(R.id.rowSpecialty, "Thần kinh");
//        setRowText(R.id.rowDegree, "Tiến sĩ Y khoa");
//        setRowText(R.id.rowType, "Khám tại bệnh viện");
//        setRowText(R.id.rowWorkplace, "Bệnh viện Bạch Mai");
//
//        // ====== LICENSE ======
//        TextView tvLicense = findViewById(R.id.tvLicenseNo);
//        tvLicense.setText("CCHN-123456789");
//    }
//
//    // ================= SET EVENT =================
//    private void setEvents() {
//
//        // Nút quay lại
//        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
//
//        // Follow
//        findViewById(R.id.btnFollow).setOnClickListener(v ->
//                Toast.makeText(this, "Đã theo dõi", Toast.LENGTH_SHORT).show()
//        );
//
//        // Đọc thêm
//        findViewById(R.id.tvReadMore).setOnClickListener(v ->
//                Toast.makeText(this, "Xem thêm thông tin...", Toast.LENGTH_SHORT).show()
//        );
//
//        // Nhắn tin
//        findViewById(R.id.btnMessage).setOnClickListener(v ->
//                Toast.makeText(this, "Mở chat...", Toast.LENGTH_SHORT).show()
//        );
//
//        // Xem lịch
//        findViewById(R.id.btnViewSchedule).setOnClickListener(v ->
//                Toast.makeText(this, "Xem lịch khám...", Toast.LENGTH_SHORT).show()
//        );
//
//        // Đặt lịch
//        findViewById(R.id.btnBook).setOnClickListener(v ->
//                Toast.makeText(this, "Chuyển sang đặt lịch...", Toast.LENGTH_SHORT).show()
//        );
//    }
//
//    // ================= HELPER =================
//    private void setRowText(int rowId, String value) {
//        View row = findViewById(rowId);
//        if (row != null) {
//            TextView tv = row.findViewById(R.id.tvInfoValue);
//            if (tv != null && value != null) {
//                tv.setText(value);
//            }
//        }
//    }
//}
