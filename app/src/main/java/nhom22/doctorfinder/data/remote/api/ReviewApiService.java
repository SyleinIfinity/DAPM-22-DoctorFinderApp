package nhom22.doctorfinder.data.remote.api;

import nhom22.doctorfinder.data.remote.dto.request.ReviewRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Retrofit API service cho chức năng đánh giá bác sĩ.
 * ⚠️ Không dùng Authorization header – API không yêu cầu token.
 */
public interface ReviewApiService {

    /**
     * Gửi đánh giá bác sĩ.
     * POST /api/reviews
     *
     * @param body {@link ReviewRequest} chứa maNguoiDung, maBacSi, soSao, noiDung
     * @return Call<Void> – chỉ cần kiểm tra response.isSuccessful()
     */
    @POST("api/reviews")
    Call<Void> submitReview(@Body ReviewRequest body);
}
