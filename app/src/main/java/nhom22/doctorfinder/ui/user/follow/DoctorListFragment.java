package nhom22.doctorfinder.ui.user.follow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import nhom22.doctorfinder.R;
import nhom22.doctorfinder.data.model.Doctor;
import nhom22.doctorfinder.ui.adapter.DoctorAdapter;

public class DoctorListFragment extends Fragment {

    public static DoctorListFragment newInstance() {
        return new DoctorListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_doctor_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewDoctors);
        View emptyState = view.findViewById(R.id.emptyState);

        // Dữ liệu mẫu — sau thay bằng ViewModel/Repository
        List<Doctor> doctorList = getDummyData();

        if (doctorList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);

            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            recyclerView.setAdapter(new DoctorAdapter(doctorList));
        }
    }

    private List<Doctor> getDummyData() {
        List<Doctor> list = new ArrayList<>();
        list.add(new Doctor("PGS.TS Nguyễn Anh", "Thần kinh", "4.9", "15 năm", "1.2 km", "Có lịch hôm nay"));
        list.add(new Doctor("TS. Trần Minh", "Tim mạch", "4.8", "12 năm", "2.5 km", "Đang online"));
        return list;
    }
}