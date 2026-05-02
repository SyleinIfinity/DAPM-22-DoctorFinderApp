package nhom22.doctorfinder.data.remote.api;

import java.util.List;

import nhom22.doctorfinder.data.remote.dto.response.DoctorResponse;
import nhom22.doctorfinder.model.Doctor;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DoctorApiService {

    @GET("api/doctors/search")
    Call<List<DoctorResponse>> searchDoctors(
            @Query("keyword") String keyword,
            @Query("limit") int limit,
            @Query("offset") int offset
    );
}