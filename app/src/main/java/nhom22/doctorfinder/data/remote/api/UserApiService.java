package nhom22.doctorfinder.data.remote.api;

import nhom22.doctorfinder.data.remote.dto.request.AppointmentRequest;
import nhom22.doctorfinder.data.remote.dto.response.AppointmentResponse;
import nhom22.doctorfinder.data.remote.dto.response.UserProfileResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserApiService {

    /**
     * Lấy thông tin chi tiết người dùng theo maNguoiDung.
     *
     * @param maNguoiDung ID người dùng (lấy từ SharedPrefManager)
     */
    @GET("api/users/{maNguoiDung}")
    Call<UserProfileResponse> getUserProfile(@Path("maNguoiDung") int maNguoiDung);

    /**
     * Tạo phiếu đặt lịch mới.
     *
     * @param body AppointmentRequest với maNguoiDung, maChiTiet, loaiPhieu, trieuChungGhiChu
     */
    @POST("api/appointments")
    Call<AppointmentResponse> createAppointment(@Body AppointmentRequest body);
}
