package nhom22.doctorfinder.ui.user.schedule;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

import nhom22.doctorfinder.R;
import nhom22.doctorfinder.ui.user.search.SearchResultActivity;
import nhom22.doctorfinder.ui.user.schedule.SchedulingInformationActivity;

public class SelectCenlendarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_cenlendar);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        View btnTiepTuc = findViewById(R.id.btnTiepTuc);
        View btnHuy = findViewById(R.id.btnHuy);

        if (btnTiepTuc != null) {
            btnTiepTuc.setOnClickListener(v -> {
                Intent intent = new Intent(SelectCenlendarActivity.this, SchedulingInformationActivity.class);
                // pass selected slot information via extras if needed
                startActivity(intent);
            });
        }

        if (btnHuy != null) {
            btnHuy.setOnClickListener(v -> finish());
        }
    }
}
