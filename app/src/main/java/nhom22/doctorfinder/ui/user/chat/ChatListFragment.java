package nhom22.doctorfinder.ui.user.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import nhom22.doctorfinder.R;
import nhom22.doctorfinder.adapter.ConversationListAdapter;
import nhom22.doctorfinder.data.remote.api.ConversationApiService;
import nhom22.doctorfinder.data.remote.client.RetrofitClient;
import nhom22.doctorfinder.data.remote.dto.response.ConversationListItem;
import nhom22.doctorfinder.utils.SharedPrefManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Danh sách cuộc hội thoại — GET /api/conversations?maNguoiDung=...
 */
public class ChatListFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvEmpty;

    private ConversationListAdapter adapter;
    private ConversationApiService conversationApi;
    private Call<List<ConversationListItem>> pendingCall;

    public static ChatListFragment newInstance() {
        return new ChatListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_listchat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        SharedPrefManager prefs = SharedPrefManager.getInstance(requireContext());
        int userId = prefs.getUserId();
        adapter = new ConversationListAdapter(requireContext(), userId);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        conversationApi = RetrofitClient.getClient().create(ConversationApiService.class);
        loadConversations();
    }

    @Override
    public void onDestroyView() {
        if (pendingCall != null) {
            pendingCall.cancel();
            pendingCall = null;
        }
        super.onDestroyView();
    }

    private void initViews(@NonNull View root) {
        recyclerView = root.findViewById(R.id.rvChatList);
        progressBar = root.findViewById(R.id.progressBar);
        tvEmpty = root.findViewById(R.id.tvEmpty);
    }

    private void loadConversations() {
        SharedPrefManager prefs = SharedPrefManager.getInstance(requireContext());
        int userId = prefs.getUserId();

        if (userId < 0) {
            showEmpty("Vui lòng đăng nhập để xem tin nhắn.");
            return;
        }

        showLoading(true);

        pendingCall = conversationApi.getConversations(userId);
        pendingCall.enqueue(new Callback<List<ConversationListItem>>() {
            @Override
            public void onResponse(@NonNull Call<List<ConversationListItem>> call,
                                   @NonNull Response<List<ConversationListItem>> response) {
                if (!isAdded()) return;
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<ConversationListItem> list = response.body();
                    if (list.isEmpty()) {
                        showEmpty("Chưa có cuộc trò chuyện.");
                    } else {
                        showList(list);
                    }
                } else {
                    showEmpty("Không tải được danh sách. Vui lòng thử lại.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ConversationListItem>> call,
                                  @NonNull Throwable t) {
                if (!isAdded()) return;
                showLoading(false);
                if (!call.isCanceled()) {
                    showEmpty("Lỗi kết nối: " + (t.getMessage() != null ? t.getMessage() : ""));
                    Toast.makeText(requireContext(), "Không kết nối được server", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showLoading(boolean loading) {
        if (progressBar != null) {
            progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        }
        if (recyclerView != null) {
            recyclerView.setVisibility(loading ? View.GONE : View.VISIBLE);
        }
        if (tvEmpty != null) {
            tvEmpty.setVisibility(View.GONE);
        }
    }

    private void showList(@NonNull List<ConversationListItem> list) {
        if (recyclerView != null) recyclerView.setVisibility(View.VISIBLE);
        if (tvEmpty != null) tvEmpty.setVisibility(View.GONE);
        adapter.submitList(list);
    }

    private void showEmpty(@NonNull String message) {
        if (recyclerView != null) recyclerView.setVisibility(View.GONE);
        if (tvEmpty != null) {
            tvEmpty.setVisibility(View.VISIBLE);
            tvEmpty.setText(message);
        }
    }
}
