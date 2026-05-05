package nhom22.doctorfinder.data.remote.api;

import java.util.List;

import nhom22.doctorfinder.data.remote.dto.response.DoctorResponse;
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


}