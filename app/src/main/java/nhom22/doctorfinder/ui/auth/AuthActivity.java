package nhom22.doctorfinder.ui.auth;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import nhom22.doctorfinder.R;

public class AuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.authContainer, new LoginFragment())
                    .commit();
        }
    }
}
