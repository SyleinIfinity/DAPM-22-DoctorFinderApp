package nhom22.doctorfinder.ui.user.booking.history;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import nhom22.doctorfinder.data.remote.api.UserApiService;
import nhom22.doctorfinder.data.remote.client.RetrofitClient;
import nhom22.doctorfinder.data.remote.dto.response.AppointmentResponse;
import nhom22.doctorfinder.ui.user.schedule.ConfirmAppointmentActivity;
import nhom22.doctorfinder.utils.SharedPrefManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryFragment extends Fragment {

    private RecyclerView rvAppointments;
    private TabLayout tabLayout;
    private TextView tvTotalCount, tvDoneCount;
    private AppointmentAdapter adapter;
    private UserApiService apiService;

    private String currentScope = "upcoming";

    public HistoryFragment() {
        // constructor rỗng
    }

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

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

        apiService = RetrofitClient.getClient().create(UserApiService.class);

        loadAppointments();
    }

    private void initViews(View view) {
        rvAppointments = view.findViewById(R.id.rvAppointments);
        tabLayout = view.findViewById(R.id.tabLayout);
        tvTotalCount = view.findViewById(R.id.tvTotalCount);
        tvDoneCount = view.findViewById(R.id.tvDoneCount);
    }

    private void setupRecyclerView() {
        adapter = new AppointmentAdapter(requireContext(), new AppointmentAdapter.OnAppointmentClickListener() {
            @Override
            public void onPrimaryClick(AppointmentResponse appointment) {
                Intent intent = new Intent(requireContext(), ConfirmAppointmentActivity.class);
                intent.putExtra("appointment_result", new Gson().toJson(appointment));
                startActivity(intent);
            }

            @Override
            public void onSecondaryClick(AppointmentResponse appointment) {
                // TODO: call cancel API
                Toast.makeText(requireContext(), "Cancel API not implemented yet", Toast.LENGTH_SHORT).show();
            }
        });
        rvAppointments.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvAppointments.setAdapter(adapter);
    }

    private void setupTabLayout() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    currentScope = "upcoming";
                } else if (tab.getPosition() == 1) {
                    currentScope = "history";
                }
                loadAppointments();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void loadAppointments() {
        int maNguoiDung = SharedPrefManager.getInstance(requireContext()).getUserId();
        if (maNguoiDung == -1) {
            Toast.makeText(requireContext(), "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        apiService.getAppointments(maNguoiDung, currentScope).enqueue(new Callback<List<AppointmentResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<AppointmentResponse>> call, @NonNull Response<List<AppointmentResponse>> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    List<AppointmentResponse> list = response.body();
                    adapter.submitList(list);

                    // Optional: update counts if needed based on list size, although not specified in the prompt
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
            public void onFailure(@NonNull Call<List<AppointmentResponse>> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}