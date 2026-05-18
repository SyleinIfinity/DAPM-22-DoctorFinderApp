package nhom22.doctorfinder.data.remote.api;

import java.util.List;

import nhom22.doctorfinder.data.remote.dto.request.CreateConversationRequest;
import nhom22.doctorfinder.data.remote.dto.request.SendMessageRequest;
import nhom22.doctorfinder.data.remote.dto.response.ConversationResponse;
import nhom22.doctorfinder.data.remote.dto.response.MessageResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ConversationApiService {

    @POST("api/conversations")
    Call<ConversationResponse> createConversation(@Body CreateConversationRequest body);

    @GET("api/conversations/{id}/messages")
    Call<List<MessageResponse>> getMessages(
            @Path("id") int maCuocHoiThoai,
            @Query("limit") int limit,
            @Query("before") String before
    );

    @POST("api/conversations/{id}/messages")
    Call<MessageResponse> sendMessage(
            @Path("id") int maCuocHoiThoai,
            @Body SendMessageRequest body
    );
}
