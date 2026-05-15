package nhom22.doctorfinder.ui.user.booking.history;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import java.util.List;

import nhom22.doctorfinder.R;
import nhom22.doctorfinder.adapter.AppointmentAdapter;
import nhom22.doctorfinder.data.remote.api.ReviewApiService;
import nhom22.doctorfinder.data.remote.api.UserApiService;
import nhom22.doctorfinder.data.remote.client.RetrofitClient;
import nhom22.doctorfinder.data.remote.dto.request.ReviewRequest;
import nhom22.doctorfinder.data.remote.dto.response.AppointmentResponse;
import nhom22.doctorfinder.ui.user.schedule.ConfirmAppointmentActivity;
import nhom22.doctorfinder.utils.SharedPrefManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryFragment extends Fragment {

    private RecyclerView rvAppointments;
    private TabLayout    tabLayout;
    private TextView     tvTotalCount, tvDoneCount;
    private AppointmentAdapter adapter;
    private UserApiService   userApiService;
    private ReviewApiService reviewApiService;

    private String currentScope = "upcoming";

    public HistoryFragment() { /* constructor rỗng */ }

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    // ─── Lifecycle ────────────────────────────────────────────────────────────

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupRecyclerView();
        setupTabLayout();

        userApiService   = RetrofitClient.getClient().create(UserApiService.class);
        reviewApiService = RetrofitClient.getClient().create(ReviewApiService.class);

        loadAppointments();
    }

    // ─── Init ─────────────────────────────────────────────────────────────────

    private void initViews(View view) {
        rvAppointments = view.findViewById(R.id.rvAppointments);
        tabLayout      = view.findViewById(R.id.tabLayout);
        tvTotalCount   = view.findViewById(R.id.tvTotalCount);
        tvDoneCount    = view.findViewById(R.id.tvDoneCount);
    }

    // ─── RecyclerView ─────────────────────────────────────────────────────────

    private void setupRecyclerView() {
        adapter = new AppointmentAdapter(requireContext(), new AppointmentAdapter.OnAppointmentClickListener() {

            @Override
            public void onPrimaryClick(AppointmentResponse appointment, int position) {
                Intent intent = new Intent(requireContext(), ConfirmAppointmentActivity.class);
                intent.putExtra("appointment_result", new Gson().toJson(appointment));
                startActivity(intent);
            }

            @Override
            public void onSecondaryClick(AppointmentResponse appointment, int position) {
                if (appointment == null || appointment.maPhieuDatLich == 0) return;

                userApiService.cancelAppointment(appointment.maPhieuDatLich)
                        .enqueue(new Callback<AppointmentResponse>() {
                            @Override
                            public void onResponse(@NonNull Call<AppointmentResponse> call,
                                                   @NonNull Response<AppointmentResponse> response) {
                                if (!isAdded()) return;
                                if (response.isSuccessful() && response.body() != null) {
                                    appointment.trangThaiPhieu = "DA_HUY";
                                    adapter.notifyItemChanged(position);
                                    Toast.makeText(requireContext(), "Đã huỷ lịch", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(requireContext(), "Lỗi khi huỷ lịch", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<AppointmentResponse> call,
                                                  @NonNull Throwable t) {
                                if (!isAdded()) return;
                                Toast.makeText(requireContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            @Override
            public void onReviewClick(AppointmentResponse appointment, int position) {
                // Fragment chịu trách nhiệm mở dialog và gọi API
                showReviewDialog(appointment, position);
            }
        });

        rvAppointments.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvAppointments.setAdapter(adapter);
    }

    // ─── Tab layout ───────────────────────────────────────────────────────────

    private void setupTabLayout() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentScope = (tab.getPosition() == 0) ? "upcoming" : "history";
                loadAppointments();
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    // ─── Load appointments ────────────────────────────────────────────────────

    private void loadAppointments() {
        int maNguoiDung = SharedPrefManager.getInstance(requireContext()).getUserId();
        if (maNguoiDung == -1) {
            Toast.makeText(requireContext(), "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        userApiService.getAppointments(maNguoiDung, currentScope)
                .enqueue(new Callback<List<AppointmentResponse>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<AppointmentResponse>> call,
                                           @NonNull Response<List<AppointmentResponse>> response) {
                        if (!isAdded()) return;
                        if (response.isSuccessful() && response.body() != null) {
                            List<AppointmentResponse> list = response.body();
                            adapter.submitList(list);

                            if (currentScope.equals("upcoming")) {
                                tvTotalCount.setText(String.valueOf(list.size()));
                            } else {
                                tvDoneCount.setText(String.valueOf(list.size()));
                            }
                        } else {
                            Toast.makeText(requireContext(), "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<AppointmentResponse>> call,
                                          @NonNull Throwable t) {
                        if (!isAdded()) return;
                        Toast.makeText(requireContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // ─── Review dialog ────────────────────────────────────────────────────────

    /**
     * Hiển thị AlertDialog để người dùng đánh giá bác sĩ.
     * Dialog gồm: RatingBar (1–5 sao) + EditText nhập nội dung.
     * Gọi API POST /api/reviews – không cần token.
     */
    private void showReviewDialog(AppointmentResponse appointment, int position) {
        if (!isAdded() || getContext() == null) return;

        // Inflate dialog view từ layout
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_review, null);

        RatingBar ratingBar     = dialogView.findViewById(R.id.ratingBar);
        EditText  etContent     = dialogView.findViewById(R.id.etReviewContent);
        Button    btnSubmit     = dialogView.findViewById(R.id.btnSubmitReview);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(true)
                .create();

        // Bo tròn dialog (tùy chọn)
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        btnSubmit.setOnClickListener(v -> {
            float rating  = ratingBar.getRating();
            String content = etContent.getText().toString().trim();

            // ── Validate ──────────────────────────────────────────────────────
            if (rating <= 0) {
                Toast.makeText(requireContext(), "Vui lòng chọn số sao", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(content)) {
                etContent.setError("Vui lòng nhập nội dung đánh giá");
                etContent.requestFocus();
                return;
            }

            // ── Gọi API ───────────────────────────────────────────────────────
            int maNguoiDung = SharedPrefManager.getInstance(requireContext()).getUserId();
            ReviewRequest request = new ReviewRequest(
                    maNguoiDung,
                    appointment.maBacSi,
                    (int) rating,
                    content
            );

            btnSubmit.setEnabled(false);

            reviewApiService.submitReview(request).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call,
                                       @NonNull Response<Void> response) {
                    if (!isAdded()) return;
                    dialog.dismiss();

                    if (response.isSuccessful()) {
                        Toast.makeText(requireContext(), "Đánh giá thành công", Toast.LENGTH_SHORT).show();

                        appointment.daDanhGia = true;
                        adapter.notifyItemChanged(position);

                    } else {
                        try {
                            String errorBody = response.errorBody() != null
                                    ? response.errorBody().string()
                                    : "";

                            if (errorBody.contains("Ban da danh gia bac si nay")) {
                                Toast.makeText(requireContext(), "Bạn đã đánh giá bác sĩ này rồi", Toast.LENGTH_SHORT).show();

                                // 🔥 Fix UI ngay lập tức
                                appointment.daDanhGia = true;
                                adapter.notifyItemChanged(position);

                            } else {
                                Toast.makeText(requireContext(), "Gửi đánh giá thất bại", Toast.LENGTH_SHORT).show();
                                btnSubmit.setEnabled(true);
                            }

                        } catch (Exception e) {
                            Toast.makeText(requireContext(), "Lỗi xử lý phản hồi", Toast.LENGTH_SHORT).show();
                            btnSubmit.setEnabled(true);
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    if (!isAdded()) return;
                    dialog.dismiss();
                    Toast.makeText(requireContext(), "Gửi đánh giá thất bại", Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }
}