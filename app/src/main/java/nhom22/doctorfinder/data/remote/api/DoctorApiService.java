package nhom22.doctorfinder.data.remote.api;

import java.util.List;

import nhom22.doctorfinder.data.remote.dto.response.DoctorImageResponse;
import nhom22.doctorfinder.data.remote.dto.response.DoctorResponse;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface DoctorApiService {
    Call<List<DoctorResponse>> searchDoctors(
            @Query("keyword") String keyword,
            @Query("chuyenKhoa") String chuyenKhoa,
            @Query("diaChiLamViec") String diaChiLamViec,
            @Query("trangThaiHoSo") String trangThaiHoSo,
            @Query("limit") Integer limit,
            @Query("offset") Integer offset
    );
    @Multipart
    @POST("api/doctors/search-by-image")
    Call<List<DoctorImageResponse>> searchDoctorsByImage(
            @Part MultipartBody.Part image,
            @Part("limit") RequestBody limit
    );
}