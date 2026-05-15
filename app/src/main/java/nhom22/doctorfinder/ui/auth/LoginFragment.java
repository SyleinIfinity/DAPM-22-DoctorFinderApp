package nhom22.doctorfinder.ui.auth;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import nhom22.doctorfinder.R;
import nhom22.doctorfinder.UserActivity;
import nhom22.doctorfinder.utils.SharedPrefManager;

public class LoginFragment extends AuthFragment {

    private LoginViewModel mViewModel;
    private EditText etEmail, etPassword;
    private View btnLogin;
    public static LoginFragment newInstance() {
        return new LoginFragment();
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

          return inflater.inflate(R.layout.fragment_login, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);

        // 👉 Ánh xạ view ở đây
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        btnLogin = view.findViewById(R.id.btnLogin);

        TextView tvRegister = view.findViewById(R.id.tvRegister);

        // 👉 Click register
        tvRegister.setOnClickListener(v -> navigateToRegister());

        // 👉 Click login
        btnLogin.setOnClickListener(v -> {
            String username = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            mViewModel.login(username, password);
        });

        // 👉 Observe kết quả login
        mViewModel.loginResult.observe(getViewLifecycleOwner(), response -> {
            if (response != null && Boolean.TRUE.equals(response.authenticated)) {

                SharedPrefManager prefs = SharedPrefManager.getInstance(requireContext());

                if (response.maNguoiDung != null) {
                    prefs.saveUserId(response.maNguoiDung);
                }

                prefs.saveRole(response.vaiTro);

//                Toast.makeText(requireContext(),
//                        "UserID: " + response.maNguoiDung,
//                        Toast.LENGTH_SHORT).show();

                startActivity(new Intent(requireContext(), UserActivity.class));
                requireActivity().finish();
            }
        });

        mViewModel.error.observe(getViewLifecycleOwner(), err -> {
            Toast.makeText(requireContext(), err, Toast.LENGTH_SHORT).show();
        });
    }

}
