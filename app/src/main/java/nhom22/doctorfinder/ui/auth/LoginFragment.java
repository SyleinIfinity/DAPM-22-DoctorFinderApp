package nhom22.doctorfinder.ui.auth;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import nhom22.doctorfinder.R;
import nhom22.doctorfinder.UserActivity;

public class LoginFragment extends AuthFragment {

    private LoginViewModel mViewModel;

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
        mViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        TextView tvRegister = view.findViewById(R.id.tvRegister);
        if (tvRegister != null) {
            tvRegister.setOnClickListener(v -> navigateToRegister());
        }

        View btnLogin = view.findViewById(R.id.btnLogin);
        if (btnLogin != null) {
            btnLogin.setOnClickListener(v -> {
                Intent intent = new Intent(requireContext(), UserActivity.class);
                startActivity(intent);
                requireActivity().finish();
            });
        }
    }

}
