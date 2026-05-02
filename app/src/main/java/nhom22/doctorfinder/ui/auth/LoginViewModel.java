package nhom22.doctorfinder.ui.auth;

import androidx.lifecycle.ViewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import nhom22.doctorfinder.data.remote.api.AuthApiService;
import nhom22.doctorfinder.data.remote.client.RetrofitClient;
import nhom22.doctorfinder.data.remote.dto.request.LoginRequest;
import nhom22.doctorfinder.data.remote.dto.response.LoginResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends ViewModel {

    public MutableLiveData<LoginResponse> loginResult = new MutableLiveData<>();
    public MutableLiveData<String> error = new MutableLiveData<>();

    public void login(String username, String password) {
        AuthApiService api = RetrofitClient.getClient().create(AuthApiService.class);

        LoginRequest request = new LoginRequest(username, password);

        api.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    loginResult.postValue(response.body());
                } else {
                    error.postValue("Sai tài khoản hoặc mật khẩu");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                error.postValue("Lỗi kết nối server");
            }
        });
    }
}