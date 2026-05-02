package nhom22.doctorfinder;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.navigation.NavigationBarView;

import android.os.Bundle;

import nhom22.doctorfinder.ui.home.HomeFragment;
import nhom22.doctorfinder.ui.user.booking.history.HistoryFragment;
import nhom22.doctorfinder.ui.user.chat.ChatListFragment;
import nhom22.doctorfinder.ui.user.follow.FollowFragment;
import nhom22.doctorfinder.ui.user.user_profile.AccountFragment;

public class UserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_UNLABELED);
        bottomNavigation.setItemBackgroundResource(R.drawable.bg_bottom_nav_item);
        bottomNavigation.setOnItemSelectedListener(item -> {
            getSupportFragmentManager().popBackStackImmediate(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
            if (item.getItemId() == R.id.nav_trang_chu) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, HomeFragment.newInstance())
                        .commit();
                updateBottomNavIconSizes(bottomNavigation, item.getItemId());
                return true;
            }
            if (item.getItemId() == R.id.nav_lich_hen) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, HistoryFragment.newInstance())
                        .commit();
                updateBottomNavIconSizes(bottomNavigation, item.getItemId());
                return true;
            }
            if (item.getItemId() == R.id.nav_theo_doi) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, FollowFragment.newInstance())
                        .commit();
                updateBottomNavIconSizes(bottomNavigation, item.getItemId());
                return true;
            }
            if (item.getItemId() == R.id.nav_tinnhan) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, ChatListFragment.newInstance())
                        .commit();
                updateBottomNavIconSizes(bottomNavigation, item.getItemId());
                return true;
            }
            if (item.getItemId() == R.id.nav_tai_khoan) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, AccountFragment.newInstance())
                        .commit();
                updateBottomNavIconSizes(bottomNavigation, item.getItemId());
                return true;
            }
            return false;
        });

        if (savedInstanceState == null) {
            bottomNavigation.setSelectedItemId(R.id.nav_trang_chu);
        }
    }

    private void updateBottomNavIconSizes(BottomNavigationView bottomNavigation, int selectedItemId) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) bottomNavigation.getChildAt(0);
        int defaultSize = dpToPx(24);
        int selectedSize = dpToPx(30);
        for (int i = 0; i < menuView.getChildCount(); i++) {
            BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(i);
            int itemId = bottomNavigation.getMenu().getItem(i).getItemId();
            itemView.setIconSize(itemId == selectedItemId ? selectedSize : defaultSize);
        }
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}
