package nhom22.doctorfinder.ui.auth;

import android.content.Context;
import android.content.SharedPreferences;

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
                if (response.isSuccessful() && response.body() != null) {

                    LoginResponse data = response.body();

                    if (data.authenticated) {
                        loginResult.postValue(data); // 👉 chỉ trả data về Activity
                    } else {
                        error.postValue(data.message);
                    }

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