package nhom22.doctorfinder.ui.user.user_profile;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import de.hdodenhof.circleimageview.CircleImageView;
import nhom22.doctorfinder.R;
import nhom22.doctorfinder.ui.auth.LoginFragment;


public class AccountFragment extends Fragment {

    private CircleImageView ivAvatar;
    private TextView tvUserName, tvEmail, tvAppointmentCount, tvFavoriteCount;

    private LinearLayout btnLogout;

    public AccountFragment() {
        super(R.layout.fragment_profile);
    }

    public static AccountFragment newInstance() {
        return new AccountFragment();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);
        loadUserData();
        handleClick(view);
    }

    private void initView(View view) {
        ivAvatar = view.findViewById(R.id.ivAvatar);
        tvUserName = view.findViewById(R.id.tvUserName);
        tvEmail = view.findViewById(R.id.tvEmail);

        tvAppointmentCount = view.findViewById(R.id.tvAppointmentCount);
        tvFavoriteCount = view.findViewById(R.id.tvFavoriteCount);

        btnLogout = view.findViewById(R.id.btnLogout);
    }

    // ================= LOAD DATA =================
    private void loadUserData() {
        // TODO: sau này call API thật
        tvUserName.setText("Nguyễn Văn Huy");
        tvEmail.setText("huy.nguyen@gmail.com");

        tvAppointmentCount.setText("12");
        tvFavoriteCount.setText("5");
    }

    // ================= CLICK EVENTS =================
    private void handleClick(View view) {

        // Đăng xuất
        btnLogout.setOnClickListener(v -> logout());

        // Các menu
        view.findViewById(R.id.menuPersonalInfo).setOnClickListener(v ->
                showToast("Thông tin cá nhân"));

        view.findViewById(R.id.menuChangePassword).setOnClickListener(v ->
                showToast("Đổi mật khẩu"));

        view.findViewById(R.id.menuNotifications).setOnClickListener(v ->
                showToast("Thông báo"));

        view.findViewById(R.id.menuMedicalHistory).setOnClickListener(v ->
                showToast("Lịch sử khám"));

        view.findViewById(R.id.menuFavoriteDoctors).setOnClickListener(v ->
                showToast("Bác sĩ yêu thích"));

        view.findViewById(R.id.menuHealthProfile).setOnClickListener(v ->
                showToast("Hồ sơ sức khỏe"));

        view.findViewById(R.id.menuHelp).setOnClickListener(v ->
                showToast("Trợ giúp"));

        view.findViewById(R.id.menuPrivacy).setOnClickListener(v ->
                showToast("Điều khoản"));

        view.findViewById(R.id.btnEdit).setOnClickListener(v ->
                showToast("Chỉnh sửa hồ sơ"));

        view.findViewById(R.id.btnBack).setOnClickListener(v ->
                requireActivity().onBackPressed());
    }

    // ================= LOGOUT =================
    private void logout() {
        // Xóa token
        SharedPreferences prefs = requireContext()
                .getSharedPreferences("USER_PREF", getContext().MODE_PRIVATE);
        prefs.edit().clear().apply();

        Toast.makeText(getContext(), "Đã đăng xuất", Toast.LENGTH_SHORT).show();

        // Chuyển về Login
        Intent intent = new Intent(getActivity(), LoginFragment.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void showToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}