package nhom22.doctorfinder.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import nhom22.doctorfinder.R;
import nhom22.doctorfinder.data.remote.dto.response.ConversationListItem;
import nhom22.doctorfinder.ui.user.chat.ChatBoxActivity;

/**
 * Danh sách cuộc hội thoại (màn Tin nhắn).
 */
public class ConversationListAdapter extends RecyclerView.Adapter<ConversationListAdapter.Holder> {

    private final Context context;
    private final int maTaiKhoanNguoiDung;
    private List<ConversationListItem> items = new ArrayList<>();

    public ConversationListAdapter(@NonNull Context context, int maTaiKhoanNguoiDung) {
        this.context = context;
        this.maTaiKhoanNguoiDung = maTaiKhoanNguoiDung;
    }

    public void submitList(@NonNull List<ConversationListItem> newList) {
        this.items = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_row, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private static String doctorTitle(ConversationListItem item) {
        String raw = !TextUtils.isEmpty(item.hoTenBacSi) ? item.hoTenBacSi : item.tenBacSi;

        if (TextUtils.isEmpty(raw)) return "BS.";

        raw = raw.trim();

        // tránh bị lặp "BS. BS. ..."
        if (raw.startsWith("BS.")) {
            return raw;
        }

        return "BS. " + raw;
    }

    private static String lastPreview(ConversationListItem item) {
        if (!TextUtils.isEmpty(item.noiDungTinNhanCuoi)) return item.noiDungTinNhanCuoi;
        if (!TextUtils.isEmpty(item.tinNhanCuoi)) return item.tinNhanCuoi;
        return "";
    }

    /** Hiển thị ngắn (giờ:phút hoặc đoạn đầu chuỗi). */
    private static String timeLabel(ConversationListItem item) {
        String raw = !TextUtils.isEmpty(item.thoiGianTinCuoi) ? item.thoiGianTinCuoi : item.thoiGianCapNhat;
        if (TextUtils.isEmpty(raw)) return "";
        raw = raw.trim();
        if (raw.length() >= 16 && raw.charAt(10) == 'T') {
            return raw.substring(11, 16);
        }
        if (raw.length() >= 5 && raw.charAt(2) == ':') {
            return raw.substring(0, 5);
        }
        if (raw.length() > 10) return raw.substring(0, 10);
        return raw;
    }

    private static void loadAvatar(@NonNull Context ctx, ImageView iv, String url) {
        if (iv == null) return;
        if (url != null && url.startsWith("data:image")) {
            int comma = url.indexOf(',');
            if (comma > 0 && comma < url.length() - 1) {
                String b64 = url.substring(comma + 1);
                byte[] bytes = android.util.Base64.decode(b64, android.util.Base64.DEFAULT);
                Glide.with(ctx)
                        .load(bytes)
                        .circleCrop()
                        .placeholder(R.drawable.ic_doctor_placeholder)
                        .into(iv);
            } else {
                iv.setImageResource(R.drawable.ic_doctor_placeholder);
            }
        } else if (!TextUtils.isEmpty(url)) {
            Glide.with(ctx)
                    .load(url)
                    .circleCrop()
                    .placeholder(R.drawable.ic_doctor_placeholder)
                    .into(iv);
        } else {
            iv.setImageResource(R.drawable.ic_doctor_placeholder);
        }
    }

    class Holder extends RecyclerView.ViewHolder {
        final ImageView ivAvatar;
        final TextView tvName;
        final TextView tvLastMessage;
        final TextView tvTime;
        final TextView tvUnread;

        Holder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            tvName = itemView.findViewById(R.id.tvName);
            tvLastMessage = itemView.findViewById(R.id.tvLastMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvUnread = itemView.findViewById(R.id.tvUnread);
        }

        void bind(ConversationListItem item) {
            if (tvName != null) tvName.setText(doctorTitle(item));
            String preview = lastPreview(item);
            if (tvLastMessage != null) {
                tvLastMessage.setText(!TextUtils.isEmpty(preview) ? preview : " ");
            }
            if (tvTime != null) tvTime.setText(timeLabel(item));

            if (tvUnread != null) {
                Integer n = item.soTinChuaDoc;
                if (n != null && n > 0) {
                    tvUnread.setVisibility(View.VISIBLE);
                    tvUnread.setText(n > 99 ? "99+" : String.valueOf(n));
                } else {
                    tvUnread.setVisibility(View.GONE);
                }
            }

            loadAvatar(context, ivAvatar, item.anhDaiDienBacSi);

            itemView.setOnClickListener(v -> {
                Intent i = new Intent(context, ChatBoxActivity.class);
                i.putExtra("maCuocHoiThoai", item.maCuocHoiThoai);
                i.putExtra("maTaiKhoanNguoiDung", maTaiKhoanNguoiDung);
                String name = doctorTitle(item);
                i.putExtra("doctor_name", name);
                i.putExtra("doctor_avatar_url", item.anhDaiDienBacSi != null ? item.anhDaiDienBacSi : "");
                context.startActivity(i);
            });
        }
    }
}
