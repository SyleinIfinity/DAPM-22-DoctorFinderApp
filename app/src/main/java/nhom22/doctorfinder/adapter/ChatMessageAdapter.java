package nhom22.doctorfinder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import nhom22.doctorfinder.R;
import nhom22.doctorfinder.data.remote.dto.response.MessageResponse;

/**
 * Chat: tin gửi / tin nhận theo {@code maTaiKhoanGui} so với người dùng hiện tại.
 */
public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;

    private final Context context;
    private final int maTaiKhoanNguoiDung;
    private final String doctorAvatarUrl;
    private final ArrayList<MessageResponse> messages = new ArrayList<>();

    public ChatMessageAdapter(Context context, int maTaiKhoanNguoiDung, String doctorAvatarUrl) {
        this.context = context.getApplicationContext();
        this.maTaiKhoanNguoiDung = maTaiKhoanNguoiDung;
        this.doctorAvatarUrl = doctorAvatarUrl;
    }

    public void addMessages(@NonNull List<MessageResponse> list) {
        messages.clear();
        messages.addAll(list);
        notifyDataSetChanged();
    }

    public void appendMessage(@NonNull MessageResponse message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_SENT) {
            View v = inflater.inflate(R.layout.item_chat_sent, parent, false);
            return new SentHolder(v);
        }
        View v = inflater.inflate(R.layout.item_chat_received, parent, false);
        return new ReceivedHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageResponse m = messages.get(position);
        if (holder instanceof SentHolder) {
            ((SentHolder) holder).bind(m);
        } else if (holder instanceof ReceivedHolder) {
            ((ReceivedHolder) holder).bind(m);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        MessageResponse m = messages.get(position);
        if (m.maTaiKhoanGui == maTaiKhoanNguoiDung) {
            return VIEW_TYPE_SENT;
        }
        return VIEW_TYPE_RECEIVED;
    }

    public static void sortByMessageIdAscending(@NonNull List<MessageResponse> list) {
        Collections.sort(list, Comparator.comparingInt(a -> a.maTinNhan));
    }

    private String formatTime(String thoiGianGui) {
        if (thoiGianGui == null || thoiGianGui.length() < 16) return "";
        return thoiGianGui.substring(11, 16);
    }

    private class SentHolder extends RecyclerView.ViewHolder {
        final TextView tvMessage;
        final TextView tvTime;

        SentHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
        }

        void bind(MessageResponse m) {
            if (tvMessage != null) {
                String text = m.noiDungTinNhan;
                if (text == null) text = "";
                tvMessage.setText(text);
            }
            if (tvTime != null) {
                tvTime.setText(formatTime(m.thoiGianGui));
            }
        }
    }

    private class ReceivedHolder extends RecyclerView.ViewHolder {
        final TextView tvMessage;
        final TextView tvTime;
        final ImageView ivAvatar;

        ReceivedHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
        }

        void bind(MessageResponse m) {
            if (tvMessage != null) {
                String text = m.noiDungTinNhan;
                if (text == null) text = "";
                tvMessage.setText(text);
            }
            if (tvTime != null) {
                tvTime.setText(formatTime(m.thoiGianGui));
            }
            loadDoctorAvatar(ivAvatar);
        }

        private void loadDoctorAvatar(ImageView iv) {
            if (iv == null) return;
            String url = doctorAvatarUrl;
            if (url != null && url.startsWith("data:image")) {
                int comma = url.indexOf(',');
                if (comma > 0 && comma < url.length() - 1) {
                    String base64Data = url.substring(comma + 1);
                    byte[] bytes = android.util.Base64.decode(base64Data, android.util.Base64.DEFAULT);
                    Glide.with(context)
                            .load(bytes)
                            .circleCrop()
                            .placeholder(R.drawable.ic_doctor_placeholder)
                            .into(iv);
                } else {
                    iv.setImageResource(R.drawable.ic_doctor_placeholder);
                }
            } else if (url != null && !url.isEmpty()) {
                Glide.with(context)
                        .load(url)
                        .circleCrop()
                        .placeholder(R.drawable.ic_doctor_placeholder)
                        .into(iv);
            } else {
                iv.setImageResource(R.drawable.ic_doctor_placeholder);
            }
        }
    }
}
