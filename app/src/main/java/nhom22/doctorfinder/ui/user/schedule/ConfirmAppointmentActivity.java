package nhom22.doctorfinder.ui.user.schedule;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

import nhom22.doctorfinder.R;
import nhom22.doctorfinder.ui.user.chat.ChatBoxActivity;

public class ConfirmAppointmentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_appointment);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        View btnHuyLich = findViewById(R.id.btnHuyLich);
        View btnNhanTin = findViewById(R.id.btnNhanTin);
        View btnTrangChu = findViewById(R.id.btnTrangChu);

        if (btnHuyLich != null) {
            btnHuyLich.setOnClickListener(v -> {
                // In real app we'd update server and release slot. Here just finish.
                finish();
            });
        }

        if (btnNhanTin != null) {
            btnNhanTin.setOnClickListener(v -> {
                Intent intent = new Intent(ConfirmAppointmentActivity.this, ChatBoxActivity.class);
                startActivity(intent);
            });
        }

        if (btnTrangChu != null) {
            btnTrangChu.setOnClickListener(v -> {
                // Return to previous or home; for now finish stack to return to home
                finish();
            });
        }
    }
}
