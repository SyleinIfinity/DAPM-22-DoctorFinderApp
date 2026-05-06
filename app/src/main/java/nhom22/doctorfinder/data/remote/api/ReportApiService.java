package nhom22.doctorfinder.data.remote.api;

import java.util.List;

import nhom22.doctorfinder.data.remote.dto.response.TopDoctor;
import nhom22.doctorfinder.data.remote.dto.response.TopKeyword;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Retrofit API interface for admin report endpoints.
 *
 * Endpoints:
 *   GET /api/admin/reports/top-search-keywords → danh sách từ khóa tìm kiếm phổ biến
 *   GET /api/admin/reports/top-doctors         → danh sách bác sĩ được đặt lịch nhiều nhất
 */
public interface ReportApiService {

    /**
     * Lấy danh sách từ khóa tìm kiếm phổ biến nhất.
     *
     * @param from  Thời điểm bắt đầu, định dạng yyyy-MM-ddTHH:mm:ss
     * @param to    Thời điểm kết thúc, định dạng yyyy-MM-ddTHH:mm:ss
     * @param limit Số lượng từ khóa cần lấy
     */
    @GET("api/admin/reports/top-search-keywords")
    Call<List<TopKeyword>> getTopSearchKeywords(
            @Query("from") String from,
            @Query("to") String to,
            @Query("limit") int limit
    );

    /**
     * Lấy danh sách bác sĩ hàng đầu theo tiêu chí.
     *
     * @param metric Tiêu chí xếp hạng (dùng "BOOKING")
     * @param limit  Số lượng bác sĩ cần lấy
     */
    @GET("api/admin/reports/top-doctors")
    Call<List<TopDoctor>> getTopDoctors(
            @Query("metric") String metric,
            @Query("limit") int limit
    );
}
