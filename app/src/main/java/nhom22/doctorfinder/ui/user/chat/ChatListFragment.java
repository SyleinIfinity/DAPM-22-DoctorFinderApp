package nhom22.doctorfinder.ui.user.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import nhom22.doctorfinder.R;

public class ChatListFragment extends Fragment {

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
        View rowChat1 = view.findViewById(R.id.rowChat1);
        View rowChat2 = view.findViewById(R.id.rowChat2);
        View.OnClickListener openChat = v -> {
            Intent intent = new Intent(requireContext(), ChatBoxActivity.class);
            startActivity(intent);
        };
        if (rowChat1 != null) {
            rowChat1.setOnClickListener(openChat);
        }
        if (rowChat2 != null) {
            rowChat2.setOnClickListener(openChat);
        }
    }
}
