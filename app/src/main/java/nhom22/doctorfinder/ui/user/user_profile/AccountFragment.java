package nhom22.doctorfinder.ui.user.user_profile;

import android.content.Intent;
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
import nhom22.doctorfinder.data.remote.api.UserApiService;
import nhom22.doctorfinder.data.remote.client.RetrofitClient;
import nhom22.doctorfinder.data.remote.dto.response.UserProfileResponse;
import nhom22.doctorfinder.ui.auth.AuthActivity;
import nhom22.doctorfinder.utils.SharedPrefManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountFragment extends Fragment {

    private CircleImageView ivAvatar;
    private TextView tvUserName, tvEmail, tvAppointmentCount, tvFavoriteCount;
    private LinearLayout btnLogout;

    private UserApiService userApiService;

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

        // Init API
        userApiService = RetrofitClient.getClient().create(UserApiService.class);

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

        int userId = SharedPrefManager
                .getInstance(requireContext())
                .getUserId();

        if (userId == -1) {
            showToast("Chưa đăng nhập");
            return;
        }

        userApiService.getUserProfile(userId)
                .enqueue(new Callback<UserProfileResponse>() {
                    @Override
                    public void onResponse(Call<UserProfileResponse> call,
                                           Response<UserProfileResponse> response) {

                        if (!isAdded()) return;

                        if (response.isSuccessful() && response.body() != null) {

                            UserProfileResponse user = response.body();

                            // Set dữ liệu
                            tvUserName.setText(
                                    user.hoTenDayDu != null ? user.hoTenDayDu : "Chưa cập nhật"
                            );

                            tvEmail.setText(
                                    user.email != null ? user.email : "Chưa cập nhật"
                            );

                            // Fake tạm stats (sau này call API)
                            tvAppointmentCount.setText("12");
                            tvFavoriteCount.setText("5");

                        } else {
                            showToast("Không tải được thông tin user");
                        }
                    }

                    @Override
                    public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                        if (!isAdded()) return;
                        showToast("Lỗi kết nối server");
                    }
                });
    }

    // ================= CLICK EVENTS =================
    private void handleClick(View view) {

        btnLogout.setOnClickListener(v -> logout());

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

        // Clear toàn bộ dữ liệu login
        SharedPrefManager.getInstance(requireContext()).clear();

        Toast.makeText(getContext(), "Đã đăng xuất", Toast.LENGTH_SHORT).show();

        // Về màn hình login
        Intent intent = new Intent(getActivity(), AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void showToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}