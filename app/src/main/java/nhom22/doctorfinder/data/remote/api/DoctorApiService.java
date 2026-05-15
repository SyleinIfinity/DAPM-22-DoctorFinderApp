package nhom22.doctorfinder.data.remote.api;

import java.util.List;

import nhom22.doctorfinder.data.remote.dto.response.DoctorResponse;
import nhom22.doctorfinder.data.remote.dto.response.RatingSummaryResponse;
import nhom22.doctorfinder.data.remote.dto.response.ReviewItem;
import nhom22.doctorfinder.data.remote.dto.response.WorkingSlot;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DoctorApiService {

    @GET("api/doctors/search")
    Call<List<DoctorResponse>> searchDoctors(
            @Query("keyword") String keyword,
            @Query("chuyenKhoa") String chuyenKhoa,
            @Query("diaChiLamViec") String diaChiLamViec,
            @Query("trangThaiHoSo") String trangThaiHoSo,
            @Query("limit") Integer limit,
            @Query("offset") Integer offset
    );
    @GET("api/doctors/{id}")
    Call<DoctorResponse> getDoctorById(@Path("id") int id);

    /**
     * Lấy danh sách khung giờ khám còn trống / đã đặt của bác sĩ theo ngày.
     *
     * @param maBacSi ID bác sĩ
     * @param date    Ngày cần tra cứu, định dạng yyyy-MM-dd
     */
    @GET("api/doctors/{maBacSi}/working-slots")
    Call<List<WorkingSlot>> getWorkingSlots(
            @Path("maBacSi") int maBacSi,
            @Query("date") String date
    );

    /** Lấy danh sách đánh giá của bác sĩ (không cần token). */
    @GET("api/doctors/{maBacSi}/reviews")
    Call<List<ReviewItem>> getDoctorReviews(@Path("maBacSi") int maBacSi);

    /** Lấy tổng quan đánh giá của bác sĩ (không cần token). */
    @GET("api/doctors/{maBacSi}/rating-summary")
    Call<RatingSummaryResponse> getRatingSummary(@Path("maBacSi") int maBacSi);

}