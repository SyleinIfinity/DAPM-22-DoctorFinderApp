package nhom22.doctorfinder.ui.auth;

import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import nhom22.doctorfinder.R;

public class RegisterFragment extends AuthFragment {

    private RegisterViewModel mViewModel;
    private TextView tabMember, tabDoctor, tvLogin;
    private LinearLayout layoutDoctorInfo;
    private Button btnRegister;
    private View btnBack;
    private boolean isDoctorMode = false;

    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        
        btnBack = view.findViewById(R.id.btnBack);
        tabMember = view.findViewById(R.id.tabMember);
        tabDoctor = view.findViewById(R.id.tabDoctor);
        layoutDoctorInfo = view.findViewById(R.id.layoutDoctorInfo);
        btnRegister = view.findViewById(R.id.btnRegister);
        tvLogin = view.findViewById(R.id.tvLogin);

        if (tvLogin != null) {
            tvLogin.setText(HtmlCompat.fromHtml("Đã có tài khoản? <font color=\"#1D9E75\"><b>Đăng nhập</b></font>", HtmlCompat.FROM_HTML_MODE_LEGACY));
        }

        tabMember.setOnClickListener(v -> {
            isDoctorMode = false;
            layoutDoctorInfo.setVisibility(View.GONE);
            tabMember.setBackgroundResource(R.drawable.bg_tab_active);
            tabMember.setTextColor(ContextCompat.getColor(requireContext(), R.color.teal_primary));
            tabDoctor.setBackgroundResource(android.R.color.transparent);
            tabDoctor.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_muted));
        });

        tabDoctor.setOnClickListener(v -> {
            isDoctorMode = true;
            layoutDoctorInfo.setVisibility(View.VISIBLE);
            tabDoctor.setBackgroundResource(R.drawable.bg_tab_active);
            tabDoctor.setTextColor(ContextCompat.getColor(requireContext(), R.color.teal_primary));
            tabMember.setBackgroundResource(android.R.color.transparent);
            tabMember.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_muted));
        });

        btnRegister.setOnClickListener(v -> {
            // Basic UI interaction
            if (isDoctorMode) {
                Toast.makeText(requireContext(), "Đăng ký Bác sĩ...", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Đăng ký Thành viên...", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> navigateToLogin());
        }
        if (tvLogin != null) {
            tvLogin.setOnClickListener(v -> navigateToLogin());
        }
    }
}
