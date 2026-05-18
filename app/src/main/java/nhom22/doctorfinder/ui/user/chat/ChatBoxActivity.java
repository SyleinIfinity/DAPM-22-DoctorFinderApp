package nhom22.doctorfinder.ui.user.chat;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nhom22.doctorfinder.R;
import nhom22.doctorfinder.adapter.ChatMessageAdapter;
import nhom22.doctorfinder.data.remote.api.ConversationApiService;
import nhom22.doctorfinder.data.remote.client.RetrofitClient;
import nhom22.doctorfinder.data.remote.dto.request.SendMessageRequest;
import nhom22.doctorfinder.data.remote.dto.response.MessageResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatBoxActivity extends AppCompatActivity {

    private static final int POLL_INTERVAL_MS = 5000;
    private static final int MESSAGE_PAGE_LIMIT = 50;

    private int maCuocHoiThoai = -1;
    private int maTaiKhoanNguoiDung = -1;
    private String doctorName;
    private String doctorAvatarUrl;

    private RecyclerView rvChat;
    private ProgressBar progressLoadingMessages;
    private EditText etMessage;
    private ImageButton btnSend;
    private TextView tvDoctorName;
    private TextView tvStatus;
    private ImageView ivHeaderAvatar;

    private ConversationApiService conversationApi;
    private ChatMessageAdapter adapter;

    private final Handler pollHandler = new Handler(Looper.getMainLooper());
    private final Runnable pollRunnable = this::runPollCycle;
    private boolean isPolling;
    private boolean pollRequestInFlight;

    private final Set<Integer> knownMessageIds = new HashSet<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbox);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        if (!readIntentAndValidate()) {
            return;
        }

        conversationApi = RetrofitClient.getClient().create(ConversationApiService.class);

        bindViews();
        setupRecycler();
        loadMessagesInitial();
    }

    private boolean readIntentAndValidate() {
        maCuocHoiThoai = getIntent().getIntExtra("maCuocHoiThoai", -1);
        maTaiKhoanNguoiDung = getIntent().getIntExtra("maTaiKhoanNguoiDung", -1);
        doctorName = getIntent().getStringExtra("doctor_name");
        doctorAvatarUrl = getIntent().getStringExtra("doctor_avatar_url");

        if (maCuocHoiThoai <= 0) {
            Toast.makeText(this, "Không có cuộc trò chuyện hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return false;
        }
        if (maTaiKhoanNguoiDung < 0) {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            finish();
            return false;
        }
        return true;
    }

    private void bindViews() {
        ImageButton btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        tvDoctorName = findViewById(R.id.tvDoctorName);
        if (tvDoctorName != null) {
            String name = (doctorName != null && !doctorName.isEmpty()) ? doctorName : "Bác sĩ";
            tvDoctorName.setText(name);
        }

        tvStatus = findViewById(R.id.tvStatus);
        if (tvStatus != null) {
            tvStatus.setText("Đang trò chuyện");
        }

        ivHeaderAvatar = findViewById(R.id.ivAvatar);
        loadAvatar(ivHeaderAvatar, doctorAvatarUrl);

        rvChat = findViewById(R.id.rvChat);
        progressLoadingMessages = findViewById(R.id.progressLoadingMessages);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        if (btnSend != null) {
            btnSend.setOnClickListener(v -> sendCurrentMessage());
        }

        setupSuggestionChips();
    }

    private void setupSuggestionChips() {
        bindChip(R.id.chipDatLich, "Tôi muốn đặt lịch khám");
        bindChip(R.id.chipXemHoSo, "Cho tôi xem hồ sơ bác sĩ");
        bindChip(R.id.chipGiaKham, "Giá khám là bao nhiêu?");
    }

    private void bindChip(int viewId, String messageText) {
        View chip = findViewById(viewId);
        if (chip == null) return;
        chip.setOnClickListener(v -> sendSuggestion(messageText));
    }

    private void sendSuggestion(String text) {
        if (etMessage == null) return;
        etMessage.setText(text);
        sendCurrentMessage();
    }

    /**
     * Giống {@link nhom22.doctorfinder.ui.user.profile.DoctorProfileActivity#loadAvatar(String)} —
     * hỗ trợ data URI base64 và URL thường.
     */
    private void loadAvatar(@Nullable ImageView imageView, @Nullable String avatarUrl) {
        if (imageView == null) return;

        if (avatarUrl != null && avatarUrl.startsWith("data:image")) {
            int comma = avatarUrl.indexOf(',');
            if (comma > 0 && comma < avatarUrl.length() - 1) {
                String base64Data = avatarUrl.substring(comma + 1);
                byte[] bytes = android.util.Base64.decode(base64Data, android.util.Base64.DEFAULT);
                Glide.with(this)
                        .load(bytes)
                        .circleCrop()
                        .placeholder(R.drawable.ic_doctor_placeholder)
                        .into(imageView);
            } else {
                imageView.setImageResource(R.drawable.ic_doctor_placeholder);
            }
        } else if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Glide.with(this)
                    .load(avatarUrl)
                    .circleCrop()
                    .placeholder(R.drawable.ic_doctor_placeholder)
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.ic_doctor_placeholder);
        }
    }

    private void setupRecycler() {
        if (rvChat == null) return;
        LinearLayoutManager lm = new LinearLayoutManager(this);
        rvChat.setLayoutManager(lm);
        adapter = new ChatMessageAdapter(this, maTaiKhoanNguoiDung,
                doctorAvatarUrl != null ? doctorAvatarUrl : "");
        rvChat.setAdapter(adapter);
    }

    private void loadMessagesInitial() {
        if (progressLoadingMessages != null) {
            progressLoadingMessages.setVisibility(View.VISIBLE);
        }

        conversationApi.getMessages(maCuocHoiThoai, MESSAGE_PAGE_LIMIT, null)
                .enqueue(new Callback<List<MessageResponse>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<MessageResponse>> call,
                                           @NonNull Response<List<MessageResponse>> response) {
                        if (isFinishing()) return;
                        if (progressLoadingMessages != null) {
                            progressLoadingMessages.setVisibility(View.GONE);
                        }
                        if (response.isSuccessful() && response.body() != null) {
                            List<MessageResponse> list = new ArrayList<>(response.body());
                            ChatMessageAdapter.sortByMessageIdAscending(list);
                            knownMessageIds.clear();
                            for (MessageResponse m : list) {
                                knownMessageIds.add(m.maTinNhan);
                            }
                            if (adapter != null) {
                                adapter.addMessages(list);
                            }
                            scrollChatToBottom();
                        } else {
                            Toast.makeText(ChatBoxActivity.this,
                                    "Không tải được tin nhắn",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<MessageResponse>> call,
                                          @NonNull Throwable t) {
                        if (isFinishing()) return;
                        if (progressLoadingMessages != null) {
                            progressLoadingMessages.setVisibility(View.GONE);
                        }
                        Toast.makeText(ChatBoxActivity.this,
                                "Lỗi kết nối: " + (t.getMessage() != null ? t.getMessage() : ""),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void scrollChatToBottom() {
        if (rvChat == null || adapter == null) return;
        rvChat.post(() -> {
            int n = adapter.getItemCount();
            if (n > 0) {
                rvChat.scrollToPosition(n - 1);
            }
        });
    }

    private void sendCurrentMessage() {
        if (etMessage == null || btnSend == null) return;
        String text = etMessage.getText() != null ? etMessage.getText().toString().trim() : "";
        if (text.isEmpty()) return;

        btnSend.setEnabled(false);

        SendMessageRequest body = new SendMessageRequest(maTaiKhoanNguoiDung, text, "TEXT");
        conversationApi.sendMessage(maCuocHoiThoai, body)
                .enqueue(new Callback<MessageResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<MessageResponse> call,
                                           @NonNull Response<MessageResponse> response) {
                        if (isFinishing()) return;
                        if (response.isSuccessful() && response.body() != null) {
                            MessageResponse sent = response.body();
                            if (!knownMessageIds.contains(sent.maTinNhan)) {
                                knownMessageIds.add(sent.maTinNhan);
                                if (adapter != null) {
                                    adapter.appendMessage(sent);
                                }
                            }
                            if (etMessage != null) {
                                etMessage.setText("");
                            }
                            scrollChatToBottom();
                        } else {
                            Toast.makeText(ChatBoxActivity.this,
                                    "Không gửi được tin nhắn",
                                    Toast.LENGTH_SHORT).show();
                        }
                        if (btnSend != null) {
                            btnSend.setEnabled(true);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<MessageResponse> call,
                                          @NonNull Throwable t) {
                        if (isFinishing()) return;
                        Toast.makeText(ChatBoxActivity.this,
                                "Lỗi kết nối: " + (t.getMessage() != null ? t.getMessage() : ""),
                                Toast.LENGTH_SHORT).show();
                        if (btnSend != null) {
                            btnSend.setEnabled(true);
                        }
                    }
                });
    }

    private void runPollCycle() {
        if (!isPolling || isFinishing()) return;
        if (pollRequestInFlight) {
            scheduleNextPoll(POLL_INTERVAL_MS);
            return;
        }
        pollRequestInFlight = true;
        conversationApi.getMessages(maCuocHoiThoai, MESSAGE_PAGE_LIMIT, null)
                .enqueue(new Callback<List<MessageResponse>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<MessageResponse>> call,
                                           @NonNull Response<List<MessageResponse>> response) {
                        pollRequestInFlight = false;
                        if (!isPolling || isFinishing()) return;
                        if (response.isSuccessful() && response.body() != null) {
                            mergeNewMessagesFromServer(response.body());
                        }
                        scheduleNextPoll(POLL_INTERVAL_MS);
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<MessageResponse>> call,
                                          @NonNull Throwable t) {
                        pollRequestInFlight = false;
                        if (isPolling && !isFinishing()) {
                            scheduleNextPoll(POLL_INTERVAL_MS);
                        }
                    }
                });
    }

    private void mergeNewMessagesFromServer(@NonNull List<MessageResponse> fromServer) {
        List<MessageResponse> fresh = new ArrayList<>();
        for (MessageResponse m : fromServer) {
            if (!knownMessageIds.contains(m.maTinNhan)) {
                fresh.add(m);
            }
        }
        if (fresh.isEmpty()) return;
        ChatMessageAdapter.sortByMessageIdAscending(fresh);
        for (MessageResponse m : fresh) {
            knownMessageIds.add(m.maTinNhan);
            if (adapter != null) {
                adapter.appendMessage(m);
            }
        }
        scrollChatToBottom();
    }

    private void scheduleNextPoll(long delayMs) {
        pollHandler.removeCallbacks(pollRunnable);
        if (isPolling && !isFinishing()) {
            pollHandler.postDelayed(pollRunnable, delayMs);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isPolling = true;
        scheduleNextPoll(POLL_INTERVAL_MS);
    }

    @Override
    protected void onPause() {
        isPolling = false;
        pollHandler.removeCallbacks(pollRunnable);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        pollHandler.removeCallbacks(pollRunnable);
        super.onDestroy();
    }
}
