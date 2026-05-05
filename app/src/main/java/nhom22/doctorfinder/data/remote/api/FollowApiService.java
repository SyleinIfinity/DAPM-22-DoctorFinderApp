package nhom22.doctorfinder.data.remote.api;

import java.util.List;

import nhom22.doctorfinder.data.remote.dto.response.FollowDoctorItem;
import nhom22.doctorfinder.data.remote.dto.response.FollowResponse;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Retrofit API interface cho chức năng Theo dõi Bác sĩ.
 *
 * Endpoints:
 *   POST   /api/follows/{maBacSi}  → theo dõi bác sĩ
 *   DELETE /api/follows/{maBacSi}  → hủy theo dõi
 *   GET    /api/follows             → lấy danh sách bác sĩ đã theo dõi
 */
public interface FollowApiService {

    /**
     * Theo dõi một bác sĩ.
     *
     * @param maBacSi    ID bác sĩ cần theo dõi (path param)
     * @param maNguoiDung ID người dùng đang đăng nhập (query param)
     */
    @POST("api/follows/{maBacSi}")
    Call<FollowResponse> followDoctor(
            @Path("maBacSi") int maBacSi,
            @Query("maNguoiDung") int maNguoiDung
    );

    /**
     * Hủy theo dõi một bác sĩ.
     *
     * @param maBacSi    ID bác sĩ cần hủy theo dõi (path param)
     * @param maNguoiDung ID người dùng (query param)
     */
    @DELETE("api/follows/{maBacSi}")
    Call<FollowResponse> unfollowDoctor(
            @Path("maBacSi") int maBacSi,
            @Query("maNguoiDung") int maNguoiDung
    );

    /**
     * Lấy danh sách bác sĩ mà người dùng đã theo dõi.
     *
     * @param maNguoiDung ID người dùng (query param)
     */
    @GET("api/follows")
    Call<List<FollowDoctorItem>> getFollowedDoctors(
            @Query("maNguoiDung") int maNguoiDung
    );
}
