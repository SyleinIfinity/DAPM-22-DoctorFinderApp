package nhom22.doctorfinder.ui.user.schedule;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

import nhom22.doctorfinder.R;

public class SchedulingInformationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduling_information);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        View btnQuayLai = findViewById(R.id.btnQuayLai);
        View btnGuiPhieu = findViewById(R.id.btnGuiPhieu);

        if (btnQuayLai != null) {
            btnQuayLai.setOnClickListener(v -> finish());
        }

        if (btnGuiPhieu != null) {
            btnGuiPhieu.setOnClickListener(v -> {
                Intent intent = new Intent(SchedulingInformationActivity.this, ConfirmAppointmentActivity.class);
                // pass scheduling info if needed
                startActivity(intent);
            });
        }
    }
}
