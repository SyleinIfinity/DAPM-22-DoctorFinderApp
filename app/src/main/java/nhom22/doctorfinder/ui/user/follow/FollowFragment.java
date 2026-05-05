package nhom22.doctorfinder.ui.user.follow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import nhom22.doctorfinder.R;
import nhom22.doctorfinder.adapter.FollowAdapter;
import nhom22.doctorfinder.data.remote.api.FollowApiService;
import nhom22.doctorfinder.data.remote.client.RetrofitClient;
import nhom22.doctorfinder.data.remote.dto.response.FollowDoctorItem;
import nhom22.doctorfinder.utils.SharedPrefManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Fragment hiển thị danh sách bác sĩ mà người dùng đã theo dõi.
 *
 * Gọi GET /api/follows?maNguoiDung=...
 * Sử dụng FollowAdapter + RecyclerView.
 */
public class FollowFragment extends Fragment {

    // ─── Views ────────────────────────────────────────────────────────────────
    private RecyclerView recyclerView;
    private ProgressBar  progressBar;
    private TextView     tvEmpty;

    // ─── Adapter & API ────────────────────────────────────────────────────────
    private FollowAdapter    adapter;
    private FollowApiService followApi;
    private Call<List<FollowDoctorItem>> pendingCall;

    // ─── Factory ──────────────────────────────────────────────────────────────

    public static FollowFragment newInstance() {
        return new FollowFragment();
    }

    // ─── Lifecycle ────────────────────────────────────────────────────────────

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_follow, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupRecyclerView();
        followApi = RetrofitClient.getClient().create(FollowApiService.class);
        loadFollowedDoctors();
    }

    @Override
    public void onDestroyView() {
        // Huỷ request đang chờ để tránh memory leak
        if (pendingCall != null) {
            pendingCall.cancel();
            pendingCall = null;
        }
        super.onDestroyView();
    }

    // ─── Init ─────────────────────────────────────────────────────────────────

    private void initViews(@NonNull View root) {
        recyclerView = root.findViewById(R.id.recyclerView);
        progressBar  = root.findViewById(R.id.progressBar);
        tvEmpty      = root.findViewById(R.id.tvEmpty);
    }

    private void setupRecyclerView() {
        adapter = new FollowAdapter(requireContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
    }

    // ─── API call ─────────────────────────────────────────────────────────────

    private void loadFollowedDoctors() {
        SharedPrefManager prefs = SharedPrefManager.getInstance(requireContext());
        int userId = prefs.getUserId();

        if (userId < 0) {
            showEmpty("Vui lòng đăng nhập để xem danh sách theo dõi.");
            return;
        }

        showLoading(true);

        pendingCall = followApi.getFollowedDoctors( userId);
        pendingCall.enqueue(new Callback<List<FollowDoctorItem>>() {

            @Override
            public void onResponse(@NonNull Call<List<FollowDoctorItem>> call,
                                   @NonNull Response<List<FollowDoctorItem>> response) {
                if (!isAdded()) return; // Fragment đã bị detach
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<FollowDoctorItem> list = response.body();
                    if (list.isEmpty()) {
                        showEmpty("Bạn chưa theo dõi bác sĩ nào.");
                    } else {
                        showList(list);
                    }
                } else {
                    showEmpty("Không thể tải danh sách. Vui lòng thử lại.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<FollowDoctorItem>> call,
                                  @NonNull Throwable t) {
                if (!isAdded()) return;
                showLoading(false);
                if (!call.isCanceled()) {
                    showEmpty("Lỗi kết nối: " + t.getMessage());
                    Toast.makeText(requireContext(),
                            "Không kết nối được server",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // ─── UI helpers ───────────────────────────────────────────────────────────

    private void showLoading(boolean loading) {
        if (progressBar == null) return;
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(loading ? View.GONE : View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);
    }

    private void showList(@NonNull List<FollowDoctorItem> list) {
        recyclerView.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);
        adapter.submitList(list);
    }

    private void showEmpty(@NonNull String message) {
        recyclerView.setVisibility(View.GONE);
        tvEmpty.setVisibility(View.VISIBLE);
        tvEmpty.setText(message);
    }
}
