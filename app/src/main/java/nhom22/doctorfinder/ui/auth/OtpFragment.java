package nhom22.doctorfinder.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import nhom22.doctorfinder.R;
import nhom22.doctorfinder.data.remote.dto.DoctorInfo;
import nhom22.doctorfinder.data.remote.dto.UserInfo;

public class OtpFragment extends Fragment {

    private RegisterViewModel viewModel;
    private Bundle args;

    EditText otp1, otp2, otp3, otp4, otp5, otp6;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_otp, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
        args = getArguments() != null ? getArguments() : new Bundle();

        otp1 = view.findViewById(R.id.otp1);
        otp2 = view.findViewById(R.id.otp2);
        otp3 = view.findViewById(R.id.otp3);
        otp4 = view.findViewById(R.id.otp4);
        otp5 = view.findViewById(R.id.otp5);
        otp6 = view.findViewById(R.id.otp6);

        view.findViewById(R.id.btnVerify).setOnClickListener(v -> {
            String otp = otp1.getText().toString()
                    + otp2.getText().toString()
                    + otp3.getText().toString()
                    + otp4.getText().toString()
                    + otp5.getText().toString()
                    + otp6.getText().toString();

            if (otp.length() < 6) {
                Toast.makeText(requireContext(), "Nhập đủ 6 số OTP", Toast.LENGTH_SHORT).show();
                return;
            }
            viewModel.verifyOtp(args.getString("email", ""), otp);
        });

        // OTP verified → call register
        viewModel.otpToken.observe(getViewLifecycleOwner(), token -> {
            if (token != null) doRegister(token);
        });

        // Register success → go to login
        viewModel.registerSuccess.observe(getViewLifecycleOwner(), ok -> {
            if (Boolean.TRUE.equals(ok)) {
                Toast.makeText(requireContext(), "Đăng ký thành công! Hãy đăng nhập.", Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_otpFragment_to_loginFragment, null,
                                new NavOptions.Builder()
                                        .setPopUpTo(R.id.loginFragment, true)
                                        .build());
            }
        });

        viewModel.error.observe(getViewLifecycleOwner(), err -> {
            if (err != null) Toast.makeText(requireContext(), err, Toast.LENGTH_SHORT).show();
        });
    }

    private void doRegister(String otpProofToken) {
        UserInfo userInfo = new UserInfo();
        userInfo.tenDangNhap    = args.getString("tenDangNhap", "");
        userInfo.matKhau        = args.getString("matKhau", "");
        userInfo.xacNhanMatKhau = args.getString("xacNhanMatKhau", "");
        userInfo.hoLot          = args.getString("hoLot", "");
        userInfo.ten            = args.getString("ten", "");
        userInfo.soDienThoai    = args.getString("soDienThoai", "");
        userInfo.email          = args.getString("email", "");
        userInfo.cccd           = args.getString("cccd", "");
        userInfo.anhDaiDien     = "";

        boolean isDoctor = args.getBoolean("isDoctor", false);

        if (isDoctor) {
            DoctorInfo doctorInfo = new DoctorInfo();
            doctorInfo.chuyenKhoa         = args.getString("chuyenKhoa", "");
            doctorInfo.maChungChiHanhNghe = args.getString("maChungChiHanhNghe", "");
            doctorInfo.tenCoSoYTe         = args.getString("tenCoSoYTe", "");
            doctorInfo.trinhDoChuyenMon   = args.getString("trinhDoChuyenMon", "");
            doctorInfo.loaiHinhBacSi      = "";
            doctorInfo.diaChiLamViec      = "";
            doctorInfo.moTaBanThan        = "";
            viewModel.registerDoctor(userInfo, doctorInfo, otpProofToken);
        } else {
            viewModel.registerUser(userInfo, otpProofToken);
        }
    }
}