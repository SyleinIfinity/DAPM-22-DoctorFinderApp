package nhom22.doctorfinder.ui.user.schedule;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.gson.Gson;

import nhom22.doctorfinder.R;
import nhom22.doctorfinder.data.remote.dto.response.AppointmentResponse;
import nhom22.doctorfinder.ui.home.HomeFragment;

public class ConfirmAppointmentActivity extends AppCompatActivity {

    // ── Views ───────────────────────────────────────────────
    private TextView tvMaPhieu, tvStatusLabel;
    private TextView tvBacSiName, tvBacSiSpec;
    private TextView tvNgayKham, tvGioKham;
    private TextView tvDiaChi, tvLoaiKham;
    private TextView tvLoaiPhieu, tvTrieuChung;
    private TextView tvBenhNhanName, tvBenhNhanSdt;

    private Chip chipTrangThai;

    // ── Data ────────────────────────────────────────────────
    private AppointmentResponse data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_appointment);

        bindViews();
        setupToolbar();
        getDataFromIntent();
        fillData();
        setupButtons();
    }

    // ────────────────────────────────────────────────────────

    private void bindViews() {
        tvMaPhieu = findViewById(R.id.tvMaPhieu);
        tvStatusLabel = findViewById(R.id.tvStatusLabel);

        tvBacSiName = findViewById(R.id.tvBacSiName);
        tvBacSiSpec = findViewById(R.id.tvBacSiSpec);

        tvNgayKham = findViewById(R.id.tvNgayKham);
        tvGioKham = findViewById(R.id.tvGioKham);

        tvDiaChi = findViewById(R.id.tvDiaChi);
        tvLoaiKham = findViewById(R.id.tvLoaiKham);

        tvLoaiPhieu = findViewById(R.id.tvLoaiPhieu);
        tvTrieuChung = findViewById(R.id.tvTrieuChung);

        tvBenhNhanName = findViewById(R.id.tvBenhNhanName);
        tvBenhNhanSdt = findViewById(R.id.tvBenhNhanSdt);

        chipTrangThai = findViewById(R.id.chipTrangThai);
    }

    // ────────────────────────────────────────────────────────

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> {
                // về trang chủ
                finish();
            });
        }
    }

    // ────────────────────────────────────────────────────────

    private void getDataFromIntent() {
        Gson gson = new Gson();

        // API response
        String json = getIntent().getStringExtra("appointment_result");
        if (json != null) {
            data = gson.fromJson(json, AppointmentResponse.class);
        }

        // fallback tránh null
        if (data == null) data = new AppointmentResponse();
    }

    // ────────────────────────────────────────────────────────

    private void fillData() {

        // ===== API DATA =====
        if (data.maPhieuDatLich != 0) {
            tvMaPhieu.setText("Mã phiếu: #" + data.maPhieuDatLich);
        }

        // trạng thái
        setStatusUI(data.trangThaiPhieu);

        // bác sĩ
        tvBacSiName.setText("BS. " + safe(data.hoTenBacSi));
        tvBacSiSpec.setText(safe(data.chuyenKhoa));

        // thời gian
        tvNgayKham.setText(safe(data.ngayCuThe));
        tvGioKham.setText(
                safe(data.gioBatDau) + " – " +
                        safe(data.gioKetThuc) + " · " +
                        data.thoiLuongPhut + " phút"
        );

        // địa điểm
        tvLoaiKham.setText(mapLoaiPhieu(data.loaiPhieu));
        tvDiaChi.setText(safe(data.diaChiLamViec));

        // lý do khám
        tvLoaiPhieu.setText(mapLoaiPhieu(data.loaiPhieu));
        tvTrieuChung.setText(safe(data.trieuChungGhiChu));

        // bệnh nhân
        tvBenhNhanName.setText(safe(data.hoTenBenhNhan));
        tvBenhNhanSdt.setText("SĐT: " + safe(data.soDienThoaiBenhNhan));
    }

    // ────────────────────────────────────────────────────────

    private void setStatusUI(String status) {

        String text = "Chờ xác nhận";

        if (status == null) status = "CHO_XAC_NHAN";

        switch (status) {
            case "DA_XAC_NHAN":
                text = "Đã xác nhận";
                break;
            case "DA_HUY":
                text = "Đã huỷ";
                break;
            case "TU_CHOI":
                text = "Bị từ chối";
                break;
            default:
                text = "Chờ xác nhận";
        }

        tvStatusLabel.setText(text);
        chipTrangThai.setText(text);
    }

    // ────────────────────────────────────────────────────────

    private void setupButtons() {

        // Huỷ lịch
        MaterialButton btnHuyLich = findViewById(R.id.btnHuyLich);
        if (btnHuyLich != null) {
            btnHuyLich.setOnClickListener(v -> {
                // TODO: gọi API huỷ
                finish();
            });
        }

        // Nhắn tin
        MaterialButton btnNhanTin = findViewById(R.id.btnNhanTin);
        if (btnNhanTin != null) {
            btnNhanTin.setOnClickListener(v -> {
                // TODO: mở chat
            });
        }

        // Trang chủ
        MaterialButton btnTrangChu = findViewById(R.id.btnTrangChu);
        if (btnTrangChu != null) {
            btnTrangChu.setOnClickListener(v -> {
                Intent intent = new Intent(this, HomeFragment.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            });
        }
    }

    // ────────────────────────────────────────────────────────

    private String mapLoaiPhieu(String loai) {
        if (loai == null) return "Khám";

        switch (loai) {
            case "PHONGKHAM":
                return "Khám tại phòng khám";
            case "TAI_KHAM":
                return "Tái khám";
            case "YEU_CAU":
                return "Yêu cầu khác";
            default:
                return loai;
        }
    }

    private String safe(String s) {
        return s != null ? s : "";
    }
}